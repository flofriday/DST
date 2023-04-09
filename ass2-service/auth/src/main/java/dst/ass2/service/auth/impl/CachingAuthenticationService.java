package dst.ass2.service.auth.impl;

import dst.ass1.jpa.dao.IDAOFactory;
import dst.ass1.jpa.model.IRider;
import dst.ass2.service.api.auth.AuthenticationException;
import dst.ass2.service.api.auth.NoSuchUserException;
import dst.ass2.service.auth.ICachingAuthenticationService;

import javax.annotation.ManagedBean;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Singleton
@ManagedBean
public class CachingAuthenticationService implements ICachingAuthenticationService {

    ConcurrentHashMap<String, byte[]> passwords = new ConcurrentHashMap<>(); // Email -> password hash
    ConcurrentHashMap<String, String> emails = new ConcurrentHashMap<>();    // Token -> email

    @PersistenceContext(name = "dst")
    private EntityManager em;

    @Inject
    IDAOFactory daoFactory;

    private byte[] hashPassowrd(String password) {
        try {
            return MessageDigest.getInstance("SHA1").digest(password.getBytes());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void changePassword(String email, String newPassword) throws NoSuchUserException {
        var rider = daoFactory.createRiderDAO().findByEmail(email);
        if (rider == null) throw new NoSuchUserException();
        var newSha1 = hashPassowrd(newPassword);
        rider.setPassword(newSha1);
        passwords.put(email, newSha1);
    }

    @Override
    public String getUser(String token) {
        if (token == null) return null;
        return emails.get(token);
    }

    @Override
    public boolean isValid(String token) {
        if (token == null) return false;
        return emails.containsKey(token);
    }

    @Override
    public boolean invalidate(String token) {
        if (token == null) return false;
        return emails.remove(token) != null;
    }

    private String toHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    @Override
    @Transactional
    public String authenticate(String email, String password) throws NoSuchUserException, AuthenticationException {
        // Get saved (hashed) password
        var sha1 = passwords.get(email);
        if (sha1 == null) {
            var rider = daoFactory.createRiderDAO().findByEmail(email);
            if (rider == null)
                throw new NoSuchUserException();

            sha1 = rider.getPassword();
            this.passwords.put(email, sha1);
        }

        // Verify the password
        if (!Arrays.equals(sha1, hashPassowrd(password)))
            throw new AuthenticationException();

        // Create a new token
        var token = UUID.randomUUID().toString();
        emails.put(token, email);
        return token;
    }

    @Override
    @PostConstruct
    public void loadData() {
        passwords = new ConcurrentHashMap<>(
                daoFactory.createRiderDAO()
                        .findAll()
                        .stream()
                        .collect(Collectors.toMap(IRider::getEmail, IRider::getPassword)));
    }

    @Override
    public void clearCache() {
        passwords.clear();
    }
}

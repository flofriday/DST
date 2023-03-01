package dst.ass1.jpa.model;

import java.util.Collection;

public interface IRider extends IPlatformUser {

    String getEmail();

    void setEmail(String email);

    byte[] getPassword();

    void setPassword(byte[] password);

    String getAccountNo();

    void setAccountNo(String accountNo);

    String getBankCode();

    void setBankCode(String bankCode);

    Collection<ITrip> getTrips();

    void setTrips(Collection<ITrip> trips);

    void addTrip(ITrip trip);
}

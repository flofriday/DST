package dst.ass1.jpa.model.impl;

import dst.ass1.jpa.model.IRider;
import dst.ass1.jpa.model.ITrip;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collection;

@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"accountNo", "bankCode"})})
@NamedQuery(
        name = "riderByEmail",
        query = "SELECT r FROM Rider r WHERE r.email = :email"
)
public class Rider extends PlatformUser implements IRider {

    @NotNull
    @Column(unique = true, nullable = false)
    private String email;

    // Note: SHA1 hashes have a length of 20 bytes
    @Column(length = 20)
    private byte[] password;

    private String accountNo;
    private String bankCode;

    @OneToMany
    private Collection<Trip> trips = new ArrayList<>();

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public byte[] getPassword() {
        return password;
    }

    @Override
    public void setPassword(byte[] password) {
        this.password = password;
    }

    @Override
    public String getAccountNo() {
        return accountNo;
    }

    @Override
    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    @Override
    public String getBankCode() {
        return bankCode;
    }

    @Override
    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    @Override
    public Collection<ITrip> getTrips() {
        return new ArrayList<ITrip>(trips);
    }

    @Override
    public void setTrips(Collection<ITrip> trips) {
        this.trips.clear();
        for (var trip : trips) {
            addTrip(trip);
        }
    }

    @Override
    public void addTrip(ITrip trip) {
        this.trips.add((Trip) trip);
    }
}
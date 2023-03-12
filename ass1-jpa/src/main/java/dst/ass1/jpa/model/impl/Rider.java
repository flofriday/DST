package dst.ass1.jpa.model.impl;

import dst.ass1.jpa.model.IRider;
import dst.ass1.jpa.model.ITrip;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collection;

@Entity
public class Rider extends PlatformUser implements IRider {

    @NotNull
    private String email;

    private byte[] password;
    private String accountNo;
    private String bankCode;

    @OneToMany
    private Collection<Trip> trips;

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
        // FIXME: That feels soooo hacky
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
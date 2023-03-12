package dst.ass1.jpa.model.impl;

import dst.ass1.jpa.model.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
public class Match implements IMatch {
    @Id
    private Long id;

    private Date date;
    private Money fare;

    @OneToOne
    @NotNull
    private Trip trip;

    @ManyToOne
    @NotNull
    private Driver driver;

    @ManyToOne
    @NotNull
    private Vehicle car;


    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Date getDate() {
        return date;
    }

    @Override
    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public IMoney getFare() {
        return fare;
    }

    @Override
    public void setFare(IMoney fare) {
        this.fare = (Money) fare;
    }

    @Override
    public ITrip getTrip() {
        return trip;
    }

    @Override
    public void setTrip(ITrip trip) {
        this.trip = (Trip) trip;
    }

    @Override
    public IVehicle getVehicle() {
        return car;
    }

    @Override
    public void setVehicle(IVehicle vehicle) {
        this.car = (Vehicle) car;
    }

    @Override
    public IDriver getDriver() {
        return driver;
    }

    @Override
    public void setDriver(IDriver driver) {
        this.driver = (Driver) driver;
    }

}

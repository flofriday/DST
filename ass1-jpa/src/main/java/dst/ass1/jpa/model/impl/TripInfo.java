package dst.ass1.jpa.model.impl;

import dst.ass1.jpa.model.IMoney;
import dst.ass1.jpa.model.ITrip;
import dst.ass1.jpa.model.ITripInfo;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
public class TripInfo implements ITripInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Date completed;
    private Double distance;
    private Money total;
    private int driverRating;
    private int riderRating;

    @OneToOne(optional = false)
    @NotNull
    private Trip trip;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Date getCompleted() {
        return completed;
    }

    @Override
    public void setCompleted(Date completed) {
        this.completed = completed;
    }

    @Override
    public Double getDistance() {
        return distance;
    }

    @Override
    public void setDistance(Double distance) {
        this.distance = distance;
    }

    @Override
    public Money getTotal() {
        return total;
    }

    @Override
    public void setTotal(IMoney total) {
        this.total = (Money) total;
    }

    @Override
    public Integer getDriverRating() {
        return driverRating;
    }

    @Override
    public void setDriverRating(Integer driverRating) {
        this.driverRating = driverRating;
    }

    @Override
    public Integer getRiderRating() {
        return riderRating;
    }

    @Override
    public void setRiderRating(Integer riderRating) {
        this.riderRating = riderRating;
    }

    @Override
    public ITrip getTrip() {
        return trip;
    }

    @Override
    public void setTrip(ITrip trip) {
        this.trip = (Trip) trip;
    }
}

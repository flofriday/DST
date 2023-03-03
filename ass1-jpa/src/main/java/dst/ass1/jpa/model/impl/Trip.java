package dst.ass1.jpa.model.impl;

import dst.ass1.jpa.model.ITrip;
import dst.ass1.jpa.model.TripState;

import javax.persistence.*;
import java.util.Collection;
import java.util.Date;

@Entity
public class Trip implements ITrip {
    @Id
    private Long id;

    private Date created;
    private Date updated;

    private TripState state;

    @OneToOne
    private Location pickup;

    @OneToOne
    private Location destination;

    @ManyToMany
    private Collection<Location> stops;

    @ManyToOne()
    private Rider rider;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Date getCreated() {
        return created;
    }

    @Override
    public void setCreated(Date created) {
        this.created = created;
    }

    @Override
    public Date getUpdated() {
        return updated;
    }

    @Override
    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    @Override
    public TripState getState() {
        return state;
    }

    @Override
    public void setState(TripState state) {
        this.state = state;
    }

    @Override
    public Location getPickup() {
        return pickup;
    }

    @Override
    public void setPickup(Location pickup) {
        this.pickup = pickup;
    }

    @Override
    public Location getDestination() {
        return destination;
    }

    @Override
    public void setDestination(Location destination) {
        this.destination = destination;
    }

    @Override
    public Collection<Location> getStops() {
        return stops;
    }

    @Override
    public void setStops(Collection<Location> stops) {
        this.stops = stops;
    }
}

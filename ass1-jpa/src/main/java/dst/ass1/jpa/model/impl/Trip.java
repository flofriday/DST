package dst.ass1.jpa.model.impl;

import dst.ass1.jpa.model.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

@Entity
public class Trip implements ITrip {
    // FIXME: Must this file be defined in XML or are they taking about something else?
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Date created;
    private Date updated;

    private TripState state;

    @ManyToOne(optional = false)
    private Location pickup;

    @ManyToOne(optional = false)
    private Location destination;

    @ManyToMany
    private Collection<Location> stops = new ArrayList<>();

    @OneToOne(mappedBy = "trip", cascade = CascadeType.ALL)
    private TripInfo tripInfo;

    @ManyToOne()
    private Rider rider;

    @OneToOne(mappedBy = "trip")
    private Match match;

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
    public void setPickup(ILocation pickup) {
        this.pickup = (Location) pickup;
    }

    @Override
    public Location getDestination() {
        return destination;
    }

    @Override
    public void setDestination(ILocation destination) {
        this.destination = (Location) destination;
    }

    @Override
    public Collection<ILocation> getStops() {
        return new ArrayList<ILocation>(stops);
    }

    @Override
    public void setStops(Collection<ILocation> stops) {
        this.stops.clear();
        for (var stop : stops) {
            addStop(stop);
        }
    }

    @Override
    public void addStop(ILocation stop) {
        stops.add((Location) stop);
    }

    @Override
    public ITripInfo getTripInfo() {
        return tripInfo;
    }

    @Override
    public void setTripInfo(ITripInfo tripInfo) {
        this.tripInfo = (TripInfo) tripInfo;
    }

    @Override
    public IMatch getMatch() {
        return match;
    }

    @Override
    public void setMatch(IMatch match) {
        this.match = (Match) match;
    }

    @Override
    public IRider getRider() {
        return rider;
    }

    @Override
    public void setRider(IRider rider) {
        this.rider = (Rider) rider;
    }

}

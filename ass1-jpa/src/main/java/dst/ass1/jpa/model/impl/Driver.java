package dst.ass1.jpa.model.impl;

import dst.ass1.jpa.model.IDriver;
import dst.ass1.jpa.model.IEmployment;
import dst.ass1.jpa.model.IVehicle;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collection;


@Entity
@NamedQuery(
        name = "activeInMultipleOrganizationsDrivers",

        // FIXME: There are more tests needed for that query, the official ones don't test enough
        /*query = "SELECT d FROM TripInfo i JOIN i.trip.match.driver d WHERE d IN" +
                "(" +
                "SELECT d2 FROM Driver d2 " +
                "JOIN d2.employments e " +
                "JOIN e.id.organization o " +
                "WHERE o.id = :organizationId AND e.active = true AND e.since <= i.completed" +
                ")" +
                "GROUP BY d ORDER BY SUM (i.distance) DESC"*/

        query = "SELECT d " +
                "FROM TripInfo i " +
                "JOIN i.trip.match.driver d " +
                "JOIN d.employments e " +
                "JOIN e.id.organization o " +
                "WHERE o.id = :organizationId " +
                "AND e.active = true " +
                "AND e.since <= i.completed " +
                "GROUP BY d " +
                "ORDER BY SUM(i.distance) DESC"
)
public class Driver extends PlatformUser implements IDriver {

    @OneToMany(mappedBy = "id.driver")
    private Collection<Employment> employments = new ArrayList<>();

    @ManyToOne(optional = false)
    private Vehicle vehicle;

    @Override
    public Collection<IEmployment> getEmployments() {
        return new ArrayList<IEmployment>(employments);
    }

    @Override
    public void setEmployments(Collection<IEmployment> employments) {
        this.employments.clear();
        for (var employment : employments) {
            addEmployment(employment);
        }
    }

    @Override
    public void addEmployment(IEmployment employment) {
        this.employments.add((Employment) employment);
    }

    @Override
    public IVehicle getVehicle() {
        return vehicle;
    }

    @Override
    public void setVehicle(IVehicle vehicle) {
        this.vehicle = (Vehicle) vehicle;
    }
}

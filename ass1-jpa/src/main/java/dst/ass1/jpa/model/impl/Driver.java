package dst.ass1.jpa.model.impl;

import dst.ass1.jpa.model.IDriver;
import dst.ass1.jpa.model.IEmployment;
import dst.ass1.jpa.model.IVehicle;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collection;

@Entity
public class Driver extends PlatformUser implements IDriver {

    // FIXME: Make bidirectional
    @OneToMany
    private Collection<Employment> employments;

    @ManyToOne
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

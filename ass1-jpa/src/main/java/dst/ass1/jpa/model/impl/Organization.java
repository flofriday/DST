package dst.ass1.jpa.model.impl;

import dst.ass1.jpa.model.IEmployment;
import dst.ass1.jpa.model.IOrganization;
import dst.ass1.jpa.model.IVehicle;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collection;

@Entity
public class Organization implements IOrganization {
    @Id
    private Long id;

    private String name;

    @OneToMany
    private Collection<Employment> employments;

    @ManyToMany
    private Collection<Vehicle> vehicles;

    // FIXME: Is this correct?
    @ManyToMany(mappedBy = "parts")
    private Collection<Organization> partOf;
    @ManyToMany
    private Collection<Organization> parts;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Collection<IOrganization> getParts() {
        return new ArrayList<>(parts);
    }

    @Override
    public void setParts(Collection<IOrganization> parts) {
        parts.clear();
        for (var part : parts) {
            addPart((part));
        }
    }

    @Override
    public void addPart(IOrganization part) {
        this.parts.add((Organization) part);
    }

    @Override
    public Collection<IOrganization> getPartOf() {
        return new ArrayList<IOrganization>(partOf);
    }

    @Override
    public void setPartOf(Collection<IOrganization> partOf) {
        this.partOf.clear();
        for (var p : partOf) {
            addPartOf(p);
        }
    }

    @Override
    public void addPartOf(IOrganization partOf) {
        this.partOf.add((Organization) partOf);
    }

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
        employments.add((Employment) employment);
    }

    @Override
    public Collection<IVehicle> getVehicles() {
        return new ArrayList<IVehicle>(vehicles);
    }

    @Override
    public void setVehicles(Collection<IVehicle> vehicles) {
        this.vehicles.clear();
        for (var vehicle : vehicles) {
            addVehicle(vehicle);
        }
    }

    @Override
    public void addVehicle(IVehicle vehicle) {
        this.vehicles.add((Vehicle) vehicle);
    }

}

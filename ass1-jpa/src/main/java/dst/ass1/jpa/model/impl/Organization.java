package dst.ass1.jpa.model.impl;

import dst.ass1.jpa.model.IEmployment;
import dst.ass1.jpa.model.IOrganization;
import dst.ass1.jpa.model.IVehicle;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;

import static dst.ass1.jpa.util.Constants.*;

@Entity
public class Organization implements IOrganization {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "id.organization")
    private Collection<Employment> employments = new ArrayList<>();

    @ManyToMany
    private Collection<Vehicle> vehicles = new ArrayList<>();

    // FIXME: Is this correct?
    @ManyToMany
    @JoinTable(
            name = J_ORGANIZATION_PARTS,
            joinColumns = @JoinColumn(name = I_ORGANIZATION_PART_OF),
            inverseJoinColumns = @JoinColumn(name = I_ORGANIZATION_PARTS)
    )
    private Collection<Organization> parts = new ArrayList<>();
    @ManyToMany(mappedBy = "parts")
    private Collection<Organization> partOf = new ArrayList<>();

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

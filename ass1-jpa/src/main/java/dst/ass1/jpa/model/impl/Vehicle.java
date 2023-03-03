package dst.ass1.jpa.model.impl;

import dst.ass1.jpa.model.IVehicle;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;


@Entity
public class Vehicle implements IVehicle {
    @Id
    private Long id;

    @Column(unique = true)
    private String license; // FIXME: Is this the correct way to make it unique?

    private String color;
    private String type;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getLicense() {
        return license;
    }

    @Override
    public void setLicense(String license) {
        this.license = license;
    }

    @Override
    public String getColor() {
        return color;
    }

    @Override
    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }
}

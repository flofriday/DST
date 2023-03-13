package dst.ass1.jpa.model.impl;

import dst.ass1.jpa.model.IEmployment;
import dst.ass1.jpa.model.IEmploymentKey;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import java.util.Date;

@Entity
public class Employment implements IEmployment {

    @EmbeddedId
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private EmploymentKey id;

    private Date since;
    private boolean active;

    @Override
    public IEmploymentKey getId() {
        return id;
    }

    @Override
    public void setId(IEmploymentKey id) {
        this.id = (EmploymentKey) id;
    }

    @Override
    public Date getSince() {
        return since;
    }

    @Override
    public void setSince(Date since) {
        this.since = since;
    }

    @Override
    public Boolean isActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}

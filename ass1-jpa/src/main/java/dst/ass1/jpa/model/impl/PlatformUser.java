package dst.ass1.jpa.model.impl;

import dst.ass1.jpa.model.IPlatformUser;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/*
 * Three Inheritance patterns
 *
 * 1) Abstract Entities         ... can not be instatiated but normally queried (like a own table).
 * 2) Mapped Supperclass        ... liked mapped entities "inlined" into extending subtype.
 * 3) Non-Entity Superclasses   ... their members are not persistent.
 *
 */

@MappedSuperclass
public abstract class PlatformUser implements IPlatformUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @NotNull
    @Column(nullable = false)
    private String tel;

    private Double avgRating;

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
    public String getTel() {
        return tel;
    }

    @Override
    public void setTel(String tel) {
        this.tel = tel;
    }

    @Override
    public Double getAvgRating() {
        return avgRating;
    }

    @Override
    public void setAvgRating(Double avgRating) {
        this.avgRating = avgRating;
    }
}

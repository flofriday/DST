package dst.ass1.jpa.model;

import java.util.Date;

public interface ITripInfo {

    Long getId();

    void setId(Long id);

    Date getCompleted();

    void setCompleted(Date date);

    Double getDistance();

    void setDistance(Double distance);

    IMoney getTotal();

    void setTotal(IMoney money);

    Integer getDriverRating();

    void setDriverRating(Integer driverRating);

    Integer getRiderRating();

    void setRiderRating(Integer riderRating);

    ITrip getTrip();

    void setTrip(ITrip trip);

}

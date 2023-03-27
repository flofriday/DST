package dst.ass1.jooq.dao.impl;

import dst.ass1.jooq.connection.DataSource;
import dst.ass1.jooq.dao.IRiderPreferenceDAO;
import dst.ass1.jooq.model.IRiderPreference;
import org.jooq.Configuration;
import org.jooq.DSLContext;

import java.sql.SQLException;
import java.util.List;

import static dst.ass1.jooq.model.public_.Tables.PREFERENCE;
import static dst.ass1.jooq.model.public_.Tables.RIDER_PREFERENCE;

public class RiderPreferenceDAO implements IRiderPreferenceDAO {
    private DSLContext getConnection() {
        try {
            return DataSource.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public IRiderPreference findById(Long id) {
        // FIXME: Implement
        return null;
    }

    @Override
    public List<IRiderPreference> findAll() {
        // FIXME: Implement
        return null;
    }

    @Override
    public IRiderPreference insert(IRiderPreference model) {
        var dsl = getConnection();
        // FIXME: This should be a transaction

        dsl.transaction((Configuration trx) -> {
            // Insert the Rider
            trx.dsl().insertInto(RIDER_PREFERENCE)
                    .set(RIDER_PREFERENCE.RIDER_ID, model.getRiderId())
                    .set(RIDER_PREFERENCE.AREA, model.getArea())
                    .set(RIDER_PREFERENCE.VEHICLE_CLASS, model.getVehicleClass())
                    .execute();

            // Insert all Preferences
            var query = trx.dsl().batch(trx.dsl().insertInto(PREFERENCE, PREFERENCE.RIDER_ID, PREFERENCE.PREF_KEY, PREFERENCE.PREF_VALUE).values((Long) null, (String) null, (String) null));
            for (var e : model.getPreferences().entrySet()) {
                query.bind(model.getRiderId(), e.getKey(), e.getValue());
            }
            query.execute();
        });


        // FIXME: What do they want here?
        return model;
    }

    @Override
    public void delete(Long id) {
        // FIXME: Implement

    }

    @Override
    public void updatePreferences(IRiderPreference model) {
        // FIXME: Implement

    }
}

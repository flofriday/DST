package dst.ass1.jooq.dao.impl;

import dst.ass1.jooq.connection.DataSource;
import dst.ass1.jooq.dao.IRiderPreferenceDAO;
import dst.ass1.jooq.model.IRiderPreference;
import dst.ass1.jooq.model.impl.RiderPreference;
import org.jooq.Configuration;
import org.jooq.DSLContext;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        var dsl = getConnection();

        // First get the keys and values
        var result = dsl
                .select()
                .from(PREFERENCE)
                .where(PREFERENCE.RIDER_ID.eq(id))
                .fetch();

        Map<String, String> preferences = new HashMap<>();
        for (var r : result) {
            String key = r.get(PREFERENCE.PREF_KEY);
            String value = r.get(PREFERENCE.PREF_VALUE);
            preferences.put(key, value);
        }

        //  Get the rider preference
        var record = dsl.select().from(RIDER_PREFERENCE)
                .where(RIDER_PREFERENCE.RIDER_ID.eq(id))
                .fetchOne();

        RiderPreference riderPreference = new RiderPreference();
        riderPreference.setRiderId(id);
        riderPreference.setArea(record.get(RIDER_PREFERENCE.AREA));
        riderPreference.setVehicleClass(record.get(RIDER_PREFERENCE.VEHICLE_CLASS));
        riderPreference.setPreferences(preferences);

        return riderPreference;
    }

    @Override
    public List<IRiderPreference> findAll() {
        // FIXME: Implement
        return null;
    }

    @Override
    public IRiderPreference insert(IRiderPreference model) {
        var dsl = getConnection();

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

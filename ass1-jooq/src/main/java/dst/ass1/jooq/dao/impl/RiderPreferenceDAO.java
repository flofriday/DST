package dst.ass1.jooq.dao.impl;

import dst.ass1.jooq.connection.DataSource;
import dst.ass1.jooq.dao.IRiderPreferenceDAO;
import dst.ass1.jooq.model.IRiderPreference;
import dst.ass1.jooq.model.impl.RiderPreference;
import org.jooq.Configuration;
import org.jooq.DSLContext;

import java.sql.SQLException;
import java.util.ArrayList;
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

        //  Get the rider preference
        var record = dsl.select()
                .from(RIDER_PREFERENCE)
                .where(RIDER_PREFERENCE.RIDER_ID.eq(id))
                .fetchOne();

        if (record == null) return null;

        RiderPreference riderPreference = new RiderPreference();
        riderPreference.setRiderId(id);
        riderPreference.setArea(record.get(RIDER_PREFERENCE.AREA));
        riderPreference.setVehicleClass(record.get(RIDER_PREFERENCE.VEHICLE_CLASS));

        // Now get all related preferences
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
        riderPreference.setPreferences(preferences);

        return riderPreference;
    }

    @Override
    public List<IRiderPreference> findAll() {
        var dsl = getConnection();

        var records = dsl.select()
                .from(RIDER_PREFERENCE
                        .leftJoin(PREFERENCE)
                        .on(PREFERENCE.RIDER_ID.eq(RIDER_PREFERENCE.RIDER_ID)))
                .fetch();

        Map<Long, RiderPreference> map = new HashMap<>();
        for (var record : records) {
            Long riderId = record.get(RIDER_PREFERENCE.RIDER_ID);
            if (!map.containsKey(riderId)) {
                var riderPreference = new RiderPreference();
                riderPreference.setRiderId(riderId);
                riderPreference.setArea(record.get(RIDER_PREFERENCE.AREA));
                riderPreference.setVehicleClass(record.get(RIDER_PREFERENCE.VEHICLE_CLASS));
                map.put(riderId, riderPreference);
            }

            // Guard to ignore null values
            if (record.get(PREFERENCE.PREF_KEY) == null) continue;

            var riderPreference = map.get(riderId);
            if (riderPreference.getPreferences() == null)
                riderPreference.setPreferences(new HashMap<>());

            riderPreference.getPreferences().put(
                    record.get(PREFERENCE.PREF_KEY),
                    record.get(PREFERENCE.PREF_VALUE)
            );
        }

        return new ArrayList<>(map.values());
    }

    @Override
    public IRiderPreference insert(IRiderPreference model) {

        getConnection().transaction((Configuration trx) -> {
            // Insert the Rider
            trx.dsl().insertInto(RIDER_PREFERENCE)
                    .set(RIDER_PREFERENCE.RIDER_ID, model.getRiderId())
                    .set(RIDER_PREFERENCE.AREA, model.getArea())
                    .set(RIDER_PREFERENCE.VEHICLE_CLASS, model.getVehicleClass())
                    .execute();

            // Insert all Preferences, by first defining the insert query and later binding all preferences to it.
            var query = trx.dsl().batch(
                    trx.dsl().insertInto(
                            PREFERENCE,
                            PREFERENCE.RIDER_ID,
                            PREFERENCE.PREF_KEY,
                            PREFERENCE.PREF_VALUE
                    ).values(
                            (Long) null,
                            (String) null,
                            (String) null
                    ));

            for (var e : model.getPreferences().entrySet()) {
                query.bind(model.getRiderId(), e.getKey(), e.getValue());
            }
            query.execute();
        });

        return model;
    }

    @Override
    public void delete(Long id) {
        getConnection().transaction((Configuration trx) -> {
            trx.dsl()
                    .deleteFrom(PREFERENCE)
                    .where(PREFERENCE.RIDER_ID.eq(id))
                    .execute();

            trx.dsl()
                    .deleteFrom(RIDER_PREFERENCE)
                    .where(RIDER_PREFERENCE.RIDER_ID.eq(id))
                    .execute();
        });
    }

    @Override
    public void updatePreferences(IRiderPreference model) {
        getConnection().batched(trx -> {
            for (var e : model.getPreferences().entrySet()) {
                trx.dsl()
                        .insertInto(PREFERENCE)
                        .set(PREFERENCE.RIDER_ID, model.getRiderId())
                        .set(PREFERENCE.PREF_VALUE, e.getValue())
                        .set(PREFERENCE.PREF_KEY, e.getKey())
                        .onDuplicateKeyUpdate()
                        .set(PREFERENCE.PREF_VALUE, e.getValue())
                        .execute();

            }
        });
    }
}

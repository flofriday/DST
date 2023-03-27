package dst.ass1.jooq.model.impl;

import dst.ass1.jooq.connection.DataSource;
import dst.ass1.jooq.model.IRiderPreference;
import dst.ass1.jooq.model.public_.tables.Preference;
import org.jooq.DSLContext;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static dst.ass1.jooq.model.public_.Tables.PREFERENCE;

public class RiderPreference extends dst.ass1.jooq.model.public_.tables.pojos.RiderPreference implements IRiderPreference {
    private Map<String, String> preferences = new HashMap<>();

    @Override
    public Map<String, String> getPreferences() {
        /*
        DSLContext context;
        try {
            context = DataSource.getConnection();
        } catch (SQLException e) {
            return null;
        }

        var result = context
                .select()
                .from(PREFERENCE)
                .where(PREFERENCE.RIDER_ID.eq(this.getRiderId()))
                .fetch();

        Map<String, String> map = new HashMap<>();
        for (var r : result) {
            String key = r.get(PREFERENCE.PREF_KEY);
            String value = r.get(PREFERENCE.PREF_VALUE);
            map.put(key, value);
        }

        return map;
         */

        return preferences;
    }

    @Override
    public void setPreferences(Map<String, String> preferences) {
        this.preferences = preferences;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        RiderPreference that = (RiderPreference) o;
        return Objects.equals(preferences, that.preferences);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), preferences);
    }
}
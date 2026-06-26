package com.kkobau.earthquakemonitor.db;

import com.kkobau.earthquakemonitor.dto.EarthquakeFeature;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class EarthquakeDao {

    private final NamedParameterJdbcTemplate jdbc;

    public EarthquakeDao(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public void saveAll(List<EarthquakeFeature> features) {
        SqlParameterSource[] params = features.stream()
                .map(f -> new MapSqlParameterSource()
                        .addValue("id", f.id())
                        .addValue("mag", f.properties().mag())
                        .addValue("magType", f.properties().magType())
                        .addValue("place", f.properties().place())
                        .addValue("time", f.properties().time())
                        .addValue("updated", f.properties().updated())
                        .addValue("tsunami", f.properties().tsunami())
                        .addValue("sig", f.properties().sig())
                        .addValue("alert", f.properties().alert())
                        .addValue("status", f.properties().status())
                        .addValue("type", f.properties().type())
                        .addValue("title", f.properties().title())
                        .addValue("felt", f.properties().felt())
                        .addValue("cdi", f.properties().cdi())
                        .addValue("mmi", f.properties().mmi())
                        .addValue("nst", f.properties().nst())
                        .addValue("dmin", f.properties().dmin())
                        .addValue("rms", f.properties().rms())
                        .addValue("gap", f.properties().gap())
                        .addValue("net", f.properties().net())
                        .addValue("longitude", f.geometry().coordinates().longitude())
                        .addValue("latitude", f.geometry().coordinates().latitude())
                        .addValue("depth", f.geometry().coordinates().depth()))
                .toArray(SqlParameterSource[]::new);

        jdbc.batchUpdate("""
                INSERT INTO earthquakes (id, mag, mag_type, place, time, updated, tsunami, sig,
                                        alert, status, type, title, felt, cdi, mmi, nst, dmin,
                                        rms, gap, net, longitude, latitude, depth)
                VALUES (:id, :mag, :magType, :place, :time, :updated, :tsunami, :sig,
                        :alert, :status, :type, :title, :felt, :cdi, :mmi, :nst, :dmin,
                        :rms, :gap, :net, :longitude, :latitude, :depth)
                ON CONFLICT (id) DO UPDATE SET
                    mag = EXCLUDED.mag, mag_type = EXCLUDED.mag_type, place = EXCLUDED.place,
                    updated = EXCLUDED.updated, tsunami = EXCLUDED.tsunami, sig = EXCLUDED.sig,
                    alert = EXCLUDED.alert, status = EXCLUDED.status, type = EXCLUDED.type,
                    title = EXCLUDED.title, felt = EXCLUDED.felt, cdi = EXCLUDED.cdi,
                    mmi = EXCLUDED.mmi, nst = EXCLUDED.nst, dmin = EXCLUDED.dmin,
                    rms = EXCLUDED.rms, gap = EXCLUDED.gap, net = EXCLUDED.net,
                    longitude = EXCLUDED.longitude, latitude = EXCLUDED.latitude, depth = EXCLUDED.depth
                """, params);
    }
}

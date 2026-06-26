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
                        .addValue("place", f.properties().place())
                        .addValue("time", f.properties().time())
                        .addValue("updated", f.properties().updated())
                        .addValue("tsunami", f.properties().tsunami())
                        .addValue("sig", f.properties().sig())
                        .addValue("magType", f.properties().magType())
                        .addValue("longitude", f.geometry().coordinates().longitude())
                        .addValue("latitude", f.geometry().coordinates().latitude())
                        .addValue("depth", f.geometry().coordinates().depth()))
                .toArray(SqlParameterSource[]::new);

        jdbc.batchUpdate("""                                                                                                                                                                                                                                                                                      
                INSERT INTO earthquakes (id, mag, place, time, updated, tsunami, sig, mag_type,                                                                                                                                                                                                                   
                                        longitude, latitude, depth)                                                                                                                                                                                                                                               
                VALUES (:id, :mag, :place, :time, :updated, :tsunami, :sig, :magType,                                                                                                                                                                                                                             
                        :longitude, :latitude, :depth)                                                                                                                                                                                                                                                            
                ON CONFLICT (id) DO NOTHING                                                                                                                                                                                                                                                                       
                """, params);
    }
}

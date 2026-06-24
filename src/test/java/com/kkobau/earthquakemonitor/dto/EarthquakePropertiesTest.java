package com.kkobau.earthquakemonitor.dto;

import static org.assertj.core.api.Assertions.assertThat;

import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

class EarthquakePropertiesTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void deserializesFromUsgsJson() throws Exception {
        String json = """
                {
                  "mag": 6.6,
                  "place": "133 km ESE of Petropavlovsk-Kamchatsky, Russia",
                  "time": 1781851951597,
                  "updated": 1781940624773,
                  "tz": null,
                  "url": "https://earthquake.usgs.gov/earthquakes/eventpage/us7000sui3",
                  "detail": "https://earthquake.usgs.gov/earthquakes/feed/v1.0/detail/us7000sui3.geojson",
                  "felt": 1,
                  "cdi": 2,
                  "mmi": 5.354,
                  "alert": "green",
                  "status": "reviewed",
                  "tsunami": 0,
                  "sig": 670,
                  "net": "us",
                  "code": "7000sui3",
                  "ids": ",us7000sui3,",
                  "sources": ",us,",
                  "types": ",dyfi,ground-failure,losspager,moment-tensor,origin,phase-data,shakemap,",
                  "nst": 188,
                  "dmin": 1.762,
                  "rms": 0.58,
                  "gap": 31,
                  "magType": "mww",
                  "type": "earthquake",
                  "title": "M 6.6 - 133 km ESE of Petropavlovsk-Kamchatsky, Russia"
                }
                """;

        EarthquakeProperties properties = objectMapper.readValue(json, EarthquakeProperties.class);

        assertThat(properties.mag()).isEqualTo(6.6);
        assertThat(properties.place()).isEqualTo("133 km ESE of Petropavlovsk-Kamchatsky, Russia");
        assertThat(properties.time()).isEqualTo(1781851951597L);
        assertThat(properties.updated()).isEqualTo(1781940624773L);
        assertThat(properties.tz()).isNull();
        assertThat(properties.url()).isEqualTo("https://earthquake.usgs.gov/earthquakes/eventpage/us7000sui3");
        assertThat(properties.detail()).isEqualTo("https://earthquake.usgs.gov/earthquakes/feed/v1.0/detail/us7000sui3.geojson");
        assertThat(properties.felt()).isEqualTo(1);
        assertThat(properties.cdi()).isEqualTo(2);
        assertThat(properties.mmi()).isEqualTo(5.354);
        assertThat(properties.alert()).isEqualTo("green");
        assertThat(properties.status()).isEqualTo("reviewed");
        assertThat(properties.tsunami()).isEqualTo(0);
        assertThat(properties.sig()).isEqualTo(670);
        assertThat(properties.net()).isEqualTo("us");
        assertThat(properties.code()).isEqualTo("7000sui3");
        assertThat(properties.ids()).isEqualTo(",us7000sui3,");
        assertThat(properties.sources()).isEqualTo(",us,");
        assertThat(properties.types()).isEqualTo(",dyfi,ground-failure,losspager,moment-tensor,origin,phase-data,shakemap,");
        assertThat(properties.nst()).isEqualTo(188);
        assertThat(properties.dmin()).isEqualTo(1.762);
        assertThat(properties.rms()).isEqualTo(0.58);
        assertThat(properties.gap()).isEqualTo(31);
        assertThat(properties.magType()).isEqualTo("mww");
        assertThat(properties.type()).isEqualTo("earthquake");
        assertThat(properties.title()).isEqualTo("M 6.6 - 133 km ESE of Petropavlovsk-Kamchatsky, Russia");
    }

    @Test
    void allowsNullFeltAndCdi() throws Exception {
        String json = """
                {
                  "mag": 6.6,
                  "place": "central Mid-Atlantic Ridge",
                  "time": 1781722618159,
                  "updated": 1781888494516,
                  "felt": null,
                  "cdi": null,
                  "mmi": 0,
                  "tsunami": 0,
                  "sig": 670,
                  "magType": "mww",
                  "type": "earthquake",
                  "title": "M 6.6 - central Mid-Atlantic Ridge"
                }
                """;

        EarthquakeProperties properties = objectMapper.readValue(json, EarthquakeProperties.class);

        assertThat(properties.felt()).isNull();
        assertThat(properties.cdi()).isNull();
        assertThat(properties.mmi()).isEqualTo(0);
    }
}

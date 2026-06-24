package com.kkobau.earthquakemonitor.dto;

import static org.assertj.core.api.Assertions.assertThat;

import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

class EarthquakeFeatureTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void deserializesFromUsgsJson() throws Exception {
        String json = """
                {
                  "type": "Feature",
                  "properties": {
                    "mag": 6.6,
                    "place": "133 km ESE of Petropavlovsk-Kamchatsky, Russia",
                    "time": 1781851951597,
                    "magType": "mww",
                    "type": "earthquake",
                    "title": "M 6.6 - 133 km ESE of Petropavlovsk-Kamchatsky, Russia"
                  },
                  "geometry": {
                    "type": "Point",
                    "coordinates": [160.5652, 52.7943, 10.0]
                  },
                  "id": "us7000sui3"
                }
                """;

        EarthquakeFeature feature = objectMapper.readValue(json, EarthquakeFeature.class);

        assertThat(feature.type()).isEqualTo("Feature");
        assertThat(feature.id()).isEqualTo("us7000sui3");
        assertThat(feature.properties().mag()).isEqualTo(6.6);
        assertThat(feature.properties().title()).isEqualTo("M 6.6 - 133 km ESE of Petropavlovsk-Kamchatsky, Russia");
        assertThat(feature.geometry().type()).isEqualTo("Point");
        assertThat(feature.geometry().coordinates().longitude()).isEqualTo(160.5652);
        assertThat(feature.geometry().coordinates().latitude()).isEqualTo(52.7943);
        assertThat(feature.geometry().coordinates().depth()).isEqualTo(10.0);
    }
}

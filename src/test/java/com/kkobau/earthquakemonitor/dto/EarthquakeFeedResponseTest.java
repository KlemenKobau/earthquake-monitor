package com.kkobau.earthquakemonitor.dto;

import static org.assertj.core.api.Assertions.assertThat;

import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

class EarthquakeFeedResponseTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void deserializesFullFeedResponse() throws Exception {
        String json = """
                {
                  "type": "FeatureCollection",
                  "metadata": {
                    "generated": 1782289885000,
                    "url": "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/significant_month.geojson",
                    "title": "USGS Significant Earthquakes, Past Month",
                    "status": 200,
                    "api": "2.4.0",
                    "count": 1
                  },
                  "features": [
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
                  ],
                  "bbox": [160.5652, 52.7943, 10.0, 160.5652, 52.7943, 10.0]
                }
                """;

        EarthquakeFeedResponse response = objectMapper.readValue(json, EarthquakeFeedResponse.class);

        assertThat(response.type()).isEqualTo("FeatureCollection");
        assertThat(response.metadata().count()).isEqualTo(1);
        assertThat(response.features()).hasSize(1);
        assertThat(response.features().get(0).id()).isEqualTo("us7000sui3");
        assertThat(response.bbox()).containsExactly(160.5652, 52.7943, 10.0, 160.5652, 52.7943, 10.0);
    }

    @Test
    void allowsMissingBbox() throws Exception {
        String json = """
                {
                  "type": "FeatureCollection",
                  "metadata": {
                    "generated": 1782289885000,
                    "url": "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/significant_month.geojson",
                    "title": "USGS Significant Earthquakes, Past Month",
                    "status": 200,
                    "api": "2.4.0",
                    "count": 0
                  },
                  "features": []
                }
                """;

        EarthquakeFeedResponse response = objectMapper.readValue(json, EarthquakeFeedResponse.class);

        assertThat(response.bbox()).isNull();
        assertThat(response.features()).isEmpty();
    }
}

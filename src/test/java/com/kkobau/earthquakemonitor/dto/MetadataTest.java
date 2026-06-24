package com.kkobau.earthquakemonitor.dto;

import static org.assertj.core.api.Assertions.assertThat;

import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

class MetadataTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void deserializesFromUsgsJson() throws Exception {
        String json = """
                {
                  "generated": 1782289885000,
                  "url": "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/significant_month.geojson",
                  "title": "USGS Significant Earthquakes, Past Month",
                  "status": 200,
                  "api": "2.4.0",
                  "count": 16
                }
                """;

        Metadata metadata = objectMapper.readValue(json, Metadata.class);

        assertThat(metadata.generated()).isEqualTo(1782289885000L);
        assertThat(metadata.url()).isEqualTo("https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/significant_month.geojson");
        assertThat(metadata.title()).isEqualTo("USGS Significant Earthquakes, Past Month");
        assertThat(metadata.status()).isEqualTo(200);
        assertThat(metadata.api()).isEqualTo("2.4.0");
        assertThat(metadata.count()).isEqualTo(16);
    }
}

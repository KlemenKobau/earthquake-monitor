package com.kkobau.earthquakemonitor.dto;

import static org.assertj.core.api.Assertions.assertThat;

import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

class PointGeometryTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void deserializesFromUsgsJson() throws Exception {
        String json = """
                {
                  "type": "Point",
                  "coordinates": [160.5652, 52.7943, 10.0]
                }
                """;

        PointGeometry geometry = objectMapper.readValue(json, PointGeometry.class);

        assertThat(geometry.type()).isEqualTo("Point");
        assertThat(geometry.coordinates().longitude()).isEqualTo(160.5652);
        assertThat(geometry.coordinates().latitude()).isEqualTo(52.7943);
        assertThat(geometry.coordinates().depth()).isEqualTo(10.0);
    }
}

package com.kkobau.earthquakemonitor.dto;

import static org.assertj.core.api.Assertions.assertThat;

import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

class CoordinatesTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void deserializesLongitudeLatitudeDepthFromArray() throws Exception {
        Coordinates coordinates = objectMapper.readValue("[160.5652,52.7943,10.0]", Coordinates.class);

        assertThat(coordinates.longitude()).isEqualTo(160.5652);
        assertThat(coordinates.latitude()).isEqualTo(52.7943);
        assertThat(coordinates.depth()).isEqualTo(10.0);
    }

    @Test
    void defaultsDepthToZeroWhenArrayHasOnlyTwoElements() throws Exception {
        Coordinates coordinates = objectMapper.readValue("[160.5652,52.7943]", Coordinates.class);

        assertThat(coordinates.longitude()).isEqualTo(160.5652);
        assertThat(coordinates.latitude()).isEqualTo(52.7943);
        assertThat(coordinates.depth()).isEqualTo(0.0);
    }
}

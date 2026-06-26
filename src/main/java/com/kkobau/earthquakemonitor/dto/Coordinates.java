package com.kkobau.earthquakemonitor.dto;

import com.kkobau.earthquakemonitor.dto.deserializers.CoordinatesDeserializer;
import tools.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = CoordinatesDeserializer.class)
public record Coordinates(double longitude, double latitude, double depth) {
}

package com.kkobau.earthquakemonitor.dto;

public record EarthquakeFeature(
        String type,
        EarthquakeProperties properties,
        PointGeometry geometry,
        String id
) {
}

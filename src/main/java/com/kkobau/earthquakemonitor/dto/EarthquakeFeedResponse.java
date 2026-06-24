package com.kkobau.earthquakemonitor.dto;

import java.util.List;

public record EarthquakeFeedResponse(
        String type,
        Metadata metadata,
        List<EarthquakeFeature> features,
        double[] bbox
) {
}

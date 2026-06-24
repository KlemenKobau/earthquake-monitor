package com.kkobau.earthquakemonitor.dto;

public record EarthquakeProperties(
        Double mag,
        String place,
        Long time,
        Long updated,
        String tz,
        String url,
        String detail,
        Integer felt,
        Double cdi,
        Double mmi,
        String alert,
        String status,
        Integer tsunami,
        Integer sig,
        String net,
        String code,
        String ids,
        String sources,
        String types,
        Integer nst,
        Double dmin,
        Double rms,
        Double gap,
        String magType,
        String type,
        String title
) {
}

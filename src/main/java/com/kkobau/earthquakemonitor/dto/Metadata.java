package com.kkobau.earthquakemonitor.dto;

public record Metadata(
        Long generated,
        String url,
        String title,
        Integer status,
        String api,
        Integer count
) {
}

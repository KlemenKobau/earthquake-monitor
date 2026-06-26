package com.kkobau.earthquakemonitor.feed;

import com.kkobau.earthquakemonitor.config.ClientProperties;
import com.kkobau.earthquakemonitor.dto.EarthquakeFeedResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Service
public class FeedClient {

    private final RestClient client;

    public FeedClient(ClientProperties clientProperties) {
        client = RestClient.create(clientProperties.getUrl());
    }

    public EarthquakeFeedResponse fetchFeed() throws RestClientResponseException {
        return client.get()
                .retrieve()
                .toEntity(EarthquakeFeedResponse.class)
                .getBody();
    }
}

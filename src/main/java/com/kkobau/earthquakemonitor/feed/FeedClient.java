package com.kkobau.earthquakemonitor.feed;

import com.kkobau.earthquakemonitor.config.ClientProperties;
import com.kkobau.earthquakemonitor.dto.EarthquakeFeedResponse;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.RestClient;

import java.util.Optional;

@Service
public class FeedClient {

    private static final Logger LOG = LoggerFactory.getLogger(FeedClient.class);

    private final RestClient client;

    @Autowired
    public FeedClient(ClientProperties clientProperties) {
        client = RestClient.create(clientProperties.getUrl());
    }

    public Optional<EarthquakeFeedResponse> fetchFeed() {
        ResponseEntity<EarthquakeFeedResponse> response = client.get().
                retrieve()
                .toEntity(EarthquakeFeedResponse.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            return Optional.ofNullable(response.getBody());
        }

        LOG.atWarn().log("Could not fetch feed data! Status code: {}, body: {}, headers: {}",
                response.getStatusCode(), response.getBody(), response.getHeaders());
        return Optional.empty();
    }
}

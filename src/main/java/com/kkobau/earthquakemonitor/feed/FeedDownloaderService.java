package com.kkobau.earthquakemonitor.feed;

import com.kkobau.earthquakemonitor.dto.EarthquakeFeedResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class FeedDownloaderService {

    private static final Logger LOG = LoggerFactory.getLogger(FeedDownloaderService.class);

    private final FeedClient feedClient;

    @Autowired
    public FeedDownloaderService(FeedClient feedClient) {
        this.feedClient = feedClient;
    }

    @Scheduled(cron = "0 0 * * * *")
    void consumeFeed() {
        Optional<EarthquakeFeedResponse> earthquakeFeedResponse = feedClient.fetchFeed();
        earthquakeFeedResponse.ifPresent(r -> );
    }
}

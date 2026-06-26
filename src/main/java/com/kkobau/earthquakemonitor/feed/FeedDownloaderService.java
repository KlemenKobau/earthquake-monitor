package com.kkobau.earthquakemonitor.feed;

import com.kkobau.earthquakemonitor.db.EarthquakeDao;
import com.kkobau.earthquakemonitor.dto.EarthquakeFeedResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;

@Service
public class FeedDownloaderService {

    private static final Logger LOG = LoggerFactory.getLogger(FeedDownloaderService.class);

    private final FeedClient feedClient;
    private final EarthquakeDao earthquakeDao;

    public FeedDownloaderService(FeedClient feedClient, EarthquakeDao earthquakeDao) {
        this.feedClient = feedClient;
        this.earthquakeDao = earthquakeDao;
    }

    @Scheduled(cron = "0 */30 * * * *")
    void consumeFeed() {
        try {
            EarthquakeFeedResponse earthquakeFeedResponse = feedClient.fetchFeed();
            earthquakeDao.saveAll(earthquakeFeedResponse.features());
        } catch (RestClientResponseException e) {
            LOG.warn("Could not fetch feed response!");
        }
    }
}

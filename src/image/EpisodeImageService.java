package image;

import image.event.EpisodeImageProcessEvent;
import scrape.ScrapedEpisodeEntity;

import java.util.List;

public interface EpisodeImageService {

    void createImages(List<ScrapedEpisodeEntity> scrapedEpgEpisodes);

    void processEvent(String id, EpisodeImageProcessEvent imageAddedEvent);
}

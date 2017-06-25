package image;

import image.event.ImageAddedEvent;
import scrape.ScrapedEpisodeEntity;

import java.util.List;

public interface EpisodeImageService {

    void createImages(List<ScrapedEpisodeEntity> scrapedEpgEpisodes);

    void imageEvent(String id, ImageAddedEvent imageAddedEvent);

    void imageExposed(String id);

    void imageExposedFailed(String id);
}

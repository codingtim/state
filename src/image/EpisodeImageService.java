package image;

import scrape.ScrapedEpisodeEntity;

import java.util.List;

public interface EpisodeImageService {

    void createImages(List<ScrapedEpisodeEntity> scrapedEpgEpisodes);

    void imageAdded(String id, String remoteImageId);

    void imageExposed(String id);
}

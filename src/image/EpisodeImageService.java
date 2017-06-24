package image;

import scrape.ScrapedEpisodeEntity;

import java.util.List;

public interface EpisodeImageService {

    void createImages(List<ScrapedEpisodeEntity> scrapedEpgEpisodes);

    void imageAdded(String id, String remoteImageId);

    void imageAddFailed(String id);

    void imageExposed(String id);

    void imageExposedFailed(String id);
}

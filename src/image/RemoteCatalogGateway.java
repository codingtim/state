package image;

import episode.EpisodeEntity;
import image.model.EpisodeImageEntity;

public interface RemoteCatalogGateway {

    void createEditorialObject(EpisodeImageEntity episodeImageEntity, EpisodeEntity episodeEntity);
}

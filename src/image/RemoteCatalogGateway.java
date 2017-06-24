package image;

import episode.EpisodeEntity;
import image.model.EpisodeImageEntity;

public interface RemoteCatalogGateway {

    boolean createEditorialObject(EpisodeImageEntity episodeImageEntity, EpisodeEntity episodeEntity);
}

package image;

import image.model.EpisodeImageEntity;

public interface RemoteImagesGateway {
    void addImage(EpisodeImageEntity episodeImageEntity);

    void expose(EpisodeImageEntity episodeImageEntity);
}

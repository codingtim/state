package image;

import image.gateway.RemoteImagesGateway;
import image.model.EpisodeImageEntity;

import java.util.ArrayList;
import java.util.List;

class StubImagesGateway implements RemoteImagesGateway {

    private List<String> requestedImages = new ArrayList<>();
    private List<String> exposedImages = new ArrayList<>();

    @Override
    public void addImage(EpisodeImageEntity episodeImageEntity) {
        requestedImages.add(episodeImageEntity.getId());
    }

    @Override
    public void expose(EpisodeImageEntity episodeImageEntity) {
        exposedImages.add(episodeImageEntity.getId());
    }

    public boolean hasBeenRequested(EpisodeImageEntity episodeImageEntity) {
        return requestedImages.contains(episodeImageEntity.getId());
    }

    public boolean hasBeenExposed(EpisodeImageEntity episodeImageEntity) {
        return exposedImages.contains(episodeImageEntity.getId());
    }
}

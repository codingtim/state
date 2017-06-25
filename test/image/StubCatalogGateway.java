package image;

import episode.EpisodeEntity;
import image.gateway.RemoteCatalogGateway;
import image.model.EpisodeImageEntity;

import java.util.ArrayList;
import java.util.List;

public class StubCatalogGateway implements RemoteCatalogGateway {
    private List<String> episodeImageIds = new ArrayList<>();
    private boolean successfulCreation = true;

    @Override
    public boolean createEditorialObject(EpisodeImageEntity episodeImageEntity, EpisodeEntity episodeEntity) {
        episodeImageIds.add(episodeImageEntity.getId());
        return successfulCreation;
    }

    boolean editorialObjectWasCreatedFor(EpisodeImageEntity episodeImageEntity) {
        return episodeImageIds.contains(episodeImageEntity.getId());
    }

    public void setCreationToUnsuccessful() {
        successfulCreation = false;
    }
}

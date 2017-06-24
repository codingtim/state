package image;

import episode.EpisodeEntity;
import image.model.EpisodeImageEntity;

import java.util.ArrayList;
import java.util.List;

public class StubCatalogGateway implements RemoteCatalogGateway {
    private List<String> episodeImageIds = new ArrayList<>();

    @Override
    public void createEditorialObject(EpisodeImageEntity episodeImageEntity, EpisodeEntity episodeEntity) {
        episodeImageIds.add(episodeImageEntity.getId());
    }

    boolean editorialObjectWasCreatedFor(EpisodeImageEntity episodeImageEntity) {
        return episodeImageIds.contains(episodeImageEntity.getId());
    }
}

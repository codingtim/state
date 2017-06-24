package image;

import image.model.EpisodeImageEntity;

import java.util.Optional;

public interface EpisodeImageRepository {
    Optional<EpisodeImageEntity> findByEpisodeIdAndImageUrl(String id, String imageUrl);

    void save(EpisodeImageEntity episodeImageEntity);

    Optional<EpisodeImageEntity> get(String id);
}

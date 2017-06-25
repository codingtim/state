package image;

import image.gateway.EpisodeImageRepository;
import image.model.EpisodeImageEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

class StubEpisodeImageRepository implements EpisodeImageRepository {

    private Map<EpisodeImageIdentifier, EpisodeImageEntity> map = new HashMap<>();

    @Override
    public Optional<EpisodeImageEntity> findByEpisodeIdAndImageUrl(String id, String imageUrl) {
        return Optional.ofNullable(map.get(new EpisodeImageIdentifier(id, imageUrl)));
    }

    @Override
    public void save(EpisodeImageEntity episodeImageEntity) {
        map.put(new EpisodeImageIdentifier(episodeImageEntity.getEpisodeId(), episodeImageEntity.getEpgImageUrl()),
                episodeImageEntity);
    }

    @Override
    public Optional<EpisodeImageEntity> get(String id) {
        return map.values().stream().filter(episodeImageEntity -> episodeImageEntity.getId().equals(id)).findFirst();
    }

    private static class EpisodeImageIdentifier {
        private String episodeId;
        private String imageUrl;

        public EpisodeImageIdentifier(String episodeId, String imageUrl) {
            this.episodeId = episodeId;
            this.imageUrl = imageUrl;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            EpisodeImageIdentifier that = (EpisodeImageIdentifier) o;
            return Objects.equals(episodeId, that.episodeId) &&
                    Objects.equals(imageUrl, that.imageUrl);
        }

        @Override
        public int hashCode() {
            return Objects.hash(episodeId);
        }
    }
}

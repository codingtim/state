package image;

import episode.EpisodeEntity;
import episode.EpisodeService;
import image.model.EpisodeImageEntity;
import image.model.State;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import scrape.ScrapedEpisodeEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class EpisodeImageServiceImplTest {

    private EpisodeImageServiceImpl service;
    private StubEpisodeImageRepository repository;
    private StubImagesGateway remoteImagesGateway;

    @BeforeEach
    void setUp() {
        repository = new StubEpisodeImageRepository();
        StubEpisodeService episodeService = new StubEpisodeService();
        episodeService.add(new EpisodeEntity("eps1", "w1"));
        remoteImagesGateway = new StubImagesGateway();
        service = new EpisodeImageServiceImpl(repository, episodeService, remoteImagesGateway);
    }

    @Test
    void createImages() {
        ScrapedEpisodeEntity scrapedEpisodeEntity = new ScrapedEpisodeEntity("w1", Collections.singletonList("http://some.image.com"));

        service.createImages(Collections.singletonList(scrapedEpisodeEntity));
        Optional<EpisodeImageEntity> result = repository.findByEpisodeIdAndImageUrl("eps1", "http://some.image.com");
        assertSame(true, result.isPresent());
        EpisodeImageEntity episodeImageEntity = result.get();
        assertSame(true, remoteImagesGateway.hasBeenRequested(episodeImageEntity));
        assertSame(State.PROCESSING, episodeImageEntity.getState());
        //TODO hamcrest~ matcher that flowState is contained?
    }

    private static class StubImagesGateway implements RemoteImagesGateway {

        private List<String> requestedImages = new ArrayList<>();

        @Override
        public void addImage(EpisodeImageEntity episodeImageEntity) {
            requestedImages.add(episodeImageEntity.getId());
        }

        public boolean hasBeenRequested(EpisodeImageEntity episodeImageEntity) {
            return requestedImages.contains(episodeImageEntity.getId());
        }
    }

    private static class StubEpisodeService implements EpisodeService {

        private ArrayList<EpisodeEntity> list = new ArrayList<>();

        @Override
        public Optional<EpisodeEntity> findByWhatsonId(String whatsonId) {
            return list.stream()
                    .filter(episodeEntity -> episodeEntity.getWhatsonId().equals(whatsonId))
                    .findFirst();
        }

        public void add(EpisodeEntity episodeEntity) {
            list.add(episodeEntity);
        }
    }

    private static class StubEpisodeImageRepository implements EpisodeImageRepository {

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
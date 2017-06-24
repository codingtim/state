package image;

import episode.EpisodeEntity;
import image.model.EpisodeImageEntity;
import image.model.State;
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

    @Test
    void imageAdded() {
        EpisodeImageEntity image = new EpisodeImageEntity("eps1", "some.url");
        image.startProcessing();
        repository.save(image);

        service.imageAdded(image.getId());
        assertSame(true, remoteImagesGateway.hasBeenExposed(image));
        assertSame(State.PROCESSING, image.getState());
        //TODO hamcrest~ matcher that flowState is contained?
    }

}
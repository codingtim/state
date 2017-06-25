package image;

import episode.EpisodeEntity;
import image.event.ImageAddedEvent;
import image.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import scrape.ScrapedEpisodeEntity;

import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class EpisodeImageServiceImplTest {

    private EpisodeImageServiceImpl service;
    private StubEpisodeImageRepository repository;
    private StubImagesGateway remoteImagesGateway;
    private StubCatalogGateway remoteCatalogGateway;
    private ProcessFlowStates processFlowStates;

    @BeforeEach
    void setUp() {
        repository = new StubEpisodeImageRepository();
        StubEpisodeService episodeService = new StubEpisodeService();
        episodeService.add(new EpisodeEntity("eps1", "w1"));
        remoteImagesGateway = new StubImagesGateway();
        remoteCatalogGateway = new StubCatalogGateway();
        processFlowStates = new ProcessFlowStates(remoteImagesGateway);
        service = new EpisodeImageServiceImpl(processFlowStates, repository, episodeService, remoteImagesGateway, remoteCatalogGateway);
    }

    @Test
    void createImages() {
        ScrapedEpisodeEntity scrapedEpisodeEntity = new ScrapedEpisodeEntity("w1", Collections.singletonList("http://some.image.com"));

        service.createImages(Collections.singletonList(scrapedEpisodeEntity));
        Optional<EpisodeImageEntity> result = repository.findByEpisodeIdAndImageUrl("eps1", "http://some.image.com");
        assertTrue(result.isPresent());
        EpisodeImageEntity episodeImageEntity = result.get();
        assertTrue(remoteImagesGateway.hasBeenRequested(episodeImageEntity));
        assertSame(State.PROCESSING, episodeImageEntity.getState());
        assertTrue(hasFlowStates(episodeImageEntity, FlowState.PROCESS_IMAGE_SCHEDULED));
    }

    @Test
    void imageAdded() {
        EpisodeImageEntity episodeImage = new EpisodeImageEntity("eps1", "some.url", processFlowStates);
        episodeImage.startProcessing();
        repository.save(episodeImage);

        service.imageEvent(episodeImage.getId(), ImageAddedEvent.success("remoteImage1"));
        assertTrue(remoteImagesGateway.hasBeenExposed(episodeImage));
        assertSame(State.PROCESSING, episodeImage.getState());
        assertTrue(hasFlowStates(episodeImage, FlowState.PROCESS_IMAGE_FINISHED, FlowState.EXPOSE_IMAGE_SCHEDULED));
        assertSame("remoteImage1", episodeImage.getImageServiceId());
    }

    @Test
    void imageAddFailed() {
        EpisodeImageEntity episodeImage = new EpisodeImageEntity("eps1", "some.url", processFlowStates);
        episodeImage.startProcessing();
        repository.save(episodeImage);

        service.imageEvent(episodeImage.getId(), ImageAddedEvent.failure());
        assertFalse(remoteImagesGateway.hasBeenExposed(episodeImage));
        assertSame(State.PROCESSING_FAILED, episodeImage.getState());
        assertTrue(hasFlowStates(episodeImage, FlowState.PROCESS_IMAGE_FAILED));
    }

    @Test
    void imageExposed() {
        EpisodeImageEntity episodeImage = new EpisodeImageEntity("eps1", "some.url", processFlowStates);
        episodeImage.startProcessing();
        episodeImage.episodeImageProcessEventHappened(ImageAddedEvent.success("remoteImage1"));
        repository.save(episodeImage);

        service.imageExposed(episodeImage.getId());
        assertSame(State.PROCESSED, episodeImage.getState());
        assertTrue(hasFlowStates(episodeImage, FlowState.EXPOSE_IMAGE_FINISHED, FlowState.PROCESS_EO_STARTED, FlowState.PROCESS_EO_FINISHED));
        assertTrue(remoteCatalogGateway.editorialObjectWasCreatedFor(episodeImage));
    }

    @Test
    void imageExposureFailed() {
        EpisodeImageEntity episodeImage = new EpisodeImageEntity("eps1", "some.url", processFlowStates);
        episodeImage.startProcessing();
        episodeImage.episodeImageProcessEventHappened(ImageAddedEvent.success("remoteImage1"));
        repository.save(episodeImage);

        service.imageExposedFailed(episodeImage.getId());
        assertSame(State.PROCESSING_FAILED, episodeImage.getState());
        assertTrue(hasFlowStates(episodeImage, FlowState.EXPOSE_IMAGE_FAILED));
        assertFalse(remoteCatalogGateway.editorialObjectWasCreatedFor(episodeImage));
    }

    @Test
    void imageExposed_editorialObjectCreationFails() {
        EpisodeImageEntity episodeImage = new EpisodeImageEntity("eps1", "some.url", processFlowStates);
        episodeImage.startProcessing();
        episodeImage.episodeImageProcessEventHappened(ImageAddedEvent.success("remoteImage1"));
        repository.save(episodeImage);
        remoteCatalogGateway.setCreationToUnsuccessful();

        service.imageExposed(episodeImage.getId());
        assertSame(State.PROCESSING_FAILED, episodeImage.getState());
        assertTrue(hasFlowStates(episodeImage, FlowState.EXPOSE_IMAGE_FINISHED, FlowState.PROCESS_EO_STARTED, FlowState.PROCESS_EO_FAILED));
        assertTrue(remoteCatalogGateway.editorialObjectWasCreatedFor(episodeImage));
    }

    private boolean hasFlowStates(EpisodeImageEntity episodeImage, FlowState... expected) {
        return Stream.of(expected)
                .allMatch(flowState -> hasFlowState(episodeImage, flowState));
    }

    private boolean hasFlowState(EpisodeImageEntity episodeImageEntity, FlowState expected) {
        return episodeImageEntity.getFlowStates().stream()
                .map(ProcessFlowStateEntity::getFlowState)
                .anyMatch(flowState -> flowState == expected);
    }

}
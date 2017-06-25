package image;

import episode.EpisodeEntity;
import episode.EpisodeService;
import image.model.EpisodeImageEntity;
import image.event.ImageAddedEvent;
import image.model.ProcessFlowStates;
import scrape.ScrapedEpisodeEntity;

import java.util.List;

public class EpisodeImageServiceImpl implements EpisodeImageService {

    private ProcessFlowStates processFlowStates;
    private EpisodeImageRepository repository;
    private EpisodeService episodeService;
    private RemoteCatalogGateway remoteCatalogGateway;

    public EpisodeImageServiceImpl(ProcessFlowStates processFlowStates, EpisodeImageRepository repository, EpisodeService episodeService, RemoteCatalogGateway remoteCatalogGateway) {
        this.processFlowStates = processFlowStates;
        this.repository = repository;
        this.episodeService = episodeService;
        this.remoteCatalogGateway = remoteCatalogGateway;
    }

    @Override
    public void createImages(List<ScrapedEpisodeEntity> scrapedEpgEpisodes) {
        scrapedEpgEpisodes.forEach(this::createImagesForEpisode);
    }

    private void createImagesForEpisode(ScrapedEpisodeEntity scrapedEpisode) {
        episodeService.findByWhatsonId(scrapedEpisode.getWhatsonId()).ifPresent(episodeEntity -> {
            scrapedEpisode.getImageUrls().forEach(imageUrl -> createImageForEpisode(episodeEntity, imageUrl));
        });
    }

    private void createImageForEpisode(EpisodeEntity episodeEntity, String imageUrl) {
        if(imageDoesNotExistsForEpisode(episodeEntity, imageUrl)) {
            EpisodeImageEntity episodeImageEntity = new EpisodeImageEntity(episodeEntity.getId(), imageUrl, processFlowStates);
            episodeImageEntity.startProcessing();
            repository.save(episodeImageEntity);
        }
    }

    private boolean imageDoesNotExistsForEpisode(EpisodeEntity episodeEntity, String imageUrl) {
        return !repository.findByEpisodeIdAndImageUrl(episodeEntity.getId(), imageUrl).isPresent();
    }

    @Override
    public void imageEvent(String id, ImageAddedEvent imageAddedEvent) {
        repository.get(id).ifPresent(episodeImageEntity -> {
            episodeImageEntity.episodeImageProcessEventHappened(imageAddedEvent);
            repository.save(episodeImageEntity);
        });
    }

    @Override
    public void imageExposed(String id) {
        repository.get(id).ifPresent(episodeImageEntity -> {
            episodeImageEntity.imageExposed();
            repository.save(episodeImageEntity);
            episodeService.findById(episodeImageEntity.getEpisodeId()).ifPresent(episodeEntity -> {
                episodeImageEntity.creatingEditorialObject();
                if (remoteCatalogGateway.createEditorialObject(episodeImageEntity, episodeEntity)) {
                    episodeImageEntity.editorialObjectCreated();
                } else {
                    episodeImageEntity.editorialObjectCreationFailed();
                }
                repository.save(episodeImageEntity);
            });
        });
    }

    @Override
    public void imageExposedFailed(String id) {
        repository.get(id).ifPresent(episodeImageEntity -> {
            episodeImageEntity.imageExposureFailed();
            repository.save(episodeImageEntity);
        });
    }
}

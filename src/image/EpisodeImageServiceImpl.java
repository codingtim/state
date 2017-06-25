package image;

import episode.EpisodeEntity;
import episode.EpisodeService;
import image.event.EpisodeImageProcessEvent;
import image.event.ImageExposedEvent;
import image.model.EpisodeImageEntity;
import image.event.ImageAddedEvent;
import image.model.ProcessFlowStates;
import scrape.ScrapedEpisodeEntity;

import java.util.List;

public class EpisodeImageServiceImpl implements EpisodeImageService {

    private ProcessFlowStates processFlowStates;
    private EpisodeImageRepository repository;
    private EpisodeService episodeService;

    public EpisodeImageServiceImpl(ProcessFlowStates processFlowStates, EpisodeImageRepository repository, EpisodeService episodeService) {
        this.processFlowStates = processFlowStates;
        this.repository = repository;
        this.episodeService = episodeService;
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
    public void processEvent(String id, EpisodeImageProcessEvent imageAddedEvent) {
        repository.get(id).ifPresent(episodeImageEntity -> {
            try {
                episodeImageEntity.episodeImageProcessEventHappened(imageAddedEvent);
            } finally {
                repository.save(episodeImageEntity);
            }
        });
    }
}

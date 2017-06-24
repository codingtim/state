package image;

import episode.EpisodeEntity;
import episode.EpisodeService;
import image.model.EpisodeImageEntity;
import scrape.ScrapedEpisodeEntity;

import java.util.List;

public class EpisodeImageServiceImpl implements EpisodeImageService {

    private EpisodeImageRepository repository;
    private EpisodeService episodeService;
    private RemoteImagesGateway remoteImagesGateway;

    public EpisodeImageServiceImpl(EpisodeImageRepository repository, EpisodeService episodeService, RemoteImagesGateway remoteImagesGateway) {
        this.repository = repository;
        this.episodeService = episodeService;
        this.remoteImagesGateway = remoteImagesGateway;
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
            EpisodeImageEntity episodeImageEntity = new EpisodeImageEntity(episodeEntity.getId(), imageUrl);
            repository.save(episodeImageEntity);
            remoteImagesGateway.addImage(episodeImageEntity);
            episodeImageEntity.startProcessing();
            repository.save(episodeImageEntity);
        }
    }

    private boolean imageDoesNotExistsForEpisode(EpisodeEntity episodeEntity, String imageUrl) {
        return !repository.findByEpisodeIdAndImageUrl(episodeEntity.getId(), imageUrl).isPresent();
    }
}

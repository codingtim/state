package image;

import episode.EpisodeEntity;
import episode.EpisodeService;

import java.util.ArrayList;
import java.util.Optional;

class StubEpisodeService implements EpisodeService {

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

package episode;

import java.util.Optional;

public interface EpisodeService {

    Optional<EpisodeEntity> findByWhatsonId(String whatsonId);
    Optional<EpisodeEntity> findById(String id);
}

package episode;

import java.util.Optional;

public interface EpisodeService {

    Optional<EpisodeEntity> findByWhatsonId(String whatsonId);
}

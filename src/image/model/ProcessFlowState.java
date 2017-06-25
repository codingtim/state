package image.model;

import image.event.EpisodeImageProcessEvent;

public interface ProcessFlowState {
    ProcessFlowState startProcessing();

    void stateEntered(EpisodeImageEntity episodeImageEntity);

    ProcessFlowState eventHappened(EpisodeImageEntity episodeImageEntity, EpisodeImageProcessEvent event);
}

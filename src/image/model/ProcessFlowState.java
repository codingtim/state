package image.model;

public interface ProcessFlowState {
    ProcessFlowState startProcessing();

    void stateEntered(EpisodeImageEntity episodeImageEntity);
}

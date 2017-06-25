package image.model;

import image.RemoteImagesGateway;

public class ProcessFlowStates {

    private RemoteImagesGateway remoteImagesGateway;

    public ProcessFlowStates(RemoteImagesGateway remoteImagesGateway) {
        this.remoteImagesGateway = remoteImagesGateway;
    }

    public ProcessFlowState newState() {
        return new ProcessFlowState() {

            @Override
            public void startProcessing(EpisodeImageEntity episodeImageEntity) {
                remoteImagesGateway.addImage(episodeImageEntity);
                episodeImageEntity.processing(FlowState.PROCESS_IMAGE_SCHEDULED);
            }
        };
    }
}

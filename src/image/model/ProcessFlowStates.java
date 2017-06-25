package image.model;

import image.RemoteImagesGateway;

public class ProcessFlowStates {

    private RemoteImagesGateway remoteImagesGateway;

    public ProcessFlowStates(RemoteImagesGateway remoteImagesGateway) {
        this.remoteImagesGateway = remoteImagesGateway;
    }

    ProcessFlowState newState() {
        return new ProcessFlowState() {

            @Override
            public ProcessFlowState startProcessing() {
                return addImagesState();
            }

            @Override
            public void stateEntered(EpisodeImageEntity episodeImageEntity) {
                //nothing to do
            }
        };
    }

    private ProcessFlowState addImagesState() {
        return new ProcessFlowState() {

            @Override
            public ProcessFlowState startProcessing() {
                return null; //we're already processing
            }

            @Override
            public void stateEntered(EpisodeImageEntity episodeImageEntity) {
                remoteImagesGateway.addImage(episodeImageEntity);
                episodeImageEntity.processing(FlowState.PROCESS_IMAGE_SCHEDULED);
            }
        };
    }
}

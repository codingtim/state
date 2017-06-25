package image.model;

import image.RemoteImagesGateway;
import image.event.EpisodeImageProcessEvent;
import image.event.EpisodeImageProcessEventVisitor;
import image.event.ImageAddedEvent;

public class ProcessFlowStates {

    private RemoteImagesGateway remoteImagesGateway;

    public ProcessFlowStates(RemoteImagesGateway remoteImagesGateway) {
        this.remoteImagesGateway = remoteImagesGateway;
    }

    ProcessFlowState newState() {
        return new NewState();
    }

    private class NewState implements ProcessFlowState {

        @Override
        public ProcessFlowState startProcessing() {
            return new AddImageState();
        }

        @Override
        public void stateEntered(EpisodeImageEntity episodeImageEntity) {
            //nothing to do
        }

        @Override
        public ProcessFlowState eventHappened(EpisodeImageEntity episodeImageEntity, EpisodeImageProcessEvent event) {
            return null; //we shouldn't receive events when still in new state
        }
    }

    private class AddImageState implements ProcessFlowState, EpisodeImageProcessEventVisitor<EpisodeImageEntity, ProcessFlowState> {

        @Override
        public ProcessFlowState startProcessing() {
            return null; //we're already processing
        }

        @Override
        public void stateEntered(EpisodeImageEntity episodeImageEntity) {
            remoteImagesGateway.addImage(episodeImageEntity);
            episodeImageEntity.processing(FlowState.PROCESS_IMAGE_SCHEDULED);
        }

        @Override
        public ProcessFlowState eventHappened(EpisodeImageEntity episodeImageEntity, EpisodeImageProcessEvent event) {
            return event.accept(this, episodeImageEntity);
        }

        @Override
        public ProcessFlowState imageAddedEvent(ImageAddedEvent imageAddedEvent, EpisodeImageEntity episodeImageEntity) {
            episodeImageEntity.imageAdded(imageAddedEvent.getRemoteImageId());
            return new ExposeImageState();
        }
    }

    private class ExposeImageState implements ProcessFlowState, EpisodeImageProcessEventVisitor<EpisodeImageEntity, ProcessFlowState> {

        @Override
        public ProcessFlowState startProcessing() {
            return null; //we're already processing
        }

        @Override
        public void stateEntered(EpisodeImageEntity episodeImageEntity) {
            remoteImagesGateway.expose(episodeImageEntity);
            episodeImageEntity.processing(FlowState.EXPOSE_IMAGE_SCHEDULED);
        }

        @Override
        public ProcessFlowState eventHappened(EpisodeImageEntity episodeImageEntity, EpisodeImageProcessEvent event) {
            return event.accept(this, episodeImageEntity);
        }

        @Override
        public ProcessFlowState imageAddedEvent(ImageAddedEvent imageAddedEvent, EpisodeImageEntity episodeImageEntity) {
            return null; //ExposeImageState is not interested in image events
        }
    }
}

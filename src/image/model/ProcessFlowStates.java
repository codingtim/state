package image.model;

import episode.EpisodeService;
import image.RemoteCatalogGateway;
import image.RemoteImagesGateway;
import image.event.*;

public class ProcessFlowStates {

    private RemoteImagesGateway remoteImagesGateway;
    private EpisodeService episodeService;
    private RemoteCatalogGateway remoteCatalogGateway;

    public ProcessFlowStates(RemoteImagesGateway remoteImagesGateway, EpisodeService episodeService, RemoteCatalogGateway remoteCatalogGateway) {
        this.remoteImagesGateway = remoteImagesGateway;
        this.episodeService = episodeService;
        this.remoteCatalogGateway = remoteCatalogGateway;
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
            if(imageAddedEvent.wasSuccessful()) {
                episodeImageEntity.imageAdded(imageAddedEvent.getRemoteImageId());
                return new ExposeImageState();
            } else {
                return new FailedState(FlowState.PROCESS_IMAGE_FAILED);
            }
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
        public ProcessFlowState imageExposedEvent(ImageExposedEvent imageExposedEvent, EpisodeImageEntity episodeImageEntity) {
            if(imageExposedEvent.wasSuccessful()) {
                episodeImageEntity.imageExposed();
                return new CreateEditorialObjectState();
            } else {
                episodeImageEntity.processFailure(FlowState.EXPOSE_IMAGE_FAILED);
                return new FailedState(FlowState.EXPOSE_IMAGE_FAILED);
            }
        }
    }

    private class CreateEditorialObjectState implements ProcessFlowState, EpisodeImageProcessEventVisitor<EpisodeImageEntity, ProcessFlowState> {

        @Override
        public ProcessFlowState startProcessing() {
            return null; //already processing
        }
        @Override
        public void stateEntered(EpisodeImageEntity episodeImageEntity) {
            episodeImageEntity.processing(FlowState.PROCESS_EO_STARTED);
            episodeService.findById(episodeImageEntity.getEpisodeId()).ifPresent(episodeEntity -> {
                boolean success = remoteCatalogGateway.createEditorialObject(episodeImageEntity, episodeEntity);
                episodeImageEntity.episodeImageProcessEventHappened(success ?
                        EditorialObjectCreatedEvent.success() : EditorialObjectCreatedEvent.failure()
                );
            });
        }

        @Override
        public ProcessFlowState eventHappened(EpisodeImageEntity episodeImageEntity, EpisodeImageProcessEvent event) {
            return event.accept(this, episodeImageEntity);
        }

        @Override
        public ProcessFlowState editorialObjectCreatedEvent(EditorialObjectCreatedEvent editorialObjectCreatedEvent, EpisodeImageEntity episodeImageEntity) {
            if(editorialObjectCreatedEvent.wasSuccessful()) {
                episodeImageEntity.editorialObjectCreated();
                return new CompletedState();
            } else {
                return new FailedState(FlowState.PROCESS_EO_FAILED);
            }
        }

    }

    private class FailedState implements ProcessFlowState {
        private FlowState failedFlowState;

        public FailedState(FlowState failedFlowState) {
            this.failedFlowState = failedFlowState;
        }

        @Override
        public ProcessFlowState startProcessing() {
            return null;
        }

        @Override
        public void stateEntered(EpisodeImageEntity episodeImageEntity) {
            episodeImageEntity.processFailure(failedFlowState);
        }

        @Override
        public ProcessFlowState eventHappened(EpisodeImageEntity episodeImageEntity, EpisodeImageProcessEvent event) {
            return null;
        }
    }

    private class CompletedState implements ProcessFlowState {
        @Override
        public ProcessFlowState startProcessing() {
            return null;
        }

        @Override
        public void stateEntered(EpisodeImageEntity episodeImageEntity) {
            episodeImageEntity.processCompleted();
        }

        @Override
        public ProcessFlowState eventHappened(EpisodeImageEntity episodeImageEntity, EpisodeImageProcessEvent event) {
            return null;
        }
    }
}

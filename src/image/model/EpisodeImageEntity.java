package image.model;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class EpisodeImageEntity {

    private String id = UUID.randomUUID().toString();
    private String episodeId;
    private String imageServiceId;
    private String epgImageUrl;

    //needed?
    private String editorialObjectId;

    private State state = State.NEW;
    private List<ProcessFlowStateEntity> flowStates = new LinkedList<>();

    //transient
    private ProcessFlowState currentState;

    public EpisodeImageEntity(String episodeId, String epgImageUrl, ProcessFlowStates processFlowStates) {
        this.episodeId = episodeId;
        this.epgImageUrl = epgImageUrl;
        currentState = processFlowStates.newState();
    }

    public String getId() {
        return id;
    }

    public String getImageServiceId() {
        return imageServiceId;
    }

    public void setImageServiceId(String imageServiceId) {
        this.imageServiceId = imageServiceId;
    }

    public String getEpgImageUrl() {
        return epgImageUrl;
    }

    public void setEpgImageUrl(String epgImageUrl) {
        this.epgImageUrl = epgImageUrl;
    }

    public String getIdPrefix() {
        return "episode-image";
    }

    public String getEpisodeId() {
        return episodeId;
    }

    public void setEpisodeId(String episodeId) {
        this.episodeId = episodeId;
    }

    public State getState() {
        return state;
    }

    void setState(State state) {
        this.state = state;
    }

    public List<ProcessFlowStateEntity> getFlowStates() {
        flowStates.sort(Comparator.comparing(ProcessFlowStateEntity::getDate));
        return flowStates;
    }

    public void setFlowStates(List<ProcessFlowStateEntity> flowStates) {
        this.flowStates = flowStates;
    }

    void addFlowState(FlowState flowState){
        ProcessFlowStateEntity flowStateEntity = new ProcessFlowStateEntity();
        flowStateEntity.setFlowState(flowState);
        flowStates.add(flowStateEntity);
    }

    public String getEditorialObjectId() {
        return editorialObjectId;
    }

    public void setEditorialObjectId(String editorialObjectId) {
        this.editorialObjectId = editorialObjectId;
    }

    public void startProcessing() {
        currentState.startProcessing(this);
        state = State.PROCESSING;
        addFlowState(FlowState.PROCESS_IMAGE_SCHEDULED);
    }

    public void imageAdded(String remoteImageId) {
        imageServiceId = remoteImageId;
        addFlowState(FlowState.PROCESS_IMAGE_FINISHED);
    }

    public void startExposing() {
        addFlowState(FlowState.EXPOSE_IMAGE_SCHEDULED);
    }

    public void imageExposed() {
        addFlowState(FlowState.EXPOSE_IMAGE_FINISHED);
    }

    public void creatingEditorialObject() {
        addFlowState(FlowState.PROCESS_EO_STARTED);
    }

    public void editorialObjectCreated() {
        addFlowState(FlowState.PROCESS_EO_FINISHED);
        state = State.PROCESSED;
    }

    public void editorialObjectCreationFailed() {
        addFlowState(FlowState.PROCESS_EO_FAILED);
        state = State.PROCESSING_FAILED;
    }

    public void imageAddFailed() {
        addFlowState(FlowState.PROCESS_IMAGE_FAILED);
        state = State.PROCESSING_FAILED;
    }

    public void imageExposureFailed() {
        addFlowState(FlowState.EXPOSE_IMAGE_FAILED);
        state = State.PROCESSING_FAILED;
    }
}

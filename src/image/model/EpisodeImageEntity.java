package image.model;

import image.event.EpisodeImageProcessEvent;

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

    private void addFlowState(FlowState flowState){
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
        ProcessFlowState newState = currentState.startProcessing();
        //if we're already processing null will be returned
        if(newState != null) {
            currentState = newState;
            currentState.stateEntered(this);
        }
    }

    void processing(FlowState flowState) {
        state = State.PROCESSING;
        addFlowState(flowState);
    }

    void imageAdded(String remoteImageId) {
        imageServiceId = remoteImageId;
        addFlowState(FlowState.PROCESS_IMAGE_FINISHED);
    }

    void processFailure(FlowState flowState) {
        addFlowState(flowState);
        state = State.PROCESSING_FAILED;
    }

    void imageExposed() {
        addFlowState(FlowState.EXPOSE_IMAGE_FINISHED);
    }

    void editorialObjectCreated() {
        addFlowState(FlowState.PROCESS_EO_FINISHED);
        state = State.PROCESSED;
    }

    public void episodeImageProcessEventHappened(EpisodeImageProcessEvent event) {
        ProcessFlowState newState = currentState.eventHappened(this, event);
        if (newState != null) {
            currentState = newState;
            newState.stateEntered(this);
        }
    }

    void processCompleted() {
        state = State.PROCESSED;
    }
}

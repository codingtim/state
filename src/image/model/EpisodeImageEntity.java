package image.model;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class EpisodeImageEntity {

    private String episodeId;
    private String imageServiceId;
    private String epgImageUrl;

    //needed?
    private String editorialObjectId;

    private State state = State.NEW;
    private List<ProcessFlowStateEntity> flowStates = new LinkedList<>();

    public EpisodeImageEntity(String episodeId, String epgImageUrl) {
        this.episodeId = episodeId;
        this.epgImageUrl = epgImageUrl;
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
}

package image.model;

import java.util.Date;

public class ProcessFlowStateEntity {

    private FlowState flowState;
    private Date date = new Date();

    public FlowState getFlowState() {
        return flowState;
    }

    public void setFlowState(FlowState flowState) {
        this.flowState = flowState;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}

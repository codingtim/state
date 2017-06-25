package image.event;

public abstract class EpisodeImageProcessEvent {

    private final boolean success;

    protected EpisodeImageProcessEvent(boolean success) {
        this.success = success;
    }

    public abstract <IN, OUT> OUT accept(EpisodeImageProcessEventVisitor<IN, OUT> visitor, IN in);
}

package image.event;

public class ImageAddedEvent extends EpisodeImageProcessEvent {

    private final String remoteImageId;

    private ImageAddedEvent(boolean success, String remoteImageId) {
        super(success);
        this.remoteImageId = remoteImageId;
    }

    public static ImageAddedEvent success(String remoteImageId) {
        return new ImageAddedEvent(true, remoteImageId);
    }

    public String getRemoteImageId() {
        return remoteImageId;
    }

    @Override
    public <IN, OUT> OUT accept(EpisodeImageProcessEventVisitor<IN, OUT> visitor, IN in) {
        return visitor.imageAddedEvent(this, in);
    }
}

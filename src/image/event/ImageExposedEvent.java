package image.event;

public class ImageExposedEvent extends EpisodeImageProcessEvent {


    private ImageExposedEvent(boolean success) {
        super(success);
    }

    public static ImageExposedEvent success() {
        return new ImageExposedEvent(true);
    }

    public static ImageExposedEvent failure() {
        return new ImageExposedEvent(false);
    }

    @Override
    public <IN, OUT> OUT accept(EpisodeImageProcessEventVisitor<IN, OUT> visitor, IN in) {
        return visitor.imageExposedEvent(this, in);
    }
}

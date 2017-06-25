package image.event;

public class EditorialObjectCreatedEvent extends EpisodeImageProcessEvent {


    private EditorialObjectCreatedEvent(boolean success) {
        super(success);
    }

    public static EditorialObjectCreatedEvent success() {
        return new EditorialObjectCreatedEvent(true);
    }

    public static EditorialObjectCreatedEvent failure() {
        return new EditorialObjectCreatedEvent(false);
    }

    @Override
    public <IN, OUT> OUT accept(EpisodeImageProcessEventVisitor<IN, OUT> visitor, IN in) {
        return visitor.editorialObjectCreatedEvent(this, in);
    }
}

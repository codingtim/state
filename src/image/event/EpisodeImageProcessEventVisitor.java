package image.event;

//visitor so we don't need to cast
public interface EpisodeImageProcessEventVisitor<IN, OUT> {

    default OUT imageAddedEvent(ImageAddedEvent imageAddedEvent, IN in) {
        return null; //default not interested in event and just return null;
    }

    default OUT imageExposedEvent(ImageExposedEvent imageExposedEvent, IN in) {
        return null; //default not interested in event and just return null;
    }

    default OUT editorialObjectCreatedEvent(EditorialObjectCreatedEvent editorialObjectCreatedEvent, IN in) {
        return null; //default not interested in event and just return null;
    }
}

package image.event;

import image.model.EpisodeImageEntity;

//visitor so we don't need to cast
public interface EpisodeImageProcessEventVisitor<IN, OUT> {

    OUT imageAddedEvent(ImageAddedEvent imageAddedEvent, IN in);
}

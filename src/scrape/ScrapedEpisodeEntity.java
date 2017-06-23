package scrape;

import java.util.LinkedList;
import java.util.List;

public class ScrapedEpisodeEntity {

    private String whatsonId;
    private List<String> imageUrls = new LinkedList<>();

    public String getWhatsonId() {
        return whatsonId;
    }

    public void setWhatsonId(String whatsonId) {
        this.whatsonId = whatsonId;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }
}

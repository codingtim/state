package episode;

public class EpisodeEntity {

    private String id;
    private String whatsonId;
    private String catalogEpisodeId;

    public EpisodeEntity(String id, String whatsonId) {
        this.id = id;
        this.whatsonId = whatsonId;
    }

    public String getWhatsonId() {
        return whatsonId;
    }

    public void setWhatsonId(String whatsonId) {
        this.whatsonId = whatsonId;
    }

    public String getCatalogEpisodeId() {
        return catalogEpisodeId;
    }

    public void setCatalogEpisodeId(String catalogEpisodeId) {
        this.catalogEpisodeId = catalogEpisodeId;
    }

    public String getId() {
        return id;
    }
}

package estg.ipp.pt.aroundtmegaesousa.models;

/**
 * Created by José Bernardes on 02/01/2018.
 */

public class City {

    private String id;
    private String name;

    public City(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}

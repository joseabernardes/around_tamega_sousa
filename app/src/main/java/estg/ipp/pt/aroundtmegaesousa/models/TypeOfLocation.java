package estg.ipp.pt.aroundtmegaesousa.models;

/**
 * Created by José Bernardes on 12/01/2018.
 */

public class TypeOfLocation {


    private final int id;
    private final String type;

    public TypeOfLocation(int id, String type) {
        this.id = id;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return type;
    }
}

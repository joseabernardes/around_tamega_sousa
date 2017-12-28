package estg.ipp.pt.aroundtmegaesousa.models;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by José Bernardes on 21/12/2017.
 */

public class Quotes {

    private String quote;
    private String author;
    private List<Map<String,Object>> classification;

    public Quotes() {

    }

    public List<Map<String,Object>> getClassification() {
        return classification;
    }

    public String getQuote() {
        return quote;
    }

    public String getAuthor() {
        return author;
    }

    @Override
    public String toString() {
        return "Quotes{" +
                "quote='" + quote + '\'' +
                ", author='" + author + '\'' +
                ", classification=" + Arrays.toString(classification.toArray()) +
                '}';
    }
}

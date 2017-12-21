package estg.ipp.pt.aroundtmegaesousa.model;

/**
 * Created by José Bernardes on 21/12/2017.
 */

public class Quotes {

    private String quote;
    private String author;

    public Quotes() {

    }

    public Quotes(String quote, String author) {
        this.quote = quote;
        this.author = author;
    }

    public String getQuote() {
        return quote;
    }

    public String getAuthor() {
        return author;
    }
}

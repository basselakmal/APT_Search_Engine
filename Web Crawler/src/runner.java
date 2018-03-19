import java.io.IOException;
import java.util.Vector;

public class runner {
    public static void main(String [] args) {

        Thread webCrawler = new WebCrawler();
        Thread indexer = new Indexer();

        webCrawler.start();
        indexer.start();
    }
}

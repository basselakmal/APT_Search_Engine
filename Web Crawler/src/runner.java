import java.io.IOException;
import java.util.Vector;

public class runner {
    public static void main(String [] args) {
        Vector<Anchor> Crawled = new Vector<Anchor>();
        Vector<WebPage> CrawledPages = new Vector<WebPage>();

        Thread webCrawler = new WebCrawler(Crawled, CrawledPages);
        Thread indexer = new Indexer(CrawledPages);

        webCrawler.start();
        indexer.start();
    }
}

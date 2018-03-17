import java.io.IOException;
import java.util.Vector;

public class WebCrawler extends Thread {

    private Vector<Anchor> Crawled;
    private Vector<WebPage> CrawledPages;

    public WebCrawler(Vector<Anchor> Crawled, Vector<WebPage> CrawledPages)
    {
        this.Crawled = Crawled;
        this.CrawledPages = CrawledPages;
    }
    public void run() {

        String StartURL = "https://stackoverflow.com/";
        Anchor anchor = new Anchor(StartURL, StartURL);
        Vector <Anchor> Crawling = new Vector<Anchor>();

        Crawling.add(anchor);

        while(Crawling.size() > 0)
        {
            try
            {
                Crawl(Crawled, Crawling, CrawledPages);
            }
            catch (Exception e)
            {
                System.out.println("Error: " + e.getMessage() + "\nURL: " + Crawling.get(0));
                Crawling.removeElementAt(0);
                continue;
            }
        }

    }

    public void Crawl(Vector <Anchor> Crawled, Vector <Anchor> Crawling, Vector <WebPage> CrawledPages) throws IOException {
        if(isCrawled(Crawling.get(0), Crawled)) {
            Crawling.remove(0);
            return;
        }
        WebPage webPage = new WebPage(Crawling);
        CrawledPages.add(webPage);
        Crawling.get(0).linkWebPage(webPage);
        Crawled.add(Crawling.get(0));
        Crawling.remove(0);
    }

    public boolean isCrawled(Anchor a, Vector <Anchor> Crawled)
    {
        for(Anchor anchor:Crawled)
        {
            if(anchor.getAnchorURL().equals(a.getAnchorURL()))
            {
                anchor.addReferrerURLs(a.getReferrerURLs());
                return true;
            }
        }
        return false;
    }
}

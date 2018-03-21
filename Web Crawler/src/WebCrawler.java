import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashSet;
import java.util.Vector;

public class WebCrawler extends Thread {

    public String StartURL = "https://stackoverflow.com/";
    public WebCrawler ()
    {
        
    }
    public WebCrawler(String URL)
    {
        this.StartURL = URL;
    }
 
    public void run() 
    {
        Vector<Anchor> Crawled = new Vector<Anchor>();        
        System.out.println("Start crawling from website " + this.StartURL);
        Anchor anchor = new Anchor(StartURL, StartURL);
        Vector <Anchor> Crawling = new Vector<Anchor>();

        Crawling.add(anchor);

        while(Crawling.size() > 0)
        {
            try
            {
                Crawl(Crawled, Crawling);
            }
            catch (Exception e)
            {
                System.out.println("Error: " + e.getMessage() + "\nURL: " + Crawling.get(0).getAnchorURL());
                Crawling.removeElementAt(0);
                continue;
            }
        }

    }

    public void Crawl(Vector <Anchor> Crawled, Vector <Anchor> Crawling) throws IOException {
        if(isCrawled(Crawling.get(0), Crawled)) {
            InsertCrawled(Crawling.get(0)); //Insert crawled page to database (for updating the referrer urls)
            Crawling.remove(0);
            return;
        }

        String domainURL = Crawling.get(0).getAnchorURL();
        HashSet<String> referrerURLs = Crawling.get(0).getReferrerURLs();

        Document document = Jsoup.connect(domainURL).userAgent("Mozilla").get();

        //Fetching all links from page at url: domainURL, and adding them to the crawling list (To be crawled)
        Elements Links = document.getElementsByTag("a");
        for (Element Link : Links) {
            if(!Link.absUrl("href").replace(" ", "").equals(""))
            {
                Anchor tempAnchor = new Anchor(domainURL, Link.absUrl("href"));
                Crawling.add(tempAnchor);
            }
        }

        Crawled.add(Crawling.get(0));   //Add the crawled page to Crawled list
        InsertCrawled(Crawling.get(0)); //Insert the crawled page to database
        Crawling.remove(0); //Remove the crawled page from the To-Be-Crawled list
    }

    public void InsertCrawled(Anchor CrawledPage)
    {
        /* Inserts the crawled anchor to the database */

    }

    public boolean isCrawled(Anchor a, Vector <Anchor> Crawled)
    {
        /* Checks whether the page has been crawled before, and updates the page's referrer URLs */
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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Vector;

public class WebCrawler extends Thread 
{
    Vector <Anchor> Crawled = null;
    Vector <Anchor> Crawling = null;
    int index = 0; //synchronized threading by indexing
    int init = 0; //synchronized construtor
    private static final Object lock = new Object(); //thread locking "https://stackoverflow.com/questions/7659615/why-synchronized-method-allowing-multiple-thread-to-run-concurrently"
    
    public WebCrawler()
    {
        //Load Crawled and Crawling from the database
        synchronized (lock)
        {
            try
            {
                if (init == 0) //only first create the vectors
                {
                    DB_Manager Man = new DB_Manager();
                    Crawled = Man.getCrawledAnchors();
                    Crawling = Man.getCrawlingAnchors();
                    init ++;
                }
            }
            catch (SQLException e)
            {
                System.out.println("Crawler Constructor Error: " + e.getMessage());
            }
        }
    }
    @Override
    public void run() 
    {
        String StartURL = "http://localhost:8080/CrawlerTest/";
        Anchor anchor = new Anchor(StartURL, StartURL);
        if(Crawling.isEmpty())
        {
            Crawling.add(anchor);
            InsertCrawling(anchor);
            StartURL = "https://yahoo.com";
            anchor = new Anchor(StartURL, StartURL);
            Crawling.add(anchor);
            InsertCrawling(anchor);
            StartURL = "https://google.com";
            anchor = new Anchor(StartURL, StartURL);
            Crawling.add(anchor);
            InsertCrawling(anchor);
            StartURL = "https://hotmail.com";
            anchor = new Anchor(StartURL, StartURL);
            Crawling.add(anchor);
            InsertCrawling(anchor);
            StartURL = "https://youm7.com";
            anchor = new Anchor(StartURL, StartURL);
            Crawling.add(anchor);
            InsertCrawling(anchor);
        }
        while(Crawling.size() > 0 && Crawled.size() < 5000) //Limit of 5000 have been reached
        {
            Anchor toThread;
            try
            {
                synchronized (this)
                {
                    //toThread = Threading();
                    toThread = Crawling.get(uniqueindex());
                }
                if (toThread != null)
                {
                    Crawl(Crawled, toThread);
                }
            }
            catch (IOException e)
            {
                System.out.println("Error: " + e.getMessage() + "\nURL: " + Crawling.get(0).getAnchorURL());
                Crawling.removeElementAt(0);
            }
        }
    }
    
    public void Crawl(Vector <Anchor> Crawled, Anchor toCrawl) throws IOException 
    {
        if(isCrawled(toCrawl, Crawled)) 
        {
            updateCrawledStatus(toCrawl); //Insert crawled page to database (for updating the referrer urls)
            Crawling.remove(toCrawl);
            return;
        }

        String domainURL = toCrawl.getAnchorURL();
        HashSet<String> referrerURLs = toCrawl.getReferrerURLs();

        Document document = Jsoup.connect(domainURL).get();

        //Fetching all links from page at url: domainURL, and adding them to the crawling list (To be crawled)
        Elements Links = document.getElementsByTag("a");
        for (Element Link : Links) 
        {
            if(!Link.absUrl("href").replace(" ", "").equals(""))
            {
                Anchor tempAnchor = new Anchor(domainURL, Link.absUrl("href"));
                Crawling.add(tempAnchor);
                InsertCrawling(tempAnchor);
            }
        }
       if(isHTML(document) && robotAllowed(domainURL))
        {
            //System.out.println ("Crawler " + Thread.currentThread().getName() + " crawling :- " + toCrawl.getAnchorURL());
            Crawled.add(toCrawl);   //Add the crawled page to Crawled list
            updateCrawledStatus(toCrawl); //Insert the crawled page to database
        }
        Crawling.remove(toCrawl); //Remove the crawled page from the To-Be-Crawled list
    }
    
    public boolean robotAllowed(String domainURL)
    {
        String robotURL = getRobotURL(domainURL);
        try
        {
            WebPage robotPage = new WebPage(robotURL);

           return robotPage.isAllowedByRobot(domainURL);

        }
        catch (IOException e)
        {
        }
        return true;
    }
    
    public String getRobotURL(String domainURL)
    {
        //Pattern p = Pattern.compile("(.*//[a-zA-Z0-9.]*)/?");
        //Matcher m = p.matcher(domainURL);
        String domainRoot = domainURL.substring(0, domainURL.substring(8).indexOf('/') + 9);
        return domainRoot + "robots.txt";
    }
    
    public boolean isHTML(Document doc)
    {
        Elements e = doc.getElementsByTag("html");
        return !e.isEmpty();
    }
    
    public void updateCrawledStatus(Anchor CrawledPage)
    {
        /* Update the crawled anchor to the database */
        DB_Manager DB_Man = new DB_Manager();
        for(String referrerURL : CrawledPage.getReferrerURLs())
             DB_Man.executeNonQuery(" UPDATE domain_referrer SET isCrawled = 1 WHERE domainURL = '" + CrawledPage.getAnchorURL() + "' AND referrerURL = '" + referrerURL +"'");
    }
    public void InsertCrawling(Anchor CrawlingPage)
    {
        /* Inserts the crawled anchor to the database */
        DB_Manager DB_Man = new DB_Manager();
        for(String referrerURL : CrawlingPage.getReferrerURLs())
            DB_Man.executeNonQuery(" INSERT INTO domain_referrer (domainURL, referrerURL) VALUES ('" + CrawlingPage.getAnchorURL() + "', '" + referrerURL + "')");
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
    
    public synchronized Anchor Threading() //A Synchronized Function that return the first uncrawled element
    {
        for (int i = 0 ; i < Crawling.size() ; i++)
        {
            if (!isCrawled(Crawling.get(i),Crawled))
            {
                //int index = i; 
                Anchor tempAnchor = Crawling.get(i);
                System.out.println ("Crawler " + Thread.currentThread().getName() + " tried to thread "+ tempAnchor.getAnchorURL());
                return tempAnchor;
            }               
        }
        //System.out.println ("All Anchors are Crawleded"); //No Uncrawled element found
        return null;
    }
    public synchronized int uniqueindex()
    {
        index ++ ;
        //while (isCrawled(Crawling.get(index),Crawled))
        //{
            //index ++;
            if (index >= Crawling.size())
            {
                index = 0;
            }
        //}
        return index;
    }
}
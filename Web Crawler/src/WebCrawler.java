import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebCrawler implements Runnable
{

    Vector<Anchor> Crawled = null;
    Vector <Anchor> Crawling = null;
    String StartURL = "http://localhost:8080/CrawlerTest/";

    public WebCrawler()
    {
        //Load Crawled and Crawling from the database
        try
        {
            DB_Manager Man = new DB_Manager();
            Crawled = Man.getCrawledAnchors();
            Crawling = Man.getCrawlingAnchors();
        }
        catch (Exception e)
        {
            System.out.println("Crawler Constructor Error: " + e.getMessage());
        }
    }
    
 
    public void run() 
    {     
        Emptydatabase(); //to empty the database before crawling
        System.out.println ("Crawler " + Thread.currentThread().getName() + " Has Started Crawling ");
        
        if(Crawling.isEmpty())
        {
            String StartURL = "http://localhost:8080/CrawlerTest/";
            Anchor anchor = new Anchor(StartURL, StartURL);
            Crawling.add(anchor);
            InsertCrawling(anchor);
            StartURL = "https://yahoo.com";
            anchor = new Anchor(StartURL, StartURL);
            Crawling.add(anchor);
            InsertCrawling(anchor);
        }
        
        while(Crawling.size() > 0 && Crawled.size() < 100 )
        {           
            try
            {
                for (int i = 0 ; i < Crawling.size() ; i ++)
                {    
                    if (isThreaded(Crawling.get(i)) == false) //A Synchronized function
                    {
                        System.out.println ("Crawler " + Thread.currentThread().getName() + " Treaded :- " + Crawling.get(i).getAnchorURL());
                        Crawl(Crawled, Crawling);
                        break;
                    }                   
                }
            }
            catch (Exception e)
            {
                System.out.println("Error: " + e.initCause(e) + "\nURL: " + Crawling.get(0).getAnchorURL());
                Crawling.removeElementAt(0);
                continue;
            }
        }
        if (Crawling.size() >= 100)
        {
            System.out.println("The limit of 5000 has been reached");
            try 
            {
                Thread.sleep (900000000);
                System.out.println("Thread Sleeping");
            }
            catch (InterruptedException e) 
            {
                System.out.println (Thread.currentThread().getName() + "is awaken");
            }
        }
    }

    public void Crawl(Vector <Anchor> Crawled, Vector <Anchor> Crawling) throws IOException 
    {
        if(isCrawled(Crawling.get(0), Crawled)) 
        {
            updateCrawledStatus(Crawling.get(0)); //Insert crawled page to database (for updating the referrer urls)
            Crawling.remove(0);
            return;
        }

        String domainURL = Crawling.get(0).getAnchorURL();
        HashSet<String> referrerURLs = Crawling.get(0).getReferrerURLs();

        Document document = Jsoup.connect(domainURL).userAgent("Mozilla").get();

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
            Crawled.add(Crawling.get(0));   //Add the crawled page to Crawled list
            updateCrawledStatus(Crawling.get(0)); //Insert the crawled page to database
        }
        Crawling.remove(0); //Remove the crawled page from the To-Be-Crawled list
    }

    public boolean robotAllowed(String domainURL)
    {
        String robotURL = getRobotURL(domainURL);

        try
        {
           WebPage robotPage = new WebPage(robotURL);

           return robotPage.isAllowedByRobot(domainURL);
        }
        catch (Exception e)
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

        if(e.isEmpty())
            return false;
        else
            return true;
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
    
    public synchronized boolean isThreaded(Anchor CrawlingPage) throws SQLException
    {
        boolean threaded = true;
        DB_Manager DB_Man = new DB_Manager();
        for(String referrerURL : CrawlingPage.getReferrerURLs())
        {
            String th = " Select isThreaded From domain_referrer Where domainURL = '" + CrawlingPage.getAnchorURL() + "' AND referrerURL = '" + referrerURL +"'";
            Object validthreaded = DB_Man.executeScalar(th);
            if (validthreaded != null)
            {   
                threaded = (boolean)validthreaded;
                if(threaded == false)
                {
                    updateThreadStatus(CrawlingPage);
                }
            }
        }        
        return threaded;
    }
    
    public void updateThreadStatus(Anchor CrawledPage)
    {
        /* Update the crawled anchor to the database */
        DB_Manager DB_Man = new DB_Manager();
        for(String referrerURL : CrawledPage.getReferrerURLs())
        { 
            DB_Man.executeNonQuery(" UPDATE domain_referrer SET isThreaded = 1 WHERE domainURL = '" + CrawledPage.getAnchorURL() + "' AND referrerURL = '" + referrerURL +"'");
        }
    }
    
    public void Emptydatabase()
    {
        /* Update the crawled anchor to the database */
        DB_Manager DB_Man = new DB_Manager();
        DB_Man.executeNonQuery(" DELETE FROM domain_referrer");
    }
    
    
}
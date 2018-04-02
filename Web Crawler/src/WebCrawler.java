import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.HashSet;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebCrawler extends Thread {

    DB_Manager DB_Man = new DB_Manager();
    Utilities Utl = new Utilities();

    public WebCrawler(){

    }

    public void run() {

        while(CrawlerRunner.iterationsCounter < CrawlerRunner.iterationMax)
        {
            try
            {
                if(Crawl() == -1)
                    break;
            }
            catch (Exception e)
            {
                //System.out.println("Error: " + e.getMessage());
                //System.out.println("URL: " + Crawling.get(0).getAnchorURL());
                synchronized (CrawlerRunner.Crawling){
                    if(CrawlerRunner.Crawling.size() > 0)
                        CrawlerRunner.Crawling.removeElementAt(0);
                    continue;
                }
            }
        }
    }

    public Anchor getLink(){
        synchronized (CrawlerRunner.Crawling){
            Anchor link = null;

            if(CrawlerRunner.Crawling.size() > 0)
            {
                link = CrawlerRunner.Crawling.get(0);
                CrawlerRunner.Crawling.removeElementAt(0);
                if(isCrawled(link)){
                    return getLink();
                }
            }
            return link;
        }
    }

    public int Crawl() throws IOException {
        Anchor link = getLink();

        if(link == null)
            return 0;

        //System.out.println("Thread " + Thread.currentThread().getName() + " is processing link: " + link.getAnchorURL());
        String domainURL = link.getAnchorURL();
        HashSet<String> referrerURLs = link.getReferrerURLs();

        File f = new File("pages\\" + link.getAnchorHash() + ".txt");
        Document document = Utl.DownloadPage(link);
        if(document == null || !Utl.isHTML(document) || !Utl.robotAllowed(link.getAnchorURL())){
            //Invalid link. Remove it from the database!
            DB_Man.removeLink(link);
            return 0;
        }

        //Fetching all links from page at url: domainURL, and adding them to the crawling list (To be crawled)
        Elements Links = document.body().getElementsByTag("a");
        HashSet<String> linksSet = new HashSet<String>();

        for(Element Link : Links)
            if(!Link.absUrl("href").replace(" ", "").equals(""))
            {
                String url = Link.absUrl("href");
                String res = "";
                if(url.contains("#"))
                    res = url.substring(0, url.indexOf('#'));
                else
                    res = url;

                int lastIndex = res.lastIndexOf('/');
                if((res.substring(lastIndex-1, lastIndex+1).equals("//") || res.lastIndexOf('.') < lastIndex) && lastIndex + 1 < res.length())
                    res = res + "/";
                linksSet.add(res);
            }

        //System.out.println("URL: " +link.getAnchorURL() + ", Links Count: " + linksSet.size());
        if(linksSet.size() > CrawlerRunner.HighPriorityLinks)
            DB_Man.updatePriority(link);

        for (String Link : linksSet) {
            if(CrawlerRunner.iterationsCounter >= CrawlerRunner.iterationMax)
                return -1;

            Anchor tempAnchor = new Anchor(domainURL, Link);
            CrawlerRunner.Crawling.add(tempAnchor);
            DB_Man.InsertCrawling(tempAnchor);
            //System.out.println("\t\tThread " + Thread.currentThread().getName() + " added: " + tempAnchor.getAnchorURL());
        }

        synchronized (CrawlerRunner.Crawled){
            if(!isCrawled(link)){
                CrawlerRunner.Crawled.add(link);
                CrawlerRunner.iterationsCounter++;
                DB_Man.updateCrawledStatus(link);
                //System.out.println("Crawled Count: " + CrawlerRunner.Crawled.size());
                System.out.println("Thread " + Thread.currentThread().getName() + " processed link: " + link.getAnchorURL());
            }
        }
        return 0;
    }

    public boolean isCrawled(Anchor a)
    {
        /* Checks whether the page has been crawled before, and updates the page's referrer URLs */
        for(Anchor anchor : CrawlerRunner.Crawled)
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

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.Vector;

public class CrawlerRunner
{
    public static Vector <Anchor> Crawled = null;
    public static Vector <Anchor> Crawling = null;

    public static int totalMax = 5000;
    public static int HighPriorityLinks = 100;
    public static int iterationsCounter = 0;
    public static int iterationMax = 0;

    public static int nIterations = 0;

    public static void main(String [] args) throws InterruptedException
    {
        try 
        {
            Initialize();
            System.out.print("Please enter the number of threads: ");
            Scanner MyScanner = new Scanner(System.in);
            int nThreads = MyScanner.nextInt();

            for(int i=0; i<nThreads; i++) {
                Thread webCrawler = new WebCrawler();
                webCrawler.setName("Crawler " + (i + 1));
                webCrawler.start();
            }

            while(true){
                while(iterationsCounter < iterationMax){
                    //System.out.println(iterationsCounter + " : " + iterationMax);
                    Thread.sleep(200);
                }

                System.out.println("Finished an Iteration!\n");
                nIterations ++;
                RestartCrawler();
            }
        }
        catch (Exception e)
        {
            System.out.println(e);
        }  
    }

    public static void RestartCrawler() throws InterruptedException, ClassNotFoundException, SQLException, PropertyVetoException, IOException {
        DB_Manager DB_Man = new DB_Manager();
        DB_Man.executeNonQuery("UPDATE crawled_pages SET isCrawled=1 WHERE highPriority = 1;");

        DB_Man.executeNonQuery("Delete FROM crawled_pages WHERE isCrawled = 0;");
        DB_Man.executeNonQuery("UPDATE crawled_pages SET isCrawled=0, isIndexed=0 WHERE highPriority = 1");

        System.out.println("Restarting crawler....");
        Thread.sleep(5000);


        iterationsCounter = 0;
        Crawled = DB_Man.getCrawledAnchors();
        Crawling = DB_Man.getCrawlingAnchors();
        iterationMax = Crawling.size() < totalMax && Crawling.size() > 0 ? Crawling.size() : totalMax;

    }

    public static void Initialize(){
        try{
            DB_Manager DB_Man = new DB_Manager();
            Utilities Utl = new Utilities();

            File dir = new File("pages/");
            if(!dir.exists())
                dir.mkdir();

            DB_Man.executeNonQuery("UPDATE crawled_pages SET isCrawled=1 WHERE highPriority = 1;");
            //Load Crawled and Crawling from the database
            Crawled = DB_Man.getCrawledAnchors();
            Crawling = DB_Man.getCrawlingAnchors();
            iterationMax = totalMax;
            iterationsCounter = Crawled.size();

            if(Crawled.size() == 0 && Crawling.size() == 0)
            {
                //Delete old crawled pages if exist
                dir = new File("pages/");
                for(File file: dir.listFiles())
                    if (!file.isDirectory())
                        file.delete();

                String[] SeedList = new String[]{ "https://dmoztools.net/", "https://stackexchange.com/", "https://en.wikipedia.org/wiki/Main_Page/", "https://twitter.com/", "https://www.google.com/business/", "https://www.pinterest.com/", "https://www.reddit.com/", "http://www.stumbleupon.com/", "https://vimeo.com/", "https://www.tumblr.com/", "https://disqus.com/", "https://www.slideshare.net/", "https://www.yelp.com/", "http://www.dailymotion.com/us/", "https://soundcloud.com/", "https://www.behance.net/", "https://www.diigo.com/", "https://www.scribd.com/", "https://www.deviantart.com/", "https://about.me/", "https://moz.com/", "https://storify.com/", "https://pro.iconosquare.com/", "https://www.crunchbase.com/", "http://www.scoop.it/", "https://www.instapaper.com/", "https://www.wattpad.com/", "https://envato.com/", "https://www.rebelmouse.com/", "https://aboutus.com/", "http://www.authorstream.com/", "https://www.pearltrees.com/", "https://edition.cnn.com/", "http://www.skysports.com/" };

                for(String StartURL : SeedList)
                {
                    Anchor anchor = new Anchor("SEED_LIST", StartURL);
                    Crawling.add(anchor);
                    DB_Man.InsertCrawling(anchor);
                    DB_Man.InsertReferrer(anchor);
                }
            }
        }
        catch (Exception e)
        {
            System.out.println("Error in Crawling Initial List: " + e.getMessage());
        }
    }
}

import java.util.Scanner;
import java.util.Vector;

public class CrawlerRunner
{
    public static Vector<Anchor> Crawled = null;
    public static Vector <Anchor> Crawling = null;
    public static int Max = 5000;

    public static void main(String [] args) throws InterruptedException
    {
        try 
        {
            Initialize();
            System.out.print("Please enter the number of threads: ");
            Scanner MyScanner = new Scanner(System.in);
            int nThreads = MyScanner.nextInt();
            while(true){
                for(int i=0; i<nThreads; i++){
                    Thread webCrawler = new WebCrawler();
                    webCrawler.setName("Crawler " + (i+1));
                    webCrawler.start();
                }
                while(Crawled.size()<Max){};
                DB_Manager DB_Man = new DB_Manager();
                Thread.sleep(4000);
                System.out.println("Restarting Crawling Process....");
                Thread.sleep(4000);

                DB_Man.executeNonQuery("DELETE FROM domain_referrer WHERE isCrawled=0");
                DB_Man.executeNonQuery("UPDATE domain_referrer SET isCrawled=0, isIndexed=0");
                Crawled = new Vector<Anchor>();
                Crawling = DB_Man.getCrawlingAnchors();
            }


        }
        catch (Exception e)
        {
            System.out.println(e);
        }  
    }

    public static void Initialize(){
        try{
            DB_Manager DB_Man = new DB_Manager();
            Utilities Utl = new Utilities();

            //Load Crawled and Crawling from the database
            Crawled = DB_Man.getCrawledAnchors();
            Crawling = DB_Man.getCrawlingAnchors();

            if(Crawled.size() == 0 && Crawling.size() == 0)
            {
                String[] SeedList = new String[]{ "https://en.wikipedia.org/wiki/Ping_of_death" };

                for(String StartURL : SeedList)
                {
                    Anchor anchor = new Anchor("SEED_LIST", StartURL);
                    Crawling.add(anchor);
                    DB_Man.InsertCrawling(anchor);
                }
            }
        }
        catch (Exception e)
        {
            System.out.println("Error in Crawling Initial List: " + e.getMessage());
        }
    }
}

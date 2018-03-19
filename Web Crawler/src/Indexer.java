import java.util.Vector;

public class Indexer extends Thread{
    Vector<WebPage> FinishedPages = new Vector<WebPage>();

    public void run()
    {
        try
        {
            DB_Manager DB_Man = new DB_Manager();
            int failed =0;

            Vector<Object> domainURLs = DB_Man.getColumnData("domainURL", "SELECT DISTINCT domainURL FROM domain_referrer WHERE isIndexed = 0;");

            for (Object domainURL : domainURLs) {
                try {
                    WebPage webPage = new WebPage(domainURL.toString());
                    webPage.parseDocument();
                    webPage.insertToDatabase();
                    FinishedPages.add(webPage);
                    webPage.updateIndexedStatus();
                    webPage.printInfo();
                }
                catch(Exception e)
                {
                    System.out.println("Indexer Error: " + e.getMessage());
                    failed ++;
                }
            }
            System.out.println("Failed to add: " + failed);
        }
        catch (Exception e)
        {
            System.out.println("Indexer Database Error: " + e.getMessage());
        }


    }
}

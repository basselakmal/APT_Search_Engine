import java.util.Vector;

public class Indexer extends Thread{
    Vector<WebPage> CrawledPages;
    Vector<WebPage> FinishedPages = new Vector<WebPage>();

    public Indexer(Vector<WebPage> CrawledPages)
    {
        this.CrawledPages = CrawledPages;
    }
    public void run()
    {
        while (true)
        {
            try {
                for (WebPage webPage : CrawledPages) {
                    webPage.parseDocument();
                    webPage.insertToDatabase();
                    FinishedPages.add(webPage);
                    webPage.printInfo();
                    CrawledPages.remove(webPage);
                }
            }
            catch(Exception e)
            {

            }
        }
    }
}

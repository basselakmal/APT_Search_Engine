import java.util.Vector;

public class Indexer extends Thread{

    public void run()
    {
        while (true){

            try {
                String domainURL = null;
                domainURL = IndexerRunner.getURL();
                if(domainURL == null)
                    continue;

                WebPage webPage = new WebPage(domainURL);
                webPage.parseDocument();
                webPage.tokenizePage();
                webPage.insertToDatabase();
                IndexerRunner.FinishedPages.add(webPage);
                webPage.updateIndexedStatus();
                System.out.println("Thread: " + Thread.currentThread().getName() + " indexed the following:");
                webPage.printInfo();
            }
            catch(Exception e)
            {
                System.out.println("Indexer Error: " + e.getMessage());
            }
    }
    }
}

import java.util.Vector;

public class Indexer extends Thread{

    public void run()
    {
        while (true){

            try {
                String domainURL = null;
                domainURL = getURL();
                if(domainURL == null)
                    continue;

                WebPage webPage = new WebPage(domainURL);
                webPage.parseDocument();
                webPage.tokenizePage();
                webPage.insertToDatabase();
                webPage.updateIndexedStatus();
                System.out.println("Thread: " + Thread.currentThread().getName() + " indexed the following:");
                webPage.printInfo();
                synchronized (IndexerRunner.processingURLs){
                    IndexerRunner.processingURLs.remove(domainURL);
                }
            }
            catch(Exception e)
            {
                System.out.println("Indexer Error: " + e.getMessage());
            }
    }
    }

    public String getURL(){
        synchronized (IndexerRunner.domainURLs){
            String domainURL = null;
            if(IndexerRunner.domainURLs.size() > 0){
                domainURL = IndexerRunner.domainURLs.get(0).toString();
                IndexerRunner.domainURLs.removeElementAt(0);
            }
            return domainURL;
        }
    }
}

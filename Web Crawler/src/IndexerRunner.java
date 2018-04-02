import java.util.HashSet;
import java.util.Scanner;
import java.util.Vector;

public class IndexerRunner {

    private static Vector<Object> domainURLs = new Vector<Object>();
    public static HashSet<Object> processingURLs = new HashSet<Object>();

    public static Vector<WebPage> FinishedPages = new Vector<WebPage>();
    public static Vector<Thread> Threads = new Vector<Thread>();

    public static void main(String[] args){
        System.out.print("Please enter the number of threads: ");
        Scanner MyScanner = new Scanner(System.in);
        int nThreads = MyScanner.nextInt();

        for(int i=0; i< nThreads; i++){
            Thread indexer = new Indexer();
            Threads.add(indexer);
            indexer.setName("Indexer " + (i+1));
            indexer.start();
        }

        while(true){
            while(domainURLs.size()>0){}
            fetchDomainURLs();
        }
    }

    public static synchronized String getURL(){
        String domainURL = null;
        if(domainURLs.size() > 0){
            domainURL = domainURLs.get(0).toString();
            domainURLs.removeElementAt(0);
        }

        return domainURL;
    }

    private synchronized static void fetchDomainURLs(){
        try
        {
            DB_Manager DB_Man = new DB_Manager();
            Vector<Object>tempDomainURLs = DB_Man.getColumnData("domainURL", "SELECT domainURL FROM crawled_pages WHERE isIndexed=0 AND isCrawled=1;");

            for(int i=0; i<tempDomainURLs.size(); i++){
                if(!processingURLs.add(tempDomainURLs.get(i))){
                    tempDomainURLs.removeElementAt(i);
                    i--;
                }
            }
            domainURLs = tempDomainURLs;
        }
        catch(Exception e)
        {
            System.out.println("Indexer Fetching URLs Error: " + e.getMessage());
        }
    }
}

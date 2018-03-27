import java.io.IOException;
import java.util.Vector;
import java.io.*;
import java.net.*;
import java.util.Random;

public class runner 
{
    public static void main(String [] args) throws InterruptedException
    {
        try 
        {
            //Provide a multithreaded crawler implementation where the user can control the number of threads before starting the crawler.
            System.out.println("Enter The Number of Multithreaded crawlers :- ");
            BufferedReader consolereader = new BufferedReader(new InputStreamReader(System.in));
            int n = Integer.parseInt(consolereader.readLine());
            for (int i = 0 ; i < n ; i++)
            {
                Thread webCrawler = new Thread(new WebCrawler());
                webCrawler.setName("" + i + "");
                //Thread webCrawler = new Thread(new WebCrawler("https://google.com"));
                webCrawler.start();
            }
         
            //Thread indexer = new Indexer();
            //indexer.start();
        
        }
        catch (Exception e)
        {
            System.out.println(e);
        }  
    }
}
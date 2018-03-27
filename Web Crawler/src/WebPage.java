import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.Vector;
import java.util.HashSet;
import java.io.IOException;

public class WebPage 
{
    private Vector<Anchor> anchors;
    private String Title;
    private String Keywords;
    private String Description;
    private String domainURL;
    private Document document;
    private Elements HeaderLines;
    private Elements MetaTags;

    /* Constructor used for crawling */
    public WebPage(String domainURL) throws IOException 
    {
        this.domainURL = domainURL;
        document = Jsoup.connect(domainURL).userAgent("Mozilla").get();
    }

    public void parseDocument()
    {
        Title = document.title();
        MetaTags = document.getElementsByTag("meta");
        for(Element MetaTag : MetaTags)
        {
            if(MetaTag.attr("name").toLowerCase().equals("keywords"))
                Keywords = MetaTag.attr("content").replace(" ", "").equals("")? null: MetaTag.attr("content");

            if(MetaTag.attr("name").toLowerCase().equals("description"))
                Description = MetaTag.attr("content").replace(" ", "").equals("")? null: MetaTag.attr("content");
        }

        HeaderLines = document.select("h1, h2, h3, h4, h5");
        for(Element HeaderLine : HeaderLines)
        {
            Keywords += ' ' + HeaderLine.text();
        }

        Keywords = Keywords.replace(',', ' ');
        Keywords = Keywords.replaceAll("[^a-zA-Z0-9 ]", "");
    }


    public boolean isAllowedByRobot(String domainURL)
    {
        String e = document.body().text();
        String[] tokens = e.split(" ");
        boolean userAgent = false;
        boolean ourBot = false;
        boolean disallow = false;
        boolean allow = false;
        for (String t : tokens) {
            if (t.indexOf('#') != 0) 
            {
                if (disallow) 
                {
                    disallow = false;
                    if (t.equals("/") || domainURL.contains(t))
                        return false; //Return Not allowed
                } 
                else if (allow) 
                {
                    allow = false;
                    if (t.equals("/") || domainURL.contains(t))
                        return true; //Return allowed
                }
                else if (t.equals("*") || t.equals("ourBot")) 
                {
                    ourBot = true;
                }
                else if (ourBot && t.equals("Disallow:")) 
                {
                    disallow = true;
                }
                else if (ourBot && t.equals("Allow:")) 
                {
                    allow = true;
                }
                else
                    ourBot = false;

            }
        }
       return true;// Return allowed
    }
    
    public boolean insertToDatabase()
    {
        DB_Manager DB_Man = new DB_Manager();
        if(DB_Man.executeNonQuery(" INSERT INTO crawledpages (domainURL, Title, Keywords, Description) VALUES ('" + domainURL + "', '" + Title +"', '" + Keywords +"', '" + Description +"')"))
            return true;
        return false;
    }

    public boolean updateIndexedStatus()
    {
        DB_Manager DB_Man = new DB_Manager();
        if(DB_Man.executeNonQuery("UPDATE domain_referrer SET isIndexed = 1 WHERE domainURL = '" + domainURL + "'"))
            return true;
        return false;
    }

    public void printInfo()
    {
        System.out.println("Title: " + Title);
        System.out.println("Keywords: " + Keywords);
        System.out.println("Description: " + Description);
        System.out.println("Page URL: " + domainURL);
        System.out.println("\n*********************************************************************************\n");
    }
    
}
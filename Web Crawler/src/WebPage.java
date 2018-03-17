import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.Vector;
import java.util.HashSet;
import java.io.IOException;

public class WebPage {
    private Vector<Anchor> anchors;
    private String Title;
    private String Keywords;
    private String Description;
    private String domainURL;
    private HashSet<String> referrerURLs;
    private Document document;
    private Elements Links;
    private Elements HeaderLines;
    private Elements MetaTags;

    /* Constructor used for crawling */
    public WebPage(Vector <Anchor> anchors) throws IOException {
        this.anchors = anchors;
        loadDocument();
    }

    private void loadDocument() throws IOException {
        referrerURLs = anchors.elementAt(0).getReferrerURLs();
        domainURL = anchors.elementAt(0).getAnchorURL();
        document = Jsoup.connect(domainURL).get();

        Links = document.getElementsByTag("a");
        for (Element Link : Links) {
            if(!Link.absUrl("href").replace(" ", "").equals(""))
            {
                Anchor tempAnchor = new Anchor(domainURL, Link.absUrl("href"));
                anchors.add(tempAnchor);
            }
        }

    }

    public void parseDocument(){
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

    public boolean insertToDatabase()
    {
        DB_Manager DB_Man = new DB_Manager();
        if(DB_Man.executeNonQuery("INSERT INTO crawledpages (domainURL, Title, Keywords, Description) VALUES ('" + domainURL + "', '" + Title +"', '" + Keywords +"', '" + Description +"')"))
        {
            for(String referrerURL : referrerURLs)
                if(!DB_Man.executeNonQuery("INSERT INTO domain_referrer (domainURL, referrerURL) VALUES ('" + domainURL + "', '" + referrerURL +"')"))
                    return false;
            return true;
        }
        return false;
    }

    private void updateReferrersDatabase(HashSet<String> referrerURLs)
    {
        DB_Manager DB_Man = new DB_Manager();

        for(String referrerURL : referrerURLs)
            DB_Man.executeNonQuery("INSERT INTO domain_referrer (domainURL, referrerURL) VALUES ('" + domainURL + "', '" + referrerURL +"')");
    }

    public void addReferrerURLs(HashSet<String> referrerURLs)
    {
        this.referrerURLs.addAll(referrerURLs);
        updateReferrersDatabase(referrerURLs);
    }

    public void printInfo()
    {
        System.out.println("Title: " + Title);
        System.out.println("Keywords: " + Keywords);
        System.out.println("Description: " + Description);
        System.out.println("Page URL: " + domainURL);
        System.out.println("Referrer URLs:");

        for(String referrerURL : referrerURLs)
            System.out.println("\t" + referrerURL);
    /*
        System.out.println("Links:\n");
        for(Anchor a : anchors)
        {
            System.out.println("Domain: " + a.getDomainURL());
            System.out.println("Anchor: " + a.getAnchorURL());
            System.out.println();
        }
        */
        System.out.println("\n*********************************************************************************\n");

    }
}

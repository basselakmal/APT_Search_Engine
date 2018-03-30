import org.jsoup.Jsoup;
import org.jsoup.UncheckedIOException;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Base64;

public class Utilities {

    public Document DownloadPage(Anchor anchor){
    try
    {
        Document document = Jsoup.connect(anchor.getAnchorURL()).get();
        FileWriter file = new FileWriter( "pages\\" + anchor.getAnchorHash() + ".txt");
        BufferedWriter out = new BufferedWriter(file);
        out.write(document.outerHtml());
        out.close();
        return document;
    }
    catch(UncheckedIOException e)
    {
        System.out.println("It happened!\n" + e.getMessage());
    }
    catch (Exception ex)
    {
        System.out.println("Error while downloading the document: " + ex.getMessage() + "\nURL:" + anchor.getAnchorURL());
    }
        return null;
    }

    private String getRobotURL(String domainURL)
    {
        String domainRoot = domainURL.substring(0, domainURL.substring(8).indexOf('/') + 9);
        return domainRoot + "robots.txt";
    }

    public boolean isHTML(Document doc)
    {
        Elements e = doc.getElementsByTag("html");

        if(e.isEmpty())
            return false;
        else
            return true;
    }

    public boolean robotAllowed(String domainURL)
    {

        String robotURL = getRobotURL(domainURL);

        try
        {
            WebPage robotPage = new WebPage(robotURL);

            return robotPage.isAllowedByRobot(domainURL);

        }
        catch (Exception e)
        {

        }
        return true;
    }

    public String getURLHash(String anchorURL){
        String anchorHash = new String(Base64.getEncoder().encode(anchorURL.getBytes()));
        anchorHash = anchorHash.replaceAll("[^a-zA-Z0-9_]", "");
        anchorHash = anchorHash.substring(anchorHash.length()<71?0:anchorHash.length()-71, anchorHash.length()-1);  //Take Maximum of 70 Chars
        return anchorHash;
    }

}

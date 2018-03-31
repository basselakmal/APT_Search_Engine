import org.jsoup.Jsoup;
import org.jsoup.UncheckedIOException;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Base64;

public class Utilities {

    public Document DownloadPage(Anchor anchor){
    try
    {
        String filePathString = "pages\\" + anchor.getAnchorHash() + ".txt";
        File f = new File(filePathString);
        Document document = null;
        if(!f.exists()) {
            // If the file doesn't exist, create it
            document = Jsoup.connect(anchor.getAnchorURL()).get();
            FileWriter file = new FileWriter(filePathString );
            BufferedWriter out = new BufferedWriter(file);
            out.write(document.outerHtml());
            out.close();
        }
        else{
            document = Jsoup.parse(f, "ISO-8859-1");
        }

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
            Document doc = DownloadPage(new Anchor(robotURL, robotURL));
            if(doc == null)     //Robots.txt is not found
                return true;

            String e = doc.text();
            String[] tokens = e.split(" ");
            boolean ourBot = false;
            boolean disallow = false;
            boolean allow = false;
            for (String t : tokens) {
                if (t.indexOf('#') != 0) {
                    if (disallow) {
                        disallow = false;
                        if (t.equals("/") || domainURL.contains(t) )
                            return false; //Return Not allowed
                    } else if (allow) {
                        allow = false;
                        if (t.equals("/") || domainURL.contains(t))
                            return true; //Return allowed
                    } else if (t.equals("*") || t.equals("ourBot")) {
                        ourBot = true;
                    } else if (ourBot && t.equals("Disallow:")) {
                        disallow = true;
                    } else if (ourBot && t.equals("Allow:")) {
                        allow = true;
                    } else
                        ourBot = false;
                }
            }
            return true;// Return allowed

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

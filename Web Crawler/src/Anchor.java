import org.jsoup.Connection;

import java.util.Base64;
import java.util.HashSet;
import java.security.MessageDigest;


public class Anchor {
    private HashSet<String> referrerURLs = new HashSet<String>();
    private String anchorURL, anchorHash;
    private Utilities Utl = new Utilities();

    /* Constructors */
    public Anchor(String referrerURL, String anchorURL) {
        this.referrerURLs.add(referrerURL);
        this.anchorURL = anchorURL;
        anchorHash = Utl.getURLHash(anchorURL);
    }

    /* Getters */
    public HashSet<String> getReferrerURLs() {
        return referrerURLs;
    }



    public String getAnchorURL() {
        return anchorURL;
    }

    public String getAnchorHash(){
        return anchorHash;
    }

    public void addReferrerURLs(HashSet<String> referrerURLs)
    {
        this.referrerURLs.addAll(referrerURLs);
    }

    public void addReferrerURL(String referrerURL)
    {
        this.referrerURLs.add(referrerURL);
    }
}

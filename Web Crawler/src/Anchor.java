import java.util.HashSet;

public class Anchor {
    private HashSet<String> referrerURLs = new HashSet<String>();
    private String anchorURL;
    private WebPage webPage;

    /* Constructors */
    public Anchor(String referrerURL, String anchorURL) {
        this.referrerURLs.add(referrerURL);
        this.anchorURL = anchorURL;
    }

    /* Getters */
    public HashSet<String> getReferrerURLs() {
        return referrerURLs;
    }

    public String getAnchorURL() {
        return anchorURL;
    }

    public void addReferrerURLs(HashSet<String> referrerURLs)
    {
        this.referrerURLs.addAll(referrerURLs);
        this.webPage.addReferrerURLs(referrerURLs);
    }

    public void linkWebPage(WebPage webPage)
    {
        this.webPage = webPage;
    }
}

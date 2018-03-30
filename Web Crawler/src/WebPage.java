import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.security.Key;
import java.util.*;

public class WebPage {
    private Vector<Anchor> anchors;
    private String Title;
    private int TitleMaxSize = 300;

    private String Keywords;
    private int KeywordsMaxSize = 700;

    private String Description;
    private int DescriptionMaxSize = 700;

    private String domainURL;
    private Document document;
    private Elements HeaderLines;
    private Elements MetaTags;
    private DB_Manager DB_Man = new DB_Manager();
    private Utilities Utl = new Utilities();

    /* Constructor used for crawling */
    public WebPage(String domainURL) throws IOException {
        this.domainURL = domainURL;
        File file = new File("pages\\" + Utl.getURLHash(domainURL) + ".txt");
        document = Jsoup.parse(file, "ISO-8859-1");
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

        if(Description != null){
            Description = Description.replaceAll("[^a-zA-Z0-9 ]", "");
            if(Description.length()>DescriptionMaxSize){
                Description = Description.substring(0, DescriptionMaxSize-1);
                Description = Description.substring(0, Description.lastIndexOf(' '));
            }
        }
        if(Keywords != null)
        {
            Keywords = Keywords.replaceAll("[^a-zA-Z0-9 ]|(  )+", " ");
            if(Keywords.length()>KeywordsMaxSize){
                Keywords = Keywords.substring(0, KeywordsMaxSize-1);
                Keywords = Keywords.substring(0, Keywords.lastIndexOf(' '));
            }
        }

        if(Title != null){
            Title = Title.replaceAll("[^a-zA-Z0-9 ]|(  )+", " ");
            if(Title.length()>TitleMaxSize){
                Title = Title.substring(0, TitleMaxSize-1);
                Title = Title.substring(0, Title.lastIndexOf(' '));
            }
        }
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
    
    public void insertToDatabase()
    {
        String sqlQuery = "INSERT INTO crawledpages (domainURL, Title, Keywords, Description) VALUES ('" + domainURL + "', '" + Title +"', '" + Keywords +"', '" + Description +"');";
        DB_Man.executeNonQuery(sqlQuery);
    }

    public void updateIndexedStatus()
    {
        DB_Man.executeNonQuery("UPDATE domain_referrer SET isIndexed = 1 WHERE domainURL = '" + domainURL + "'");
    }

    public void printInfo()
    {
        System.out.println("Title: " + Title);
        System.out.println("Keywords: " + Keywords);
        System.out.println("Description: " + Description);
        System.out.println("Page URL: " + domainURL);
        System.out.println("\n*********************************************************************************\n");
    }

    String[] stopWordsArr = {"", " ", "a", "about", "above", "across", "after", "afterwards", "again", "against", "all", "almost", "alone", "along", "already", "also", "although", "always", "am", "among", "amongst", "amoungst", "amount", "an", "and", "another", "any", "anyhow", "anyone", "anything", "anyway", "anywhere", "are", "around", "as", "at", "back", "be", "became", "because", "become", "becomes", "becoming", "been", "before", "beforehand", "behind", "being", "below", "beside", "besides", "between", "beyond", "bill", "both", "bottom", "but", "by", "call", "can", "cannot", "cant", "co", "con", "could", "couldnt", "cry", "de", "describe", "detail", "do", "done", "down", "due", "during", "each", "eg", "eight", "either", "eleven", "else", "elsewhere", "empty", "enough", "etc", "even", "ever", "every", "everyone", "everything", "everywhere", "except", "few", "fifteen", "fify", "fill", "find", "fire", "first", "five", "for", "former", "formerly", "forty", "found", "four", "from", "front", "full", "further", "get", "give", "go", "had", "has", "hasnt", "have", "he", "hence", "her", "here", "hereafter", "hereby", "herein", "hereupon", "hers", "herself", "him", "himself", "his", "how", "however", "hundred", "ie", "if", "in", "inc", "indeed", "interest", "into", "is", "it", "its", "itself", "keep", "last", "latter", "latterly", "least", "less", "ltd", "made", "many", "may", "me", "meanwhile", "might", "mill", "mine", "more", "moreover", "most", "mostly", "move", "much", "must", "my", "myself", "name", "namely", "neither", "never", "nevertheless", "next", "nine", "no", "nobody", "none", "noone", "nor", "not", "nothing", "now", "nowhere", "of", "off", "often", "on", "once", "one", "only", "onto", "or", "other", "others", "otherwise", "our", "ours", "ourselves", "out", "over", "own", "part", "per", "perhaps", "please", "put", "rather", "re", "same", "see", "seem", "seemed", "seeming", "seems", "serious", "several", "she", "should", "show", "side", "since", "sincere", "six", "sixty", "so", "some", "somehow", "someone", "something", "sometime", "sometimes", "somewhere", "still", "such", "system", "take", "ten", "than", "that", "the", "their", "them", "themselves", "then", "thence", "there", "thereafter", "thereby", "therefore", "therein", "thereupon", "these", "they", "thickv", "thin", "third", "this", "those", "though", "three", "through", "throughout", "thru", "thus", "to", "together", "too", "top", "toward", "towards", "twelve", "twenty", "two", "un", "under", "until", "up", "upon", "us", "very", "via", "was", "we", "well", "were", "what", "whatever", "when", "whence", "whenever", "where", "whereafter", "whereas", "whereby", "wherein", "whereupon", "wherever", "whether", "which", "while", "whither", "who", "whoever", "whole", "whom", "whose", "why", "will", "with", "within", "without", "would", "yet", "you", "your", "yours", "yourself", "yourselves", "the"};
    HashSet<String> stopWords = new HashSet<>(Arrays.asList(stopWordsArr));

    public void tokenizePage()
    {
        String Body = document.body().text().toLowerCase();
        Body = Body.replaceAll("[^a-zA-Z ]|(  )+", "");
        String[] BodyArray = Body.split(" ");

        for(int i=0; i< BodyArray.length; i++)
            BodyArray[i] = new Stemmer().stem(BodyArray[i]);

        List BodyList = Arrays.asList(BodyArray);

        HashSet<String> Tokens = new HashSet(BodyList);
        Tokens.removeAll(stopWords);

        System.out.println("URL: " + domainURL + "\nNumber of Tokens: " + Tokens.size());
        for(String token : Tokens)
        {
            int Occurrences = Collections.frequency(BodyList, token);
            if(Occurrences == 0)
                continue;
            DB_Man.executeNonQuery("INSERT INTO InvertedFile (Token, Count, URL) VALUES('" + token + "', " + Occurrences + ", '" + domainURL.toString() + "');");
        }
    }
}

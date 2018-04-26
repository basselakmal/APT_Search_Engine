//Import required java libraries
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

// Extend HttpServlet class
@WebServlet(name = "QueryProcess", urlPatterns = {"/index/","/QueryProcess/*","/Query_Processor/*"})
public class QueryProcess extends HttpServlet 
{
    String[] stopWordsArr = {"", " ", "a", "about", "above", "across", "after", "afterwards", "again", "against", "all", "almost", "alone", "along", "already", "also", "although", "always", "am", "among", "amongst", "amoungst", "amount", "an", "and", "another", "any", "anyhow", "anyone", "anything", "anyway", "anywhere", "are", "around", "as", "at", "back", "be", "became", "because", "become", "becomes", "becoming", "been", "before", "beforehand", "behind", "being", "below", "beside", "besides", "between", "beyond", "bill", "both", "bottom", "but", "by", "call", "can", "cannot", "cant", "co", "con", "could", "couldnt", "cry", "de", "describe", "detail", "do", "done", "down", "due", "during", "each", "eg", "eight", "either", "eleven", "else", "elsewhere", "empty", "enough", "etc", "even", "ever", "every", "everyone", "everything", "everywhere", "except", "few", "fifteen", "fify", "fill", "find", "fire", "first", "five", "for", "former", "formerly", "forty", "found", "four", "from", "front", "full", "further", "get", "give", "go", "had", "has", "hasnt", "have", "he", "hence", "her", "here", "hereafter", "hereby", "herein", "hereupon", "hers", "herself", "him", "himself", "his", "how", "however", "hundred", "ie", "if", "in", "inc", "indeed", "interest", "into", "is", "it", "its", "itself", "keep", "last", "latter", "latterly", "least", "less", "ltd", "made", "many", "may", "me", "meanwhile", "might", "mill", "mine", "more", "moreover", "most", "mostly", "move", "much", "must", "my", "myself", "name", "namely", "neither", "never", "nevertheless", "next", "nine", "no", "nobody", "none", "noone", "nor", "not", "nothing", "now", "nowhere", "of", "off", "often", "on", "once", "one", "only", "onto", "or", "other", "others", "otherwise", "our", "ours", "ourselves", "out", "over", "own", "part", "per", "perhaps", "please", "put", "rather", "re", "same", "see", "seem", "seemed", "seeming", "seems", "serious", "several", "she", "should", "show", "side", "since", "sincere", "six", "sixty", "so", "some", "somehow", "someone", "something", "sometime", "sometimes", "somewhere", "still", "such", "system", "take", "ten", "than", "that", "the", "their", "them", "themselves", "then", "thence", "there", "thereafter", "thereby", "therefore", "therein", "thereupon", "these", "they", "thickv", "thin", "third", "this", "those", "though", "three", "through", "throughout", "thru", "thus", "to", "together", "too", "top", "toward", "towards", "twelve", "twenty", "two", "un", "under", "until", "up", "upon", "us", "very", "via", "was", "we", "well", "were", "what", "whatever", "when", "whence", "whenever", "where", "whereafter", "whereas", "whereby", "wherein", "whereupon", "wherever", "whether", "which", "while", "whither", "who", "whoever", "whole", "whom", "whose", "why", "will", "with", "within", "without", "would", "yet", "you", "your", "yours", "yourself", "yourselves", "the"};
    char brackets = '"';
    HashSet<String> stopWords = new HashSet<>(Arrays.asList(stopWordsArr));
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
    {
        // Set response content type
        response.setContentType("text/html");

        PrintWriter out = response.getWriter();
        String title = "Result";
        String docType =
        "<!doctype html public \"-//w3c//dtd html 4.0 " +
        "transitional//en\">\n";
        out.println(docType +
        "<html>\n" +
        "<head><title>" + title + "</title></head>\n" +
        "<body bgcolor=\"#f0f0f0\">\n" +
        "<h1 align=\"center\">" + title + "</h1>\n" +
        "<ul>\n" +
        "  <li><b>The Search Process</b>:\n" +
        "</ul>\n" +
        "</body></html>");
  
        out.println("Entered Words :-");
        
  
        if (request.getParameter("Query") != null)
        {
            
            String query = request.getParameter("Query").toLowerCase(); //make sure that they are in lower case
            
            //First :- Phrase Search => check if there is bracket at the beginning and end of the entered word
            if (query.charAt(0) == brackets && query.charAt(query.length()-1) == brackets)
            {
                query = query.substring(1,query.length()-1); //remove the quotations from the input
                out.println(query);
                out.println("\n");
                out.println("-----------");
                
                //put the elements of in input in order and retrieve from the database in order 
                //example :- "Search Engine Implementation"
                //Get => Search followerd by Engine followed by Implementation
                query = query.replaceAll("[^a-zA-Z ]|(  )+", "");           
                String[] BodyArray = query.split(" ");                      // split words
                
                List<String> TokenList = new ArrayList<String>();
            
                for(int i=0; i< BodyArray.length; i++)      //stem words
                {
                    BodyArray[i] = new Stemmer().stem(BodyArray[i]);
                    TokenList.add(BodyArray[i]);
                }   
                //List BodyList = Arrays.asList(BodyArray);
                HashSet<String> Tokens = new HashSet(TokenList);
                //Tokens.removeAll(stopWords);
            
                //out.println(Tokens);
            
                for(int i = 0 ; i < Tokens.size() ; i++)
                {
                    out.println(TokenList.get(i));
                    out.println("\n");
                }
                
            }
            else //Else it is just a normal search input
            {
                query = query.replaceAll("[^a-zA-Z ]|(  )+", "");           
                String[] BodyArray = query.split(" ");                      // split words
            
                for(int i=0; i< BodyArray.length; i++)
                    BodyArray[i] = new Stemmer().stem(BodyArray[i]);        //stem words

                List BodyList = Arrays.asList(BodyArray);

                HashSet<String> Tokens = new HashSet(BodyList);
                Tokens.removeAll(stopWords);
            
                //out.println(Tokens);
            
                for(String token : Tokens)
                {
                    out.println(token);
                    out.println("\n");
                }
            }
        }   
    }
    // Method to handle POST method request.
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException , IOException 
    {
         doGet(request, response);   
    }
}
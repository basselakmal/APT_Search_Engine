/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Bassel
 */
@WebServlet(urlPatterns = {"/Search"})
public class APTServlet extends HttpServlet {
   
    private boolean isPhrase = false;
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //Fady
        Vector<String> Tokens = getTokens(request);           
        
        //Bassel
        DB_Manager DB_Man = new DB_Manager();
        Vector<Page> Pages = DB_Man.getPages(Tokens, isPhrase);
        
        try {
                //Kero
                processRequest(request, response, Pages);
            } catch (PropertyVetoException ex) {
                Logger.getLogger(APTServlet.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                Logger.getLogger(APTServlet.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(APTServlet.class.getName()).log(Level.SEVERE, null, ex);
            }
        
    }
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response, Vector<Page> Pages) throws IOException, PropertyVetoException, SQLException, ClassNotFoundException{
    

        //Print Pages on the screen.
        
        //For testing only, printing the top 10 results on html page.
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {

            out.println("<!DOCTYPE html>");
            out.println("<html>");
              
            out.println(" <head>\n" +
"        <title>APT Search Engine</title>\n" +
"        <meta charset=\"UTF-8\">\n" +
"        <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
"        <script type=\"text/javascript\" src = querySuggestion.js></script>\n" +
"            	<link rel=\"stylesheet\" href=\"css/bootstrap.css\">\n" +
"		<link rel=\"stylesheet\" href=\"css/style.css\">\n" +
"		<script src=\"js/jquery.js\"></script>\n" +
"		<script src=\"js/bootstrap.js\"></script>\n" +
"              <script src=\"https://ajax.googleapis.com/ajax/libs/jquery/1.11.2/jquery.min.js\"></script>\n" +
"  <script src=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.2/js/bootstrap.min.js\"></script>\n" +
"  <script src=\"//cdnjs.cloudflare.com/ajax/libs/bootstrap-select/1.6.3/js/bootstrap-select.min.js\"></script>\n" +
"    </head>");
            
                        String searchQuery = request.getParameter("searchQuery");

            out.println("<body style=\"background-color: #eee;\" onload = \"init()\" >        \n" +
"        <nav class=\"navbar navbar-expand-lg navbar-light bg-light navbar-custom \">\n" +
"			<div class=\"container-fluid\">\n" +
"				\n" +
"				<ul class=\"nav navbar-nav navbar-left\">\n" +
"				<li><a href=\"\">Home</a></li>\n" +
"					<li><a href=\"\">Images</a></li>\n" +
"					<li><a href=\"\">Videos</a></li>\n" +
"				</ul>\n" +
"			</div>\n" +
"		\n" +
"		</nav><center>\n" +
"                            <img src=\"Images/logo.PNG\" width=\"250\" style=\"margin-top: 10px;\" alt=\"Logo\">\n" +
"<div class = \"jumbotron\" style='padding-bottom:0px; margin-bottom:0px;'>\n" +
"                    <form action=\"Search\" autocomplete=\"off\" method=\"GET\" id = \"Search\"> \n" +
"                        <input type=\"text\"  value = '" + searchQuery +"' name=\"searchQuery\" id = \"searchQuery\"  list =\"suggestions\" onkeyup = \"showSuggestion()\">\n" +
"                        <input type=\"submit\"  value=\"Search\"><br>\n" +
"                        <datalist id = \"suggestions\">\n" +
"\n" +
"                        </datalist>  " +
"                    </form>\n" +
"                </div>\n" +
" " +
"    </center>");

                  //  searchQuery = searchQuery.replace("'","");

                      //  out.println(searchQuery);

            //Add query words to database
               DB_Manager db = new DB_Manager();
               db.insertQueryWords(searchQuery,out);

         //   out.println(searchQuery);

         int numberOfPages = Pages.size();
         int pagination = (int) Math.ceil (numberOfPages / 10.0);
         // int pagination = (numberOfPages / 10);
          String index = request.getParameter("index");
                int indexNumber = 0;
                if(index != null)
                    indexNumber = Integer.parseInt(index);

               // out.println(indexNumber);
                      out.println(" There are " + numberOfPages + " pages retrieved <br>");
            
          for(int i=0; i<10; i++)
            {
                if(Pages.size() > (indexNumber * 10) + i)
                   out.println( "<br>" + Pages.get((indexNumber * 10) + i).printInfo().replace("\n", "<br>") + "<br>");
            }
    
            out.print("<center>");
            for (int i =1; i <= pagination ; i++)
            {
               if((i-1) == indexNumber)
                    out.print("<a style='color:red;' font-size:16px; href ='Search?searchQuery=" + searchQuery +  "&index=" + String.valueOf(i-1) + "'>" + String.valueOf(i) + "</a>&emsp;");
               else
                    out.print("<a style='font-size:16px;' href ='Search?searchQuery=" + searchQuery +  "&index=" + String.valueOf(i-1) + "'>" + String.valueOf(i) + "</a>&emsp;");
            }
            out.print("</center>");
            out.println("</body>");
            out.println("</html>");     
        }
    }
    
    private Vector<String> getTokens(HttpServletRequest request){
        //Extract search words from the request, stem them and then return an array of strings that correspond to the tokens.
        String[] stopWordsArr = {"", " ", "a", "about", "above", "across", "after", "afterwards", "again", "against", "all", "almost", "alone", "along", "already", "also", "although", "always", "am", "among", "amongst", "amoungst", "amount", "an", "and", "another", "any", "anyhow", "anyone", "anything", "anyway", "anywhere", "are", "around", "as", "at", "back", "be", "became", "because", "become", "becomes", "becoming", "been", "before", "beforehand", "behind", "being", "below", "beside", "besides", "between", "beyond", "bill", "both", "bottom", "but", "by", "call", "can", "cannot", "cant", "co", "con", "could", "couldnt", "cry", "de", "describe", "detail", "do", "done", "down", "due", "during", "each", "eg", "eight", "either", "eleven", "else", "elsewhere", "empty", "enough", "etc", "even", "ever", "every", "everyone", "everything", "everywhere", "except", "few", "fifteen", "fify", "fill", "find", "fire", "first", "five", "for", "former", "formerly", "forty", "found", "four", "from", "front", "full", "further", "get", "give", "go", "had", "has", "hasnt", "have", "he", "hence", "her", "here", "hereafter", "hereby", "herein", "hereupon", "hers", "herself", "him", "himself", "his", "how", "however", "hundred", "ie", "if", "in", "inc", "indeed", "interest", "into", "is", "it", "its", "itself", "keep", "last", "latter", "latterly", "least", "less", "ltd", "made", "many", "may", "me", "meanwhile", "might", "mill", "mine", "more", "moreover", "most", "mostly", "move", "much", "must", "my", "myself", "name", "namely", "neither", "never", "nevertheless", "next", "nine", "no", "nobody", "none", "noone", "nor", "not", "nothing", "now", "nowhere", "of", "off", "often", "on", "once", "one", "only", "onto", "or", "other", "others", "otherwise", "our", "ours", "ourselves", "out", "over", "own", "part", "per", "perhaps", "please", "put", "rather", "re", "same", "see", "seem", "seemed", "seeming", "seems", "serious", "several", "she", "should", "show", "side", "since", "sincere", "six", "sixty", "so", "some", "somehow", "someone", "something", "sometime", "sometimes", "somewhere", "still", "such", "system", "take", "ten", "than", "that", "the", "their", "them", "themselves", "then", "thence", "there", "thereafter", "thereby", "therefore", "therein", "thereupon", "these", "they", "thickv", "thin", "third", "this", "those", "though", "three", "through", "throughout", "thru", "thus", "to", "together", "too", "top", "toward", "towards", "twelve", "twenty", "two", "un", "under", "until", "up", "upon", "us", "very", "via", "was", "we", "well", "were", "what", "whatever", "when", "whence", "whenever", "where", "whereafter", "whereas", "whereby", "wherein", "whereupon", "wherever", "whether", "which", "while", "whither", "who", "whoever", "whole", "whom", "whose", "why", "will", "with", "within", "without", "would", "yet", "you", "your", "yours", "yourself", "yourselves", "the"};
        HashSet<String> stopWords = new HashSet<>(Arrays.asList(stopWordsArr));
        Vector<String> Tokens = new Vector<String>();
        String searchQuery = request.getParameter("searchQuery");
        if (searchQuery != null)
        {   
            String query = searchQuery.toLowerCase(); //make sure that they are in lower case
            
            //First :- Phrase Search => check if there is bracket at the beginning and end of the entered word
            if (query.startsWith("\"") && query.endsWith("\""))
            {
                query = query.substring(1,query.length()-1); //remove the quotations from the input
                isPhrase = true;
            }
            else
                isPhrase = false;

            //put the elements of in input in order and retrieve from the database in order 
            //example :- "Search Engine Implementation"
            //Get => Search followerd by Engine followed by Implementation
            query = query.replaceAll("[^a-zA-Z ]|(  )+", "");           
            String[] BodyArray = query.split(" ");                      // split words


            List TokenList = Arrays.asList(BodyArray);

            HashSet<String> TokensSet = new HashSet(TokenList);
            HashSet<String> StemmedTokens = new HashSet();
            TokensSet.removeAll(stopWords);

            for(String Token : TokensSet)      //stem words

                StemmedTokens.add(new Stemmer().stem(Token));

            for(String Token : StemmedTokens)
                Tokens.add(Token);            
        }  
        
        return Tokens;        
    }
}

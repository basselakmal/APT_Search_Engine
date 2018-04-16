/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;
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
   
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        //Fady
        Vector<String> Tokens = getTokens(request);
        
        //Bassel
        DB_Manager DB_Man = new DB_Manager();
        Vector<Page> Pages = DB_Man.getPages(Tokens);
        
        //Kero
        processRequest(request, response, Pages);
        
    }
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response, Vector<Page> Pages) throws IOException{
        //Print Pages on the screen.
        
        //For testing only, printing the top 10 results on html page.
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {

            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>APT Search Engine</title>");            
            out.println("</head>");
            out.println("<body>");
            
            for(int i=0; i<10; i++)
                out.println(Pages.get(i).printInfo().replace("\n", "<br>") + "<br>");
            
            out.println("</body>");
            out.println("</html>");     
        }
    }
    
    private Vector<String> getTokens(HttpServletRequest request){
        //Extract search words from the request, stem them and then return an array of strings that correspond to the tokens.
        
        Vector<String> Tokens = new Vector<String>();
        
        //Just for testing!
        Tokens.add("donald");
        Tokens.add("trump");
        
        return Tokens;        
    }
}

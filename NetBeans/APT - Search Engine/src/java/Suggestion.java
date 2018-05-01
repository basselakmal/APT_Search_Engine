/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
 * @author Kerelos
 */
@WebServlet(urlPatterns = {"/Suggestion"})
public class Suggestion extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
   @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        doGet(request,response);
    }
      @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
      response.setContentType("text/html");
      
                   try {
                //Kero
                Suggest(request,response);
                } catch (PropertyVetoException ex) {
                Logger.getLogger(APTServlet.class.getName()).log(Level.SEVERE, null, ex);
                }
         
           }
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet Suggestion</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet Suggestion at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

public void Suggest(HttpServletRequest request,HttpServletResponse response) throws IOException, PropertyVetoException, ServletException
    {
                Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        DB_Manager db = new DB_Manager();
        String word = request.getParameter("searchQuery");

        word = word.replace("'"," ");
        String sqlQuery = "SELECT * FROM query_words WHERE `word` LIKE '" + word + "%'";
        PrintWriter out = response.getWriter() ;
        //out.println(word);
      try {
            // fetch a connection
            conn = DataSource.getInstance().getConnection();

            if (conn != null) {
                stmt = conn.prepareStatement(sqlQuery);
                rs = stmt.executeQuery(sqlQuery);
                    
                
                while(rs.next()) {
                       out.print(rs.getString("word") + "-");

// x(request,response,rs);
                }

            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException e) {e.printStackTrace();}
            if (stmt != null) try { stmt.close(); } catch (SQLException e) {e.printStackTrace();}
            if (conn != null) try { conn.close(); } catch (SQLException e) {e.printStackTrace();}
      }
    }
}

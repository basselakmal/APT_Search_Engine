
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.Vector;

public class DB_Manager {

    /* General Methods */

    public Vector<Object> getColumnData(String columnLabel, String sqlQuery) throws PropertyVetoException, SQLException, IOException, ClassNotFoundException {

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Vector<Object> objectVector = new Vector<Object>();

        try {
            // fetch a connection
            conn = DataSource.getInstance().getConnection();

            if (conn != null) {
                stmt = conn.prepareStatement(sqlQuery);
                rs=stmt.executeQuery();
                while(rs.next()) {
                    objectVector.add(rs.getObject(columnLabel));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException e) {e.printStackTrace();}
            if (stmt != null) try { stmt.close(); } catch (SQLException e) {e.printStackTrace();}
            if (conn != null) try { conn.close(); } catch (SQLException e) {e.printStackTrace();}
        }

        return objectVector;
    }

    public boolean executeNonQuery(String sqlQuery){

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            // fetch a connection
            conn = DataSource.getInstance().getConnection();

            if (conn != null) {
                stmt = conn.prepareStatement(sqlQuery);
                stmt.execute();
            }
        } catch (Exception ex) {
            if (stmt != null) try { stmt.close(); } catch (SQLException e) {e.printStackTrace();}
            if (conn != null) try { conn.close(); } catch (SQLException e) {e.printStackTrace();}

            return false;
        } finally {
            if (stmt != null) try { stmt.close(); } catch (SQLException e) {e.printStackTrace();}
            if (conn != null) try { conn.close(); } catch (SQLException e) {e.printStackTrace();}
        }
        return true;
    }

    public Object executeScalar(String sqlQuery) throws PropertyVetoException, SQLException, IOException, ClassNotFoundException {

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Object obj = null;

        try {
            // fetch a connection
            conn = DataSource.getInstance().getConnection();

            if (conn != null) {
                stmt = conn.prepareStatement(sqlQuery);
                rs = stmt.executeQuery();
                if(rs.next())
                    obj = rs.getObject(1);
            }
        } catch (Exception ex) {
            if (stmt != null) try { stmt.close(); } catch (SQLException e) {e.printStackTrace();}
            if (conn != null) try { conn.close(); } catch (SQLException e) {e.printStackTrace();}
            return null;
        } finally {
            if (stmt != null) try { stmt.close(); } catch (SQLException e) {e.printStackTrace();}
            if (conn != null) try { conn.close(); } catch (SQLException e) {e.printStackTrace();}
        }
        return obj;
    }


    /* Crawler Methods */

    public Vector<Anchor> getCrawledAnchors() throws PropertyVetoException, SQLException, IOException, ClassNotFoundException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sqlQuery = "SELECT dr.domainURL, dr.referrerURL, cp.highPriority FROM domain_referrer as dr, crawled_pages as cp WHERE cp.domainURL = dr.domainURL AND cp.isCrawled=1 ORDER BY domainURL;";
        Vector<Anchor> Crawled = new Vector<Anchor>();
        String LastURL = null;

        try {
            // fetch a connection
            conn = DataSource.getInstance().getConnection();

            if (conn != null) {
                stmt = conn.prepareStatement(sqlQuery);
                rs = stmt.executeQuery(sqlQuery);
                while(rs.next()) {
                    if (rs.getString("domainURL").equals(LastURL)) {
                        //Add referrerURL
                        Crawled.lastElement().addReferrerURL(rs.getString("referrerURL"));
                    } else {
                        LastURL = rs.getString("domainURL");
                        Crawled.add(new Anchor(rs.getString("referrerURL"), rs.getString("domainURL")));
                        if(rs.getInt("highPriority") == 1)
                            Crawled.lastElement().setHighPriority();
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException e) {e.printStackTrace();}
            if (stmt != null) try { stmt.close(); } catch (SQLException e) {e.printStackTrace();}
            if (conn != null) try { conn.close(); } catch (SQLException e) {e.printStackTrace();}
        }
        return Crawled;
    }

    public Vector<Anchor> getCrawlingAnchors() throws PropertyVetoException, SQLException, IOException, ClassNotFoundException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sqlQuery = "SELECT dr.domainURL, dr.referrerURL, cp.highPriority FROM domain_referrer as dr, crawled_pages as cp WHERE cp.domainURL = dr.domainURL AND cp.isCrawled=0 ORDER BY domainURL;";
        Vector<Anchor> Crawling = new Vector<Anchor>();
        String LastURL = "";

        try {
            // fetch a connection
            conn = DataSource.getInstance().getConnection();

            if (conn != null) {
                stmt = conn.prepareStatement(sqlQuery);
                rs = stmt.executeQuery(sqlQuery);

                while(rs.next()) {
                    if (rs.getString("domainURL").equals(LastURL)) {
                        //Add referrerURL
                        Crawling.lastElement().addReferrerURL(rs.getString("referrerURL"));
                    } else {
                        LastURL = rs.getString("domainURL");
                        Crawling.add(new Anchor(rs.getString("referrerURL"), rs.getString("domainURL")));
                        if(rs.getBoolean("highPriority"))
                            Crawling.lastElement().setHighPriority();
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException e) {e.printStackTrace();}
            if (stmt != null) try { stmt.close(); } catch (SQLException e) {e.printStackTrace();}
            if (conn != null) try { conn.close(); } catch (SQLException e) {e.printStackTrace();}
        }
        return Crawling;
    }

    public synchronized void updateCrawledStatus(Anchor CrawledPage)
    {
        /* Update the crawled anchor to the database */
        executeNonQuery("UPDATE crawled_pages SET isCrawled = 1 WHERE domainURL = '" + CrawledPage.getAnchorURL() + "'");
    }

    public void InsertCrawling(Anchor CrawlingPage)
    {
        /* Inserts the crawled anchor to the database */
        for(String referrerURL : CrawlingPage.getReferrerURLs()){
            executeNonQuery( "INSERT INTO crawled_pages (domainURL) VALUES ('" + CrawlingPage.getAnchorURL() + "');");
        }
    }

    public synchronized void InsertReferrer(Anchor CrawlingPage)
    {
        /* Inserts the crawled anchor to the database */
        for(String referrerURL : CrawlingPage.getReferrerURLs()){
            executeNonQuery("INSERT INTO domain_referrer (domainURL, referrerURL) VALUES ('" + CrawlingPage.getAnchorURL() + "', '" + referrerURL + "');");
        }
    }

    public void removeLink(Anchor link){
        //executeNonQuery("DELETE FROM domain_referrer WHERE referrerURL = '" + link.getAnchorURL() + "';");
        executeNonQuery("DELETE FROM crawled_pages WHERE domainURL = '" + link.getAnchorURL() + " WHERE isCrawled=0;");
    }

    public void updatePriority(Anchor link){
        executeNonQuery("UPDATE crawled_pages SET highPriority = 1 WHERE domainURL = '" + link.getAnchorURL() + "';");
    }


    /* Ranker Methods */

    public double getPageRank(String URL) throws ClassNotFoundException, SQLException, PropertyVetoException, IOException {

        double result = 0.0;
        Vector<Object> Ranks = getColumnData("Value", "CALL PageRank('" + URL + "');");
        for(Object Rank :Ranks){
            result += (double) Rank;
        }
        return result;
    }

    public Vector<Page> getPages(Vector<String> Tokens){

        Vector<Page> Pages = new Vector<Page>();
        HashMap<String, Double> URL_Map = new HashMap<>();
        HashMap<String, Double> Rank_Map = new HashMap<>();
        HashMap<String, String> Title_Map = new HashMap<>();
        HashMap<String, String> Description_Map = new HashMap<>();

        for(String token : Tokens){
            Connection conn = null;
            PreparedStatement stmt = null;
            ResultSet rs = null;

            String sqlQuery = "Call Search('" + token + "')";

            try {
                // fetch a connection
                conn = DataSource.getInstance().getConnection();

                if (conn != null) {
                    stmt = conn.prepareStatement(sqlQuery);
                    rs = stmt.executeQuery(sqlQuery);

                    while(rs.next()) {
                        double TF_IDF = rs.getDouble("TF_IDF");
                        String URL = rs.getString("URL");
                        double PageRank = rs.getDouble("PageRank");
                        String Title = rs.getString("Title");
                        String Description = rs.getString("Description");
                        if(Description.equals("null"))
                            Description = null;

                        double OLD_TF_IDF = URL_Map.get(URL) == null?0:URL_Map.get(URL);

                        if(OLD_TF_IDF == 0){
                            URL_Map.put(URL, TF_IDF);
                            Rank_Map.put(URL, PageRank);
                            Title_Map.put(URL, Title);
                            Description_Map.put(URL, Description);
                        }
                        else
                            URL_Map.put(URL, TF_IDF + OLD_TF_IDF);
                    }
                }

            } catch (SQLException e) {
                e.printStackTrace();
            } catch (PropertyVetoException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (rs != null) try { rs.close(); } catch (SQLException e) {e.printStackTrace();}
                if (stmt != null) try { stmt.close(); } catch (SQLException e) {e.printStackTrace();}
                if (conn != null) try { conn.close(); } catch (SQLException e) {e.printStackTrace();}
            }
        }

        Utilities utl = new Utilities();

        for(String URL : URL_Map.keySet())
            Pages.add(new Page(URL, URL_Map.get(URL), Rank_Map.get(URL), Title_Map.get(URL), Description_Map.get(URL)));

        Utilities util = new Utilities();

        Pages = util.SortPages(Pages, "PageRank");
        Pages = util.SortPages(Pages, "TF_IDF");

        return Pages;
    }

    public Vector<String> getURLs() throws ClassNotFoundException, SQLException, PropertyVetoException, IOException {
        Vector<Object> URLs = getColumnData("domainURL", "SELECT domainURL FROM indexed_pages;");
        Vector<String> result = new Vector<>();
        for(Object URL :URLs){
            result.add((String)URL);
        }
        return result;
    }

}

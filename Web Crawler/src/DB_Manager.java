import com.mysql.jdbc.MySQLConnection;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.sql.*;
import java.util.List;
import java.util.Vector;
import com.jolbox.bonecp.BoneCPConfig;

public class DB_Manager {

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

    public Vector<Anchor> getCrawledAnchors() throws PropertyVetoException, SQLException, IOException, ClassNotFoundException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sqlQuery = "SELECT domainURL, referrerURL FROM domain_referrer WHERE isCrawled = 1";
        Vector<Anchor> Crawled = new Vector<Anchor>();
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
                        Crawled.lastElement().addReferrerURL(rs.getString("referrerURL"));
                    } else {
                        LastURL = rs.getString("domainURL");
                        Crawled.add(new Anchor(rs.getString("referrerURL"), rs.getString("domainURL")));
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

        String sqlQuery = "SELECT domainURL, referrerURL FROM domain_referrer WHERE isCrawled = 0";
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

    public synchronized void updateCrawledStatus(Anchor CrawledPage)
    {
        /* Update the crawled anchor to the database */
        for(String referrerURL : CrawledPage.getReferrerURLs())
            executeNonQuery("UPDATE domain_referrer SET isCrawled = 1 WHERE domainURL = '" + CrawledPage.getAnchorURL() + "' AND referrerURL = '" + referrerURL +"'");

    }

    public void InsertCrawling(Anchor CrawlingPage)
    {
        /* Inserts the crawled anchor to the database */
        for(String referrerURL : CrawlingPage.getReferrerURLs())
            executeNonQuery("INSERT INTO domain_referrer (domainURL, referrerURL) VALUES ('" + CrawlingPage.getAnchorURL() + "', '" + referrerURL + "')");

    }

    public void removeLink(Anchor link){
        executeNonQuery("DELETE FROM domain_referrer WHERE domainURL = '" + link.getAnchorURL() + "';");
    }
}

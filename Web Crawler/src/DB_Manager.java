import com.mysql.jdbc.MySQLConnection;
import com.mysql.jdbc.Statement;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import java.util.List;
import java.util.Vector;

public class DB_Manager 
{
    private MysqlDataSource dataSource = new MysqlDataSource();
    private Connection conn;
    public DB_Manager()
    {
        dataSource.setUser("root");
        dataSource.setPassword("usbw");
        dataSource.setServerName("localhost");
        dataSource.setPortNumber(3307);
        dataSource.setDatabaseName("WebCrawler");
    }

    public Vector<Object> getColumnData(int columnIndex, String sqlQuery) throws SQLException 
    {
        conn = dataSource.getConnection();
        Statement stmt = (Statement) conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ResultSet rs = stmt.executeQuery(sqlQuery);
        Vector<Object> objectVector = new Vector<Object>();
        while(rs.next()) 
        {
            objectVector.add(rs.getObject(columnIndex));
        }

        rs.close();
        stmt.close();
        conn.close();

        return objectVector;
    }

    public Vector<Object> getColumnData(String columnLabel, String sqlQuery) throws SQLException 
    {
        conn = dataSource.getConnection();
        Statement stmt = (Statement) conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ResultSet rs = stmt.executeQuery(sqlQuery);

        Vector<Object> objectVector = new Vector<Object>();

        while(rs.next()) 
        {
            objectVector.add(rs.getObject(columnLabel));
        }

        rs.close();
        stmt.close();
        conn.close();

        return objectVector;
    }
	
    public Vector<Anchor> getCrawledAnchors() throws SQLException 
    {
        conn = dataSource.getConnection();
        Statement stmt = (Statement) conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
	String sqlQuery = "SELECT domainURL, referrerURL FROM domain_referrer WHERE isCrawled = 1";
        ResultSet rs = stmt.executeQuery(sqlQuery);

        Vector<Anchor> Crawled = new Vector<Anchor>();
	String LastURL = "";
        while(rs.next()) 
        {
            if(rs.getString("domainURL").equals(LastURL))
            {
		//Add referrerURL
		Crawled.lastElement().addReferrerURL(rs.getString("referrerURL"));
            }
            else 
            {
                LastURL = rs.getString("domainURL");
                Crawled.add(new Anchor(rs.getString("referrerURL"), rs.getString("domainURL")));
            }
        }

        rs.close();
        stmt.close();
        conn.close();

        return Crawled;
    }

    public Vector<Anchor> getCrawlingAnchors() throws SQLException 
    {
        conn = dataSource.getConnection();
        Statement stmt = (Statement) conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        String sqlQuery = "SELECT domainURL, referrerURL FROM domain_referrer WHERE isCrawled = 0";
        ResultSet rs = stmt.executeQuery(sqlQuery);

        Vector<Anchor> Crawling = new Vector<Anchor>();
        String LastURL = "";
        while(rs.next()) 
        {
            if(rs.getString("domainURL") == LastURL)
            {
                //Add referrerURL
                Crawling.lastElement().addReferrerURL(rs.getString("referrerURL"));
            }
            else 
            {
                LastURL = rs.getString("domainURL");
                Crawling.add(new Anchor(rs.getString("domainURL"), rs.getString("referrerURL")));
            }
        }

        rs.close();
        stmt.close();
        conn.close();

        return Crawling;
    }

    public boolean executeNonQuery(String sqlQuery) 
    {
        try 
        {
            conn = dataSource.getConnection();
            Statement stmt = (Statement) conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            stmt.execute(sqlQuery);
            return true;
        }
        catch (SQLException ex) 
        {
            return false;
        }
    }

    public Object executeScalar(String sqlQuery) throws SQLException 
    {
        conn = dataSource.getConnection();
        Statement stmt = (Statement) conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ResultSet rs = stmt.executeQuery(sqlQuery);
        Object obj = null;
        if(rs.next())
            obj = rs.getObject(1);
        rs.close();
        stmt.close();
        conn.close();
        return obj;
    }
    
}
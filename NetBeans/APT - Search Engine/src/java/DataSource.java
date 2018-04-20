import java.beans.PropertyVetoException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;


public class DataSource {

    private static DataSource     datasource;
    private BoneCP connectionPool;

    private String Host = "localhost";
    private int Port = 3307;
    private String DB_Name = "webcrawler";
    private String Username = "root";
    private String Password = "usbw";

    private DataSource() throws IOException, SQLException, PropertyVetoException {
        try {
            // load the database driver (make sure this is in your classpath!)
            Class.forName("com.mysql.jdbc.Driver");
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        try {
            // setup the connection pool using BoneCP Configuration
            BoneCPConfig config = new BoneCPConfig();
            // jdbc url specific to your database, eg jdbc:mysql://127.0.0.1/yourdb
            config.setJdbcUrl("jdbc:mysql://" + Host + ":" + String.valueOf(Port) +"/" + DB_Name +"?useSSL=false");
            config.setUsername(Username);
            config.setPassword(Password);
            config.setMinConnectionsPerPartition(10);
            config.setMaxConnectionsPerPartition(20);
            config.setPartitionCount(10);
            config.setAcquireIncrement(10);
            // setup the connection pool
            connectionPool = new BoneCP(config);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

    }

    public static DataSource getInstance() throws IOException, SQLException, PropertyVetoException {
        if (datasource == null) {
            datasource = new DataSource();
            return datasource;
        } else {
            return datasource;
        }
    }

    public Connection getConnection() throws SQLException {
        return this.connectionPool.getConnection();
    }

}

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Vector;


public class PageRank extends Thread {

    private static Vector<String> URLs;
    private static DB_Manager DB_Man = new DB_Manager();
    private static double d = 0.85;

    public static void main(String[] args) throws ClassNotFoundException, SQLException, PropertyVetoException, IOException, InterruptedException {

        updateURLs();
        DB_Man.executeNonQuery("UPDATE indexed_pages SET PageRank=" + 1.0/URLs.size() + " WHERE PageRank = 0;");

        int k=0;

        for(int i=0; i<50; i++)
            new PageRank().start();

        while(true){
            if(URLs.size() == 0){
                updateURLs();
                k++;
                System.out.println("Finished iteration " + k);
            }
            Thread.sleep(1000);
            System.out.println("URLs Count: " + URLs.size());
        }
    }

    public synchronized String getURL(){
        String result = null;

        if(URLs.size() >0)
        {
            result = URLs.get(0);
            URLs.removeElementAt(0);
        }
        return result;
    }

    public static void updateURLs() throws ClassNotFoundException, SQLException, PropertyVetoException, IOException {
        URLs = DB_Man.getURLs();
    }

    @Override
    public void run(){

        while(true){

            String URL = getURL();
            if(getURL() != null)
            {
                try {
                    double Rank = (1-d) + d*(DB_Man.getPageRank(URL));
                    DB_Man.executeNonQuery("UPDATE indexed_pages SET PageRank = " + Rank + " WHERE domainURL = '" + URL +"';");

                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (PropertyVetoException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

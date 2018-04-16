
public class Page {
    private String URL;
    private String Title;
    private String Description;
    private double TF_IDF;
    private double PageRank;

    public String getDescription() {
        return Description;
    }

    public String getTitle() {
        return Title;
    }

    public String getURL() {
        return URL;
    }

    public double getTF_IDF(){
        return  TF_IDF;
    }

     public Page(String URL, double TF_IDF, double PageRank, String Title, String Description){
        this.URL = URL;
        this.TF_IDF = TF_IDF;
        this.PageRank = PageRank;
        this.Title = Title;
        this.Description = Description;
     }

    public double getPageRank() {
        return PageRank;
    }


    // This method is used for testing only.
    public String printInfo(){
        String result ="";
        result += "URL: " + URL;
        result += "\nTF IDF: " + TF_IDF;
        result += "\nRank: " + PageRank;
        result += "\nTitle: " + Title;
        result += "\nDescription: " + Description;
        result += "\n***********************************************\n";
        return result;
    }

}

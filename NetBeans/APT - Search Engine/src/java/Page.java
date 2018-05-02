
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
        String result ="<table>";
        if(Title.equals(""))
            result += "<tr><td><a href = '" +  URL + "'>" + URL + "</a></td></tr>";
        else
            result += "<tr><td><a href = '" +  URL + "'>" + Title + "</a></td></tr>";
        if(Description != null)
            result += "<tr><td><div>" + Description +"</div></td></tr>";
        result += "<tr><td style='color:green; font-style:italic'>" + URL + "</td></tr>";
        result += "</table><br>";
        return result;
    }

}

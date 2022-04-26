package VanMorrison;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


public class CSVReader {

    /*String fileName;
    public CSVReader(String fileName){
        this.fileName = fileName;
        readItemsFromCSV(fileName);
    }*/
    List<Item> items;

    public CSVReader(){
        items = readItemsFromCSV("src/main/resources/exampleList.csv");
    }

    public List<Item> getItemList(){
        return items;
    }

    private static List<Item> readItemsFromCSV(String fileName){
        List<Item> items = new ArrayList<Item>();
        Path pathToFile = Paths.get(fileName);

        try(BufferedReader br = Files.newBufferedReader(pathToFile, StandardCharsets.UTF_8)){
            String line = br.readLine();

            while(line != null){
                String[] attributes = line.split(",");

                Item item = createItem(attributes);

                items.add(item);

                line = br.readLine();
            }
        }catch(IOException ioe){
            ioe.printStackTrace();
        }

        return items;
    }

    private static Item createItem(String[] metadata){
        String artNr = metadata[0];
        String name = metadata[1];
        String price = metadata[2];

        return new Item(artNr,name,price);
    }

    public String printToString(){
        String s = generateList();
        return s;
    }

    private String generateList(){
        String s = "<br />" +
                "<div class='container'><caption>Tillg&auml;ngliga produkter</caption><table>" +
                "<thead><tr>" +
                generateTableHead() +
                "</tr></thead><tbody>" +
                generateTableRows() +
                "</tbody></table></div>";
        return s;
    }

    private String generateTableRows(){
        String s = "";
        for(int i = 1; i < getItemList().size(); i++){
            s += "<tr><td>" + getItemList().get(i).getArtNr() + "</td><td>" + getItemList().get(i).getName() + "</td><td>" + getItemList().get(i).getPrice() + "</td><td>" + "<button onClick='addItem(" + getItemList().get(i).getArtNr() + ")'>Lägg till</button>" + "</td></tr>";
        }
        return s;
    }

    private String generateTableHead(){
        String s = "<th>";
        s += getItemList().get(0).getArtNr() + "</th><th>" + getItemList().get(0).getName() + "</th><th>" + getItemList().get(0).getPrice() + "</th>";
        return s;
    }

    public String getStyle(){
        String s = "<style>" +
                "html,\n" +
                "body {\n" +
                "\theight: 100%;\n" +
                "}\n" +
                "\n" +
                "body {\n" +
                "\tmargin: 0;\n" +
                "\tbackground: linear-gradient(45deg, #49a09d, #5f2c82);\n" +
                "\tfont-family: sans-serif;\n" +
                "\tfont-weight: 100;\n" +
                "}\n" +
                "\n" +
                ".container {\n" +
                "\tposition: absolute;\n" +
                "\ttop: 50%;\n" +
                "\tleft: 50%;\n" +
                "\ttransform: translate(-50%, -50%);\n" +
                "}\n" +
                "\n" +
                "table {\n" +
                "\twidth: 800px;\n" +
                "\tborder-collapse: collapse;\n" +
                "\toverflow: hidden;\n" +
                "\tbox-shadow: 0 0 20px rgba(0,0,0,0.1);\n" +
                "}\n" +
                "\n" +
                "th,\n" +
                "td {\n" +
                "\tpadding: 15px;\n" +
                "\tbackground-color: rgba(255,255,255,0.2);\n" +
                "\tcolor: #fff;\n" +
                "}\n" +
                "\n" +
                "th {\n" +
                "\ttext-align: left;\n" +
                "}\n" +
                "\n" +
                "thead {\n" +
                "\tth {\n" +
                "\t\tbackground-color: #55608f;\n" +
                "\t}\n" +
                "}\n" +
                "\n" +
                "tbody {\n" +
                "\ttr {\n" +
                "\t\t&:hover {\n" +
                "\t\t\tbackground-color: rgba(255,255,255,0.3);\n" +
                "\t\t}\n" +
                "\t}\n" +
                "\ttd {\n" +
                "\t\tposition: relative;\n" +
                "\t\t&:hover {\n" +
                "\t\t\t&:before {\n" +
                "\t\t\t\tcontent: \"\";\n" +
                "\t\t\t\tposition: absolute;\n" +
                "\t\t\t\tleft: 0;\n" +
                "\t\t\t\tright: 0;\n" +
                "\t\t\t\ttop: -9999px;\n" +
                "\t\t\t\tbottom: -9999px;\n" +
                "\t\t\t\tbackground-color: rgba(255,255,255,0.2);\n" +
                "\t\t\t\tz-index: -1;\n" +
                "\t\t\t}\n" +
                "\t\t}\n" +
                "\t}\n" +
                "}" +
                "</style>";
        return s;
    }
}
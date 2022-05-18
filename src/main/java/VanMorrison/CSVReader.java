package VanMorrison;
import java.io.BufferedReader;
import java.io.FileReader;
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

    public CSVReader(String fileName){
        items = readItemsFromCSV(fileName);
    }

    public CSVReader(List<Item> items){

        this.items = items;
        //String[] meta = new String[]{"artNr","Namn","Pris"};
        this.items.add(0, createItem(new String[]{"artNr","Namn","Pris"}));
    }

    public List<Item> getItemList(){
        return items;
    }

    public List<Item> getItems(){
        return items.subList(1,items.size());
    }

    private static List<Item> readItemsFromCSV(String fileName){
        List<Item> items = new ArrayList<Item>();
        Path pathToFile = Paths.get(fileName);
        
        try(BufferedReader br = new BufferedReader(new FileReader(fileName));){
            String line = br.readLine();
            System.out.println(line);
            while(line != null){
                String[] attributes = line.split("[,;]");

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
                    " <table>" +
                "<thead><tr>" +
                generateTableHead() +
                "</tr></thead><tbody>" +
                generateTableRows() +
                "</tbody></table>";
        return s;
    }

    private String generateTableRows(){
        String s = "";
        for(int i = 1; i < getItemList().size(); i++){
            s += "<tr onClick='addItem(\"" + getItemList().get(i).getArtNr() + "\")'><td>" + getItemList().get(i).getArtNr() + "</td><td>" + getItemList().get(i).getName() + "</td><td>" + getItemList().get(i).getPrice() + "<td><button class='add-button' type='button'><i class='fa fa-shopping-cart' aria-hidden='true'></i></button></td>";
        }
        return s;
    }

    private String generateTableHead(){
        String s = "<th>";
        s += getItemList().get(0).getArtNr() + "</th><th>" + getItemList().get(0).getName() + "</th><th>" + getItemList().get(0).getPrice() + "</th><th></th>";
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
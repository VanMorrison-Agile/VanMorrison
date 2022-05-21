package VanMorrison;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


public class CSVReader {

    List<Item> items;

    /**
     * Reads a CSV file with a given path as a string
     * @param fileName the path to the CSV file
     */
    public CSVReader(String fileName){
        items = readItemsFromCSV(fileName);
    }

    /**
     * @param items list of items.
     */
    public CSVReader(List<Item> items){
        this.items = items;
        //String[] meta = new String[]{"artNr","Namn","Pris"};
        this.items.add(0, createItem(new String[]{"artNr","Namn","Pris"}));
    }

    /**
     * @return the complete list including the header information
     */
    public List<Item> getItemList(){
        return items;
    }

    /**
     * @return the itmes of a list, except the header information
     */
    public List<Item> getItems(){
        return items.subList(1,items.size());
    }

    /* Reads a CSV file and returns it as a list with the items with ArtNr,Name,Price */
    private static List<Item> readItemsFromCSV(String fileName){
        List<Item> items = new ArrayList<Item>();
        Path pathToFile = Paths.get(fileName);
        
        try(BufferedReader br = new BufferedReader(new FileReader(fileName));){
            String line = br.readLine();
            while(line != null){
                String[] attributes = line.split("[,;]"); // Split to a new item at , or ;

                Item item = createItem(attributes);

                items.add(item);

                line = br.readLine();
            }
        }catch(IOException ioe){
            ioe.printStackTrace();
        }

        return items;
    }

    /* Create the item metadata from the items read in the CSV file */
    private static Item createItem(String[] metadata){
        String artNr = metadata[0];
        String name = metadata[1];
        String price = metadata[2];

        return new Item(artNr,name,price);
    }

    /* Returns the list as a correct string */
    public String printToString(){
        String s = generateList();
        return s;
    }

    /* Generates a string of HTML with a correct table structure */
    /* Uses the methods generateTableRows() and generateTableHead() to input the items */
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

    /* Generates the rows of the table given a list of items */
    private String generateTableRows(){
        String s = "";
        for(int i = 1; i < getItemList().size(); i++){
            s += "<tr onClick='addItem(\"" + getItemList().get(i).getArtNr() + "\")'><td>" + getItemList().get(i).getArtNr() + "</td><td>" + getItemList().get(i).getName() + "</td><td>" + getItemList().get(i).getPrice() + "<td><button class='add-button' type='button'><i class='fa fa-shopping-cart' aria-hidden='true'></i></button></td>";
        }
        return s;
    }

    /* Generates the header of the table for a fixed metadata structure */
    private String generateTableHead(){
        String s = "<th>";
        s += getItemList().get(0).getArtNr() + "</th><th>" + getItemList().get(0).getName() + "</th><th>" + getItemList().get(0).getPrice() + "</th><th></th>";
        return s;
    }
}
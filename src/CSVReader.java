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
        items = readItemsFromCSV("src/resources/exampleList.csv");
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
}
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

    public static void main(String... args){
        List<Item> items = readItemsFromCSV("resources/exampleList.csv");

        for(Item i : items){
            System.out.println(i);
        }
    }

    private static List<Item> readItemsFromCSV(String fileName){
        List<Item> items = new ArrayList<Item>();
        Path pathToFile = Paths.get(fileName);

        try(BufferedReader br = Files.newBufferedReader(pathToFile, StandardCharsets.US_ASCII)){
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
        int price = Integer.parseInt(metadata[2]);

        return new Item(artNr,name,price);
    }
}
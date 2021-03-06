import VanMorrison.CSVReader;
import VanMorrison.Item;
import VanMorrison.Search;
import org.junit.Test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

public class SearchTest {
    @Test
    public void searchTest() {
        CSVReader csvReader = new CSVReader("provider/exampleList.csv");
        List<Item> csvContent = csvReader.getItemList();
        Search search = new Search(csvContent);
        List<Item> searchResult = new ArrayList<>(search.search("St"));
        for (Item item : searchResult) {
            System.out.println(item.getName());
        }
        assertEquals(searchResult.size(), 3);
    }
}

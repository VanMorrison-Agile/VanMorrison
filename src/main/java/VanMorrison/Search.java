package VanMorrison;

import java.util.ArrayList;
import java.util.List;

public class Search {

    private static List<Item> items;

    public Search(List<Item> items){
        this.items = items;
    }

    public Search(){

    }

    /**
     * A function to search a list of items with a string.
     * @param term The term for which we are searching.
     * @return a list of matching items to the given search term.
     */
    public static List<Item> search(String term){
        List<Item> matches = new ArrayList<>();

        //If the items' article number or name matches the search term: Add it to the list which contains the matching items.
        for (Item item : items) {
            if(item.getArtNr().contains(term) || item.getName().contains(term)){
                matches.add(item);
            }
        }

        return matches;
    }
}

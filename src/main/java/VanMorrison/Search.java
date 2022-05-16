package VanMorrison;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Search {

    private static List<Item> items;

    public Search(List<Item> items){
        this.items = items;
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
            if(item.getArtNr().contains(term) || Pattern.compile(Pattern.quote(term), Pattern.CASE_INSENSITIVE).matcher(item.getName()).find()){
                matches.add(item);
            }
        }
        //Sorts the items in alphabetical case-insensitive order
        matches.sort(Item.compare(term));
        
        return matches;
    }
}

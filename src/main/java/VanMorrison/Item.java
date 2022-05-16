package VanMorrison;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Item {
    private String artNr;
    private String name;
    private String price;

    public Item (String artNr, String name, String price){
        this.artNr = artNr;
        this.name = name;
        this.price = price;
    }

    public String getArtNr(){
        return artNr;
    }


    public String getName(){
        return name;
    }


    public String getPrice(){
        return price;
    }

    // Returns list of all values
    public List<String> getValues(){
        List<String> values = new ArrayList<String>();
        values.add(name);
        values.add(artNr);
        return values;
    }


    public String toString(){
        return "Item [article number = " + artNr + ", name = " + name + ", price = " + price + "]";
    }

    public static final Comparator<Item> byLexicographicOrder = (o1, o2) -> o1.getName().compareToIgnoreCase(o2.getName());

    public static Comparator<Item> compare(String term) {
        return (o1, o2) -> {
            int i1 = o1.getName().indexOf(term);
            int i2 = o2.getName().indexOf(term);

            if(i1 == i2) {
                return byLexicographicOrder.compare(o1,o2);
            }

            return Integer.compare(i1, i2);
        };
    }
}


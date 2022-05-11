package VanMorrison;

import java.util.ArrayList;
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

}


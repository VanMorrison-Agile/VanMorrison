package VanMorrison;
public class Item implements Comparable<Item> {
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


    public String toString(){
        return "Item [article number = " + artNr + ", name = " + name + ", price = " + price + "]";
    }

    /**
     * Compares the name of two items in case-insensitive alphabetical order
     * @param o item that will be compared with
     */
    @Override
    public int compareTo(Item o) {
        return this.getName().compareToIgnoreCase(o.getName());
    }
}


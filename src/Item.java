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


    public String toString(){
        return "Item [article number = " + artNr + ", name = " + name + ", price = " + price + "]";
    }

}


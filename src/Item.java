public class Item {
    private String artNumber;
    private String name;
    private String price;

    public Item (String artNumber, String name, String price){
        this.artNumber = artNumber;
        this.name = name;
        this.price = price;
    }

    public String getArtNumber(){
        return artNumber;
    }


    public String getName(){
        return name;
    }


    public String getPrice(){
        return price;
    }


    public String toString(){
        return "Item [article number = " + artNumber + ", name = " + name + ", price = " + price + "]";
    }

}


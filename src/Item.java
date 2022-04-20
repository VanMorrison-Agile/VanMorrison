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

    public void setArtnumber(String artNumber){
        this.artNumber = artNumber;
    }

    public String GetName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getPrice(){
        return price;
    }

    public void setPrice(String price){
        this.price = price;
    }

    public String toString(){
        return "Item [article number = " + artNumber + ", name = " + name + ", price = " + price + "]";
    }

}


public class Item {
    private String artNumber;
    private String name;
    private int price;

    public Item (String artNumber, String name, int price){
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

    public int getPrice(){
        return price;
    }

    public void setPrice(int price){
        this.price = price;
    }

    public String toString(){
        return "Item [article number = " + artNumber + "name = " + name + "price = " + price + "]";
    }

}


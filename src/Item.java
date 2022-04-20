public class Item {
    private int artNumber;
    private String name;
    private int price;

    public Item (int artNumber, String name, int price){
        this.artNumber = artNumber;
        this.name = name;
        this.price = price;
    }

    public int getArtNumber(){
        return artNumber;
    }

    public void setArtnumber(int artNumber){
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


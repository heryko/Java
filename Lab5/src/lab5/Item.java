package lab5;

public class Item {

    private int id;
    private int weight;
    private int value;


    public Item(int id, int value, int weight) {

        this.id = id;
        this.value = value;
        this.weight = weight;
    }

    public int getValue() {
        return value;
    }

    public int getWeight() {
        return weight;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "No: " + id + "v: " + value + "w " + weight;
    }
}

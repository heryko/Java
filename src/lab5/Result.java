package lab5;

import java.util.List;

public class Result {
    private List<Integer> itemsInKnapsack;
    private int totalValue;
    private int totalWeight;

    public Result(List<Integer> itemsInKnapsack, int totalValue, int totalWeight) {
        this.itemsInKnapsack = itemsInKnapsack;
        this.totalValue = totalValue;
        this.totalWeight = totalWeight;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Integer id : itemsInKnapsack) {
            sb.append("Lab5.Item ID: ").append(id).append("\n");
        }
        sb.append("Total Weight: ").append(totalWeight).append("\n");
        sb.append("Total Value: ").append(totalValue);
        return sb.toString();
    }
}
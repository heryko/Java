package lab5;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;



public class Problem {
    private int n;
    private int seed;
    private int lowerBound;
    private int upperBound;
    private List<Item> items;

    public Problem(int n, int seed, int lowerBound, int upperBound) {
        this.n = n;
        this.seed = seed;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.items = new ArrayList<>();
        Random random = new Random(seed);

        for (int i = 0; i < n; i++) {
            int value = random.nextInt(upperBound - lowerBound + 1) + lowerBound;
            int weight = random.nextInt(upperBound - lowerBound + 1) + lowerBound;
            items.add(new Item(i, value, weight));
        }
    }

    public Result solve(int capacity) {
        List<Item> sortedItems = new ArrayList<>(items);

        sortedItems.sort((a, b) -> Double.compare(
                (double) b.getValue() / b.getWeight(),
                (double) a.getValue() / a.getWeight()
        ));

        List<Integer> solutionIds = new ArrayList<>();
        int currentWeight = 0;
        int currentValue = 0;

        for (Item item : sortedItems) {
            while (currentWeight + item.getWeight() <= capacity) {
                solutionIds.add(item.getId());
                currentWeight += item.getWeight();
                currentValue += item.getValue();
            }
        }

        return new Result(solutionIds, currentValue, currentWeight);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Item item : items) {
            sb.append(item.toString()).append("\n");
        }
        return sb.toString();
    }
}
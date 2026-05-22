package lab5;


import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Give number of items:");
        int n = scanner.nextInt();

        System.out.println("Give seed:");
        int seed = scanner.nextInt();

        System.out.println("Give capacity:");
        int capacity = scanner.nextInt();

        Problem problem = new Problem(n, seed, 1, 10);
        System.out.println("\nGenerated items:");
        System.out.println(problem.toString());

        Result result = problem.solve(capacity);
        System.out.println("--- Result ---");
        System.out.println(result.toString());
    }
}
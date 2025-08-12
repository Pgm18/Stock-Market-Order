package com.stockmarket;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Market market = new Market();
        market.loadFromFiles();  // Load saved data on startup

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("Choose action: 1) Add Order  2) Cancel Order  3) Print Market  4) Exit");
            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1 -> {
                    System.out.print("Enter Ticker (e.g. AAPL): ");
                    String ticker = scanner.nextLine().trim();

                    System.out.print("Enter Type (BUY/SELL): ");
                    Order.Type type = Order.Type.valueOf(scanner.nextLine().trim().toUpperCase());

                    System.out.print("Enter Order Kind (LIMIT/MARKET): ");
                    Order.OrderKind kind = Order.OrderKind.valueOf(scanner.nextLine().trim().toUpperCase());

                    double price = 0;
                    if (kind == Order.OrderKind.LIMIT) {
                        System.out.print("Enter Price: ");
                        price = scanner.nextDouble();
                        scanner.nextLine(); // consume newline
                    }

                    System.out.print("Enter Quantity: ");
                    int qty = scanner.nextInt();
                    scanner.nextLine(); // consume newline

                    Order order = new Order(ticker, type, kind, price, qty);
                    market.addOrder(order);

                    System.out.println("Order added: " + order);
                }
                case 2 -> {
                    System.out.print("Enter Ticker of order to cancel: ");
                    String ticker = scanner.nextLine().trim();

                    System.out.print("Enter Order ID to cancel: ");
                    int orderId = scanner.nextInt();
                    scanner.nextLine(); // consume newline

                    market.cancelOrder(ticker, orderId);
                    System.out.println("Order canceled if found.");
                }
                case 3 -> market.printMarket();
                case 4 -> {
                    running = false;
                    market.saveToFiles(); // Save before exiting
                    System.out.println("Exiting and saving data.");
                }
                default -> System.out.println("Invalid choice. Try again.");
            }
        }

        scanner.close();
    }
}

package com.stockmarket;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.HashMap;
import java.util.Map;

public class Market {
    private Map<String, OrderBook> books = new HashMap<>();

    private static final String ORDERS_FILE = "orders.txt";
    private static final String TRADES_FILE = "trades.txt";

    public void addOrder(Order order) {
        books.putIfAbsent(order.getTicker(), new OrderBook(order.getTicker()));
        books.get(order.getTicker()).addOrder(order);
    }

    public void cancelOrder(String ticker, int orderId) {
        if (books.containsKey(ticker)) {
            books.get(ticker).cancelOrder(orderId);
        }
    }

    public void printMarket() {
        for (OrderBook book : books.values()) {
            book.printOrderBook();
        }
    }

    // -------- File saving/loading methods --------

    public void saveToFiles() {
        try (BufferedWriter orderWriter = Files.newBufferedWriter(Paths.get(ORDERS_FILE));
             BufferedWriter tradeWriter = Files.newBufferedWriter(Paths.get(TRADES_FILE))) {

            for (OrderBook book : books.values()) {
                for (Order o : book.getBuyOrders()) {
                    orderWriter.write(orderToLine(o));
                    orderWriter.newLine();
                }
                for (Order o : book.getSellOrders()) {
                    orderWriter.write(orderToLine(o));
                    orderWriter.newLine();
                }
                for (String tradeLine : book.getTradeHistoryRawLines()) {
                    tradeWriter.write(tradeLine);
                    tradeWriter.newLine();
                }
            }
        } catch (IOException e) {
            System.out.println("Error saving files: " + e.getMessage());
        }
    }

    public void loadFromFiles() {
        if (Files.exists(Paths.get(ORDERS_FILE))) {
            try (BufferedReader orderReader = Files.newBufferedReader(Paths.get(ORDERS_FILE))) {
                String line;
                while ((line = orderReader.readLine()) != null) {
                    Order o = lineToOrder(line);
                    if (o != null) addOrder(o);
                }
            } catch (IOException e) {
                System.out.println("Error loading orders: " + e.getMessage());
            }
        }

        if (Files.exists(Paths.get(TRADES_FILE))) {
            try (BufferedReader tradeReader = Files.newBufferedReader(Paths.get(TRADES_FILE))) {
                String line;
                while ((line = tradeReader.readLine()) != null) {
                    String ticker = line.split(",")[0];
                    if (books.containsKey(ticker)) {
                        books.get(ticker).addTradeHistoryRawLine(line);
                    }
                }
            } catch (IOException e) {
                System.out.println("Error loading trades: " + e.getMessage());
            }
        }
    }

    private String orderToLine(Order o) {
        return String.join(",",
                o.getTicker(),
                o.getType().toString(),
                o.getKind().toString(),
                String.valueOf(o.getPrice()),
                String.valueOf(o.getQuantity()),
                String.valueOf(o.getId())
        );
    }

    private Order lineToOrder(String line) {
        try {
            String[] parts = line.split(",");
            String ticker = parts[0];
            Order.Type type = Order.Type.valueOf(parts[1]);
            Order.OrderKind kind = Order.OrderKind.valueOf(parts[2]);
            double price = Double.parseDouble(parts[3]);
            int qty = Integer.parseInt(parts[4]);
            int id = Integer.parseInt(parts[5]);

            Order o = new Order(ticker, type, kind, price, qty);
            o.setId(id);
            return o;
        } catch (Exception e) {
            System.out.println("Error parsing order line: " + line);
            return null;
        }
    }
}
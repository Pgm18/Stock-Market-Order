package com.stockmarket;

import java.util.*;

public class OrderBook {
    private String ticker;
    private PriorityQueue<Order> buyOrders;
    private PriorityQueue<Order> sellOrders;
    private List<String> tradeHistory = new ArrayList<>();

    public OrderBook(String ticker) {
        this.ticker = ticker;

        buyOrders = new PriorityQueue<>((a, b) -> Double.compare(b.getPrice(), a.getPrice())); // high first
        sellOrders = new PriorityQueue<>(Comparator.comparingDouble(Order::getPrice)); // low first
    }

    public void addOrder(Order order) {
        if (order.getKind() == Order.OrderKind.MARKET) {
            executeMarketOrder(order);
        } else {
            if (order.getType() == Order.Type.BUY) buyOrders.add(order);
            else sellOrders.add(order);
            matchOrders();
        }
    }

    private void executeMarketOrder(Order order) {
        if (order.getType() == Order.Type.BUY) {
            while (order.getQuantity() > 0 && !sellOrders.isEmpty()) {
                Order sell = sellOrders.poll();
                int tradedQty = Math.min(order.getQuantity(), sell.getQuantity());
                order.setQuantity(order.getQuantity() - tradedQty);
                sell.setQuantity(sell.getQuantity() - tradedQty);
                tradeHistory.add("Market Trade: BUY " + tradedQty + " @ " + sell.getPrice());

                if (sell.getQuantity() > 0) sellOrders.add(sell);
            }
        } else {
            while (order.getQuantity() > 0 && !buyOrders.isEmpty()) {
                Order buy = buyOrders.poll();
                int tradedQty = Math.min(order.getQuantity(), buy.getQuantity());
                order.setQuantity(order.getQuantity() - tradedQty);
                buy.setQuantity(buy.getQuantity() - tradedQty);
                tradeHistory.add("Market Trade: SELL " + tradedQty + " @ " + buy.getPrice());

                if (buy.getQuantity() > 0) buyOrders.add(buy);
            }
        }
    }

    public void cancelOrder(int orderId) {
        buyOrders.removeIf(o -> o.getId() == orderId);
        sellOrders.removeIf(o -> o.getId() == orderId);
    }

    private void matchOrders() {
        while (!buyOrders.isEmpty() && !sellOrders.isEmpty() &&
                buyOrders.peek().getPrice() >= sellOrders.peek().getPrice()) {

            Order buy = buyOrders.poll();
            Order sell = sellOrders.poll();
            int tradedQty = Math.min(buy.getQuantity(), sell.getQuantity());
            double tradePrice = sell.getPrice();

            tradeHistory.add("Trade: " + ticker + " - " + tradedQty + " @ " + tradePrice);

            buy.setQuantity(buy.getQuantity() - tradedQty);
            sell.setQuantity(sell.getQuantity() - tradedQty);

            if (buy.getQuantity() > 0) buyOrders.add(buy);
            if (sell.getQuantity() > 0) sellOrders.add(sell);
        }
    }

    public void printOrderBook() {
        System.out.println("================== " + ticker + " ORDER BOOK ==================");
        System.out.printf("%-20s %-20s\n", "BUY ORDERS", "SELL ORDERS");
        System.out.printf("%-6s %-8s    %-8s %-6s\n", "Qty", "Price", "Price", "Qty");

        Iterator<Order> buys = buyOrders.iterator();
        Iterator<Order> sells = sellOrders.iterator();
        while (buys.hasNext() || sells.hasNext()) {
            String buyStr = buys.hasNext() ?
                    String.format("%-6d %-8.2f", buys.next().getQuantity(), buyOrders.peek().getPrice()) :
                    String.format("%-6s %-8s", "", "");
            String sellStr = sells.hasNext() ?
                    String.format("%-8.2f %-6d", sellOrders.peek().getPrice(), sells.next().getQuantity()) :
                    String.format("%-8s %-6s", "", "");
            System.out.printf("%-20s %-20s\n", buyStr, sellStr);
        }

        System.out.println("---------------------------------------------------");
        System.out.println("TRADE HISTORY:");
        tradeHistory.forEach(System.out::println);
        System.out.println();
    }

    // Getter methods for Market.java's file saving/loading
    public Collection<Order> getBuyOrders() {
        return buyOrders;
    }

    public Collection<Order> getSellOrders() {
        return sellOrders;
    }

    public List<String> getTradeHistoryRawLines() {
        return tradeHistory;
    }

    // To add trade lines loaded from file
    public void addTradeHistoryRawLine(String line) {
        tradeHistory.add(line);
    }
}
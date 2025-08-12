package com.stockmarket;

public class Order {
    public enum Type { BUY, SELL }
    public enum OrderKind{LIMIT, MARKET}

    private static int counter = 0;

    private int id;
    private String ticker;
    private Type type;
    private OrderKind kind;
    private double price;
    private int quantity;

    public Order(String ticker, Type type, OrderKind kind, double price, int quantity) {
        this.id = ++counter;
        this.ticker = ticker;
        this.type = type;
        this.kind = kind;
        this.price = price;
        this.quantity = quantity;
    }

    public void setId(int id) {
        this.id=id;
    }

    public String getTicker() {
        return ticker;
    }

    public int getId() {
        return id;
    }

    public Type getType() {
        return type;
    }

    public OrderKind getKind() {
        return kind;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    // This is the method your compiler is complaining about
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return String.format("%-6s %-4s Price: %.2f Qty: %d - ID: %d",
                ticker, type, price, quantity, id);
    }
}
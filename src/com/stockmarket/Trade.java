package com.stockmarket;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Trade {
    private String ticker;
    private int buyId;
    private int sellId;
    private int quantity;
    private double price;
    private String time;

    public Trade(String ticker, int buyId, int sellId, int quantity, double price) {
        this.ticker = ticker;
        this.buyId = buyId;
        this.sellId = sellId;
        this.quantity = quantity;
        this.price = price;
        this.time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

    public double getPrice() {
        return price;
    }

    public String toFormattedString() {
        return String.format("%-8s %-6d %-6d %-6d %-8.2f %s",
                ticker, buyId, sellId, quantity, price, time);
    }
}
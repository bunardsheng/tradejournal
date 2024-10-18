package com.tradejournal.buntradejournal.util;
import com.tradejournal.buntradejournal.model.Trade;
import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.List;

public class OptionTradeTracker {
    // create the hashmap
    // functions for each trade
    private static class Position {
        LocalDate buyDate;
        LocalDate sellDate;
        int quantity;
        BigDecimal avgPrice;
        BigDecimal totalCost;
        BigDecimal totalProfit;
        

        // sets up the current position and initilizes
        Position(Trade trade) {
            this.buyDate = trade.getActivityDate();
            this.quantity = trade.getQuantity();
            this.avgPrice = trade.getContractPrice();
            this.totalCost = trade.getAmount();
            this.totalProfit = BigDecimal.ZERO;
    
        }

        // updates
        void updateBuy(Trade trade) {
            int newQuantity = this.quantity + trade.getQuantity();
            this.totalCost = this.totalCost.add(trade.getAmount());
            this.avgPrice = this.totalCost.divide(BigDecimal.valueOf(newQuantity));
            this.quantity = trade.getQuantity();
            if (trade.getActivityDate().isBefore(this.buyDate)) {
                this.buyDate = trade.getActivityDate();
            }
        }

        void updateSell(Trade trade) {
            this.quantity -= trade.getQuantity();
            // if total profit is null, set simply to the current sell value, otherwise, add the profit to previous sell
            this.totalProfit = (this.totalProfit == null) ? trade.getAmount().abs() : this.totalProfit.add(trade.getAmount().abs()); 
            this.sellDate = trade.getActivityDate();
        }

        boolean isClosed() {
            return this.quantity == 0;
        }

        Map<String, Object> getTradeDetails() {
            Map<String, Object> posDetailsMap = new HashMap<>();
            posDetailsMap.put("firstBuyDate", this.buyDate);
            posDetailsMap.put("lastTradeDate", this.sellDate);
            posDetailsMap.put("duration", this.sellDate.toEpochDay() - this.buyDate.toEpochDay());
            posDetailsMap.put("initialQuantity", this.quantity);
            posDetailsMap.put("averageBuyPrice", this.avgPrice);
            posDetailsMap.put("totalBuyCost", this.totalCost);
            posDetailsMap.put("realizedProfit", this.totalProfit);
            posDetailsMap.put("isOpen", this.quantity > 0);
            return posDetailsMap;

        }

        


        // need a way to return the closed trades

    }
    
    private Map<String, Position> positions = new HashMap<>();
    private List<Map<String, Object>> closedTrades = new ArrayList<>();
    // a function to process each trade
    // void function
    public void processTrade(Trade trade) {
        String key = getPositionKey(trade);
        
        if (trade.getTransactionCode().equals("BTO")) {
            positions.computeIfAbsent(key, k -> new Position(trade)).updateBuy(trade);
        } else if (trade.getTransactionCode().equals("STC")) {
            Position position = positions.get(key);
            if (position == null) {
                throw new IllegalStateException("Attempting to sell a non-existent position: " + key);
            }
            position.updateSell(trade);
            if (position.isClosed()) {
                closedTrades.add(position.getTradeDetails());
                positions.remove(key);
            }
        }
    }

    private String getPositionKey(Trade trade) {
        return String.format("%s_%s_%s_%s", 
            trade.getInstrument(), 
            trade.getExpiryDate(), 
            trade.getOptionType(), 
            trade.getStrikePrice());
    }

    public List<Map<String, Object>> getOpenPositions() {
        return positions.values().stream()
            .map(Position::getTradeDetails)
            .collect(Collectors.toList());
    }

    public List<Map<String, Object>> getClosedTrades() {
        return new ArrayList<>(closedTrades);
    }

    public List<Map<String, Object>> getAllTrades() {
        List<Map<String, Object>> allTrades = new ArrayList<>(getClosedTrades());
        allTrades.addAll(getOpenPositions());
        return allTrades;
    }
}

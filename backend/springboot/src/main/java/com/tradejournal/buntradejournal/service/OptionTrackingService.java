package com.tradejournal.buntradejournal.service;

import com.tradejournal.buntradejournal.model.Trade;
import com.tradejournal.buntradejournal.util.OptionTradeTracker;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.List;

@Service
public class OptionTrackingService {
    private final OptionTradeTracker tracker;

    public OptionTrackingService() {
        this.tracker = new OptionTradeTracker();
    }

    public void processTrade(Trade trade) {
        tracker.processTrade(trade);
    }

    public List<Map<String, Object>> getOpenPositions() {
        return tracker.getOpenPositions();
    }

    public List<Map<String, Object>> getClosedTrades() {
        return tracker.getClosedTrades();
    }

    // You can add more methods here to expose specific functionality or analytics
    // For example:
    public int getOpenPositionCount() {
        return tracker.getOpenPositions().size();
    }

    public int getClosedTradeCount() {
        return tracker.getClosedTrades().size();
    }

    // Add any other business logic or data processing methods as needed
}
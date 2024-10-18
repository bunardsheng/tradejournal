package com.tradejournal.buntradejournal.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.tradejournal.buntradejournal.service.TradeService;
import com.tradejournal.buntradejournal.service.OptionTrackingService;
import com.tradejournal.buntradejournal.dto.TradeDTO;
import com.tradejournal.buntradejournal.model.Trade;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/trades")
public class TradeController {


    private static final Logger logger = LoggerFactory.getLogger(TradeController.class);

    @Autowired
    private TradeService tradeService;

    @Autowired
    private OptionTrackingService optionTrackingService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadTrades(@RequestBody List<TradeDTO> tradeDTOs) {
        try {
            List<Trade> savedTrades = tradeService.convertAndSaveTrades(tradeDTOs);
            logger.info("Trades saved successfully. Count: {}", savedTrades.size());

            for (Trade trade : savedTrades) {
                optionTrackingService.processTrade(trade);
                logger.debug("Processed trade: {}", trade);
            } 




            return new ResponseEntity<>("Trades uploaded successfully!", HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error uploading trades", e);
            return new ResponseEntity<>("Error uploading trades: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/sorted-by-expiry")
    public List<Trade> getAllTradesSortedByExpiryDate() {
        return tradeService.getAllTradesSortedByExpiryDate();
    }


    @GetMapping("/by-instrument/{instrument}")
    public List<Trade> getTradesByInstrument(@PathVariable String instrument) {
        return tradeService.getTradesByInstrument(instrument);
    }

    @GetMapping("/by-option-type/{optionType}")
    public List<Trade> getTradesByOptionType(@PathVariable String optionType) {
        return tradeService.getTradesByOptionType(optionType);
    }

  
    @GetMapping("/active")
    public List<Trade> getActiveTradesSortedByExpiryDate() {
        return tradeService.getActiveTradesSortedByExpiryDate();
    }

    @GetMapping("/open-positions")
    public List<Map<String, Object>> getOpenPositions() {
        return optionTrackingService.getOpenPositions();
    }
}
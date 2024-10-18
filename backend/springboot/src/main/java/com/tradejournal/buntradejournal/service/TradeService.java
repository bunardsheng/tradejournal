
package com.tradejournal.buntradejournal.service;

import com.tradejournal.buntradejournal.repository.TradeRepository;
import com.tradejournal.buntradejournal.dto.TradeDTO;
import com.tradejournal.buntradejournal.model.Trade;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

import java.util.stream.Collectors;

@Service
@Slf4j
public class TradeService {

    @Autowired
    private TradeRepository tradeRepository;

    public List<Trade> convertAndSaveTrades(List<TradeDTO> tradeDTOs) {
        List<Trade> trades = tradeDTOs.stream()
            .map(dto -> {
                try {
                    return Trade.fromDTO(dto);
                } catch (Exception e) {
                    log.error("Error converting DTO to Trade: {}", dto, e);
                    throw new IllegalArgumentException("Invalid trade data: " + e.getMessage(), e);
                }
            })
            .collect(Collectors.toList());
        
        return tradeRepository.saveAll(trades);
    }

    public List<Trade> getAllTradesSortedByExpiryDate() {
        return tradeRepository.findAll(Sort.by(Sort.Direction.ASC, "expiryDate"));
    }

   
    public List<Trade> getTradesByInstrument(String instrument) {
        return tradeRepository.findByInstrument(instrument, Sort.by(Sort.Direction.ASC, "activityDate"));
    }

    public List<Trade> getTradesByOptionType(String optionType) {
        return tradeRepository.findByOptionType(optionType, Sort.by(Sort.Direction.ASC, "activityDate"));
    }

   

    public List<Trade> getActiveTradesSortedByExpiryDate() {
        return tradeRepository.findByExpiryDateAfter(LocalDate.now(), Sort.by(Sort.Direction.ASC, "expiryDate"));
    }
}
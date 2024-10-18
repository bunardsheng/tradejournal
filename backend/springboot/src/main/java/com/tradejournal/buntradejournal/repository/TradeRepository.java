package com.tradejournal.buntradejournal.repository;

import com.tradejournal.buntradejournal.dto.TradeDTO;
import com.tradejournal.buntradejournal.model.Trade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import org.springframework.data.domain.Sort;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface TradeRepository extends JpaRepository<Trade, Long> {
    List<Trade> findAll(Sort sort);
    List<Trade> findByExpiryDateAfter(LocalDate date, Sort sort);
    List<Trade> findByOptionType(String optionType, Sort sort);
    List<Trade> findByInstrument(String instrument, Sort sort);
   
}

package com.tradejournal.buntradejournal.model;

import com.tradejournal.buntradejournal.dto.TradeDTO;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.NoArgsConstructor;

@Entity
@Slf4j
@Table(name = "trades")
@Getter @Setter @NoArgsConstructor
public class Trade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "M/d/yyyy")
    private LocalDate activityDate;

    @Column(nullable = false)
    private String instrument;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String transactionCode;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal contractPrice;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    // Extracted fields
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "M/d/yyyy")
    @Column(nullable = false)
    private LocalDate expiryDate;

    @Column(nullable = false)
    private String optionType;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal strikePrice;

    // Constructor that takes string inputs and parses them
    public static Trade fromDTO(TradeDTO dto) {
        
        Trade trade = new Trade();
        trade.setActivityDate(LocalDate.parse(dto.getActivityDate(), DateTimeFormatter.ofPattern("M/d/yyyy")));
        trade.setInstrument(dto.getInstrument());
        trade.setDescription(dto.getDescription());
        trade.setTransactionCode(dto.getTransactionCode());
        trade.setQuantity(Integer.parseInt(dto.getQuantity()));
        trade.setContractPrice(new BigDecimal(dto.getContractPrice().replace("$", "").replace(",", "")));
        trade.setAmount(new BigDecimal(dto.getAmount().replace("$", "").replace(",", "").replace("(", "-").replace(")", "")));
        trade.parseDescription();
        return trade;
    }

    private void parseDescription() {
        String[] parts = this.description.split("\\s+");
        if (parts.length != 4) {
            log.error("Invalid description format: {}", this.description);
            throw new IllegalArgumentException("Invalid description format: " + this.description);
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
        try {
            this.expiryDate = LocalDate.parse(parts[1], formatter);
        } catch (DateTimeParseException e) {
            log.error("Failed to parse expiry date: {}", parts[1], e);
            throw new IllegalArgumentException("Invalid expiry date format: " + parts[1], e);
        }
        this.optionType = parts[2];
        try {
            this.strikePrice = new BigDecimal(parts[3].replace("$", ""));
        } catch (NumberFormatException e) {
            log.error("Failed to parse strike price: {}", parts[3], e);
            throw new IllegalArgumentException("Invalid strike price format: " + parts[3], e);
        }
    }
}
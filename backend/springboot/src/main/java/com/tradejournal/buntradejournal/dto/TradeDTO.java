package com.tradejournal.buntradejournal.dto;
import lombok.Getter;
import lombok.Setter;


@Getter @Setter
public class TradeDTO {
    private String activityDate;
    private String instrument;
    private String description;
    private String transactionCode;
    private String quantity;
    private String contractPrice;
    private String amount;
}

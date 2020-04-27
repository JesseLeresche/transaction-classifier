package za.co.leresche.jesse.ocrdemo.model;

import com.univocity.parsers.annotations.Parsed;
import lombok.Data;

@Data
public class TransactionHistory {

    @Parsed(field = "Date")
    private String date;

    @Parsed(field = "Amount")
    private Double amount;

    @Parsed(field = "Balance")
    private Double balance;

    @Parsed(field = "Description")
    private String description;
}

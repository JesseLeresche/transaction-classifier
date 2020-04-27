package za.co.leresche.jesse.ocrdemo.model;

import com.univocity.parsers.annotations.Parsed;
import lombok.Data;

import java.util.List;

@Data
public class CategoryMap {

    @Parsed(field = "Category")
    private String category;

    @Parsed(field = "Keywords")
    private String rawKeywords;

    private List<String> keywords;

    private Double total = 0.0;
}

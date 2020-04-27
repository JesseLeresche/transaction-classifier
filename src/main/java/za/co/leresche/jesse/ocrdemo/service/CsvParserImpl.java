package za.co.leresche.jesse.ocrdemo.service;

import com.univocity.parsers.common.processor.BeanListProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import za.co.leresche.jesse.ocrdemo.model.CategoryMap;
import za.co.leresche.jesse.ocrdemo.model.TransactionHistory;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class CsvParserImpl implements CommandLineRunner {

    @Override
    public void run(String... args) {
        String transactionHistoryFilePath = args[0];
        String categoriesFilePath = args[1];
        log.info("Filepath {}", transactionHistoryFilePath);
        List<TransactionHistory> transactionHistoryList = getTransactionHistories(transactionHistoryFilePath);
        List<CategoryMap> categoryMaps = getCategories(categoriesFilePath);

        Map<Boolean, List<TransactionHistory>> collect = transactionHistoryList.stream().collect(Collectors.partitioningBy(o -> o.getAmount() > 0));
        ArrayList<List<TransactionHistory>> lists = new ArrayList<>(collect.values());
        List<TransactionHistory> credits = lists.get(0);
        credits.forEach(transactionHistory -> log.info("Credit {}", transactionHistory));
        List<TransactionHistory> debits = lists.get(1);
        debits.forEach(transactionHistory -> log.info("Debit {}", transactionHistory));

        categoryMaps.forEach(categoryMap -> filterCategory(credits, categoryMap));

        credits.forEach(credit -> log.info("Remaining {}", credit.toString()));
        CategoryMap otherCategoryMap = new CategoryMap();
        otherCategoryMap.setCategory("Other");
        otherCategoryMap.setRawKeywords("Other");
        otherCategoryMap.setKeywords(Collections.singletonList("Other"));
        calculateFields(otherCategoryMap, credits);
        Double total = categoryMaps.stream().reduce((categoryMap, categoryMap2) -> {
            CategoryMap categoryMapTotal = new CategoryMap();
            categoryMapTotal.setTotal(categoryMap.getTotal() + categoryMap2.getTotal());
            return categoryMapTotal;
        }).get().getTotal();
        log.info("Total Spend {}", total);
    }

    private void filterCategory(List<TransactionHistory> credits, CategoryMap categoryMap) {
        List<TransactionHistory> categoryRecords = credits.stream().filter(transactionHistory -> categoryMap.getKeywords().stream().anyMatch(s -> StringUtils.containsIgnoreCase(transactionHistory.getDescription(), s))).collect(Collectors.toList());

        calculateFields(categoryMap, categoryRecords);

        credits.removeAll(categoryRecords);
    }

    private void calculateFields(CategoryMap categoryMap, List<TransactionHistory> categoryRecords) {
        categoryRecords.stream().forEach(transactionHistory -> {
            categoryMap.setTotal(categoryMap.getTotal() + transactionHistory.getAmount());
        });
        log.info("Total spent at {}: {}", categoryMap.getCategory(), categoryMap.getTotal());
    }

    private List<CategoryMap> getCategories(String categoriesFilePath) {
        BeanListProcessor<CategoryMap> categoryMapBeanListProcessor = new BeanListProcessor<>(CategoryMap.class);

        CsvParserSettings settings = new CsvParserSettings();
        settings.setHeaderExtractionEnabled(true);
        settings.setRowProcessor(categoryMapBeanListProcessor);

        CsvParser csvParser = new CsvParser(settings);
        csvParser.parse(new File(categoriesFilePath));
        List<CategoryMap> categoryMaps = categoryMapBeanListProcessor.getBeans();
        categoryMaps.forEach(categoryMap -> categoryMap.setKeywords(Arrays.stream(categoryMap.getRawKeywords().split("\\|")).collect(Collectors.toList())));
        return categoryMaps;
    }

    private List<TransactionHistory> getTransactionHistories(String filePath) {
        BeanListProcessor<TransactionHistory> transactionHistoryBeanListProcessor = new BeanListProcessor<>(TransactionHistory.class);

        CsvParserSettings settings = new CsvParserSettings();
        settings.setHeaderExtractionEnabled(true);
        settings.setRowProcessor(transactionHistoryBeanListProcessor);

        CsvParser csvParser = new CsvParser(settings);
        csvParser.parse(new File(filePath));

        return transactionHistoryBeanListProcessor.getBeans();
    }
}

package za.co.leresche.jesse.ocrdemo;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class TransactionClassifierApplicationTests {

    @Test
    void testContainsIgnoreCase() {
        String str = "39.60 STEAMGAMES.CO 410588*0030  05 APR";
        String steam = "STEAM";
        String oculus = "Oculus";
        List<String> strings = new ArrayList<>();
        strings.add(steam);
        strings.add(oculus);
        boolean b = strings.stream().anyMatch(s -> StringUtils.containsIgnoreCase(str, s));
//        boolean containsIgnoreCase = StringUtils.containsIgnoreCase(str, searchStr);
        assertTrue(b);
    }
}

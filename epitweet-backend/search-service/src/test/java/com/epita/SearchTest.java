package com.epita;

import com.epita.service.SearchService;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import jakarta.inject.Inject;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class SearchTest {

    @Inject
    SearchService searchService;

    @Test
    void testTokenizeText() {
        String input = "Hello, World!";
        List<String> result = searchService.tokenizeText(input);
        assertEquals("hello world", result);
    }
}

package com.epita;

import com.epita.service.SearchService;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import jakarta.inject.Inject;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class TokenizerTest {

    @Inject
    SearchService searchService;

    @Test
    void testSimpleText() {
        String input = "Hello World";
        List<String> expected = List.of("hello", "world");
        assertEquals(expected, searchService.tokenizeText(input));
    }

    @Test
    void testPunctuation() {
        String input = "Hello, world. This is Quarkus!";
        List<String> expected = List.of("hello", "world", "this", "is", "quarkus");
        assertEquals(expected, searchService.tokenizeText(input));
    }

    @Test
    void testAccentsAndUnicode() {
        String input = "Café déjà vu naïve façade";
        List<String> expected = List.of("cafe", "deja", "vu", "naive", "facade");
        assertEquals(expected, searchService.tokenizeText(input));
    }

    @Test
    void testMixedHashtags() {
        String input = "#Hello #World This is #Quarkus.";
        List<String> expected = List.of("#Hello", "#World", "this", "is", "#Quarkus");
        assertEquals(expected, searchService.tokenizeText(input));
    }

    @Test
    void testHashtagVsWord() {
        String input = "Testing #word and word side by side.";
        List<String> expected = List.of("testing", "#word", "and", "word", "side", "by", "side");
        assertEquals(expected, searchService.tokenizeText(input));
    }

    @Test
    void testOnlySpecialCharacters() {
        String input = "@$%!&*()";
        List<String> expected = List.of();
        assertEquals(expected, searchService.tokenizeText(input));
    }

    @Test
    void testMultipleSpacesAndTabs() {
        String input = "This   is\t\t\tmessy   text.";
        List<String> expected = List.of("this", "is", "messy", "text");
        assertEquals(expected, searchService.tokenizeText(input));
    }

    @Test
    void testEmojiHandling() {
        String input = "I love Java 😍 and Quarkus 💚!";
        List<String> expected = List.of("i", "love", "java", "and", "quarkus");
        assertEquals(expected, searchService.tokenizeText(input));
    }

    @Test
    void testHashtagCaseSensitivity() {
        String input = "Exploring #Java, #JAVA and java today!";
        List<String> expected = List.of("exploring", "#Java", "#JAVA", "and", "java", "today");
        assertEquals(expected, searchService.tokenizeText(input));
    }

    @Test
    void testAccentsAndMixedInput() {
        String input = "C'était l'été à São Paulo, avec #Été2024!";
        List<String> expected = List.of("c", "etait", "l", "ete", "a", "sao", "paulo", "avec", "#Été2024");
        assertEquals(expected, searchService.tokenizeText(input));
    }

    @Test
    void testHashtagWithPunctuationAttached() {
        String input = "Stay safe. #Health! #Well-being, #Fitness...";
        List<String> expected = List.of("stay", "safe", "#Health", "#Wellbeing", "#Fitness");
        assertEquals(expected, searchService.tokenizeText(input));
    }

    @Test
    void testMessyWhitespaces() {
        String input = "  This\tis   a\n#Test\t\t\tcase ";
        List<String> expected = List.of("this", "is", "a", "#Test", "case");
        assertEquals(expected, searchService.tokenizeText(input));
    }

    @Test
    void testEmojiAndSymbolRemoval() {
        String input = "Great job 💪🔥 #Motivation!!! $$$";
        List<String> expected = List.of("great", "job", "#Motivation");
        assertEquals(expected, searchService.tokenizeText(input));
    }

    @Test
    public void testContractions() {
        String input = "C'est l'été";
        List<String> expected = List.of("c", "est", "l", "ete");
        assertEquals(expected, searchService.tokenizeText(input));
    }

    @Test
    public void testHashtagWithAccentAndPunctuation() {
        String input = "Vive #Été2024!";
        List<String> expected = List.of("vive", "#Été2024");
        assertEquals(expected, searchService.tokenizeText(input));
    }

    @Test
    public void testTextWithEmoji() {
        String input = "Incroyable été ☀️ à la plage 🏖️";
        List<String> expected = List.of("incroyable", "ete", "a", "la", "plage");
        assertEquals(expected, searchService.tokenizeText(input));
    }

    @Test
    public void testMentions() {
        String input = "Merci @Jean pour l'aide !";
        List<String> expected = List.of("merci", "jean", "pour", "l", "aide");
        assertEquals(expected, searchService.tokenizeText(input));
    }

    @Test
    public void testHashtagWithUnderscore() {
        String input = "On participe à #Open_AI_dev2025";
        List<String> expected = List.of("on", "participe", "a", "#Open_AI_dev2025");
        assertEquals(expected, searchService.tokenizeText(input));
    }

    @Test
    public void testEmptyAndNullInput() {
        assertEquals(Collections.emptyList(), searchService.tokenizeText(""));
        assertNull(searchService.tokenizeText(null));
    }

}

package com.example.guestblur.presentation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class TextObfuscatorTest {

    @Test
    void obfuscatesAllNonWhitespaceCharacters() {
        String original = "Hello World";
        String obfuscated = TextObfuscator.obfuscate(original);

        assertThat(obfuscated).hasSize(original.length());
        assertThat(obfuscated).isNotEqualTo(original);
    }

    @Test
    void preservesWhitespacePositions() {
        String original = "Hello World Test";
        String obfuscated = TextObfuscator.obfuscate(original);

        for (int i = 0; i < original.length(); i++) {
            if (Character.isWhitespace(original.charAt(i))) {
                assertThat(obfuscated.charAt(i)).isEqualTo(original.charAt(i));
            }
        }
    }

    @Test
    void preservesNewlines() {
        String original = "Line1\nLine2\nLine3";
        String obfuscated = TextObfuscator.obfuscate(original);

        assertThat(obfuscated).contains("\n");
        assertThat(obfuscated.split("\n")).hasSize(3);
    }

    @Test
    void preservesTabs() {
        String original = "col1\tcol2";
        String obfuscated = TextObfuscator.obfuscate(original);

        assertThat(obfuscated).contains("\t");
    }

    @Test
    void replacesNonWhitespaceWithKoreanCharacters() {
        String koreanChars = "가나다라마바사아자차카타파하";
        String original = "ABC";
        String obfuscated = TextObfuscator.obfuscate(original);

        for (char c : obfuscated.toCharArray()) {
            assertThat(koreanChars).contains(String.valueOf(c));
        }
    }

    @Test
    void producesDeterministicOutput() {
        String original = "Same input every time";
        String first = TextObfuscator.obfuscate(original);
        String second = TextObfuscator.obfuscate(original);

        assertThat(first).isEqualTo(second);
    }

    @Test
    void returnsNullForNullInput() {
        assertThat(TextObfuscator.obfuscate(null)).isNull();
    }

    @Test
    void returnsEmptyForEmptyInput() {
        assertThat(TextObfuscator.obfuscate("")).isEmpty();
    }

    @Test
    void handlesKoreanInput() {
        String original = "안녕하세요";
        String obfuscated = TextObfuscator.obfuscate(original);

        assertThat(obfuscated).hasSize(original.length());
        assertThat(obfuscated).isNotEqualTo(original);
    }

    @Test
    void preservesLengthForMixedContent() {
        String original = "Hello 안녕 World 123!@#";
        String obfuscated = TextObfuscator.obfuscate(original);

        assertThat(obfuscated).hasSize(original.length());
    }

    @ParameterizedTest
    @ValueSource(strings = {"a", "AB", "abcdefghijklmnopqrstuvwxyz"})
    void preservesLengthForVariousInputs(String input) {
        assertThat(TextObfuscator.obfuscate(input)).hasSize(input.length());
    }

    @Test
    void onlyWhitespaceStringRemainsUnchanged() {
        String original = "   \t\n  ";
        String obfuscated = TextObfuscator.obfuscate(original);

        assertThat(obfuscated).isEqualTo(original);
    }
}

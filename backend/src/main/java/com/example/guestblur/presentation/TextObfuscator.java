package com.example.guestblur.presentation;

public final class TextObfuscator {

    private static final String CHARS = "가나다라마바사아자차카타파하";

    private TextObfuscator() {
    }

    public static String obfuscate(String text) {
        if (text == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder(text.length());
        for (char c : text.toCharArray()) {
            if (Character.isWhitespace(c)) {
                sb.append(c);
            } else {
                sb.append(CHARS.charAt(Math.abs(c % CHARS.length())));
            }
        }
        return sb.toString();
    }
}

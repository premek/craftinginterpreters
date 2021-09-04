package com.craftinginterpreters.lox;

import java.util.HashMap;
import java.util.Map;

import static com.craftinginterpreters.lox.TokenType.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.PushbackReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Scanner {

    private static final int LOOKAHEAD = 2;
    private static final Map<String, TokenType> keywords = new HashMap<String, TokenType>() {
        {

            put("and", AND);
            put("class", CLASS);
            put("else", ELSE);
            put("false", FALSE);
            put("for", FOR);
            put("fun", FUN);
            put("if", IF);
            put("nil", NIL);
            put("or", OR);
            put("print", PRINT);
            put("return", RETURN);
            put("super", SUPER);
            put("this", THIS);
            put("true", TRUE);
            put("var", VAR);
            put("while", WHILE);
        }
    };

    private final LineNumberReader lineNumberReader;
    private final PushbackReader source;
    private final StringBuilder lexemeBuilder = new StringBuilder();

    Scanner(InputStream source) {
        this.lineNumberReader = new LineNumberReader(new InputStreamReader(source, StandardCharsets.UTF_8));
        this.lineNumberReader.setLineNumber(1);
        this.source = new PushbackReader(lineNumberReader, LOOKAHEAD);
    }

    Stream<Token> scanTokens() {
        return StreamSupport.stream(new TokenSpliterator(), false);
    }

    private class TokenSpliterator extends Spliterators.AbstractSpliterator<Token> {

        public TokenSpliterator() {
            super(Long.MAX_VALUE, TokenSpliterator.NONNULL | TokenSpliterator.ORDERED | TokenSpliterator.IMMUTABLE);
        }

        @Override
        public boolean tryAdvance(Consumer<? super Token> action) {
            try {
                if (!isAtEnd()) {
                    scanToken(action);
                    return true;
                }

                action.accept(new Token(EOF, "", null, getLineNumber()));
                return false;
            }
            catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }

    private void scanToken(Consumer<? super Token> action) throws IOException {
        lexemeBuilder.delete(0, lexemeBuilder.length());
        char c = advance();
        switch (c) {
            case '(' -> addToken(LEFT_PAREN, c, action);
            case ')' -> addToken(RIGHT_PAREN, c, action);
            case '{' -> addToken(LEFT_BRACE, c, action);
            case '}' -> addToken(RIGHT_BRACE, c, action);
            case ',' -> addToken(COMMA, c, action);
            case '.' -> addToken(DOT, c, action);
            case '-' -> addToken(MINUS, c, action);
            case '+' -> addToken(PLUS, c, action);
            case ';' -> addToken(SEMICOLON, c, action);
            case '*' -> addToken(STAR, c, action);
            case '!' -> addToken(match('=') ? BANG_EQUAL : BANG, c, action);
            case '=' -> addToken(match('=') ? EQUAL_EQUAL : EQUAL, c, action);
            case '<' -> addToken(match('=') ? LESS_EQUAL : LESS, c, action);
            case '>' -> addToken(match('=') ? GREATER_EQUAL : GREATER, c, action);
            case '/' -> {
                if (match('/')) {
                    // A comment goes until the end of the line.
                    while (peek() != '\n' && !isAtEnd()) {
                        advance();
                    }
                } else {
                    addToken(SLASH, c, action);
                }
            }

            case ' ', '\r', '\n', '\t' -> {
                // Ignore whitespace.
            }
            case '"' -> string(action);

            default -> {
                if (isDigit(c)) {
                    number(action);
                } else if (isAlpha(c)) {
                    identifier(action);
                } else {
                    Lox.error(getLineNumber(), "Unexpected character.");
                }
            }
        }
    }

    private void identifier(Consumer<? super Token> action) throws IOException {
        while (isAlphaNumeric(peek())) {
            advance();
        }

        String text = lexemeBuilder.toString();
        TokenType type = keywords.get(text);
        if (type == null) {
            type = IDENTIFIER;
        }
        addToken(type, action);
    }

    private void number(Consumer<? super Token> action) throws IOException {
        while (isDigit(peek())) {
            advance();
        }

        // Look for a fractional part.
        if (peek() == '.' && isDigit(peekNext())) {
            // Consume the "."
            advance();

            while (isDigit(peek())) {
                advance();
            }
        }
        addToken(NUMBER, Double.parseDouble(lexemeBuilder.toString()), action);
    }

    private void string(Consumer<? super Token> action) throws IOException {
        
        // The opening ".
        advance();

        while (peek() != '"' && !isAtEnd()) {
            advance();
        }

        if (isAtEnd()) {
            Lox.error(getLineNumber(), "Unterminated string.");
            return;
        }

        // The closing ".
        advance();

        // Trim the surrounding quotes.
        String value = lexemeBuilder.toString().substring(1, lexemeBuilder.length()-1);
        addToken(STRING, value, action);
    }

    private boolean match(char expected) throws IOException {
        if (isAtEnd()) {
            return false;
        }

        int c = source.read();
        if ((char) c != expected) {
            source.unread(c);
            return false;
        }

        return true;
    }

    private char peek() throws IOException {
        if (isAtEnd()) {
            return '\0';
        }
        int c = source.read();
        source.unread(c);
        return (char) c;
    }

    private char peekNext() throws IOException {
        if (isAtEnd()) {
            return '\0';
        }
        int c1 = source.read();
        if (isAtEnd()) {
            return '\0';
        }
        int c2 = source.read();
        source.unread(c2);
        source.unread(c1);
        return (char) c2;
    }

    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z')
                || (c >= 'A' && c <= 'Z')
                || c == '_';
    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean isAtEnd() throws IOException {
        int r = source.read();
        if (r == -1) {
            return true;
        }
        source.unread(r);
        return false;
    }

    private char advance() throws IOException {
        char c = (char) source.read();
        lexemeBuilder.append(c);
        return c;
    }

    private void addToken(TokenType type, Consumer<? super Token> action) {
        addToken(type, null, action);
    }

    private void addToken(TokenType type, Object literal, Consumer<? super Token> action) {
        String text = lexemeBuilder.toString();
        action.accept(new Token(type, text, literal, getLineNumber()));
    }


    private int getLineNumber() {
        return lineNumberReader.getLineNumber();
    }
}

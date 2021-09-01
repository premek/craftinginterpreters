package com.craftinginterpreters.lox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.craftinginterpreters.lox.TokenType.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.PushbackReader;
import java.nio.charset.StandardCharsets;

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
    private final List<Token> tokens = new ArrayList<>();
    private final StringBuilder lexemeBuilder = new StringBuilder();

    Scanner(InputStream source) {
        this.lineNumberReader = new LineNumberReader(new InputStreamReader(source, StandardCharsets.UTF_8));
        this.lineNumberReader.setLineNumber(1);
        this.source = new PushbackReader(lineNumberReader, LOOKAHEAD);
    }

    List<Token> scanTokens() throws IOException {
        while (!isAtEnd()) {
            lexemeBuilder.delete(0, lexemeBuilder.length());
            scanToken();
        }

        tokens.add(new Token(EOF, "", null, getLineNumber()));
        return tokens; // TODO return stream
    }

    private void scanToken() throws IOException {
        char c = advance();
        switch (c) {
            case '(' -> addToken(LEFT_PAREN, c);
            case ')' -> addToken(RIGHT_PAREN, c);
            case '{' -> addToken(LEFT_BRACE, c);
            case '}' -> addToken(RIGHT_BRACE, c);
            case ',' -> addToken(COMMA, c);
            case '.' -> addToken(DOT, c);
            case '-' -> addToken(MINUS, c);
            case '+' -> addToken(PLUS, c);
            case ';' -> addToken(SEMICOLON, c);
            case '*' -> addToken(STAR, c);
            case '!' -> addToken(match('=') ? BANG_EQUAL : BANG, c);
            case '=' -> addToken(match('=') ? EQUAL_EQUAL : EQUAL, c);
            case '<' -> addToken(match('=') ? LESS_EQUAL : LESS, c);
            case '>' -> addToken(match('=') ? GREATER_EQUAL : GREATER, c);
            case '/' -> {
                if (match('/')) {
                    // A comment goes until the end of the line.
                    while (peek() != '\n' && !isAtEnd()) {
                        advance();
                    }
                } else {
                    addToken(SLASH, c);
                }
            }

            case ' ', '\r', '\n', '\t' -> {
                // Ignore whitespace.
            }
            case '"' -> string();

            default -> {
                if (isDigit(c)) {
                    number();
                } else if (isAlpha(c)) {
                    identifier();
                } else {
                    Lox.error(getLineNumber(), "Unexpected character.");
                }
            }
        }
    }

    private void identifier() throws IOException {
        while (isAlphaNumeric(peek())) {
            advance();
        }

        String text = lexemeBuilder.toString();
        TokenType type = keywords.get(text);
        if (type == null) {
            type = IDENTIFIER;
        }
        addToken(type);
    }

    private void number() throws IOException {
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
        addToken(NUMBER, Double.parseDouble(lexemeBuilder.toString()));
    }

    private void string() throws IOException {
        
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
        addToken(STRING, value);
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

    private void addToken(TokenType type) {
        addToken(type, null);
    }

    private void addToken(TokenType type, Object literal) {
        String text = lexemeBuilder.toString();
        tokens.add(new Token(type, text, literal, getLineNumber()));
    }


    private int getLineNumber() {
        return lineNumberReader.getLineNumber();
    }
}

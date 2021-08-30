package com.craftinginterpreters.lox;

import java.util.List;

public sealed interface Expr permits Expr.Assign, Expr.Binary, Expr.Call, Expr.Get, Expr.Grouping, Expr.Literal, Expr.Logical, Expr.Set, Expr.Super, Expr.This, Expr.Unary, Expr.Variable {

    public record Assign(Token name, Expr value) implements Expr {

    }

    public record Binary(Expr left, Token operator, Expr right) implements Expr {

    }

    public record Call(Expr callee, Token paren, List<Expr> arguments) implements Expr {

    }

    public record Get(Expr object, Token name) implements Expr {

    }

    public record Grouping(Expr expression) implements Expr {

    }

    public record Literal(Object value) implements Expr {

    }

    public record Logical(Expr left, Token operator, Expr right) implements Expr {

    }

    public record Set(Expr object, Token name, Expr value) implements Expr {

    }

    public record Super(Token keyword, Token method) implements Expr {

    }

    public record This(Token keyword) implements Expr {

    }

    public record Unary(Token operator, Expr right) implements Expr {

    }

    public record Variable(Token name) implements Expr {

    }

}

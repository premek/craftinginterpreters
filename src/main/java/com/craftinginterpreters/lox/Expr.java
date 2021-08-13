package com.craftinginterpreters.lox;

import java.util.List;

public interface Expr {

    interface Visitor<R> {

        R visitAssignExpr(Assign expr);

        R visitBinaryExpr(Binary expr);

        R visitCallExpr(Call expr);

        R visitGetExpr(Get expr);

        R visitGroupingExpr(Grouping expr);

        R visitLiteralExpr(Literal expr);

        R visitLogicalExpr(Logical expr);

        R visitSetExpr(Set expr);

        R visitSuperExpr(Super expr);

        R visitThisExpr(This expr);

        R visitUnaryExpr(Unary expr);

        R visitVariableExpr(Variable expr);
    }

    // Nested Expr classes here...
    public record Assign(Token name, Expr value) implements Expr {

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitAssignExpr(this);
        }

    }

    public record Binary(Expr left, Token operator, Expr right) implements Expr {

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitBinaryExpr(this);
        }

    }

    public record Call(Expr callee, Token paren, List<Expr> arguments) implements Expr {

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitCallExpr(this);
        }

    }

    public record Get(Expr object, Token name) implements Expr {

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitGetExpr(this);
        }

    }

    public record Grouping(Expr expression) implements Expr {

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitGroupingExpr(this);
        }

    }

    public record Literal(Object value) implements Expr {

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitLiteralExpr(this);
        }

    }

    public record Logical(Expr left, Token operator, Expr right) implements Expr {

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitLogicalExpr(this);
        }

    }

    public record Set(Expr object, Token name, Expr value) implements Expr {

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitSetExpr(this);
        }

    }

    public record Super(Token keyword, Token method) implements Expr {

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitSuperExpr(this);
        }

    }

    public record This(Token keyword) implements Expr {

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitThisExpr(this);
        }

    }

    public record Unary(Token operator, Expr right) implements Expr {

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitUnaryExpr(this);
        }

    }

    public record Variable(Token name) implements Expr {

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitVariableExpr(this);
        }

    }

    public <R> R accept(Visitor<R> visitor);
}

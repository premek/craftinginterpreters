package com.craftinginterpreters.lox;

import java.util.List;

interface Stmt {

    interface Visitor<R> {

        R visitBlockStmt(Block stmt);

        R visitClassStmt(Class stmt);

        R visitExpressionStmt(Expression stmt);

        R visitFunctionStmt(Function stmt);

        R visitIfStmt(If stmt);

        R visitPrintStmt(Print stmt);

        R visitReturnStmt(Return stmt);

        R visitVarStmt(Var stmt);

        R visitWhileStmt(While stmt);
    }

    public record Block(List<Stmt> statements) implements Stmt {

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitBlockStmt(this);
        }

    }

    public record Class(Token name,
            Expr.Variable superclass,
            List<Stmt.Function> methods) implements Stmt {

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitClassStmt(this);
        }
    }

    public record Expression(Expr expression) implements Stmt {

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitExpressionStmt(this);
        }

    }

    public record Function(Token name, List<Token> params, List<Stmt> body) implements Stmt {

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitFunctionStmt(this);
        }
    }

    public record If(Expr condition, Stmt thenBranch, Stmt elseBranch) implements Stmt {

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitIfStmt(this);
        }
    }

    public record Print(Expr expression) implements Stmt {

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitPrintStmt(this);
        }

    }

    public record Return(Token keyword, Expr value) implements Stmt {

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitReturnStmt(this);
        }

    }

    public record Var(Token name, Expr initializer) implements Stmt {

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitVarStmt(this);
        }
    }

    public static record While(Expr condition, Stmt body) implements Stmt {

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitWhileStmt(this);
        }
    }

    public <R> R accept(Visitor<R> visitor);
}

package com.craftinginterpreters.lox;

import java.util.List;

public sealed interface Stmt permits Stmt.Block, Stmt.Class, Stmt.Expression, Stmt.Function, Stmt.If, Stmt.Print, Stmt.Return, Stmt.Var, Stmt.While {

    public record Block(List<Stmt> statements) implements Stmt {

    }

    public record Class(Token name,
            Expr.Variable superclass,
            List<Stmt.Function> methods) implements Stmt {

    }

    public record Expression(Expr expression) implements Stmt {

    }

    public record Function(Token name, List<Token> params, List<Stmt> body) implements Stmt {

    }

    public record If(Expr condition, Stmt thenBranch, Stmt elseBranch) implements Stmt {

    }

    public record Print(Expr expression) implements Stmt {

    }

    public record Return(Token keyword, Expr value) implements Stmt {

    }

    public record Var(Token name, Expr initializer) implements Stmt {

    }

    public record While(Expr condition, Stmt body) implements Stmt {

    }

}

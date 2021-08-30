package com.craftinginterpreters.lox;

import java.util.List;

public class AstPrinter {

    String print(Expr expr) {
        return switch (expr) {
            case Expr.Assign e -> visitAssignExpr(e);
            case Expr.Binary e -> visitBinaryExpr(e);
            case Expr.Call e -> visitCallExpr(e);
            case Expr.Get e -> visitGetExpr(e);
            case Expr.Grouping e -> visitGroupingExpr(e);
            case Expr.Literal e -> visitLiteralExpr(e);
            case Expr.Logical e -> visitLogicalExpr(e);
            case Expr.Set e -> visitSetExpr(e);
            case Expr.Super e -> visitSuperExpr(e);
            case Expr.This e -> visitThisExpr(e);
            case Expr.Unary e -> visitUnaryExpr(e);
            case Expr.Variable e -> visitVariableExpr(e);
        };
    }

    String print(Stmt stmt) {
        return switch (stmt) {
            case Stmt.Block s -> visitBlockStmt(s);
            case Stmt.Class s -> visitClassStmt(s);
            case Stmt.Expression s -> visitExpressionStmt(s);
            case Stmt.Function s -> visitFunctionStmt(s);
            case Stmt.If s -> visitIfStmt(s);
            case Stmt.Print s -> visitPrintStmt(s);
            case Stmt.Return s -> visitReturnStmt(s);
            case Stmt.Var s -> visitVarStmt(s);
            case Stmt.While s -> visitWhileStmt(s);
        };
    }

    public String visitBlockStmt(Stmt.Block stmt) {
        StringBuilder builder = new StringBuilder();
        builder.append("(block ");

        for (Stmt statement : stmt.statements()) {
            builder.append(print(statement));
        }

        builder.append(")");
        return builder.toString();
    }

    public String visitClassStmt(Stmt.Class stmt) {
        StringBuilder builder = new StringBuilder();
        builder.append("(class ").append(stmt.name().lexeme());

        if (stmt.superclass() != null) {
            builder.append(" < ").append(print(stmt.superclass()));
        }

        for (Stmt.Function method : stmt.methods()) {
            builder.append(" ").append(print(method));
        }

        builder.append(")");
        return builder.toString();
    }

    public String visitExpressionStmt(Stmt.Expression stmt) {
        return parenthesize(";", stmt.expression());
    }

    public String visitFunctionStmt(Stmt.Function stmt) {
        StringBuilder builder = new StringBuilder();
        builder.append("(fun ").append(stmt.name().lexeme()).append("(");

        for (Token param : stmt.params()) {
            if (param != stmt.params().get(0)) {
                builder.append(" ");
            }
            builder.append(param.lexeme());
        }

        builder.append(") ");

        for (Stmt body : stmt.body()) {
            builder.append(print(body));
        }

        builder.append(")");
        return builder.toString();
    }

    public String visitIfStmt(Stmt.If stmt) {
        if (stmt.elseBranch() == null) {
            return parenthesize2("if", stmt.condition(), stmt.thenBranch());
        }

        return parenthesize2("if-else", stmt.condition(), stmt.thenBranch(),
                stmt.elseBranch());
    }

    public String visitPrintStmt(Stmt.Print stmt) {
        return parenthesize("print", stmt.expression());
    }

    public String visitReturnStmt(Stmt.Return stmt) {
        if (stmt.value() == null) {
            return "(return)";
        }
        return parenthesize("return", stmt.value());
    }

    public String visitVarStmt(Stmt.Var stmt) {
        if (stmt.initializer() == null) {
            return parenthesize2("var", stmt.name());
        }

        return parenthesize2("var", stmt.name(), "=", stmt.initializer());
    }

    public String visitWhileStmt(Stmt.While stmt) {
        return parenthesize2("while", stmt.condition(), stmt.body());
    }

    public String visitAssignExpr(Expr.Assign expr) {
        return parenthesize2("=", expr.name().lexeme(), expr.value());
    }

    public String visitBinaryExpr(Expr.Binary expr) {
        return parenthesize(expr.operator().lexeme(),
                expr.left(), expr.right());
    }

    public String visitCallExpr(Expr.Call expr) {
        return parenthesize2("call", expr.callee(), expr.arguments());
    }

    public String visitGetExpr(Expr.Get expr) {
        return parenthesize2(".", expr.object(), expr.name().lexeme());
    }

    public String visitGroupingExpr(Expr.Grouping expr) {
        return parenthesize("group", expr.expression());
    }

    public String visitLiteralExpr(Expr.Literal expr) {
        if (expr.value() == null) {
            return "nil";
        }
        return expr.value().toString();
    }

    public String visitLogicalExpr(Expr.Logical expr) {
        return parenthesize(expr.operator().lexeme(), expr.left(), expr.right());
    }

    public String visitSetExpr(Expr.Set expr) {
        return parenthesize2("=",
                expr.object(), expr.name().lexeme(), expr.value());
    }

    public String visitSuperExpr(Expr.Super expr) {
        return parenthesize2("super", expr.method());
    }

    public String visitThisExpr(Expr.This expr) {
        return "this";
    }

    public String visitUnaryExpr(Expr.Unary expr) {
        return parenthesize(expr.operator().lexeme(), expr.right());
    }

    public String visitVariableExpr(Expr.Variable expr) {
        return expr.name().lexeme();
    }

    private String parenthesize(String name, Expr... exprs) {
        StringBuilder builder = new StringBuilder();

        builder.append("(").append(name);
        for (Expr expr : exprs) {
            builder.append(" ");
            builder.append(print(expr));
        }
        builder.append(")");

        return builder.toString();
    }
    // Note: AstPrinting other types of syntax trees is not shown in the
    // book, but this is provided here as a reference for those reading
    // the full code.

    private String parenthesize2(String name, Object... parts) {
        StringBuilder builder = new StringBuilder();

        builder.append("(").append(name);
        transform(builder, parts);
        builder.append(")");

        return builder.toString();
    }

    private void transform(StringBuilder builder, Object... parts) {
        for (Object part : parts) {
            builder.append(" ");
            if (part instanceof Expr) {
                builder.append(print((Expr) part));
            } else if (part instanceof Stmt) {
                builder.append(print((Stmt) part));
            } else if (part instanceof Token) {
                builder.append(((Token) part).lexeme());
            } else if (part instanceof List) {
                transform(builder, ((List) part).toArray());
            } else {
                builder.append(part);
            }
        }
    }
}

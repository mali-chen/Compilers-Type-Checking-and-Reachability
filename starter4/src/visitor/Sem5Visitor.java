package visitor;

import syntaxtree.*;
import errorMsg.*;

// The purpose of this class it detect and report unreachable code,
// according to Java's rules.
public class Sem5Visitor extends Visitor
{
    ErrorMsg errorMsg;

    // true if the current statement can be reached
    private boolean canReach;

    public Sem5Visitor(ErrorMsg e)
    {
        errorMsg = e;
        canReach = true;
    }

    // visit a list of statements, tracking reachability through each one
    // returns true if execution can fall through to the end of the list
    private boolean visitStmts(StmtList stmts)
    {
        canReach = true;
        for(Object obj : stmts){
            if(obj instanceof Stmt){
                Stmt s = (Stmt) obj;
                if (!canReach){
                    // warn on first unreachable statement only
                    errorMsg.warning(s.pos, CompWarning.UnreachableCode());
                    return false;
                }
                s.accept(this);
            }
        }
        return canReach;
    }

    @Override
    public Object visit(MethodDeclVoid n){
        canReach = true;
        visitStmts(n.stmts);
        return null;
    }

    @Override
    public Object visit(MethodDeclNonVoid n){
        canReach = true;
        visitStmts(n.stmts);
        return null;
    }

    // break makes next statement unreachable
    @Override
    public Object visit(Break n){
        canReach = false;
        return null;
    }

    @Override
    public Object visit(Block n){
        visitStmts(n.stmts);
        return null;
    }

    @Override
    public Object visit(If n){
        // visit both branches, canReach after if = either branch can reach end
        boolean savedReach = canReach;

        canReach = savedReach;
        n.trueStmt.accept(this);
        boolean trueReach = canReach;

        canReach = savedReach;
        n.falseStmt.accept(this);
        boolean falseReach = canReach;

        // can reach after only if at least 1 branch can reach the end
        canReach = trueReach || falseReach;
        return null;
    }

    @Override
    public Object visit(While n){
        // check if condition is a constant
        boolean isAlwaysTrue  = isConstantTrue(n.exp);
        boolean isAlwaysFalse = isConstantFalse(n.exp);

        if(isAlwaysFalse){
            // while(false) body is unreachable
            boolean savedReach = canReach;
            canReach = false;
            n.body.accept(this);
            // execution continues after the loop
            canReach = savedReach;
        }
        else if(isAlwaysTrue){
            // while(true) body executes, nothing after loop is reachable
            canReach = true;
            n.body.accept(this);
            // can only reach after loop if there's a break inside
            canReach = false;
        }
        else{
            // normal while: body may or may not execute
            canReach = true;
            n.body.accept(this);
            canReach = true; // loop may not execute at all
        }
        return null;
    }

    // helper method to check is this expression 'true'
    private boolean isConstantTrue(Exp e){
        return e instanceof True;
    }

    // helper method to check is this expression 'false'
    private boolean isConstantFalse(Exp e){
        return e instanceof False;
    }

    @Override
    public Object visit(Assign n){
        return null;
    }

    @Override
    public Object visit(CallStmt n){
        return null;
    }

    @Override 
    public Object visit(LocalDeclStmt n){
        return null;
    }
}

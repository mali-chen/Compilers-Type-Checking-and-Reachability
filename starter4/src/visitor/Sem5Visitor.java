package visitor;

import errorMsg.*;
import syntaxtree.*;

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

    @Override 
    public Object visit(ClassDecl n){
        n.decls.accept(this);
        return null;
    }

    // helper method evaluate a constant int expression
    private Integer evalConstant(Exp e){
        // plain number
        if(e instanceof IntLit){
            IntLit literal = (IntLit) e;
            return literal.val;
        }

        // addition
        if(e instanceof Plus){
            Plus p = (Plus) e;
            Integer leftside = evalConstant(p.left);
            Integer rightside = evalConstant(p.right);
            
            // add 2 ints
            if(leftside != null && rightside != null){
                return leftside + rightside;
            }
        }

        // subtraction
        if(e instanceof Minus){
            Minus m = (Minus) e;
            Integer leftside = evalConstant(m.left);
            Integer rightside = evalConstant(m.right);

            // subtract 2 ints
            if(leftside != null && rightside != null){
                return leftside - rightside;
            }
        }

        // multiplicaion
        if(e instanceof Times){
            Times t = (Times) e;
            Integer leftside = evalConstant(t.left);
            Integer rightside = evalConstant(t.right);
            
            // multiply 2 ints
            if(leftside != null && rightside != null){
                return leftside * rightside;
            }
        }

        // division
        if(e instanceof Divide){
            Divide d = (Divide) e;
            Integer leftside = evalConstant(d.left);
            Integer rightside = evalConstant(d.right);
            
            // divide 2 ints
            if(leftside != null && rightside != null){
                return leftside / rightside;
            }
        }

        // remainder
        if(e instanceof Remainder){
            Remainder r = (Remainder) e;
            Integer leftside = evalConstant(r.left);
            Integer rightside = evalConstant(r.right);
            
            // divide 2 ints
            if(leftside != null && rightside != null){
                return leftside % rightside;
            }
        }
        return null;
    }

    @Override
    public Object visit(NewArray n){
        Integer val = evalConstant(n.sizeExp);
        if (val != null && val < 0){
            errorMsg.warning(n.sizeExp.pos, CompWarning.NegativeLength());
        }
        return null;
    }

    @Override
    public Object visit(ArrayLookup n) {
        Integer val = evalConstant(n.idxExp);
        if (val != null && val < 0){
            errorMsg.warning(n.idxExp.pos, CompWarning.NegativeIndex());
        }
        return null;
    }

}

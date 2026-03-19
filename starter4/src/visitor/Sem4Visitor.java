package visitor;

import errorMsg.*;
import java.util.*;
import syntaxtree.*;
// The purpose of this class is to do type-checking and related
// actions.  These include:
// - evaluate the type for each expression, 
//   filling in the 'type' link for each
// - ensure that each expression follows MiniJava's type rules (e.g.,
//   that the arguments to '*' are both integer, the argument to
//   '.length' is an array, etc.)
// - ensure that each method-call follows Java's type rules:
//   - there exists a method for the given class (or a superclass)
//     for the receiver's object type
//   - the method has the correct number of parameters
//   - the types of each actual parameter is compatible with that
//     of its corresponding formal parameter
// - ensure that for each instance variable access (e.g., abc.foo),
//   there is an instance variable defined for the given class (or
//   in a superclass
//   - sets the 'varDec' link in the InstVarAccess to refer to the
//     method
// - ensure that the RHS expression in each assignment statement is
//   type-compatible with its corresponding LHS
//   - also checks that the LHS an lvalue
// - ensure that if a method with a given name is defined in both
//   a subclass and a superclass, that they have the same parameters
//   (with identical types) and the same return type
// - ensure that the declared return-type of a method is compatible
//   with its "return" expression
// - ensuring that the type of the control expression for an if- or
//   while-statement is boolean
public class Sem4Visitor extends Visitor
{
    ClassDecl currentClass;
    IDType currentType;
    IDType superType;
    ErrorMsg errorMsg;

    // Constants for types we'll need
    BoolType Bool;
    IntType Int;
    VoidType Void;
    NullType Null;
    ErrorType Error;
    IDType ObjectType;
    IDType StringType;

    HashMap<String,ClassDecl> classEnv;

    public Sem4Visitor(HashMap<String,ClassDecl> env, ErrorMsg e)
    {
        errorMsg = e;
        classEnv = env;
        currentClass = null;

        Bool = new BoolType(-1);
        Int  = new IntType(-1);
        Null = new NullType(-1);
        Void = new VoidType(-1);
        Error = new ErrorType(-1);
        StringType = new IDType(-1, "String");
        ObjectType = new IDType(-1, "Object");
        StringType.link = classEnv.get("String");
        ObjectType.link = classEnv.get("Object");
    }

    @Override
    public Object visit(IntLit i)
    {
        i.type = Int;
        return Int;
    }

    public Object visit(Plus p)
    {
        Type t1 = (Type)p.left.accept(this);
        Type t2 = (Type)p.right.accept(this);
        if(!t1.isInt())
        {
            errorMsg.error(p.pos, CompError.TypeMismatch(t1, Int));
        }
        else if(!t2.isInt())
        {
            errorMsg.error(p.pos, CompError.TypeMismatch(t2, Int));
        }
        p.type = Int;
        return Int;
    }

}


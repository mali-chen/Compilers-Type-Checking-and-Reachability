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

    // operators

    // + operator
    @Override
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

    // - operator
    @Override
    public Object visit(Minus m)
    {
        Type t1 = (Type)m.left.accept(this);
        Type t2 = (Type)m.right.accept(this);
        if(!t1.isInt())
        {
            errorMsg.error(m.pos, CompError.TypeMismatch(t1, Int));
        }
        else if(!t2.isInt())
        {
            errorMsg.error(m.pos, CompError.TypeMismatch(t2, Int));
        }
        m.type = Int;
        return Int;
    }

    // * operator
    @Override
    public Object visit(Times t)
    {
        Type t1 = (Type)t.left.accept(this);
        Type t2 = (Type)t.right.accept(this);
        if(!t1.isInt())
        {
            errorMsg.error(t.pos, CompError.TypeMismatch(t1, Int));
        }
        else if(!t2.isInt())
        {
            errorMsg.error(t.pos, CompError.TypeMismatch(t2, Int));
        }
        t.type = Int;
        return Int;
    }

    // / operator
    @Override
    public Object visit(Divide d)
    {
        Type t1 = (Type)d.left.accept(this);
        Type t2 = (Type)d.right.accept(this);
        if(!t1.isInt())
        {
            errorMsg.error(d.pos, CompError.TypeMismatch(t1, Int));
        }
        else if(!t2.isInt())
        {
            errorMsg.error(d.pos, CompError.TypeMismatch(t2, Int));
        }
        d.type = Int;
        return Int;
    }

    // % operator
    @Override
    public Object visit(Remainder r)
    {
        Type t1 = (Type)r.left.accept(this);
        Type t2 = (Type)r.right.accept(this);
        if(!t1.isInt())
        {
            errorMsg.error(r.pos, CompError.TypeMismatch(t1, Int));
        }
        else if(!t2.isInt())
        {
            errorMsg.error(r.pos, CompError.TypeMismatch(t2, Int));
        }
        r.type = Int;
        return Int;
    }

    // > operator
    @Override
    public Object visit(LessThan l)
    {
        Type t1 = (Type)l.left.accept(this);
        Type t2 = (Type)l.right.accept(this);
        if(!t1.isInt())
        {
            errorMsg.error(l.pos, CompError.TypeMismatch(t1, Int));
        }
        else if(!t2.isInt())
        {
            errorMsg.error(l.pos, CompError.TypeMismatch(t2, Int));
        }
        l.type = Bool;
        return Bool;
    }

    // < operator
    @Override
    public Object visit(GreaterThan g)
    {
        Type t1 = (Type)g.left.accept(this);
        Type t2 = (Type)g.right.accept(this);
        if(!t1.isInt())
        {
            errorMsg.error(g.pos, CompError.TypeMismatch(t1, Int));
        }
        else if(!t2.isInt())
        {
            errorMsg.error(g.pos, CompError.TypeMismatch(t2, Int));
        }
        g.type = Bool;
        return Bool;
    }

    // && operator
    @Override
    public Object visit(And a)
    {
        Type t1 = (Type)a.left.accept(this);
        Type t2 = (Type)a.right.accept(this);
        if(!t1.isBoolean())
        {
            errorMsg.error(a.pos, CompError.TypeMismatch(t1, Int));
        }
        else if(!t2.isBoolean())
        {
            errorMsg.error(a.pos, CompError.TypeMismatch(t2, Int));
        }
        a.type = Bool;
        return Bool;
    }

    // || operator
    @Override
    public Object visit(Or o)
    {
        Type t1 = (Type)o.left.accept(this);
        Type t2 = (Type)o.right.accept(this);
        if(!t1.isBoolean())
        {
            errorMsg.error(o.pos, CompError.TypeMismatch(t1, Int));
        }
        else if(!t2.isBoolean())
        {
            errorMsg.error(o.pos, CompError.TypeMismatch(t2, Int));
        }
        o.type = Bool;
        return Bool;
    }

    // == operator
    @Override
    public Object visit(Equals e)
    {
        Type t1 = (Type)e.left.accept(this);
        Type t2 = (Type)e.right.accept(this);
        if(!isSubtype(t1, t2) && !isSubtype(t2, t1))
        {
            errorMsg.error(e.pos, CompError.IncompatibleType(t1, t2));
        }
        if(t1.isVoid() || t2.isVoid())
        {
            errorMsg.error(e.pos, CompError.IncompatibleType(t1, t2));
        }
        e.type = Bool;
        return Bool;
    }
    
    @Override
    public Object visit(ClassDecl n){
        ClassDecl prevClass = currentClass;
        IDType prevType = currentType;
        IDType prevSuper = superType;

        currentClass = n;
        currentType = new IDType(n.pos, n.name);
        currentType.link = n;

        if(n.superLink != null){
            superType = new IDType(n.pos, n.superName);
            superType.link = n.superLink;
        }
        else{
            superType = null;
        }

        // visit all methods/fields inside
        n.decls.accept(this);

        currentClass = prevClass;
        currentType = prevType;
        superType = prevSuper;

        return null;
    }

    // expression visitors
    @Override
    public Object visit(True t){
        t.type = Bool;
        return Bool;
    }

    @Override
    public Object visit(False f){
        f.type = Bool;
        return Bool;
    }

    @Override
    public Object visit(Null n){
        n.type = Null;
        return Null;
    }

    @Override
    public Object visit(StringLit s){
        s.type = StringType;
        return StringType;
    }

    @Override
    public Object visit(This t){
        t.type = currentType;
        return currentType;
    }

    @Override
    public Object visit(Super s){
        s.type = superType;
        return superType;
    }

    @Override
    public Object visit(IDExp i){
        if(i.link == null){
            i.type = Error;
            return Error;
        }
        
        i.type = i.link.type;
        return i.link.type;
    }

    private boolean isSubtype(Type sub, Type sup){
        // if either type is missing, assume it's okay 
        if (sub == null){
            return true;
        }
        if (sup == null){
            return true;
        }

        // error check
        if (sub.isError()){
            return true;
        }
        if (sup.isError()){
            return true;
        }

        // if the same type, it's a match
        if (sub.equals(sup)){
            return true;
        }

        // null check
        if (sub.isNull()){
            if (sup.isID() || sup.isArray() || sup.isObject()){
                return true;
            }
        }

        // object check
        if (sup.isObject()){
            if (sub.isID() || sub.isArray() || sub.isNull()){
                return true;
            }
        }

        // inheritance check
        if (sub.isID()){
            if (sup.isID()){
                // get the class definitions from the links
                ClassDecl cur = ((IDType) sub).link;
                ClassDecl target = ((IDType) sup).link;

                // follow the 'superLink' up until we find a match or hit the top (null)
                while (cur != null) {
                    if (cur == target) {
                        return true; // found the parent
                    }
                    // move up to the next parent class
                    cur = cur.superLink;
                }
            }
        }

        // if none matched, it's not a subtype
        return false;
    }
}


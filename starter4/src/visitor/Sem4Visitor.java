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
        Type t1 = safeType((Type)p.left.accept(this));
        Type t2 = safeType((Type)p.right.accept(this));
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
        Type t1 = safeType((Type)m.left.accept(this));
        Type t2 = safeType((Type)m.right.accept(this));
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
        Type t1 = safeType((Type)t.left.accept(this));
        Type t2 = safeType((Type)t.right.accept(this));
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
        Type t1 = safeType((Type)d.left.accept(this));
        Type t2 = safeType((Type)d.right.accept(this));
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
        Type t1 = safeType((Type)r.left.accept(this));
        Type t2 = safeType((Type)r.right.accept(this));
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
        Type t1 = safeType((Type)l.left.accept(this));
        Type t2 = safeType((Type)l.right.accept(this));
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
        Type t1 = safeType((Type)g.left.accept(this));
        Type t2 = safeType((Type)g.right.accept(this));
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
        Type t1 = safeType((Type)a.left.accept(this));
        Type t2 = safeType((Type)a.right.accept(this));
        if(!t1.isBoolean())
        {
            errorMsg.error(a.pos, CompError.TypeMismatch(t1, Bool));
        }
        else if(!t2.isBoolean())
        {
            errorMsg.error(a.pos, CompError.TypeMismatch(t2, Bool));
        }
        a.type = Bool;
        return Bool;
    }

    // || operator
    @Override
    public Object visit(Or o)
    {
        Type t1 = safeType((Type)o.left.accept(this));
        Type t2 = safeType((Type)o.right.accept(this));
        if(!t1.isBoolean())
        {
            errorMsg.error(o.pos, CompError.TypeMismatch(t1, Bool));
        }
        else if(!t2.isBoolean())
        {
            errorMsg.error(o.pos, CompError.TypeMismatch(t2, Bool));
        }
        o.type = Bool;
        return Bool;
    }

    // ! operator
    @Override
    public Object visit(Not n)
    {
        Type t = safeType((Type)n.exp.accept(this));
        if(!t.isBoolean())
            {
            errorMsg.error(n.pos, CompError.TypeMismatch(t, Bool));
        }
        n.type = Bool;
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
        if((t1 != null && t1.isVoid()) || (t2 != null && t2.isVoid()))
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
        // if either type is missing, it's ok
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

                // follow the 'superLink' up until a match is found or hit the top (null)
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

    private Type safeType(Type t){
        // subexpression missing, return "Error" type
        if (t == null){
            return Error;
        }
        else{ // not null, safe to use original type
            return t;
        }
    }

    // return the IDType of the class being instantiated
    @Override
    public Object visit(NewObject n){
        n.type = n.objType;
        return n.objType;
    }

    //arrays

    // check .length is called on an array and return int
    @Override
    public Object visit(ArrayLength n){
        Type t = safeType((Type)n.exp.accept(this));
        if(!t.isArray()){
            errorMsg.error(n.pos, CompError.IllegalLength());
        }
        n.type = Int;
        return Int;
    }

    // ensure size is int and wrap the base type in an ArrayType
    @Override
    public Object visit(NewArray n){
        // check size expression is an int
        Type sizeType = safeType((Type)n.sizeExp.accept(this));
        if(!sizeType.isInt()){
            errorMsg.error(n.pos, CompError.TypeMismatch(sizeType, Int));
        }
        n.type = new ArrayType(n.pos, n.objType);
        return n.type;
    }

    // a[i] has to be an array, index must be int type
    @Override
    public Object visit(ArrayLookup n){
        // check size expression is an int
        Type arrType = safeType((Type)n.arrExp.accept(this));
        Type ideType = safeType((Type)n.idxExp.accept(this));
        if(!ideType.isInt()){
            errorMsg.error(n.pos, CompError.TypeMismatch(ideType, Int));
        }
        if(!arrType.isArray()){
            errorMsg.error(n.pos, CompError.ArrayType());
            n.type = Error;
            return Error;
        }

        Type elemType = ((ArrayType) arrType).baseType;
        n.type = elemType;
        return elemType;
    }

    // helper method searches method by name 
    private MethodDecl findMethod(ClassDecl startClass, String methodName){
        ClassDecl searchClass = startClass;
        
        // look for class to check
        while(searchClass != null){
            MethodDecl foundMethod = searchClass.methodEnv.get(methodName);
            
            // if found, return it
            if(foundMethod != null){
                return foundMethod;
            }
            // not found, move to parent class and try again
            searchClass = searchClass.superLink; 
        }
        return null;
    }

    // helper method searches for a variable (field) by name 
    private FieldDecl findField(ClassDecl startClass, String fieldName) {
        ClassDecl searchClass = startClass;
        
        while (searchClass != null){
            // look in the current class's variables
            FieldDecl foundField = searchClass.fieldEnv.get(fieldName);
            
            // if found, return it
            if (foundField != null){
                return foundField;
            }
            // not found, move to parent class and try again
            searchClass = searchClass.superLink; 
        }
        return null;
    }
       
    @Override
    public Object visit(FieldAccess n){
        // type of the object before the dot
        Type objType = safeType((Type)n.exp.accept(this));

        // can only access fields on Classes (IDTypes)
        if(!objType.isID()){
            errorMsg.error(n.pos, CompError.UndefinedField(n.varName, objType));
            n.type = Error;
            return Error;
        }

        // get the class definition and search for the field name
        ClassDecl cls = ((IDType) objType).link;
        FieldDecl field = findField(cls, n.varName);

        // if the field doesn't exist in this class or any parent classes
        if (field == null){
            errorMsg.error(n.pos, CompError.UndefinedField(n.varName, objType));
            n.type = Error;
            return Error;
        }

        // link AST node to the field and record its type
        n.varDec = field;
        n.type = field.type;
        return field.type;
    }

    @Override
    public Object visit(Call n){
        // determine type of object being called
        Type objType = safeType((Type)n.obj.accept(this));

        // visit all arguments passed in
        for(int i = 0; i < n.args.size(); i++){
            syntaxtree.Exp argument = (syntaxtree.Exp) n.args.get(i);
            argument.accept(this);
        }

        // only call methods on classes
        if(!objType.isID()){
            errorMsg.error(n.pos, CompError.UndefinedMethod(n.methName, objType));
            n.type = Error;
            return Error;
        }

        // look up method in class hierarchy
        ClassDecl receiverClass = ((IDType) objType).link;
        MethodDecl method = findMethod(receiverClass, n.methName);

        // error if the method name doesn't exist in the hierarchy
        if(method == null){
            errorMsg.error(n.pos, CompError.UndefinedMethod(n.methName, objType));
            n.type = Error;
            return Error;
        }

        // link call to method definition
        n.methodLink = method;
        
        // compare the number of arguments provided to what the method requires
        if (n.args.size() != method.params.size()){
            errorMsg.error(n.pos, CompError.ParameterMismatch(n.methName, n.args.size(), method.params.size()));
        } else{
            // check if each argument's type matches the parameter's type
            for (int i = 0; i < n.args.size(); i++){
                syntaxtree.Exp argNode = (syntaxtree.Exp) n.args.get(i);
                syntaxtree.VarDecl paramNode = (syntaxtree.VarDecl) method.params.get(i);
                
                Type argType = safeType(argNode.type);
                Type paramType = paramNode.type;

                // use helper to see if the types are compatible
                if (!isSubtype(argType, paramType)) {
                    errorMsg.error(n.pos, CompError.TypeMismatch(argType, paramType));
                }
            }
        }

        // determine the return type of the whole expression
        if(method instanceof MethodDeclNonVoid nonVoidMethod){
            n.type = nonVoidMethod.rtnType;
            return n.type;
        } else {
            // void methods have no return type
            n.type = Void;
            return Void;
        }
    }

    // call used as a statement
    @Override
    public Object visit(CallStmt n){
        n.callExp.accept(this);
        return null;
    }
 
    // block
    @Override
    public Object visit(Block n){
        n.stmts.accept(this);
        return null;
    }

    // statement wrapping a local variable declaration
    @Override
    public Object visit(LocalDeclStmt n){
        n.localVarDecl.accept(this);
        return null;
    }

    // LocalVarDecl: check that the init expression type is a subtype of the declared type
    @Override
    public Object visit(LocalVarDecl n){
        Type initType = safeType((Type)n.initExp.accept(this));
        n.type.accept(this);

        if(!isSubtype(initType, n.type)){
            errorMsg.error(n.pos, CompError.TypeMismatch(initType, n.type));
        }
        return null;
    }

    // Assign: check lhs is an lvalue and rhs is a subtype of lhs type
    @Override
    public Object visit(Assign n){
        Type lhsType = safeType((Type)n.lhs.accept(this));
        Type rhsType = safeType((Type)n.rhs.accept(this));

        // lhs must be a variable, field access, or array lookup, not a literal
        if (!(n.lhs instanceof IDExp) && !(n.lhs instanceof FieldAccess) &&
            !(n.lhs instanceof ArrayLookup)){
            errorMsg.error(n.pos, CompError.Assignment());
        }
        else if(!isSubtype(rhsType, lhsType)){
            errorMsg.error(n.pos, CompError.TypeMismatch(rhsType, lhsType));
        }

        return null;
    }

    // If: condition needs to be boolean
    @Override
    public Object visit(If n){
        Type condType = safeType((Type)n.exp.accept(this));
        if (!condType.isBoolean()){
            errorMsg.error(n.pos, CompError.TypeMismatch(condType, Bool));
        }
        n.trueStmt.accept(this);
        n.falseStmt.accept(this);

        return null;
    }

    // While: condition needs to be boolean
    @Override
    public Object visit(While n){
        Type condType = safeType((Type)n.exp.accept(this));
        if (!condType.isBoolean()){
            errorMsg.error(n.pos, CompError.TypeMismatch(condType, Bool));
        }
        n.body.accept(this);

        return null;
    }

    // cast: checks if 2 types are related (1 is a parent of the other)
    @Override
    public Object visit(Cast n){
        Type exprType = safeType((Type)n.exp.accept(this));
        Type castTo = n.castType;
        // cast is legal if the types are in the same family tree
        if (!isSubtype(exprType, castTo) && !isSubtype(castTo, exprType)){
            errorMsg.error(n.pos, CompError.IncompatibleType(exprType, castTo));
        }

        n.type = castTo;
        return castTo;
    }

    // InstanceOf: checks if 2 types are related (1 is a parent of the other)
    @Override
    public Object visit(InstanceOf n) {
        Type exprType = safeType((Type)n.exp.accept(this));
        Type checkType = n.checkType;
        // cast is legal if the types are in the same family tree
        if (!isSubtype(exprType, checkType) && !isSubtype(checkType, exprType)) {
            errorMsg.error(n.pos, CompError.IncompatibleType(exprType, checkType));
        }

        n.type = Bool;
        return Bool;
    }

    // helper to check that a subclass method has the same signature as the superclass method
    private void checkOverride(MethodDecl sub, MethodDecl sup){
        // must have same void-ness
        boolean subVoid = (sub instanceof MethodDeclVoid);
        boolean supVoid = (sup instanceof MethodDeclVoid);
        if(subVoid != supVoid) {
            errorMsg.error(sub.pos, CompError.ReturnOverride());
            return;
        }

        // must have same number of parameters
        if(sub.params.size() != sup.params.size()){
            errorMsg.error(sub.pos, CompError.NumArgsOverride());
            return;
        }
 
        // each parameter type must be exact match
        for(int i = 0; i < sub.params.size(); i++){
            Type subParam = ((VarDecl) sub.params.get(i)).type;
            Type supParam = ((VarDecl) sup.params.get(i)).type;
            if(!subParam.equals(supParam)){
                errorMsg.error(sub.pos, CompError.ArgTypeOverride());
                return;
            }
        }
 
        // if both non void, return types must be exact match
        if(!subVoid){
            Type subRtn = ((MethodDeclNonVoid) sub).rtnType;
            Type supRtn = ((MethodDeclNonVoid) sup).rtnType;
            if(!subRtn.equals(supRtn)) {
                errorMsg.error(sub.pos, CompError.ReturnOverride());
            }
        }
    }
 
    // void method declaration
    @Override
    public Object visit(MethodDeclVoid n)
    {
        // check if this overrides a superclass method
        if(currentClass != null && currentClass.superLink != null){
            MethodDecl supMethod = findMethod(currentClass.superLink, n.name);
            if(supMethod != null){
                n.superMethod = supMethod;
                checkOverride(n, supMethod);
            }
        }

        n.stmts.accept(this);
        return null;
    }
 
    // non void method declaration
    @Override
    public Object visit(MethodDeclNonVoid n)
    {
        // check if this overrides a superclass method
        if(currentClass != null && currentClass.superLink != null){
            MethodDecl supMethod = findMethod(currentClass.superLink, n.name);
            if(supMethod != null){
                n.superMethod = supMethod;
                checkOverride(n, supMethod);
            }
        }

        n.params.accept(this);
        n.stmts.accept(this);

        Type rtnExpType = safeType((Type)n.rtnExp.accept(this));
        if(!isSubtype(rtnExpType, n.rtnType)){
            errorMsg.error(n.pos, CompError.Subtype(rtnExpType, n.rtnType));
        }
        return null;
    }
    
}


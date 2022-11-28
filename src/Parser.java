import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Parser {

    private Symbol lookAhead; //look ahead token creating the predictive parsing
    //here we store the look ahead, we need it in case it goes to far, if to far => can't go backward
    private Symbol actualToken;
    private LexicalUnit lookAheadType; //lexical unit of the look ahead for the switch/cases
    final LexicalAnalyzer lexer; //lexical analyzer implemented in first part of project
    private ArrayList<Integer> usedRules = new ArrayList<>(); //list storing the rules leading to the final derivation tree

    public Parser(FileReader source){ lexer = new LexicalAnalyzer(source);}

    /**
     * print the sequence of rules used to parse and make the derivation tree
     * of the input files
     */
    public void printUsedRules(){
        for(Integer integer: usedRules){
            System.out.print(integer + " ");
        }
    }

    /**
     * function corresponding to the non-terminal Program
     * [1] Program           → BEGIN [ProgName] Code END
     * @return a node which parent is Program  and the
     */
    public ParseTree Program(){
        ArrayList<ParseTree> chldn = new ArrayList<>();
        getNextToken();
        switch (lookAheadType){
            case BEGIN:
                usedRules.add(1);
                chldn.add(match(LexicalUnit.BEGIN));
                getNextToken();
                chldn.add(match(LexicalUnit.PROGNAME));
                chldn.add(Code());
                getNextToken();
                chldn.add(match(LexicalUnit.END));
                break;
            default:
                syntaxError("BEGIN expected");
                break;

        }
        return new ParseTree(new Symbol("Program"), chldn);
    }

    /**
     * function corresponding to the non terminal Code
     * [2] Code                → Instruction CodeF
     * [3]                     → ε
     * @return a node which parent is
     */
    private ParseTree Code() {
        ArrayList<ParseTree> chldn = new ArrayList<>();
        getNextToken();
        switch (lookAheadType){
            case END:
            case ELSE:
                usedRules.add(3);
                System.out.println("$\\varepsilon$");
                chldn.add(new ParseTree(new Symbol("$\\varepsilon$")));
                return new ParseTree(new Symbol("code"), chldn);
            default:
        }
        usedRules.add(2);
        chldn.add(Instruction()); chldn.add(CodeF());

        return new ParseTree(new Symbol("Code"), chldn);
    }

    private ParseTree CodeF() {
        ArrayList<ParseTree> chldn = new ArrayList<>();
        getNextToken();
        switch (lookAheadType){
            case END:
            case ELSE:
                usedRules.add(5);
                System.out.println("$\\varepsilon$");
                chldn.add(new ParseTree(new Symbol("$\\varepsilon$")));
                return new ParseTree(new Symbol("CodeF"), chldn);
            case COMMA:
                usedRules.add(4);
                chldn.add(match(LexicalUnit.COMMA));
                chldn.add(Code());
                break;
            default:
                syntaxError("CodeF");
                break;
        }
        return new ParseTree(new Symbol("CodeF"), chldn);
    }

    private ParseTree Instruction() {
        ArrayList<ParseTree> chldn = new ArrayList<>();
        getNextToken();
        switch(lookAheadType){
            case VARNAME:
                usedRules.add(6);
                chldn.add(Assign());
                break;
            case IF:
                usedRules.add(7);
                chldn.add(If());
                break;
            case WHILE:
                usedRules.add(8);
                chldn.add(While());
                break;
            case PRINT:
                usedRules.add(9);
                chldn.add(Print());
                break;
            case READ:
                usedRules.add(10);
                chldn.add(Read());
                break;
            default:
                syntaxError("Instruction variable expected");
                break;
        }
        return new ParseTree(new Symbol("Instruction"), chldn);
    }

    private ParseTree Assign(){
        ArrayList<ParseTree> chldn = new ArrayList<>();
        getNextToken();
        switch (lookAheadType){
            case VARNAME:
                usedRules.add(11);
                chldn.add(match(LexicalUnit.VARNAME));
                getNextToken();
                chldn.add(match(LexicalUnit.ASSIGN));
                getNextToken();
                chldn.add(ExprArith());
                break;
            default:
                syntaxError("VarName expected");
                break;
        }
        return new ParseTree(new Symbol("Assign"), chldn);
    }

    private ParseTree If(){
        ArrayList<ParseTree> chldn = new ArrayList<>();
        getNextToken();
        switch (lookAheadType){
            case IF:
                usedRules.add(12);
                chldn.add(match(LexicalUnit.IF));
                getNextToken();
                chldn.add(match(LexicalUnit.LPAREN));
                chldn.add(Cond());
                getNextToken();
                chldn.add(match(LexicalUnit.RPAREN));
                getNextToken();
                chldn.add(match(LexicalUnit.THEN));
                chldn.add(Code());
                chldn.add(IfSeq());
                break;
            default:
                syntaxError("If exepected");
                break;
        }
        return new ParseTree(new Symbol("If"), chldn);
    }

    private ParseTree IfSeq(){
        ArrayList<ParseTree> chldn = new ArrayList<>();
        getNextToken();
        switch (lookAheadType){
            case END:
                usedRules.add(13);
                chldn.add(match(LexicalUnit.END));
                break;
            case ELSE:
                usedRules.add(14);
                chldn.add(match(LexicalUnit.ELSE));
                chldn.add(Code());
                getNextToken();
                chldn.add(match(LexicalUnit.END));
                break;
            default:
                syntaxError("END or ELSE Expected");
                break;
        }

        return new ParseTree(new Symbol("IfSeq"), chldn);

    }

    private ParseTree While(){
        ArrayList<ParseTree> chldn = new ArrayList<>();
        getNextToken();
        switch (lookAheadType){
            case WHILE:
                usedRules.add(15);
                chldn.add(match(LexicalUnit.WHILE));
                getNextToken();
                chldn.add(match(LexicalUnit.LPAREN));
                getNextToken();
                chldn.add(Cond());
                getNextToken();
                chldn.add(match(LexicalUnit.RPAREN));
                getNextToken();
                chldn.add(match(LexicalUnit.DO));
                chldn.add(Code());
                getNextToken();
                chldn.add(match(LexicalUnit.END));
                break;
            default:
                syntaxError("An Error occured at while function");
                break;
        }
        return new ParseTree(new Symbol("While"), chldn);
    }

    private ParseTree Cond(){
        ArrayList<ParseTree> chldn = new ArrayList<>();
        usedRules.add(16);
        chldn.add(ExprArith());
        chldn.add(Comp());
        return new ParseTree(new Symbol("Cond"), chldn);

    }

    private ParseTree Comp(){
        ArrayList<ParseTree> chldn = new ArrayList<>();
        getNextToken();
        switch (lookAheadType){
            case EQUAL:
                usedRules.add(17);
                chldn.add(match(LexicalUnit.EQUAL));
                getNextToken();
                chldn.add(ExprArith());
                break;
            case GREATER:
                usedRules.add(18);
                chldn.add(match(LexicalUnit.GREATER));
                getNextToken();
                chldn.add(ExprArith());
                break;
            case SMALLER:
                usedRules.add(19);
                chldn.add(match(LexicalUnit.SMALLER));
                getNextToken();
                chldn.add(ExprArith());
                break;
            default:
                syntaxError("kek");
                break;
        }
        return new ParseTree(new Symbol("Comp"), chldn);

    }

    private ParseTree ExprArith(){
        ArrayList<ParseTree> chldn = new ArrayList<>();
        usedRules.add(20);
        chldn.add(Prod());
        chldn.add(ExprArithF());
        return new ParseTree(new Symbol("ExprArith"), chldn);
    }

    private ParseTree ExprArithF(){
        ArrayList<ParseTree> chldn = new ArrayList<>();
        getNextToken();
        switch (lookAheadType){
            case END:
            case COMMA:
            case ELSE:
            case EQUAL:
            case GREATER:
            case SMALLER:
            case RPAREN:
                usedRules.add(23);
                chldn.add(new ParseTree(new Symbol("$\\varepsilon$")));
                return new ParseTree(new Symbol("ExprArithF"), chldn);
            case PLUS:
                usedRules.add(21);
                chldn.add(match(LexicalUnit.PLUS));
                getNextToken();
                chldn.add(Prod());
                chldn.add(ExprArithF());
                break;
            case MINUS:
                usedRules.add(22);
                chldn.add(match(LexicalUnit.MINUS));
                getNextToken();
                chldn.add(Prod());
                chldn.add(ExprArithF());
                break;
            default:
                syntaxError("kek2");
                break;
        }
        return new ParseTree(new Symbol("ExprArithF"), chldn);
    }

    private ParseTree Prod(){
        ArrayList<ParseTree> chldn = new ArrayList<>();
        usedRules.add(24);
        chldn.add(Atom());
        chldn.add(ProdF());
        return new ParseTree(new Symbol("Prod"), chldn);
    }

    private ParseTree ProdF(){
        ArrayList<ParseTree> chldn = new ArrayList<>();
        getNextToken();
        switch (lookAheadType){
            case END:
            case ELSE:
            case COMMA:
            case EQUAL:
            case GREATER:
            case SMALLER:
            case PLUS:
            case MINUS:
            case RPAREN:
                usedRules.add(27);
                chldn.add(new ParseTree(new Symbol("$\\varepsilon$")));
                return new ParseTree(new Symbol("ProdF"), chldn);
            case TIMES:
                usedRules.add(25);
                chldn.add(match(LexicalUnit.TIMES));
                chldn.add(Atom());
                chldn.add(ProdF());
                break;
            case DIVIDE:
                usedRules.add(26);
                chldn.add(match(LexicalUnit.DIVIDE));
                chldn.add(Atom());
                chldn.add(ProdF());
                break;
            default:
                syntaxError("kek");
                break;
        }
        return new ParseTree(new Symbol("ProdF"), chldn);
    }

    private ParseTree Atom(){
        ArrayList<ParseTree> chldn = new ArrayList<>();
        getNextToken();
        switch (lookAheadType){
            case MINUS:
                usedRules.add(28);
                chldn.add(match(LexicalUnit.MINUS));
                chldn.add(Atom());
                break;
            case LPAREN:
                usedRules.add(29);
                chldn.add(match(LexicalUnit.LPAREN));
                chldn.add(ExprArith());
                getNextToken();
                chldn.add(match(LexicalUnit.RPAREN));
                break;
            case NUMBER:
                usedRules.add(30);
                chldn.add(match(LexicalUnit.NUMBER));
                break;
            case VARNAME:
                usedRules.add(31);
                chldn.add(match(LexicalUnit.VARNAME));
                break;
            default:
                syntaxError("Atom");
                break;
        }

        return new ParseTree(new Symbol("Atom"), chldn);
    }

    private ParseTree Print(){
        ArrayList<ParseTree> chldn = new ArrayList<>();
        getNextToken();
        switch (lookAheadType){
            case PRINT:
                usedRules.add(32);
                chldn.add(match(LexicalUnit.PRINT));
                getNextToken();
                chldn.add(match(LexicalUnit.LPAREN));
                getNextToken();
                chldn.add(match(LexicalUnit.VARNAME));
                getNextToken();
                chldn.add(match(LexicalUnit.RPAREN));
                break;
            default:
                syntaxError("Print()");
                break;
        }
        return new ParseTree(new Symbol("Print"), chldn);
    }

    private ParseTree Read(){
        ArrayList<ParseTree> chldn = new ArrayList<>();
        getNextToken();
        switch (lookAheadType){
            case READ:
                usedRules.add(33);
                chldn.add(match(LexicalUnit.READ));
                getNextToken();
                chldn.add(match(LexicalUnit.LPAREN));
                getNextToken();
                chldn.add(match(LexicalUnit.VARNAME));
                getNextToken();
                chldn.add(match(LexicalUnit.RPAREN));
                break;
            default:
                syntaxError("Read()");
                break;
        }
        return new ParseTree(new Symbol("Read"), chldn);
    }

    private ParseTree match(LexicalUnit expectedToken){
        if(expectedToken.equals(lookAheadType)){
            System.out.println(lookAheadType);
            ParseTree parent = new ParseTree(lookAhead);
            actualToken = lookAhead;
            return parent;
        }else
            syntaxError(lookAhead.toString());

        return null;
    }

    private void getNextToken(){
        // we get the next token only if the lookahead has already been treated
        // as we can't go backward, we must have a limiting variable
        if(actualToken == null || actualToken.equals(lookAhead)){
            try{
                lookAhead = lexer.nextToken();
            }catch (IOException e){
                e.printStackTrace();
            }
            lookAheadType = lookAhead.getType();
        }

    }

    private void syntaxError(String c){
        printUsedRules();
        System.err.println("An error occured," + c);
        System.exit(1);
    }
}

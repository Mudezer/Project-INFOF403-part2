import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Parser {

    private Symbol lookAhead;
    private LexicalUnit lookAheadType;
    final LexicalAnalyzer lexer;
    private ArrayList<Integer> usedRules = new ArrayList<>();

    public Parser(FileReader source){ lexer = new LexicalAnalyzer(source);}

    public void printUsedRules(){
        for(Integer integer: usedRules){
            System.out.println(integer);
        }
    }

    public ParseTree PROGRAM(){
        ArrayList<ParseTree> chldn = new ArrayList<>();
        getNextToken();
        switch (lookAheadType){
            case BEGIN:
                usedRules.add(1);
                if(lookAheadType.equals(LexicalUnit.BEGIN)){
                    System.out.println(lookAheadType);
                    ParseTree parent = new ParseTree(lookAhead);
                    chldn.add(parent);
                }else{
                    syntaxError(lookAhead.toString());
                }
                getNextToken();
                if(lookAheadType.equals(LexicalUnit.PROGNAME)){
                    ParseTree parent = new ParseTree(lookAhead);

                }else {
                    syntaxError(lookAhead.toString());
                }
                chldn.add(Code());
            default:
                syntaxError("BEGIN expected");


        }
        return new ParseTree(new Symbol(lookAhead), chldn);
    }

    private ParseTree Code() {
        ArrayList<ParseTree> chldn = new ArrayList<>();
        getNextToken();
        switch (lookAheadType){
            case END:
                usedRules.add(3);
                return null;
            case ELSE:
                usedRules.add(3);
                return null;
        }
        usedRules.add(2);
        chldn.add(Instruction()); chldn.add(CodeF());
        


    }

    private ParseTree CodeF() {
    }

    private ParseTree Instruction() {
        return null;
    }


    private void getNextToken(){
        try{
            lookAhead = lexer.nextToken();
        }catch (IOException e){
            e.printStackTrace();
        }
        lookAheadType = lookAhead.getType();
    }


    private void syntaxError(String c){
        System.err.println("An error occured," + c);
        System.exit(1);
    }
}

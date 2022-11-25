import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Parser {

    private Symbol lookAhead;
    private Symbol actualToken;
    private LexicalUnit type;
    final LexicalAnalyzer lexer;
    private ArrayList<Integer> usedRules = new ArrayList<>();

    public Parser(FileReader source){ lexer = new LexicalAnalyzer(source);}

    public ParseTree Program(){
        ArrayList<ParseTree> chldn = new ArrayList<>();
        getNextToken();
        switch(type){
            case BEGIN:
                usedRules.add(1);
                match(type);
        }
        for(Integer i: usedRules){
            System.out.println(i.toString());
        }
        return null;
    }

    private void match(LexicalUnit expectedToken) {
        if(expectedToken.equals(type)){
            System.out.println(type);
        }



    }


    private void getNextToken(){
        if(actualToken == null || actualToken.equals(lookAhead)){
            try{
                lookAhead = lexer.nextToken();
            }catch (IOException e){
                e.printStackTrace();
            }
            actualToken = lookAhead;
            type = actualToken.getType();
        }

    }
}

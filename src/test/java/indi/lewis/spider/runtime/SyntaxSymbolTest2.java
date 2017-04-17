package indi.lewis.spider.runtime;

import indi.lewis.spider.runtime.symbol.Quote;

/**
 * Created by lewis on 17-4-13.
 */
public class SyntaxSymbolTest2 {

    public static void main(String[] args){
        System.out.print(SyntaxSymbol.class.isAssignableFrom(Quote.class));
    }

}

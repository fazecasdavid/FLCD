package com.fd;

import com.fd.model.Grammar;

public class Main {

    public static void main(final String[] args) {
        final Grammar grammar = Grammar.readFromFile("in/g2.txt");
        System.out.println(grammar.getNonterminals());
        System.out.println(grammar.getTerminals());
        grammar.getProductions().forEach(System.out::println);
        System.out.println(grammar.getProductionsForNonterminal("A"));
    }

}

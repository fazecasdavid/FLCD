package com.fd;

import com.fd.model.Grammar;
import com.fd.model.Parser;
import lombok.SneakyThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Main {

    @SneakyThrows
    public static void main(final String[] args) {
        final Grammar grammar = Grammar.readFromFile("in/g2.txt");

//        System.out.println(grammar.getNonterminals());
//        System.out.println(grammar.getTerminals());
//        grammar.getProductions().forEach(System.out::println);
//        System.out.println(grammar.getProductionsForNonterminal("Program"));

        System.out.printf(
            "%nProduction string: %s%n",
            Parser.parse(grammar, Files.readAllLines(Path.of("in/program.txt")))
        );

//        System.out.printf(
//            "%nProduction string: %s%n",
////            Parser.parse(grammar, List.of("b", "b", "a")) // this one crashes
////            Parser.parse(grammar, List.of("b", "a", "a", "b", "a", "a")) // this one also crashes
////            Parser.parse(grammar, List.of("b", "a", "a", "b", "b", "a"))
////            Parser.parse(grammar, List.of("a", "b", "a", "a", "b", "b"))
//            Parser.parse(grammar, List.of("a", "b", "b", "a"))
//        );
//
    }

}

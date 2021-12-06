package com.fd;

import com.fd.model.Grammar;
import com.fd.model.parser.Parser;
import com.fd.model.parser.output.ParserOutput;
import lombok.SneakyThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Main {

    @SneakyThrows
    public static void main(final String[] args) {
        final Grammar grammar = Grammar.readFromFile("in/g4.txt");

//        System.out.println(grammar.getNonterminals());
//        System.out.println(grammar.getTerminals());
//        grammar.getProductions().forEach(System.out::println);
//        System.out.println(grammar.getProductionsForNonterminal("Program"));

//        final ParserOutput parserOutput = Parser.parse(grammar, List.of("a", "b", "a", "a", "b", "b"));
//        final ParserOutput parserOutput = Parser.parse(grammar, List.of("b", "a", "b", "b", "a", "a"));
//        final ParserOutput parserOutput = Parser.parse(grammar, List.of("a", "a", "b", "b", "a", "b"));
//        final ParserOutput parserOutput = Parser.parse(grammar, List.of("b", "a", "a", "b", "b", "a"));
//        final ParserOutput parserOutput = Parser.parse(grammar, List.of("b", "b", "a"));

        final ParserOutput parserOutput = Parser.parse(grammar, List.of("a", "a", "c", "b", "c"));

//        final ParserOutput parserOutput = Parser.parse(grammar, Files.readAllLines(Path.of("in/program.txt")));

        System.out.println();
        parserOutput.printToScreen();
        parserOutput.printToFile("output.txt");
    }

}

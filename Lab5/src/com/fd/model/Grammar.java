package com.fd.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class Grammar {

    // N
    private final Set<String> nonterminals;

    // Σ
    private final Set<String> terminals;

    // P
    private final List<Production> productions;

    // S
    private final String startingSymbol;

    public static Grammar readFromFile(final String filename) {
        try (final BufferedReader bufferedReader = new BufferedReader(new FileReader(filename))) {
            final Set<String> nonterminals = Set.of(bufferedReader.readLine().strip().split("\\s+"));
            final Set<String> terminals = Set.of(bufferedReader.readLine().strip().split("\\s+"));
            final String startingSymbol = bufferedReader.readLine().strip();
            final List<Production> productions = new ArrayList<>();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                line = line.strip();
                if (line.isEmpty()) continue;
                final String[] tokens = line.split("->");
                final String[] leftSide = tokens[0].split("\\s+");
                if (leftSide.length != 1) {
                    throw new RuntimeException("The given grammar is not context-free!");
                }
                final String[] rightSide = tokens[1].split("\\|");
                final List<List<String>> productionRules = new ArrayList<>();
                for (final String rule : rightSide) {
                    productionRules.add(List.of(rule.strip().split("\\s+")));
                }
                final Optional<Production> productionOptional = productions.stream().filter(production -> production.getStartingNonterminal().equals(leftSide[0])).findAny();
                if (productionOptional.isPresent()) {
                    final Production production = productionOptional.get();
                    production
                        .getRules()
                        .addAll(
                            productionRules
                                .stream()
                                .filter(rule -> !productionOptional.get().getRules().contains(rule))
                                .collect(Collectors.toList())
                        );
                } else {
                    productions.add(new Production(leftSide[0], productionRules));
                }
            }
            final Grammar grammar = new Grammar(nonterminals, terminals, productions, startingSymbol);
            grammar.validate();
            return grammar;
        } catch (final IOException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("Could not read grammar from file!");
    }

    public Production getProductionsForNonterminal(final String nonterminal) {
        if (!nonterminals.contains(nonterminal)) {
            throw new RuntimeException(String.format("Given nonterminal: %s is not one of the nonterminals of the grammar.", nonterminal));
        }
        return productions
            .stream()
            .filter(production -> production.getStartingNonterminal().equals(nonterminal))
            .findAny()
            .orElse(null);
    }

    private void validate() {
        if (!nonterminals.contains(startingSymbol)) {
            throw new RuntimeException("Starting symbol is not one of the nonterminals.");
        }
        productions.forEach(production -> {
            if (!nonterminals.contains(production.getStartingNonterminal())) {
                throw new RuntimeException(String.format("Starting nonterminal %s from production %s is not one of the nonterminals.", production.getStartingNonterminal(), production));
            }
            production.getRules().forEach(rule -> rule.forEach(symbol -> {
                if (!(terminals.contains(symbol) || nonterminals.contains(symbol))) {
                    throw new RuntimeException(String.format("Symbol %s from production %s is neither terminal, nor nonterminal.", symbol, production));
                }
            }));
        });
    }

    @Data
    @AllArgsConstructor
    public static class Production {

        private final String startingNonterminal;

        private final List<List<String>> rules;

    }

}

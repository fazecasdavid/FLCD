package com.fd.model.parser.output;

import com.fd.model.Grammar;
import com.fd.model.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DerivationStringOutput extends ParserOutput {

    public DerivationStringOutput(final List<Pair<String, Integer>> workingStack, final Grammar grammar) {
        super(workingStack, grammar);
    }

    @Override
    public String getOutputAsString() {
        final List<Pair<String, Integer>> productions = workingStack
            .stream()
            .filter(pair -> pair.getSecond() != -1)
            .collect(Collectors.toList());

        final Pair<String, Integer> firstProduction = productions.get(0);
        final List<List<String>> derivations = new ArrayList<>();
        derivations.add(grammar.getProductionsForNonterminal(firstProduction.getFirst()).getRules().get(firstProduction.getSecond()));

        for (int i = 1; i < productions.size(); ++i) {
            final List<String> derivation = derivations.get(i - 1);
            final String firstNonterminal = derivation.stream().filter(grammar::isNonterminal).findFirst().orElseThrow();
            final List<String> newDerivation = new ArrayList<>();
            newDerivation.addAll(derivation.subList(0, derivation.indexOf(firstNonterminal)));
            newDerivation.addAll(grammar.getProductionsForNonterminal(productions.get(i).getFirst()).getRules().get(productions.get(i).getSecond()));
            newDerivation.addAll(derivation.subList(derivation.indexOf(firstNonterminal) + 1, derivation.size()));
            derivations.add(newDerivation);
        }

        return derivations.stream().map(derivation -> String.join("", derivation)).collect(Collectors.joining(" => "));
    }

}

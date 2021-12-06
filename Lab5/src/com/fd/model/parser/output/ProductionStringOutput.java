package com.fd.model.parser.output;

import com.fd.model.Grammar;
import com.fd.model.Pair;

import java.util.List;
import java.util.stream.Collectors;

public class ProductionStringOutput extends ParserOutput {

    public ProductionStringOutput(final List<Pair<String, Integer>> workingStack, final Grammar grammar) {
        super(workingStack, grammar);
    }

    @Override
    public String getOutputAsString() {
        return workingStack
            .stream()
            .filter(pair -> pair.getSecond() != -1)
            .map(pair ->
                String.format(
                    "%s -> %s",
                    pair.getFirst(),
                    String.join("", grammar.getProductionsForNonterminal(pair.getFirst()).getRules().get(pair.getSecond()))
                )
            )
            .collect(Collectors.joining(", "));
    }

}

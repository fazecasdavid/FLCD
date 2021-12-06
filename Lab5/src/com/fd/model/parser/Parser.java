package com.fd.model.parser;

import com.fd.model.Grammar;
import com.fd.model.Pair;
import com.fd.model.parser.output.DerivationStringOutput;
import com.fd.model.parser.output.ParserOutput;
import com.fd.model.parser.output.ProductionStringOutput;
import com.fd.model.parser.output.TableOutput;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Parser {

    public static ParserOutput parse(final Grammar grammar, final List<String> inputSequence) {
        Configuration configuration = new Configuration(
            Configuration.ParsingState.NORMAL,
            0,
            new ArrayList<>(),
            new ArrayList<>(List.of(grammar.getStartingSymbol()))
        );

        while (!configuration.getParsingState().equals(Configuration.ParsingState.FINAL) && !configuration.getParsingState().equals(Configuration.ParsingState.ERROR)) {
            System.out.print(configuration);
            if (configuration.getParsingState().equals(Configuration.ParsingState.NORMAL)) {
                if (configuration.getCurrentSymbolPosition() == inputSequence.size() && configuration.getInputStack().isEmpty()) {
                    configuration = success(configuration);
                } else {
                    if (configuration.getInputStack().isEmpty() || configuration.getCurrentSymbolPosition() >= inputSequence.size()) {
                        configuration = momentaryInsuccess(configuration);
                    } else {
                        final String headOfInputStack = configuration.getInputStackHead();
                        if (grammar.isNonterminal(headOfInputStack)) {
                            configuration = expand(configuration, grammar);
                        } else {
                            if (headOfInputStack.equals(inputSequence.get(configuration.getCurrentSymbolPosition()))) {
                                configuration = advance(configuration);
                            } else {
                                configuration = momentaryInsuccess(configuration);
                            }
                        }
                    }
                }
            } else {
                if (configuration.getParsingState().equals(Configuration.ParsingState.BACK)) {
                    if (grammar.isTerminal(configuration.getWorkingStackHead().getFirst())) {
                        configuration = back(configuration);
                    } else {
                        configuration = anotherTry(configuration, grammar);
                    }
                }
            }
        }

        System.out.print(configuration);
        System.out.println();

        if (configuration.getParsingState().equals(Configuration.ParsingState.ERROR)) {
            throw new RuntimeException("Error.");
        }

        return new TableOutput(configuration.getWorkingStack(), grammar);
    }

    private static Configuration success(final Configuration configuration) {
        System.out.println(" -> success");

        return new Configuration(
            Configuration.ParsingState.FINAL,
            configuration.getCurrentSymbolPosition(),
            configuration.getWorkingStack(),
            configuration.getInputStack()
        );
    }

    private static Configuration expand(final Configuration configuration, final Grammar grammar) {
        System.out.println(" -> expand");

        final List<Pair<String, Integer>> newWorkingStack = new ArrayList<>(configuration.getWorkingStack());
        newWorkingStack.add(new Pair<>(configuration.getInputStackHead(), 0));

        final List<String> newInputStack = new ArrayList<>(grammar.getProductionsForNonterminal(configuration.getInputStackHead()).getRules().get(0));
        final List<String> auxiliaryList = configuration.getInputStack();
        auxiliaryList.remove(0);
        newInputStack.addAll(auxiliaryList);

        return new Configuration(
            Configuration.ParsingState.NORMAL,
            configuration.getCurrentSymbolPosition(),
            newWorkingStack,
            newInputStack
        );
    }

    private static Configuration advance(final Configuration configuration) {
        System.out.println(" -> advance");

        final List<Pair<String, Integer>> newWorkingStack = new ArrayList<>(configuration.getWorkingStack());
        newWorkingStack.add(new Pair<>(configuration.getInputStackHead(), -1));

        final List<String> newInputStack = new ArrayList<>(configuration.getInputStack());
        newInputStack.remove(0);

        return new Configuration(
            Configuration.ParsingState.NORMAL,
            configuration.getCurrentSymbolPosition() + 1,
            newWorkingStack,
            newInputStack
        );
    }

    private static Configuration momentaryInsuccess(final Configuration configuration) {
        System.out.println(" -> momentary insuccess");

        return new Configuration(
            Configuration.ParsingState.BACK,
            configuration.getCurrentSymbolPosition(),
            configuration.getWorkingStack(),
            configuration.getInputStack()
        );
    }

    private static Configuration back(final Configuration configuration) {
        System.out.println(" -> back");

        final List<Pair<String, Integer>> newWorkingStack = new ArrayList<>(configuration.getWorkingStack());

        final Pair<String, Integer> symbol = newWorkingStack.remove(newWorkingStack.size() - 1);
        final List<String> newInputStack = new ArrayList<>(configuration.getInputStack());
        newInputStack.add(0, symbol.getFirst());

        return new Configuration(
            Configuration.ParsingState.BACK,
            configuration.getCurrentSymbolPosition() - 1,
            newWorkingStack,
            newInputStack
        );
    }

    private static Configuration anotherTry(final Configuration configuration, final Grammar grammar) {
        System.out.println(" -> another try");

        final Pair<String, Integer> A = configuration.getWorkingStackHead();

        final Grammar.Production production = grammar.getProductionsForNonterminal(A.getFirst());
        if (A.getSecond() + 1 < production.getRules().size()) {
            final List<Pair<String, Integer>> newWorkingStack = new ArrayList<>(configuration.getWorkingStack());
            newWorkingStack.remove(newWorkingStack.size() - 1);
            newWorkingStack.add(new Pair<>(A.getFirst(), A.getSecond() + 1));

            List<String> newInputStack = new ArrayList<>(configuration.getInputStack());
            if (production.getRules().get(A.getSecond()).size() > 0) {
                newInputStack.subList(0, production.getRules().get(A.getSecond()).size()).clear();
            }
            List<String> auxiliaryList = new ArrayList<>(production.getRules().get(A.getSecond() + 1));
            auxiliaryList.addAll(newInputStack);
            newInputStack = auxiliaryList;

            return new Configuration(
                Configuration.ParsingState.NORMAL,
                configuration.getCurrentSymbolPosition(),
                newWorkingStack,
                newInputStack
            );
        } else {
            if (configuration.getCurrentSymbolPosition() == 0 && A.getFirst().equals(grammar.getStartingSymbol())) {
                return new Configuration(
                    Configuration.ParsingState.ERROR,
                    configuration.getCurrentSymbolPosition(),
                    configuration.getWorkingStack(),
                    configuration.getInputStack()
                );
            }

            final List<Pair<String, Integer>> newWorkingStack = new ArrayList<>(configuration.getWorkingStack());
            newWorkingStack.remove(newWorkingStack.size() - 1);

            final List<String> newInputStack = new ArrayList<>(configuration.getInputStack());
            if (production.getRules().get(A.getSecond()).size() > 0) {
                newInputStack.subList(0, production.getRules().get(A.getSecond()).size()).clear();
            }
            newInputStack.add(0, A.getFirst());

            return new Configuration(
                Configuration.ParsingState.BACK,
                configuration.getCurrentSymbolPosition(),
                newWorkingStack,
                newInputStack
            );
        }
    }

    @Data
    private static class Configuration {

        private final ParsingState parsingState;

        private final int currentSymbolPosition;

        private final List<Pair<String, Integer>> workingStack;

        private final List<String> inputStack;

        public Pair<String, Integer> getWorkingStackHead() {
            return workingStack.get(workingStack.size() - 1);
        }

        public String getInputStackHead() {
            return inputStack.get(0);
        }

        @Override
        public String toString() {
            final StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("(");
            stringBuilder.append(parsingState.description).append(", ");
            stringBuilder.append(currentSymbolPosition + 1).append(", ");
            if (workingStack.isEmpty()) {
                stringBuilder.append("ε").append(", ");
            } else {
                stringBuilder.append(
                    workingStack.stream().map(pair -> {
                        if (pair.getSecond() == -1) {
                            return pair.getFirst();
                        } else {
                            return String.format("%s%s", pair.getFirst(), pair.getSecond() + 1);
                        }
                    }).collect(Collectors.joining(" "))
                ).append(", ");
            }
            if (inputStack.isEmpty()) {
                stringBuilder.append("ε");
            } else {
                stringBuilder.append(String.join(" ", inputStack));
            }
            stringBuilder.append(")");
            return stringBuilder.toString();
        }

        enum ParsingState {

            NORMAL("q"), BACK("b"), FINAL("f"), ERROR("e");

            private final String description;

            ParsingState(final String description) {
                this.description = description;
            }

        }

    }

}

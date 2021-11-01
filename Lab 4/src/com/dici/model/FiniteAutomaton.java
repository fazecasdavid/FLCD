package com.dici.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FiniteAutomaton {

    private final List<String> states;
    private final List<String> alphabet;
    private final String initialState;
    private final List<String> finalStates;
    private final Map<Pair<String, String>, List<String>> transitions;

    public FiniteAutomaton(final List<String> states, final List<String> alphabet, final String initialState, final List<String> finalStates, final Map<Pair<String, String>, List<String>> transitions) {
        this.states = states;
        this.alphabet = alphabet;
        this.initialState = initialState;
        this.finalStates = finalStates;
        this.transitions = transitions;
    }

    public static FiniteAutomaton readFromFile(final String filename) {
        try (final BufferedReader bufferedReader = new BufferedReader(new FileReader(filename))) {
            final List<String> states = Arrays.stream(bufferedReader.readLine().strip().split("\\s+")).collect(Collectors.toList());
            final List<String> alphabet = Arrays.stream(bufferedReader.readLine().strip().split("\\s+")).collect(Collectors.toList());
            final String initialState = bufferedReader.readLine().strip();
            final List<String> finalStates = Arrays.stream(bufferedReader.readLine().strip().split("\\s+")).collect(Collectors.toList());

            final Map<Pair<String, String>, List<String>> transitions = new LinkedHashMap<>();

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                final String[] parts = line.replaceAll("\\s+", "").split("->");
                final String[] firstPartTokens = parts[0].substring(1, parts[0].length() - 1).split(",");
                final Pair<String, String> sourceAndRoute = new Pair<>(firstPartTokens[0], firstPartTokens[1]);
                final String destination = parts[1];
                if (transitions.containsKey(sourceAndRoute)) {
                    transitions.get(sourceAndRoute).add(destination);
                } else {
                    transitions.put(sourceAndRoute, new ArrayList<>(Collections.singletonList(destination)));
                }
            }

            final FiniteAutomaton fa = new FiniteAutomaton(states, alphabet, initialState, finalStates, transitions);
            fa.validate();
            return fa;
        } catch (final IOException e) {
            e.printStackTrace();
        }

        throw new RuntimeException("Could not read FA from file.");
    }

    public boolean isDFA() {
        for (final List<String> destinations : transitions.values()) {
            if (destinations.size() > 1) {
                return false;
            }
        }
        return true;
    }

    private void validate() {
        if (!states.contains(initialState)) {
            throw new RuntimeException(String.format("Initial state ('%s') not part of the available states.", initialState));
        }
        for (final String finalState : finalStates) {
            if (!states.contains(initialState)) {
                throw new RuntimeException(String.format("Final state ('%s') not part of the available states.", finalState));
            }
        }
        for (final Map.Entry<Pair<String, String>, List<String>> transition : transitions.entrySet()) {
            final Pair<String, String> sourceAndRoute = transition.getKey();
            final List<String> destinations = transition.getValue();
            if (!states.contains(sourceAndRoute.getFirst())) {
                throw new RuntimeException(String.format("Source state ('%s') not part of the available states.", sourceAndRoute.getFirst()));
            }
            if (!alphabet.contains(sourceAndRoute.getSecond())) {
                throw new RuntimeException(String.format("Route ('%s') not part of the alphabet.", sourceAndRoute.getSecond()));
            }
            for (final String destination : destinations) {
                if (!states.contains(destination)) {
                    throw new RuntimeException(String.format("Destination state ('%s') not part of the available states.", destination));
                }
            }
        }
    }

    public boolean accepts(final String sequence) {
        if (!isDFA()) {
            throw new RuntimeException("This FA is not a DFA.");
        }

        String currentState = initialState;
        for (final char symbol : sequence.toCharArray()) {
            final Pair<String, String> sourceAndRoute = new Pair<>(currentState, String.valueOf(symbol));
            if (transitions.containsKey(sourceAndRoute)) {
                currentState = transitions.get(sourceAndRoute).get(0);
            } else {
                return false;
            }
        }

        return finalStates.contains(currentState);
    }

    public List<String> getStates() {
        return states;
    }

    public List<String> getAlphabet() {
        return alphabet;
    }

    public String getInitialState() {
        return initialState;
    }

    public List<String> getFinalStates() {
        return finalStates;
    }

    public Map<Pair<String, String>, List<String>> getTransitions() {
        return transitions;
    }

    @Override
    public String toString() {
        return "FiniteAutomaton{" +
            "\n\tstates=" + states +
            ", \n\talphabet=" + alphabet +
            ", \n\tinitialState='" + initialState + '\'' +
            ", \n\tfinalStates=" + finalStates +
            ", \n\ttransitions=" + transitions +
            "\n}";
    }

}
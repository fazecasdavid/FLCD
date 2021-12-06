package com.fd.model.parser.output;

import com.fd.model.Grammar;
import com.fd.model.Pair;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.UUID;
import java.util.stream.Collectors;

public class TableOutput extends ParserOutput {

    public TableOutput(final List<Pair<String, Integer>> workingStack, final Grammar grammar) {
        super(workingStack, grammar);
    }

    @Override
    public String getOutputAsString() {
        final List<Pair<String, Integer>> productions = workingStack
            .stream()
            .filter(pair -> pair.getSecond() != -1)
            .collect(Collectors.toList());

        final Pair<String, Integer> firstProduction = productions.get(0);
        final String firstNonterminal = firstProduction.getFirst();
        final List<String> firstRule = grammar.getProductionsForNonterminal(firstNonterminal).getRules().get(firstProduction.getSecond());

        final Node root = new Node(firstNonterminal);
        root.child = buildTree(firstRule, productions);

        final List<List<String>> rows = BFS(root);

        return rows.stream().map(row -> String.join(",\t", row)).collect(Collectors.joining("\n"));
    }

    private List<List<String>> BFS(final Node root) {
        final Queue<Node> queue = new ArrayDeque<>();
        queue.add(root);
        final List<Node> traversal = new ArrayList<>();
        final Map<Node, Node> parentOf = new HashMap<>();
        final Map<Node, Node> siblingOf = new HashMap<>();
        int currentIndex = 1;
        while (!queue.isEmpty()) {
            final Node node = queue.remove();
            node.index = currentIndex++;
            traversal.add(node);
            Node currentSibling = node;
            Node rightSibling = node.rightSibling;
            while (rightSibling != null) {
                if (!queue.contains(rightSibling)) {
                    queue.add(rightSibling);
                    parentOf.put(rightSibling, parentOf.get(node));
                    siblingOf.put(rightSibling, currentSibling);
                }
                currentSibling = rightSibling;
                rightSibling = rightSibling.rightSibling;
            }
            if (node.child != null) {
                queue.add(node.child);
                parentOf.put(node.child, node);
            }
        }

        final List<List<String>> rows = new ArrayList<>();
        for (int i = 0; i < traversal.size(); ++i) {
            final Node node = traversal.get(i);
            rows.add(List.of(
                String.valueOf(i + 1),
                node.value,
                String.valueOf(parentOf.get(node) == null ? 0 : parentOf.get(node).index),
                String.valueOf(siblingOf.get(node) == null ? 0 : siblingOf.get(node).index)
            ));
        }

        return rows;
    }

    private Node buildTree(final List<String> rule, final List<Pair<String, Integer>> productions) {
        if (rule.isEmpty()) {
            return null;
        }
        final String symbol = rule.get(0);
        if (grammar.isTerminal(symbol)) {
            final Node node = new Node(symbol);
            node.rightSibling = buildTree(rule.subList(1, rule.size()), productions);
            return node;
        } else if (grammar.isNonterminal(symbol)) {
            final Node node = new Node(symbol);
            productions.remove(0);
            final Pair<String, Integer> firstProduction = productions.get(0);
            final String firstNonterminal = firstProduction.getFirst();
            final List<String> firstRule = grammar.getProductionsForNonterminal(firstNonterminal).getRules().get(firstProduction.getSecond());
            node.child = buildTree(firstRule, productions);
            node.rightSibling = buildTree(rule.subList(1, rule.size()), productions);
            return node;
        } else {
            return new Node("ε");
        }
    }

    @Data
    private static class Node {

        @ToString.Exclude
        private final String uuid = UUID.randomUUID().toString();

        private final String value;

        @ToString.Exclude
        private Node child;

        @ToString.Exclude
        private Node rightSibling;

        private int index;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node node = (Node) o;
            return Objects.equals(uuid, node.uuid);
        }

        @Override
        public int hashCode() {
            return Objects.hash(uuid);
        }

    }

}

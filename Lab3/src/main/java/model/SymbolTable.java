package model;

import java.util.ArrayList;
import java.util.List;

public class SymbolTable {

    private static final int DEFAULT_CAPACITY = 4;

    private final List<List<String>> table;

    public SymbolTable() {
        this(DEFAULT_CAPACITY);
    }

    public SymbolTable(final int capacity) {
        this.table = new ArrayList<>();
        for (int i = 0; i < capacity; ++i) {
            this.table.add(new ArrayList<>());
        }
    }

    public List<List<String>> getUnderlyingData() {
        return table;
    }

    public synchronized String get(final Pair<Integer, Integer> position) {
        if (position == null) {
            return null;
        }
        final int hash = position.getFirst();
        if (hash >= table.size()) {
            return null;
        }
        final int index = position.getSecond();
        if (index >= table.get(hash).size()) {
            return null;
        }
        return table.get(hash).get(index);
    }

    public synchronized Pair<Integer, Integer> get(final String value) {
        final int hash = hash(value);
        if (hash >= table.size()) {
            return null;
        }
        final int position = table.get(hash).indexOf(value);
        if (position == -1) {
            return null;
        }
        return new Pair<>(hash, position);
    }

    public synchronized Pair<Integer, Integer> put(final String value) {
        final Pair<Integer, Integer> existingPosition = get(value);
        if (existingPosition != null) {
            return existingPosition;
        }
        final int hash = hash(value);
        final int position = table.get(hash).size();
        table.get(hash).add(value);
        return new Pair<>(hash, position);
    }

    public int hash(final String value) {
        return Math.abs(value.hashCode()) % table.size();
    }

    @Override
    public synchronized String toString() {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("SymbolTable{");
        for (int i = 0; i < table.size(); ++i) {
            final List<String> currentChain = table.get(i);
            stringBuilder.append(i).append(" -> [");
            for (int j = 0; j < currentChain.size(); ++j) {
                stringBuilder.append(currentChain.get(j));
                if (j < currentChain.size() - 1) {
                    stringBuilder.append(", ");
                }
            }
            stringBuilder.append("]");
            if (i < table.size() - 1) {
                stringBuilder.append(", ");
            }
        }
        stringBuilder.append("}");
        return stringBuilder.toString();
    }

}
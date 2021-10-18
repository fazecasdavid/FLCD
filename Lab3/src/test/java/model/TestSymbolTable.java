package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class TestSymbolTable {

    private static final int SYMBOL_TABLE_CAPACITY = 8;
    private SymbolTable symbolTable;

    @BeforeEach
    public void setUp() {
        symbolTable = new SymbolTable(SYMBOL_TABLE_CAPACITY);
    }

    @Test
    public void testPut_uniqueSymbol_returnsCorrectPosition() {
        final String symbol = "D";
        final Pair<Integer, Integer> position = symbolTable.put(symbol);
        final Pair<Integer, Integer> expectedPosition = new Pair<>(symbolTable.hash(symbol), 0);
        assertEquals(position, expectedPosition);
    }

    @Test
    public void testPut_duplicateSymbol_returnsOldPosition() {
        final String symbol = "D";
        final Pair<Integer, Integer> oldPosition = symbolTable.put(symbol);
        final Pair<Integer, Integer> position = symbolTable.put(symbol);
        assertEquals(oldPosition, position);
    }

    @Test
    public void testGet_valueNotPresent_shouldReturnNull() {
        final String symbol = "D";
        assertNull(symbolTable.get(symbol));
    }

    @Test
    public void testGet_presentValue_shouldReturnCorrectPosition() {
        final String symbol = "D";
        final Pair<Integer, Integer> position = symbolTable.put(symbol);
        assertEquals(position, symbolTable.get(symbol));
    }

    @Test
    public void testGet_validPosition_shouldCorrectSymbol() {
        final String symbol = "D";
        final Pair<Integer, Integer> position = symbolTable.put(symbol);
        assertEquals(symbol, symbolTable.get(position));
    }

    @Test
    public void testGet_invalidPosition_shouldReturnNull() {
        final String symbol = "D";
        final Pair<Integer, Integer> position = symbolTable.put(symbol);
        final Pair<Integer, Integer> firstInvalidPosition = new Pair<>(SYMBOL_TABLE_CAPACITY, position.getSecond());
        final Pair<Integer, Integer> secondInvalidPosition = new Pair<>(position.getFirst(), position.getSecond() + 1);
        final Pair<Integer, Integer> thirdInvalidPosition = new Pair<>(position.getFirst() + 1, position.getSecond() + 1);
        assertNull(symbolTable.get(firstInvalidPosition));
        assertNull(symbolTable.get(secondInvalidPosition));
        assertNull(symbolTable.get(thirdInvalidPosition));
        assertNull(symbolTable.get((Pair<Integer, Integer>) null));
    }

}
import model.Pair;
import model.Scanner;
import model.SymbolTable;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

// TODO: create PIF class, refactor, generate class diagram, add documentation

public class Main {

    private static final String INPUT_FILENAME = "in/p1.dici";

    public static void main(final String[] args) {
        final Pair<SymbolTable, List<Pair<Integer, Pair<Integer, Integer>>>> scanResult = Scanner.scan(INPUT_FILENAME);

        final SymbolTable symbolTable = scanResult.getFirst();
        final List<Pair<Integer, Pair<Integer, Integer>>> programInternalForm = scanResult.getSecond();

        System.out.println();
        System.out.println("Lexically correct.");

        try (final BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("out/ST.out"))) {
            for (int i = 0; i < symbolTable.getUnderlyingData().size(); ++i) {
                bufferedWriter.write(String.format("%d -> %s%n", i, symbolTable.getUnderlyingData().get(i)));
            }
        } catch (final IOException e) {
            e.printStackTrace();
        }

        try (final BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("out/PIF.out"))) {
            for (final Pair<Integer, Pair<Integer, Integer>> pair : programInternalForm) {
                bufferedWriter.write(pair.getFirst() + " -> " + pair.getSecond() + "\n");
            }
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

}
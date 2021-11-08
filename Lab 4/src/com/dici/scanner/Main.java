package com.dici.scanner;

import com.dici.scanner.model.Pair;
import com.dici.scanner.model.ProgramInternalForm;
import com.dici.scanner.model.Scanner;
import com.dici.scanner.model.SymbolTable;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Main {

    private static final String INPUT_FILENAME = "in/p1.dici";

    public static void main(final String[] args) {
        final Pair<SymbolTable, ProgramInternalForm> scanResult = Scanner.scan(INPUT_FILENAME);

        final SymbolTable symbolTable = scanResult.getFirst();
        final ProgramInternalForm programInternalForm = scanResult.getSecond();

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
            for (final Pair<Integer, Pair<Integer, Integer>> pair : programInternalForm.getUnderlyingData()) {
                bufferedWriter.write(pair.getFirst() + " -> " + pair.getSecond() + "\n");
            }
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

}
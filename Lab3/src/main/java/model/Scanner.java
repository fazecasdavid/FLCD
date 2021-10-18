package model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class Scanner {

    private static final List<String> separators = Arrays.asList(
        "[", "]", "{", "}", "(", ")", ";"
    );

    private static final String separatorsString = "[]{}(); ";

    private static final List<String> operators = Arrays.asList(
        "+", "-", "*", "/", "=", "<", ">", "<=", ">=", "==", "!=", "!", "&", "|"
    );

    private static final List<String> reservedWords = Arrays.asList(
        "game", "stop", "integer", "character", "string", "array",
        "while", "for", "if", "elseif", "else", "read", "write"
    );

    private static final Map<String, Integer> codificationTable = new LinkedHashMap<>();

    static {
        codificationTable.put("identifier", 0);
        codificationTable.put("constant", 1);
        separators.forEach(separator -> codificationTable.put(separator, codificationTable.size()));
        operators.forEach(operator -> codificationTable.put(operator, codificationTable.size()));
        reservedWords.forEach(reservedWord -> codificationTable.put(reservedWord, codificationTable.size()));
    }

    private static boolean isIdentifier(final String token) {
        return token.matches("^[a-zA-Z]([a-zA-Z]|[0-9])*$");
    }

    private static boolean isConstant(final String token) {
        return token.matches("^(0|[+\\-]?[1-9][0-9]*)|('([a-zA-Z]|[0-9])')|(\"([a-zA-Z]|[0-9])*\")$");
    }

    public static Pair<SymbolTable, List<Pair<Integer, Pair<Integer, Integer>>>> scan(final String filename) {
        final SymbolTable symbolTable = new SymbolTable();
        final List<Pair<Integer, Pair<Integer, Integer>>> programInternalForm = new ArrayList<>();

        try (final BufferedReader bufferedReader = new BufferedReader(new FileReader(filename))) {
            String line;
            int lineNumber = 1;
            while ((line = bufferedReader.readLine()) != null) {
                final StringTokenizer stringTokenizer = new StringTokenizer(line.strip(), separatorsString, true);
                while (stringTokenizer.hasMoreTokens()) {
                    final String token = stringTokenizer.nextToken().strip();
                    if (token.length() > 0) {
                        System.out.print("Token '" + token + "' -> ");
                        if (separators.contains(token) || operators.contains(token) || reservedWords.contains(token)) {
                            System.out.println("separator / operator / reserved word");
                            programInternalForm.add(new Pair<>(codificationTable.get(token), new Pair<>(-1, -1)));
                        } else if (isIdentifier(token)) {
                            System.out.println("identifier");
                            programInternalForm.add(new Pair<>(codificationTable.get("identifier"), symbolTable.put(token)));
                        } else if (isConstant(token)) {
                            System.out.println("constant");
                            programInternalForm.add(new Pair<>(codificationTable.get("constant"), symbolTable.put(token)));
                        } else {
                            throw new RuntimeException(String.format("Unknown token %s at line %s%n", token, lineNumber));
                        }
                    }
                }
                ++lineNumber;
            }
        } catch (final IOException e) {
            e.printStackTrace();
        }

        return new Pair<>(symbolTable, programInternalForm);
    }

    public static void printCodificationTable() {
        codificationTable.forEach((k, v) -> System.out.println(k + " -> " + v));
    }

}
package model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Scanner {

    private static final List<String> separators = Arrays.asList(
        "[", "]", "{", "}", "(", ")", ";"
    );

    private static final List<String> compoundOperators = Arrays.asList(
        "<=", ">=", "==", "!="
    );

    private static final List<String> simpleOperators = Arrays.asList(
        "+", "-", "*", "/", "=", "<", ">", "!", "&", "|"
    );

    private static final List<String> reservedWords = Arrays.asList(
        "game", "stop", "integer", "character", "string", "array",
        "while", "for", "if", "elseif", "else", "read", "write"
    );

    private static final String letterRegex = "[a-zA-Z]";
    private static final String digitRegex = "[0-9]";
    private static final String anyStringRegex = "\\b(" + digitRegex + "|" + letterRegex + ")*\\b";
    private static final String identifierRegex = letterRegex + "(" + letterRegex + "|" + digitRegex + ")*";
    private static final String integerConstantRegex = "(0|[+\\-]?[1-9]" + digitRegex + "*)";
    private static final String characterConstantRegex = "('(" + letterRegex + "|" + digitRegex + ")')";
    private static final String stringConstantRegex = "(\"(" + letterRegex + "|" + digitRegex + ")*\")";
    private static final String constantRegex = integerConstantRegex + "|" + characterConstantRegex + "|" + stringConstantRegex;

    private static final Pattern tokenizerPattern;
    private static final Map<String, Integer> codificationTable = new LinkedHashMap<>();

    static {
        final StringBuilder tokenizerRegex = new StringBuilder();
        tokenizerRegex.append("(");
        for (final String operator : compoundOperators) {
            tokenizerRegex.append(Pattern.quote(operator)).append("|");
        }
        for (final String separator : separators) {
            tokenizerRegex.append(Pattern.quote(separator)).append("|");
        }
        tokenizerRegex.append("\\s+");
        tokenizerRegex.append(anyStringRegex).append("|");
        tokenizerRegex.append(identifierRegex).append("|");
        tokenizerRegex.append(constantRegex).append("|");
        for (final String operator : simpleOperators) {
            tokenizerRegex.append(Pattern.quote(operator)).append("|");
        }
        tokenizerRegex.append(")");
        tokenizerPattern = Pattern.compile(tokenizerRegex.toString());
    }

    static {
        codificationTable.put("identifier", 0);
        codificationTable.put("constant", 1);
        separators.forEach(separator -> codificationTable.put(separator, codificationTable.size()));
        simpleOperators.forEach(operator -> codificationTable.put(operator, codificationTable.size()));
        compoundOperators.forEach(operator -> codificationTable.put(operator, codificationTable.size()));
        reservedWords.forEach(reservedWord -> codificationTable.put(reservedWord, codificationTable.size()));
    }

    private static List<String> tokenize(final String line) {
        final List<String> tokens = new ArrayList<>();
        final Matcher matcher = tokenizerPattern.matcher(line);
        int position = 0;
        while (matcher.find()) {
            if (position != matcher.start()) {
                tokens.add(line.substring(position, matcher.start()));
            }
            tokens.add(matcher.group());
            position = matcher.end();
        }
        if (position != line.length()) {
            tokens.add(line.substring(position));
        }
        return tokens.stream().map(String::trim).filter(string -> string.length() > 0).collect(Collectors.toList());
    }

    private static boolean isIdentifier(final String token) {
        return token.matches("^" + identifierRegex + "$");
    }

    private static boolean isConstant(final String token) {
        return token.matches("^" + constantRegex + "$");
    }

    public static Pair<SymbolTable, ProgramInternalForm> scan(final String filename) {
        final SymbolTable symbolTable = new SymbolTable();
        final ProgramInternalForm programInternalForm = new ProgramInternalForm();
        try (final BufferedReader bufferedReader = new BufferedReader(new FileReader(filename))) {
            String line;
            int lineNumber = 1;
            while ((line = bufferedReader.readLine()) != null) {
                final List<String> tokens = tokenize(line);
                for (final String token : tokens) {
                    if (token.length() > 0) {
                        System.out.print("Token '" + token + "' -> ");
                        if (separators.contains(token) || simpleOperators.contains(token) || compoundOperators.contains(token) || reservedWords.contains(token)) {
                            System.out.println("separator / operator / reserved word");
                            programInternalForm.add(codificationTable.get(token), new Pair<>(-1, -1));
                        } else if (isIdentifier(token)) {
                            System.out.println("identifier");
                            programInternalForm.add(codificationTable.get("identifier"), symbolTable.put(token));
                        } else if (isConstant(token)) {
                            System.out.println("constant");
                            programInternalForm.add(codificationTable.get("constant"), symbolTable.put(token));
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

}
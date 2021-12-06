package com.fd.model.parser.output;

import com.fd.model.Grammar;
import com.fd.model.Pair;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public abstract class ParserOutput {

    protected final List<Pair<String, Integer>> workingStack;

    protected final Grammar grammar;

    public ParserOutput(final List<Pair<String, Integer>> workingStack, final Grammar grammar) {
        this.workingStack = workingStack;
        this.grammar = grammar;
    }

    public void printToScreen() {
        System.out.println(getOutputAsString());
    }

    public void printToFile(final String filename) {
        try (final BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filename))) {
            bufferedWriter.write(getOutputAsString());
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    public abstract String getOutputAsString();

    @Override
    public String toString() {
        return getOutputAsString();
    }

}

package model;

import java.util.ArrayList;
import java.util.List;

public class ProgramInternalForm {

    private final List<Pair<Integer, Pair<Integer, Integer>>> data;

    public ProgramInternalForm() {
        data = new ArrayList<>();
    }

    public void add(final Integer tokenCode, final Pair<Integer, Integer> tokenPosition) {
        data.add(new Pair<>(tokenCode, tokenPosition));
    }

    public List<Pair<Integer, Pair<Integer, Integer>>> getUnderlyingData() {
        return data;
    }

}
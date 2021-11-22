package com.fd.model;

import lombok.Data;

@Data
public class Pair<T1, T2> {

    private final T1 first;

    private final T2 second;

}

package com.igweze.ebi.wafermessenger.Functions;

public interface Function<T, R> {
    R apply(T t);
}

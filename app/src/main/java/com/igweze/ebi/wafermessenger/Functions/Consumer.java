package com.igweze.ebi.wafermessenger.Functions;

public interface Consumer<T> {
    void accept(T t);
}
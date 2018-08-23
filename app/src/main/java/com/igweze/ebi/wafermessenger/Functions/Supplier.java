package com.igweze.ebi.wafermessenger.Functions;

import org.json.JSONException;

import java.io.IOException;

public interface Supplier<T> {
    T get();
}

package com.gorgexec.mvvmcore.common;

import java.util.Map;

import javax.inject.Provider;

public abstract class MapValuesFactory<TKey, TVal> {

    private final Map<TKey, Provider<TVal>> map;

    public MapValuesFactory(Map<TKey, Provider<TVal>> map) {
        this.map = map;
    }

    public TVal create(TKey key) throws RuntimeException {
        Provider<TVal> provider = map.get(key);
        if (provider == null) {
            throw new IllegalArgumentException("unknown value with key " + key);
        }
        try {
            return provider.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

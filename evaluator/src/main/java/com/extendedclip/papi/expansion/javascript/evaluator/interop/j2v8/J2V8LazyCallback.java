package com.extendedclip.papi.expansion.javascript.evaluator.interop.j2v8;

import com.eclipsesource.v8.JavaCallback;
import com.eclipsesource.v8.V8Array;
import com.eclipsesource.v8.V8Object;

import java.lang.reflect.Method;
import java.util.Collection;

public final class J2V8LazyCallback implements JavaCallback {
    private final String name;
    private final Collection<Method> methods;

    public J2V8LazyCallback(final String name, final Collection<Method> methods) {
        this.name = name;
        this.methods = methods;
    }

    @Override
    public Object invoke(final V8Object v8Object, final V8Array v8Array) {

        return null;
    }
}

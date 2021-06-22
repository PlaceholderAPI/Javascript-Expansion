package com.extendedclip.papi.expansion.javascript.evaluator;

import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.utils.MemoryManager;

public final class ClosableMemoryManager implements AutoCloseable {
    private final MemoryManager memoryManager;

    public ClosableMemoryManager(final V8 v8) {
        this.memoryManager = new MemoryManager(v8);
    }

    @Override
    public void close() throws Exception {
        memoryManager.release();
    }
}

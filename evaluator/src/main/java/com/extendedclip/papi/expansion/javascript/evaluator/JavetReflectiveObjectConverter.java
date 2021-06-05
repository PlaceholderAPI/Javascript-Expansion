package com.extendedclip.papi.expansion.javascript.evaluator;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.converters.JavetObjectConverter;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.reference.V8ValueObject;
import com.extendedclip.papi.expansion.javascript.evaluator.binding.Binder;
import com.extendedclip.papi.expansion.javascript.evaluator.binding.BindingException;
import com.extendedclip.papi.expansion.javascript.evaluator.binding.javet.V8Bindable;

public class JavetReflectiveObjectConverter extends JavetObjectConverter {

    private final Binder binder;

    public JavetReflectiveObjectConverter(Binder binder) {
        this.binder = binder;
    }

    @Override
    public <T extends V8Value> T toV8Value(V8Runtime v8Runtime, Object object) throws JavetException {
        final T v8Value = super.toV8Value(v8Runtime, object);
        if (v8Value != null && !(v8Value.isUndefined())) {
            return v8Value;
        }
        final V8ValueObject valueObject = v8Runtime.createV8ValueObject();
        try {
            binder.bind(object, new V8Bindable(valueObject));
        } catch (final BindingException e) {
            throw new RuntimeException(e);
        }
        final T value = (T) valueObject;
        return v8Runtime.decorateV8Value(value);
    }
}

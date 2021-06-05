package com.extendedclip.papi.expansion.javascript.evaluator.binding.javet;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.utils.JavetCallbackContext;
import com.caoccao.javet.values.reference.V8ValueObject;
import com.extendedclip.papi.expansion.javascript.evaluator.binding.Bindable;
import com.extendedclip.papi.expansion.javascript.evaluator.binding.BindingException;

import java.lang.reflect.Method;

public final class V8Bindable implements Bindable {
    private final V8ValueObject bindable;

    public V8Bindable(final V8ValueObject bindable) {
        this.bindable = bindable;
    }

    @Override
    public void bindMethod(final Object object, final String name, final Method method) throws BindingException {
        final JavetCallbackContext callbackContext = new JavetCallbackContext(object, method);
        try {
            bindable.bindFunction(name, callbackContext);
        } catch (final JavetException exception) {
            throw new BindingException(exception);
        }
    }

    @Override
    public void bindProperty(final Object object, final String name, final Method getter, final Method setter) throws BindingException {
        final JavetCallbackContext getterCallbackContext = new JavetCallbackContext(object, getter);
        final JavetCallbackContext setterCallbackContext = new JavetCallbackContext(object, setter);
        try {
            bindable.bindProperty(name, getterCallbackContext, setterCallbackContext);
        } catch (final JavetException exception) {
            throw new BindingException(exception);
        }
    }
}

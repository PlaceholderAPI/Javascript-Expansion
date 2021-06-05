package com.extendedclip.papi.expansion.javascript.evaluator.binding.javet;

import com.extendedclip.papi.expansion.javascript.evaluator.binding.Bindable;
import com.extendedclip.papi.expansion.javascript.evaluator.binding.Binder;
import com.extendedclip.papi.expansion.javascript.evaluator.binding.BindingException;

public final class MediatingMethodBinder extends Binder {
    private final Binder staticBinder;
    private final Binder objectBinder;

    public MediatingMethodBinder(final Binder staticBinder, final Binder objectBinder) {
        this.staticBinder = staticBinder;
        this.objectBinder = objectBinder;
    }

    @Override
    public void bind(final Object object, final Bindable valueObject) throws BindingException {
        if (object instanceof Class) {
            staticBinder.bind(object, valueObject);
        } else {
            objectBinder.bind(object, valueObject);
        }
    }
}

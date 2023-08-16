package coni.executor;

import coni.executor.arg.Arg;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Invoke a function using reflection
 */
public class Func {
    Object owner;
    String name;

    public Func(Object owner, String name) {
        this.owner = owner;
        this.name = name;
    }

    public Object exec(List<Arg> args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class<?>[] types = args.stream()
                .map(Arg::getType)
                .toArray(Class<?>[]::new);
        Object[] values = args.stream()
                .map(Arg::getValue)
                .toArray(Object[]::new);

        Method method = owner.getClass().getMethod(name, types);
        Object res = method.invoke(owner, values);
        return res;
    }

    public Object exec(Arg arg) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class<?> type = arg.getType();
        Object value = arg.getValue();

        Method method = owner.getClass().getMethod(name, type);
        Object res = method.invoke(owner, value);
        return res;
    }

    public Object exec() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = owner.getClass().getMethod(name, null);
        Object res = method.invoke(owner, null);
        return res;
    }
}

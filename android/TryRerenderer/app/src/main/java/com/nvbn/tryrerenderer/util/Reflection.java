package com.nvbn.tryrerenderer.util;

import android.util.Log;

import java.lang.reflect.Method;

public class Reflection {
    public static boolean areTypesCompatible(Class<?>[] targets, Class<?>[] sources) {
        if (targets.length != sources.length)
            return (false);

//        for (int i = 0; i < targets.length; i++) {
//            if (sources[i] == null)
//                continue;
//
//            if (targets[i].isInterface()) {
//                Class<?>[] interfaces = sources[i].getInterfaces();
//                for (Class<?> in : interfaces) {
//                    if (targets[i].equals(in))
//                        return true;
//                }
//            }
//
//            if (!translateFromPrimitive(targets[i]).isAssignableFrom(translateFromPrimitive(sources[i])))
//                return false;
//        }
        return true;
    }

    /**
     * If this specified class represents a primitive type (int, float, etc.) then
     * it is translated into its wrapper type (Integer, Float, etc.).  If the
     * passed class is not a primitive then it is just returned.
     */
    public static Class<?> translateFromPrimitive(Class<?> primitive) {
        if (!primitive.isPrimitive())
            return (primitive);

        if (Boolean.TYPE.equals(primitive))
            return (Boolean.class);
        if (Character.TYPE.equals(primitive))
            return (Character.class);
        if (Byte.TYPE.equals(primitive))
            return (Byte.class);
        if (Short.TYPE.equals(primitive))
            return (Short.class);
        if (Integer.TYPE.equals(primitive))
            return (Integer.class);
        if (Long.TYPE.equals(primitive))
            return (Long.class);
        if (Float.TYPE.equals(primitive))
            return (Float.class);
        if (Double.TYPE.equals(primitive))
            return (Double.class);
        throw new RuntimeException("Error translating type:" + primitive);
    }


    public static Method searchForMethod(Object who, String name, Class[] parms) {
        Method[] methods = who.getClass().getMethods();
        for (int i = 0; i < methods.length; i++) {
            // Has to be named the same of course.
            if (!methods[i].getName().equals(name))
                continue;

            Class[] types = methods[i].getParameterTypes();

            // Does it have the same number of arguments that we're looking for.
            if (types.length != parms.length)
                continue;

            // Check for type compatibility
            if (areTypesCompatible(types, parms))
                return methods[i];
        }
        return null;
    }

    public static void call(Object who, String method, Object[] args) {
        Class[] argsClasses = new Class[args.length];
        for (Integer i = 0; i < args.length; i++) {
            argsClasses[i] = args[i].getClass();
        }
        try {
            searchForMethod(who, method, argsClasses).invoke(who, args);
            Log.e("Call " + method + " of " + who.getClass(), args.toString());
        } catch (Exception e) {
            Log.e("Can't call " + method + " of " + who.getClass(), e.toString());
        }
    }
}

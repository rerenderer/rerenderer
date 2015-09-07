package com.nvbn.tryrerenderer.util

import android.util.Log
//
//import java.lang.reflect.Method
//
//public object Reflection {
//    public fun areTypesCompatible(targets: Array<Class<*>>, sources: Array<Class<*>>): Boolean {
//        if (targets.size() != sources.size())
//            return (false)
//
//        //        for (int i = 0; i < targets.length; i++) {
//        //            if (sources[i] == null)
//        //                continue;
//        //
//        //            if (targets[i].isInterface()) {
//        //                Class<?>[] interfaces = sources[i].getInterfaces();
//        //                for (Class<?> in : interfaces) {
//        //                    if (targets[i].equals(in))
//        //                        return true;
//        //                }
//        //            }
//        //
//        //            if (!translateFromPrimitive(targets[i]).isAssignableFrom(translateFromPrimitive(sources[i])))
//        //                return false;
//        //        }
//        return true
//    }
//
//    /**
//     * If this specified class represents a primitive type (int, float, etc.) then
//     * it is translated into its wrapper type (Integer, Float, etc.).  If the
//     * passed class is not a primitive then it is just returned.
//     */
//    public fun translateFromPrimitive(primitive: Class<Any>): Class<Any> {
//        if (!primitive.isPrimitive())
//            return (primitive)
//
//        if (java.lang.Boolean.TYPE == primitive)
//            return (javaClass<Boolean>())
//        if (Character.TYPE == primitive)
//            return (javaClass<Char>())
//        if (java.lang.Byte.TYPE == primitive)
//            return (javaClass<Byte>())
//        if (java.lang.Short.TYPE == primitive)
//            return (javaClass<Short>())
//        if (Integer.TYPE == primitive)
//            return (javaClass<Int>())
//        if (java.lang.Long.TYPE == primitive)
//            return (javaClass<Long>())
//        if (java.lang.Float.TYPE == primitive)
//            return (javaClass<Float>())
//        if (java.lang.Double.TYPE == primitive)
//            return (javaClass<Double>())
//        throw RuntimeException("Error translating type:" + primitive)
//    }
//
//
//    public fun searchForMethod(who: Any, name: String, parms: Array<Class<Any>>): Method? {
//        val methods = who.javaClass.getMethods()
//        for (i in methods.indices) {
//            // Has to be named the same of course.
//            if (methods[i].getName() != name)
//                continue
//
//            val types = methods[i].getParameterTypes()
//
//            // Does it have the same number of arguments that we're looking for.
//            if (types.size() != parms.size())
//                continue
//
//            // Check for type compatibility
//            if (areTypesCompatible(types, parms))
//                return methods[i]
//        }
//        return null
//    }
//
//    public fun call(who: Any, method: String, args: Array<Any>) {
//        val argsClasses = arrayOfNulls<Class<Any>>(args.size())
//        for (i in args.indices) {
//            argsClasses[i] = args[i].javaClass
//        }
//        try {
//            searchForMethod(who, method, argsClasses)!!.invoke(who, *args)
//            Log.e("Call " + method + " of " + who.javaClass, args.toString())
//        } catch (e: Exception) {
//            Log.e("Can't call " + method + " of " + who.javaClass, e.toString())
//        }
//
//    }
//}

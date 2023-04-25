package com.tangrun.kits.util;


import java.lang.reflect.*;

public class TypeUtils {

    public static abstract class Token<T> {
        public Type getType() {
            return TypeUtils.getType(this, Token.class, 0);
        }
    }

    public static Type getType(Object obj, Class<?> parameterType, int parameterIndex) {
        return getType(obj.getClass(), parameterType, parameterIndex);
    }

    public static Type getType(Object obj, Class<?> parameterType, String parameterName) {
        return getType(obj.getClass(), parameterType, parameterName);
    }

    public static Type getType(Class<?> objClass, Class<?> parameterType, int parameterIndex) {
        return matchType(objClass, objClass, parameterType, parameterIndex);
    }

    public static Type getType(Class<?> objClass, Class<?> parameterType, String parameterName) {
        return getType(objClass, parameterType, getTypeParameterIndex(parameterType, parameterName));
    }

    /**
     * 同名方法 处理结果 只返回一级
     *
     * @param objClass
     * @param objType
     * @param parameterType
     * @param parameterIndex
     * @return
     */
    public static Type matchType(Class<?> objClass, Type objType, Class<?> parameterType, int parameterIndex) {
        if (objClass == null || objType == null || parameterType == null || parameterIndex < 0) {
            return null;
        }
        if (objClass == parameterType) {
            if (objType instanceof ParameterizedType) {
                Type[] actualTypeArguments = ((ParameterizedType) objType).getActualTypeArguments();
                return actualTypeArguments[parameterIndex];
            }
        } else {
            Type type3 = matchType(objClass, parameterType, parameterIndex);
            if (type3 instanceof ParameterizedType) {
                return ((ParameterizedType) type3).getRawType();
            }
            if (type3 instanceof Class) {
                return type3;
            }
            if (type3 instanceof GenericArrayType) {
                Type componentType = ((GenericArrayType) type3).getGenericComponentType();
                if (componentType instanceof ParameterizedType) {
                    componentType = ((ParameterizedType) componentType).getRawType();
                }
                if (componentType instanceof Class) {
                    return Array.newInstance((Class<?>) componentType, 0).getClass();
                }
                if (componentType instanceof GenericArrayType) {
                    return componentType;
                }
                if (componentType instanceof TypeVariable) {
                    int typeParameterIndex = getTypeParameterIndex(objClass, ((TypeVariable<?>) componentType).getName());
                    Type[] actualTypeArguments = ((ParameterizedType) objType).getActualTypeArguments();
                    Type actualTypeArgument = actualTypeArguments[typeParameterIndex];
                    return GenericArrayTypeImpl.make(actualTypeArgument);
                }
            }
            if (type3 instanceof TypeVariable) {
                if (((TypeVariable<?>) type3).getGenericDeclaration() == objClass) {
                    if (objType instanceof ParameterizedType) {
                        int typeParameterIndex = getTypeParameterIndex(objClass, ((TypeVariable<?>) type3).getName());
                        Type[] actualTypeArguments = ((ParameterizedType) objType).getActualTypeArguments();
                        return actualTypeArguments[typeParameterIndex];
                    }
                }
            }
        }
        return null;
    }

    /**
     * 遍历objClass父一级 返回结果可能为空 不为空则是结果或者泛型
     *
     * @param objClass
     * @param parameterType
     * @param parameterIndex
     * @return
     */
    public static Type matchType(Class<?> objClass, Class<?> parameterType, int parameterIndex) {
        Type[] types;
        if (parameterType.isInterface()) {
            types = objClass.getGenericInterfaces();
        } else {
            types = new Type[]{objClass.getGenericSuperclass()};
        }

        for (Type type : types) {
            Class<?> cls = null;
            if (type instanceof ParameterizedType) {
                Type rawType = ((ParameterizedType) type).getRawType();
                if (rawType == parameterType) {
                    Type[] actualTypeArguments = ((ParameterizedType) type).getActualTypeArguments();
                    return actualTypeArguments[parameterIndex];
                } else if (parameterType.isAssignableFrom((Class<?>) rawType)) {
                    cls = (Class<?>) rawType;
                }
            }
            if (type instanceof Class) {
                if (parameterType.isAssignableFrom((Class<?>) type)) {
                    cls = (Class<?>) type;
                }
            }
            if (cls != null) {
                return matchType(cls, type, parameterType, parameterIndex);
            }
        }

        Type genericSuperclass = objClass.getGenericSuperclass();
        if (genericSuperclass == null) {
            return null;
        }

        Class<?> cls = null;
        if (genericSuperclass instanceof ParameterizedType) {
            cls = (Class<?>) ((ParameterizedType) genericSuperclass).getRawType();
        } else if (genericSuperclass instanceof Class) {
            cls = (Class<?>) genericSuperclass;
        }
        if (cls == null) {
            return null;
        }

        return matchType(cls, genericSuperclass, parameterType, parameterIndex);
    }

    private static int getTypeParameterIndex(Class<?> clz, String name) {
        if (clz == null) {
            return -1;
        }
        TypeVariable<? extends Class<?>>[] typeParameters = clz.getTypeParameters();
        for (int i = 0; i < typeParameters.length; i++) {
            if (typeParameters[i].getName().equals(name)) {
                return i;
            }
        }
        return -1;
    }
}

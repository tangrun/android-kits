package com.tangrun.kits.util;


import androidx.arch.core.util.Function;

import java.lang.reflect.*;
import java.util.*;

public class FindTypeUtil {


    public static Type findType(Object obj, Class<?> parameterType, int parameterIndex) {
        return findType(obj.getClass(), parameterType, parameterIndex);
    }

    public static Type findType(Object obj, Class<?> parameterType, String parameterName) {
        return findType(obj.getClass(), parameterType, parameterName);
    }

    public static Type findType(Class<?> objClass, Class<?> parameterType, int parameterIndex) {
        return findType_(objClass, parameterType, parameterIndex);
    }

    public static Type findType(Class<?> objClass, Class<?> parameterType, String parameterName) {
        return findType(objClass, parameterType, getTypeParameterIndex(parameterType, parameterName));
    }


    private static Type findType_(Class<?> objClass, Class<?> pClass, int pIndex) {
        Class<?> currentClass = objClass;
        for (; ; ) {
            if (currentClass == null) {
                return null;
            }

            if (pClass.isInterface()) {
                Type genericSuperclass = null;
                for (Type genericInterface : currentClass.getGenericInterfaces()) {
                    Class<?> actualClass = getActualClass(genericInterface);
                    if (actualClass == null || !pClass.isAssignableFrom(actualClass)) {
                        continue;
                    }
                    genericSuperclass = genericInterface;
                    break;
                }
                if (genericSuperclass != null) {
                    Class<?> actualClass = getActualClass(genericSuperclass);
                    if (actualClass == pClass) {
                        return findType_(objClass, currentClass, genericSuperclass, pIndex);
                    } else {
                        currentClass = actualClass;
                    }
                } else {
                    currentClass = currentClass.getSuperclass();
                }
            } else {
                if (currentClass.getSuperclass() == pClass) {
                    Type genericSuperclass = currentClass.getGenericSuperclass();
                    return findType_(objClass, currentClass, genericSuperclass, pIndex);
                }
                currentClass = currentClass.getSuperclass();
            }
        }
    }

    private static Type findType_(Class<?> objClass, Class<?> currentClass, Type currentType, int currentParameterizedTypeIndex) {
        if (currentType instanceof ParameterizedType) {
            Type[] types = ((ParameterizedType) currentType).getActualTypeArguments();
            if (currentParameterizedTypeIndex < 0 || currentParameterizedTypeIndex > types.length) {
                return null;
            }
            Type type = types[currentParameterizedTypeIndex];
            return getActualType(type, new Function<String, Type>() {
                @Override
                public Type apply(String s) {
                    int parameterIndex = getTypeParameterIndex(currentClass, s);
                    if (parameterIndex < 0) {
                        return null;
                    }
                    return findType(objClass, currentClass, parameterIndex);
                }
            });
        } else {
            return null;
        }
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

    private static Class<?> getActualClass(Type type) {
        if (type instanceof ParameterizedType) {
            type = ((ParameterizedType) type).getRawType();
        }
        if (type instanceof Class) {
            return (Class<?>) type;
        }
        return null;
    }

    private static Type getActualType(Type originType, androidx.arch.core.util.Function<String, Type> function) {
        if (originType instanceof Class) {
            return originType;
        }
        if (originType instanceof TypeVariable) {
            Type apply = function.apply(((TypeVariable<?>) originType).getName());
            return getActualType(apply, function);
        }
        if (originType instanceof ParameterizedType) {
            Type[] types = ((ParameterizedType) originType).getActualTypeArguments();
            for (int i = 0; i < types.length; i++) {
                types[i] = getActualType(types[i], function);
                if (types[i] == null) {
                    return null;
                }
            }
            return new ParameterizedTypeImpl((Class<?>) ((ParameterizedType) originType).getRawType(), ((ParameterizedType) originType).getOwnerType(), types);
        }
        if (originType instanceof GenericArrayType) {
            Type genericComponentType = ((GenericArrayType) originType).getGenericComponentType();
            Type aType = getActualType(genericComponentType, function);
            if (aType == null) return null;
            if (genericComponentType instanceof TypeVariable) {
                if (aType instanceof Class) {
                    return Array.newInstance((Class<?>) aType, 0).getClass();
                } else if (aType instanceof ParameterizedType) {
                    return new GenericArrayTypeImpl(aType);
                }
            }
            if (genericComponentType instanceof ParameterizedType) {
                return new GenericArrayTypeImpl(aType);
            }
        }
        return originType;
    }

    private static final class ParameterizedTypeImpl implements ParameterizedType {
        private final Class<?> rawType;
        private final Type ownerType;
        private final Type[] actualTypeArguments;

        /**
         * Constructor
         *
         * @param rawClass      type
         * @param useOwner      owner type to use, if any
         * @param typeArguments formal type arguments
         */
        private ParameterizedTypeImpl(final Class<?> rawClass, final Type useOwner, final Type[] typeArguments) {
            this.rawType = rawClass;
            this.ownerType = useOwner;
            this.actualTypeArguments = Arrays.copyOf(typeArguments, typeArguments.length, Type[].class);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Type[] getActualTypeArguments() {
            return actualTypeArguments.clone();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Type getOwnerType() {
            return ownerType;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Type getRawType() {
            return rawType;
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof ParameterizedType) {
                // Check that information is equivalent
                ParameterizedType that = (ParameterizedType) o;

                if (this == that)
                    return true;

                Type thatOwner = that.getOwnerType();
                Type thatRawType = that.getRawType();

                return
                        (ownerType == null ?
                                thatOwner == null :
                                ownerType.equals(thatOwner)) &&
                                (rawType == null ?
                                        thatRawType == null :
                                        rawType.equals(thatRawType)) &&
                                Arrays.equals(actualTypeArguments, // avoid clone
                                        that.getActualTypeArguments());
            } else
                return false;
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(actualTypeArguments) ^
                    (ownerType == null ? 0 : ownerType.hashCode()) ^
                    (rawType == null ? 0 : rawType.hashCode());
        }


        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();

            if (ownerType != null) {
                if (ownerType instanceof Class)
                    sb.append(((Class) ownerType).getName());
                else
                    sb.append(ownerType.toString());

                sb.append(".");

                if (ownerType instanceof ParameterizedTypeImpl) {
                    // Find simple name of nested type by removing the
                    // shared prefix with owner.
                    sb.append(rawType.getName().replace(((ParameterizedTypeImpl) ownerType).rawType.getName() + "$",
                            ""));
                } else
                    sb.append(rawType.getName());
            } else
                sb.append(rawType.getName());

            if (actualTypeArguments != null &&
                    actualTypeArguments.length > 0) {
                sb.append("<");
                boolean first = true;
                for (Type t : actualTypeArguments) {
                    if (!first)
                        sb.append(", ");
                    if (t instanceof Class)
                        sb.append(((Class) t).getName());
                    else
                        sb.append(t.toString());
                    first = false;
                }
                sb.append(">");
            }

            return sb.toString();
        }
    }

    private static final class GenericArrayTypeImpl implements GenericArrayType {
        private final Type genericComponentType;

        /**
         * Constructor
         *
         * @param componentType of this array type
         */
        private GenericArrayTypeImpl(final Type componentType) {
            this.genericComponentType = componentType;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Type getGenericComponentType() {
            return genericComponentType;
        }

        public String toString() {
            Type componentType = getGenericComponentType();
            StringBuilder sb = new StringBuilder();

            if (componentType instanceof Class)
                sb.append(((Class) componentType).getName());
            else
                sb.append(componentType.toString());
            sb.append("[]");
            return sb.toString();
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof GenericArrayType) {
                GenericArrayType that = (GenericArrayType) o;

                Type thatComponentType = that.getGenericComponentType();
                return genericComponentType.equals(thatComponentType);
            } else
                return false;
        }

        @Override
        public int hashCode() {
            return genericComponentType.hashCode();
        }
    }
}

package com.github.lemniscate.spring.crud.util;

import com.github.lemniscate.spring.crud.annotation.ApiResource;
import com.github.lemniscate.spring.crud.mapping.ApiResourceMapping;
import com.google.common.base.CaseFormat;
import org.reflections.Reflections;
import org.springframework.core.GenericTypeResolver;
import org.springframework.hateoas.Identifiable;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.Set;

/**
 * @Author dave 8/8/14 10:34 PM
 */
public class ApiResourceUtil {


    public static <ID extends Serializable, E extends Identifiable<ID>, CB, RB extends Identifiable<ID>, UB>
            ApiResourceMapping<ID, E, CB, RB, UB> from(Class<?> domainClass){

        ApiResource a = domainClass.getAnnotation(ApiResource.class);
        if( a != null ){
            Class idClass = a.idClass();

            Class createBeanClass = a.createBeanClass();
            if( Object.class.equals( createBeanClass) ){
                createBeanClass = domainClass;
            }

            Class readBeanClass = a.readBeanClass();
            if( Object.class.equals( readBeanClass) ){
                readBeanClass = domainClass;
            }

            Class updateBeanClass = a.updateBeanClass();
            if( Object.class.equals( updateBeanClass) ){
                updateBeanClass = domainClass;
            }

            ApiResourceMapping<ID, E, CB, RB, UB> result = new ApiResourceMapping.ComplexApiResourceMapping(idClass, domainClass, createBeanClass, readBeanClass, updateBeanClass, a.omitController());
            return result;
        }
        return null;
    }

    public static String getPath(Class<?> domainClass){
        ApiResource ar = domainClass.getAnnotation(ApiResource.class);
        Assert.notNull(ar, "Class was not an ApiResource: " + domainClass.getSimpleName());

        String path = ar.path();
        if( path == null || path.trim().isEmpty() ){
            path = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, domainClass.getSimpleName())
                    + "s";
        }

        return path;
    };

    public static Set<Class<?>> getAllTaggedClasses(String basePackage){
        Reflections reflections = new Reflections(basePackage);
        Set<Class<?>> entities = reflections.getTypesAnnotatedWith(ApiResource.class);
        return entities;
    }

    // this is a nasty little method that gets us our default constructor...
    public static <ID extends Serializable, E extends Identifiable<ID>, CB, RB extends Identifiable<ID>, UB> ApiResourceMapping.ComplexApiResourceMapping<ID, E, CB, RB, UB>
            generateMapping(int index, Class<?> superclass){
        Class<?> controller;
        try{
            StackTraceElement[] st = Thread.currentThread().getStackTrace();
            controller = Class.forName(st[index].getClassName());
        }catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to locate controller class from Stack Trace", e);
        }


        Class<?>[] types = GenericTypeResolver.resolveTypeArguments(controller, superclass);
        Assert.notNull(types, "Could not resolve type arguments for controller: " + controller.getSimpleName());
        Assert.isTrue( types.length >= 5, "Could not determine type arguments for controller: " + controller.getSimpleName());

        Class<ID> id = (Class<ID>) types[0];
        Class<E> domain = (Class<E>) types[1];
        Class<CB> cb = (Class<CB>) types[2];
        Class<RB> rb = (Class<RB>) types[3];
        Class<UB> ub = (Class<UB>) types[4];

        ApiResourceMapping.ComplexApiResourceMapping<ID, E, CB, RB, UB> result = new ApiResourceMapping.ComplexApiResourceMapping<ID, E, CB, RB, UB>(id, domain, cb, rb, ub, false);
        return result;
    }
}

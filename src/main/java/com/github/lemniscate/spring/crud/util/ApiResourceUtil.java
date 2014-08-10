package com.github.lemniscate.spring.crud.util;

import com.github.lemniscate.spring.crud.annotation.ApiResource;
import com.github.lemniscate.spring.crud.mapping.ApiResourceMapping;
import com.google.common.base.CaseFormat;
import org.reflections.Reflections;
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
}

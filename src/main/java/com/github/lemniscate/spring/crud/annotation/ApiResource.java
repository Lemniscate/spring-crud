package com.github.lemniscate.spring.crud.annotation;


import com.github.lemniscate.spring.crud.mapping.ApiResourceMapping;

import java.io.Serializable;
import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ApiResource {

    Class<? extends Serializable> idClass() default Long.class;
    Class<?> createBeanClass() default Object.class;
    Class<?> readBeanClass() default Object.class;
    Class<?> updateBeanClass() default Object.class;
    String path() default "";


}

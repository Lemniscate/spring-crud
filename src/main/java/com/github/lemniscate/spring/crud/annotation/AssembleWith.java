package com.github.lemniscate.spring.crud.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AssembleWith {
    String value();
    Class<?> domainClass(); // default Object.class;
}

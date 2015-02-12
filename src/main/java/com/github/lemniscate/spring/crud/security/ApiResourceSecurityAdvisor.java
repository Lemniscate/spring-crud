package com.github.lemniscate.spring.crud.security;

import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.hateoas.Identifiable;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by dave on 2/11/15.
 */
public interface ApiResourceSecurityAdvisor<ID extends Serializable, E extends Identifiable<ID>, CB, RB extends Identifiable<ID>, UB> {

    Class<E> getDomainClass();

    void secureFind(Class<?> domain, Iterable<E> results);

    void secureFindOne(Class<?> domain, ID id, E result);

    void secureFindByIds(Class<?> domain, Iterable<ID> ids, Iterable<E> results);

    void secureSave(Class<?> domain, E entity);
    
    void secureSaveMany(Class<?> domain, Iterable<E> entities);

    void secureDelete(Class<?> domain, E entity);

    void secureDeleteMany(Class<?> domain, Iterable<E> entities);

    void secureDeleteAll(Class<?> domain);

    void secureSearch(Class<?> domain, Map<String, Object> search, Iterable<E> results);

    void secureCatchAll(Class<?> domain, Object controller, MethodSignature signature, Object result);

    
}

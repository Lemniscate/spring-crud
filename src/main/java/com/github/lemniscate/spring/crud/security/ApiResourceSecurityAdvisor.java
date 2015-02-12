package com.github.lemniscate.spring.crud.security;

import org.springframework.data.domain.Page;
import org.springframework.hateoas.Identifiable;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by dave on 2/11/15.
 */
public interface ApiResourceSecurityAdvisor<ID extends Serializable, E extends Identifiable<ID>, CB, RB extends Identifiable<ID>, UB> {

    Class<E> getDomainClass();

    void secureGetAll(Class<?> domain, Page<RB> result);

    void secureGetOne(Class<?> domain, RB result);

    void secureDelete(Class<?> domain, ID idToDelete);

    void securePostOne(Class<?> domain, CB toCreate);

    void securePutOne(Class<?> domain, ID entityId, UB bean);

    void secureSearch(Class<?> domain, Map<String, Object> search, Page<RB> results);

    void secureCatchAll(Class<?> domain, Object controller, Class<?> domainClass, Object result);
}

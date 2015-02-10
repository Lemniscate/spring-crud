package com.github.lemniscate.spring.crud.security;

import org.springframework.data.domain.Page;
import org.springframework.hateoas.Identifiable;

import java.io.Serializable;
import java.util.Map;

/**
 * A naive abstraction for verifying
 *
 * Created by dave on 2/10/15.
 */
public interface ApiResourceSecurityAdvisor<ID extends Serializable, E extends Identifiable<ID>, CB, RB extends Identifiable<ID>, UB> {

    void secureGetAll(Page<RB> result);

    void secureGetOne(RB result);

    void secureDelete(ID idToDelete);

    void securePostOne(CB toCreate);

    void securePutOne(ID entityId, UB bean);

    void secureSearch(Map<String, Object> search, Page<RB> results);

}

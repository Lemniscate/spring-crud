package com.github.lemniscate.spring.crud.security;

import org.springframework.data.domain.Page;
import org.springframework.hateoas.Identifiable;

import java.io.Serializable;
import java.util.Map;

/**
 * This is a temporary solution for ensuring security on synthesized controllers -- it will almost certainly disappear
 * in the future. Use at your own risk!
 *
 * Created by dave on 2/10/15.
 */
public class BaseSecurityAdvisor<ID extends Serializable, E extends Identifiable<ID>, CB, RB extends Identifiable<ID>, UB>
        implements ApiResourceSecurityAdvisor<ID, E, CB, RB, UB> {


    @Override
    public void secureGetAll(Page<RB> result) {}

    @Override
    public void secureGetOne(RB result) {}

    @Override
    public void secureDelete(ID idToDelete) {}

    @Override
    public void securePostOne(CB toCreate) {}

    @Override
    public void securePutOne(ID entityId, UB bean) {}

    @Override
    public void secureSearch(Map<String, Object> search, Page<RB> results) {}

}

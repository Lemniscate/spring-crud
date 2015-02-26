package com.github.lemniscate.spring.crud.web.assembler;

import com.github.lemniscate.spring.crud.mapping.ApiResourceMapping;
import org.springframework.hateoas.Identifiable;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * Created by dave on 2/24/15.
 */
public interface IApiResourceAssembler<ID extends Serializable, E extends Identifiable<ID>, CB, RB extends Identifiable<ID>, UB> {

    List<Resource<RB>> toResources(Iterable<? extends RB> entities);

    Resource<RB> toResource(RB bean);

    void addLinks(Collection<Link> links, RB bean);

    ApiResourceMapping<ID, E, CB, RB, UB> getMapping();

    void setMapping(ApiResourceMapping<ID, E, CB, RB, UB> mapping);
}

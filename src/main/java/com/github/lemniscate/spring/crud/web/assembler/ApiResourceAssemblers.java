package com.github.lemniscate.spring.crud.web.assembler;

import com.github.lemniscate.spring.crud.util.ApiResourceRegistry;
import com.google.common.collect.Lists;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.Identifiable;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.inject.Inject;
import java.util.List;

/**
 * Helper class for generically assembling resources, whether it be a single instance, a collection or a
 * {@link Page}d result; includes convenience methods for creating {@link ResponseEntity} instances.
 *
 * @Author dave 12/11/14 11:58 AM
 */
public class ApiResourceAssemblers {

    public static final String X_SELF_HREF = "X-SELF-HREF";

    @Inject
    private ApiResourceRegistry registry;

    public <E extends Identifiable<?>> List<Resource<E>> assemble(Class<?> entity, Iterable<E> list){
        ApiResourceAssembler a = registry.getAssembler(entity);
        Assert.notNull(a, "Could not find the appropriate assembler for " + entity.getSimpleName());

        List<Resource<E>> result = Lists.newArrayList();
        for(E e : list){
            Resource<E> resource = a.toResource(e);
            result.add(resource);
        }
        return result;
    }

    public <E extends Identifiable<?>> Page<Resource<E>> assemble(Class<?> entity, Page<E> paged, Pageable pageable){
        List<Resource<E>> list = assemble(entity, paged.getContent());
        return new PageImpl<Resource<E>>(list, pageable, paged.getTotalElements());
    }

    public <E extends Identifiable<?>> Resource<E> assemble(E entity){
        if( entity == null ){
            return null;
        }

        ApiResourceAssembler a = registry.getAssembler(entity.getClass());
        Assert.notNull(a, "Could not find the appropriate assembler for " + entity.getClass().getSimpleName());
        return a.toResource(entity);
    }

    public <E extends Identifiable<?>> ResponseEntity<Resource<E>> respond(E o, HttpStatus status){
        if( o == null ){
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        Resource<E> resource = assemble(o);
        Link self = resource.getLink("self");
        MultiValueMap<String,String> headers = new LinkedMultiValueMap<String, String>();
        if( status.equals(HttpStatus.CREATED) && self != null){
            headers.add(X_SELF_HREF, self.getHref() );
        }
        return new ResponseEntity<Resource<E>>( resource, headers, status);
    }

    public <E extends Identifiable<?>> ResponseEntity<Page<Resource<E>>> respond(Class<E> entity, Page<E> page, Pageable pageable, HttpStatus status){
        return new ResponseEntity( assemble(entity, page, pageable), status);
    }

    public <E extends Identifiable<?>> ResponseEntity<List<E>> respond(Class<E> entity, Iterable<E> entities, HttpStatus status){
        return new ResponseEntity( assemble(entity, entities), status);
    }


}

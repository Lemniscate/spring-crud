package com.github.lemniscate.spring.crud.web.assembler;

import com.github.lemniscate.spring.crud.mapping.ApiResourceMapping;
import com.github.lemniscate.spring.crud.util.ApiResourceUtil;
import com.github.lemniscate.spring.crud.web.ApiResourceController;
import org.springframework.hateoas.Identifiable;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.LinkBuilder;
import org.springframework.hateoas.core.AbstractEntityLinks;
import org.springframework.util.Assert;

import javax.inject.Inject;

/**
 * @Author dave 5/5/14 4:39 PM
 */
public class ApiResourceEntityLinks extends AbstractEntityLinks {

    @Inject
    private ApiResourceLinkBuilderFactory linkBuilderFactory;

    @Override
    public LinkBuilder linkFor(Class<?> entity) {
        return linkFor(entity, new Object[0]);
    }

    @Override
    public LinkBuilder linkFor(Class<?> entity, Object... parameters) {
        Assert.notNull(entity);
        return linkBuilderFactory.linkTo(ApiResourceController.class, entity, parameters);
    }

    /*
	 * (non-Javadoc)
	 * @see org.springframework.hateoas.EntityLinks#getLinkToCollectionResource(java.lang.Class)
	 */
    @Override
    public Link linkToCollectionResource(Class<?> entity) {
        return linkFor(entity).withSelfRel();
    }

    @Override
    public Link linkToSingleResource(Class<?> entityClass, Object id) {
        Assert.notNull(entityClass);
        ApiResourceMapping details = ApiResourceUtil.from(entityClass);
        Link result = linkFor(entityClass).slash(id).withSelfRel();
        return result;
    }

    @Override
    public Link linkToSingleResource(Identifiable<?> entity) {
        Assert.notNull(entity);
        return linkFor( entity.getClass() ).slash( entity.getId()).withSelfRel();
    }

    @Override
    public boolean supports(Class<?> entity) {
        return ApiResourceUtil.from(entity) != null;
    }

}
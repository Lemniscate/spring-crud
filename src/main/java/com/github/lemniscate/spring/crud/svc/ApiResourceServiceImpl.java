package com.github.lemniscate.spring.crud.svc;

import com.github.lemniscate.spring.crud.lifecycle.ApiResourceLifecycleListener;
import com.github.lemniscate.spring.crud.mapping.ApiResourceMapping;
import com.github.lemniscate.spring.crud.repo.ApiResourceRepository;
import com.github.lemniscate.spring.crud.util.ApiResourceUtil;
import com.github.lemniscate.spring.search.OperationParser;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.hateoas.Identifiable;
import org.springframework.util.MultiValueMap;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ApiResourceServiceImpl<ID extends Serializable, E extends Identifiable<ID>, CB, RB extends Identifiable<ID>, UB>
        implements ApiResourceService<ID,E,CB,RB,UB> {

    @Getter
    protected final ApiResourceMapping<ID, E, CB, RB, UB> mapping;

    @Inject
    protected ApiResourceRepository<ID, E> repo;

    @Inject
    protected ConversionService conversionService;

    @Autowired(required=false)
    private List<ApiResourceLifecycleListener<E>> listeners = Lists.newArrayList();

    @Inject
    public ApiResourceServiceImpl(ApiResourceMapping<ID, E, CB, RB, UB> mapping) {
        this.mapping = mapping;
    }

    public ApiResourceServiceImpl() {
        this((ApiResourceMapping<ID, E, CB, RB, UB>) ApiResourceUtil.generateMapping(3, ApiResourceService.class));
    }

    @Override
    public E findOne(ID id){
        E result = repo.findOne(id);
        return result;
    }


    @Override
    public Page<E> find(Pageable p) {
        Page<E> result = repo.findAll(p);
        return result;
    }

    @Override
    public List<E> findByIds(Iterable<ID> ids) {
        return repo.findByIdIn(ids);
    }

    @Override
    public Page<RB> findForRead(Pageable p) {
        Page<E> entities = find(p);
        List<RB> list = Lists.newArrayList();
        for( E e : entities ){
            RB bean = conversionService.convert(e, mapping.readBeanClass());
            list.add(bean);
        }

        Page<RB> result = new PageImpl<RB>(list, p, entities.getTotalElements());
        return result;
    }

    @Override
    public Page<E> query(MultiValueMap<String, String> params, Pageable p) {
        // TODO implement me
        throw new IllegalStateException("Not implemented yet");
    }

    @Override
    public Page<RB> queryForRead(MultiValueMap<String, String> params, Pageable p) {
        Page<E> entities = query(params, p);
        List<RB> items = Lists.newArrayList();
        for( E e : entities ){
            RB bean = conversionService.convert(e, mapping.readBeanClass());
            items.add(bean);
        }

        Page<RB> result = new PageImpl<RB>(items, p, entities.getTotalElements());
        return result;
    }

    @Override
    public E save(E entity) {
        E pre = entity;
        for(ApiResourceLifecycleListener<E> listener : listeners){
            pre = listener.beforeSave(pre);
        }

        E saved = doSave(pre);

        E post = saved;
        for(ApiResourceLifecycleListener<E> listener : listeners){
            post = listener.afterSave(post);
        }
        return post;
    }

    @Override
    public E create(CB bean){
        E entity = conversionService.convert(bean, mapping.domainClass());

        E pre = entity;
        for(ApiResourceLifecycleListener<E> listener : listeners){
            pre = listener.beforeCreate(pre);
        }

        E saved = doSave(pre);

        E post = saved;
        for(ApiResourceLifecycleListener<E> listener : listeners){
            post = listener.afterCreate(post);
        }
        return post;
    }

    protected E doSave(E entity){
        return repo.save(entity);
    }

    @Override
    public RB createForRead(CB bean) {
        E entity = create(bean);
        RB result = conversionService.convert(entity, mapping.readBeanClass());
        return result;
    }

    @Override
    public RB read(ID id){
        E entity = findOne(id);
        RB result = conversionService.convert(entity, mapping.readBeanClass());
        return result;
    }

    @Override
    public E update(ID id, UB bean){
        E entity = findOne(id);
        E copy = conversionService.convert(bean, mapping.domainClass());
        try {
            BeanUtils.copyProperties(entity, copy);
        } catch (Exception e) {
            throw new RuntimeException("Failed copying properties", e);
        }
        E result = save(entity);
        return result;
    }

    @Override
    public RB updateForRead(ID id, UB bean){
        E entity = update(id, bean);
        RB result = conversionService.convert(entity, mapping.readBeanClass());
        return result;
    }

    @Override
    public void delete(ID id){
        E entity = findOne(id);
        delete(entity);
    }

    @Override
    public void delete(Iterable<ID> ids) {
        List<E> entities = findByIds(ids);
        for(E entity : entities){
            E pre = entity;
            for (ApiResourceLifecycleListener<E> listener : listeners) {
                pre = listener.beforeDelete(pre);
            }
        }
        repo.delete(entities);
        for(E entity : entities){
            E post = entity;
            for (ApiResourceLifecycleListener<E> listener : listeners) {
                listener.afterDelete(post);
            }
        }
    }

    @Override
    public void delete(E entity){
        if( entity != null ) {
            E pre = entity;
            for (ApiResourceLifecycleListener<E> listener : listeners) {
                pre = listener.beforeDelete(pre);
            }

            repo.delete(pre);

            for (ApiResourceLifecycleListener<E> listener : listeners) {
                listener.afterDelete(entity);
            }
        }
    }

    // TODO use an adapter approach to support multiple SpringData projects?
    @Override
    public Page<E> search(Map<String, Object> search, Pageable pageable) {
        OperationParser parser = new OperationParser(conversionService);
        Specification spec = parser.parse(search);
        Page<E> result = repo.findAll(spec, pageable);
        return result;
    }

    @Override
    public Page<RB> searchForRead(Map<String, Object> search, Pageable pageable) {
        Page<E> entities = search(search, pageable);
        List<RB> list = Lists.newArrayList();
        for( E e : entities ){
            RB bean = conversionService.convert(e, mapping.readBeanClass());
            list.add(bean);
        }
        Page<RB> result = new PageImpl<RB>(list, pageable, entities.getTotalElements());
        return result;
    }
}

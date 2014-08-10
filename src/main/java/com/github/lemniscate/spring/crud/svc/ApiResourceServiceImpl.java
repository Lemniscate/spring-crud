package com.github.lemniscate.spring.crud.svc;

import com.github.lemniscate.spring.crud.mapping.ApiResourceMapping;
import com.github.lemniscate.spring.crud.repo.ApiResourceRepository;
import com.github.lemniscate.spring.crud.util.ApiResourceUtil;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.Identifiable;
import org.springframework.util.MultiValueMap;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.List;

public class ApiResourceServiceImpl<ID extends Serializable, E extends Identifiable<ID>, CB, RB extends Identifiable<ID>, UB>
        implements ApiResourceService<ID,E,CB,RB,UB> {

    @Getter
    protected final ApiResourceMapping<ID, E, CB, RB, UB> mapping;

    @Inject
    protected ApiResourceRepository<ID, E> repo;

    @Inject
    protected ConversionService conversionService;

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
    public E create(CB bean){
        E entity = conversionService.convert(bean, mapping.domainClass());
        E result = repo.save(entity);
        return result;
    }

    @Override
    public RB createForRead(CB bean) {
        E entity = create(bean);
        RB result = conversionService.convert(entity, mapping.readBeanClass());
        return result;
    }

    @Override
    public RB read(ID id){
        E entity = repo.findOne(id);
        RB result = conversionService.convert(entity, mapping.readBeanClass());
        return result;
    }

    @Override
    public E update(UB bean){
        E entity = conversionService.convert(bean, mapping.domainClass());
        E result = repo.save(entity);
        return result;
    }

    @Override
    public RB updateForRead(UB bean){
        E entity = update(bean);
        RB result = conversionService.convert(entity, mapping.readBeanClass());
        return result;
    }

    @Override
    public void delete(ID id){
        repo.delete(id);
    }

    @Override
    public void delete(E entity){
        delete(entity.getId());
    }

}

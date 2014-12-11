package com.github.lemniscate.spring.crud.util;

import com.github.lemniscate.spring.crud.repo.ApiResourceRepository;
import com.github.lemniscate.spring.crud.svc.ApiResourceService;
import com.github.lemniscate.spring.crud.web.ApiResourceController;
import com.github.lemniscate.spring.crud.web.assembler.ApiResourceAssembler;
import com.google.common.collect.Maps;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.GenericTypeResolver;
import org.springframework.hateoas.Identifiable;

import java.io.Serializable;
import java.util.Map;

/**
 * Utility class for looking up service-objects by entity type.
 *
 * @Author dave 12/11/14 11:32 AM
 */
public class ApiResourceRegistry implements ApplicationContextAware{

    private Map<Class<?>, ApiResourceAssembler<?,?,?,?,?>> assemblers = Maps.newHashMap();
//    private Map<Class<?>, ApiResourceController<?,?,?,?,?>> controllers = Maps.newHashMap();
//    private Map<Class<?>, ApiResourceService<?,?,?,?,?>> services = Maps.newHashMap();
//    private Map<Class<?>, ApiResourceRepository<?,?>> repos = Maps.newHashMap();

    public ApiResourceRegistry register(ApiResourceAssembler<?,?,?,?,?> e){
        Class<?> entity = GenericTypeResolver.resolveTypeArguments(e.getClass(), ApiResourceAssembler.class)[1];
        assemblers.put(entity, e);
        return this;
    }

    public <ID extends Serializable, E extends Identifiable<ID>, CB, RB extends Identifiable<ID>, UB>
            ApiResourceAssembler<ID, E, CB, RB, UB> getAssembler(Class<?> entity){
        ApiResourceAssembler<?, ?, ?, ?, ?> result = assemblers.get(entity);
        return (ApiResourceAssembler<ID, E, CB, RB, UB>) result;
    }

    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        for(ApiResourceAssembler a : ctx.getBeansOfType(ApiResourceAssembler.class).values()){
            register(a);
        }
    }
}

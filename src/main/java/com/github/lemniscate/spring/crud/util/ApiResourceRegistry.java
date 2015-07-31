package com.github.lemniscate.spring.crud.util;

import com.github.lemniscate.spring.crud.mapping.ApiResourceMapping;
import com.github.lemniscate.spring.crud.repo.ApiResourceRepository;
import com.github.lemniscate.spring.crud.svc.ApiResourceService;
import com.github.lemniscate.spring.crud.web.ApiResourceController;
import com.github.lemniscate.spring.crud.web.assembler.ApiResourceAssembler;
import com.github.lemniscate.spring.crud.web.assembler.IApiResourceAssembler;
import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.core.GenericTypeResolver;
import org.springframework.hateoas.Identifiable;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Utility class for looking up service-objects by entity type.
 *
 * @Author dave 12/11/14 11:32 AM
 */
@Slf4j
public class ApiResourceRegistry{

    private Map<Class<?>, IApiResourceAssembler<?,?,?,?,?>> assemblers = Maps.newHashMap();
    private Map<Class<?>, ApiResourceController<?,?,?,?,?>> controllers = Maps.newHashMap();
    private Map<Class<?>, ApiResourceService<?,?,?,?,?>> services = Maps.newHashMap();
    private Map<Class<?>, ApiResourceRepository<?,?>> repos = Maps.newHashMap();

    @Getter
    private final List<ApiResourceMapping> mappings;

    public ApiResourceRegistry(List<ApiResourceMapping> mappings) {
        this.mappings = mappings;
    }

    @Inject
    private ApplicationContext ctx;

    public ApiResourceRegistry register(Object o){
        Assert.notNull(o, "Cannot register null object");
        Class<?> c = o.getClass();


        // if we get proxies and it's not a repository, look at the underlying class
        if (AopUtils.isJdkDynamicProxy(o) && !ApiResourceRepository.class.isAssignableFrom(c)) {
            c = ((Advised) o).getTargetClass();
        }

        if (ApiResourceMapping.class.isAssignableFrom(c) ){
            mappings.add((ApiResourceMapping<?, ?, ?, ?, ?>) o);
        }else if( IApiResourceAssembler.class.isAssignableFrom(c) ){
            Class<?> entity = GenericTypeResolver.resolveTypeArguments(c, IApiResourceAssembler.class)[1];
            assemblers.put(entity, (IApiResourceAssembler<?, ?, ?, ?, ?>) o);
        }else if( ApiResourceService.class.isAssignableFrom(c) ){
            Class<?> entity = GenericTypeResolver.resolveTypeArguments(c, ApiResourceService.class)[1];
            services.put(entity, (ApiResourceService<?, ?, ?, ?, ?>) o);
        }else if( ApiResourceRepository.class.isAssignableFrom(c) ){
            Class<?> entity = GenericTypeResolver.resolveTypeArguments(c, ApiResourceRepository.class)[1];
            repos.put(entity, (ApiResourceRepository<?, ?>) o);
        }else if( ApiResourceController.class.isAssignableFrom(c) ){
            Class<?> entity = GenericTypeResolver.resolveTypeArguments(c, ApiResourceController.class)[1];
            controllers.put(entity, (ApiResourceController<?, ?, ?, ?, ?>) o);
        }else {
            throw new IllegalStateException("Unknown object trying to be registered: " + c);
        }

        return this;
    }

    public <ID extends Serializable, E extends Identifiable<ID>, CB, RB extends Identifiable<ID>, UB, RT extends IApiResourceAssembler>
            RT getAssembler(Class<?> entity){
        IApiResourceAssembler<?, ?, ?, ?, ?> result = assemblers.get(entity);
        Assert.notNull("Could not locate assembler for " + entity.getName());
        return (RT) result;
    }

    public <ID extends Serializable, E extends Identifiable<ID>, CB, RB extends Identifiable<ID>, UB, RT extends ApiResourceService>
    RT getService(Class<?> entity){
        ApiResourceService<?, ?, ?, ?, ?> result = services.get(entity);
        Assert.notNull("Could not locate service for " + entity.getName());
        return (RT) result;
    }

    protected Class<?> findDomainByMappingType(Class<?> clazz, MappingType type){
        for(ApiResourceMapping mapping : mappings){
            Class<?> actual;
            switch(type){
                case CREATE:
                    actual = mapping.createBeanClass();
                    break;
                case READ:
                    actual = mapping.createBeanClass();
                    break;
                case UPDATE:
                    actual = mapping.createBeanClass();
                    break;
                default:
                    throw new IllegalStateException("Unhandled Mapping Type: " + type);
            }
            if( actual.equals(clazz) ){
                return mapping.domainClass();
            }
        }
        throw new IllegalStateException("Could not determine mapping type " + type + " from " + clazz.getSimpleName());
    }

    public ApiResourceMapping getMapping(Class<?> domainClass){
        for(ApiResourceMapping mapping : mappings){
            if( domainClass.equals(mapping.domainClass()) ){
                return mapping;
            }
        }
        return null;
    }


    public Class<?> findDomainByCreateType(Class<?> type) {
        return findDomainByMappingType(type, MappingType.CREATE);
    }

    public Class<?> findDomainByReadType(Class<?> type) {
        return findDomainByMappingType(type, MappingType.READ);
    }

    public Class<?> findDomainByUpdateType(Class<?> type) {
        return findDomainByMappingType(type, MappingType.UPDATE);
    }

    protected enum MappingType{
        CREATE, READ, UPDATE
    }

    @PostConstruct
    public void init() throws BeansException {
        Class<?>[] classes = new Class[]{
            IApiResourceAssembler.class, ApiResourceController.class,
            ApiResourceService.class, ApiResourceRepository.class
        };
        for(Class c : classes){
            for(Object o : ctx.getBeansOfType(c).values()){
                register(o);
            }
        }
        log.info("Populated registry");
    }
}

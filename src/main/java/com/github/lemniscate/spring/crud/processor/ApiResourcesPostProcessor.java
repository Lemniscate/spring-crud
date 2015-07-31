package com.github.lemniscate.spring.crud.processor;

import com.github.lemniscate.spring.crud.mapping.ApiResourceHandlerMapping;
import com.github.lemniscate.spring.crud.mapping.ApiResourceMapping;
import com.github.lemniscate.spring.crud.repo.ApiResourceRepository;
import com.github.lemniscate.spring.crud.svc.ApiResourceService;
import com.github.lemniscate.spring.crud.svc.ApiResourceServiceImpl;
import com.github.lemniscate.spring.crud.svc.ApiResourceServices;
import com.github.lemniscate.spring.crud.util.ApiResourceRegistry;
import com.github.lemniscate.spring.crud.util.ApiResourceUtil;
import com.github.lemniscate.spring.crud.web.ApiResourceController;
import com.github.lemniscate.spring.crud.web.assembler.ApiResourceAssembler;
import com.github.lemniscate.spring.crud.web.assembler.ApiResourceAssemblers;
import com.github.lemniscate.spring.crud.web.assembler.IApiResourceAssembler;
import com.github.lemniscate.util.bytecode.JavassistUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import javassist.CannotCompileException;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.*;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.PriorityOrdered;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;

import java.util.*;

@Slf4j
@RequiredArgsConstructor
public class ApiResourcesPostProcessor implements
        BeanDefinitionRegistryPostProcessor,
        InitializingBean,
        PriorityOrdered{

    private final String basePackage;
    private Set<Class<?>> entities;
    private Map<Class<?>, BeanDefinitionDetails> map = new HashMap<Class<?>, BeanDefinitionDetails>();

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        try {
            long start = System.currentTimeMillis();
            populateMap(registry);
            log.info("Populated map from registry in {} ms", System.currentTimeMillis() - start);
            long generate = System.currentTimeMillis();
            generateBeans(registry);
            log.info("Generated beans in {} ms", System.currentTimeMillis() - generate);

            generateHelpers(registry);

            log.info("Completed ApiResource processing in {} ms (total)", System.currentTimeMillis() - start);
        } catch (Exception e){
            throw new FatalBeanException("Failed generating ApiResources", e);
        }

    }

    private void generateHelpers(BeanDefinitionRegistry registry) {
        List<ApiResourceMapping> mappings = new ArrayList<ApiResourceMapping>();
        for( Class<?> c : entities){
            mappings.add( ApiResourceUtil.from(c) );
        }

        registry.registerBeanDefinition("apiResourceRegistry", BeanDefinitionBuilder.rootBeanDefinition(ApiResourceRegistry.class)
                .addConstructorArgValue(mappings)
                .getBeanDefinition());
        registry.registerBeanDefinition("apiResourceAssemblers", new RootBeanDefinition(ApiResourceAssemblers.class));
        registry.registerBeanDefinition("apiResourceServices", new RootBeanDefinition(ApiResourceServices.class));
    }

    public void generateBeans(BeanDefinitionRegistry registry) throws NotFoundException, CannotCompileException {
        for( Class<?> entity : entities ){
            ApiResourceMapping wrapper = ApiResourceUtil.from(entity);

            generateBeansForEntity(registry, entity);

            if( wrapper.omitController() ){
                log.warn("Omitting controller for " + entity);
            }else{
                AbstractBeanDefinition def = BeanDefinitionBuilder.rootBeanDefinition(ApiResourceHandlerMapping.class)
                        .addConstructorArgValue(wrapper)
                        .addConstructorArgReference(entity.getSimpleName() + "Controller")
                        .getBeanDefinition();
                registry.registerBeanDefinition( entity.getSimpleName() + "ResourceHandlerMapping", def);
            }
        }
    }


    private void generateBeansForEntity(BeanDefinitionRegistry registry, Class<?> entity) throws NotFoundException, CannotCompileException {
        ApiResourceMapping mapping = ApiResourceUtil.from(entity);
        BeanDefinitionDetails details = map.get(entity);

        if( details.service == null){
            String component = "Service";
            String name = entity.getSimpleName() + component;
            Class<?> abstractClass = ApiResourceServiceImpl.class;
            Class<?> serviceClass = generateTypedSubclass(name, abstractClass, mapping.idClass(), mapping.domainClass(), mapping.createBeanClass(), mapping.readBeanClass(), mapping.updateBeanClass());

            AbstractBeanDefinition def = BeanDefinitionBuilder.rootBeanDefinition(serviceClass)
                    .addConstructorArgValue(mapping)
                    .getBeanDefinition();
            registry.registerBeanDefinition( name, def);
            details.service = def;
            log.info("Generated {} for {}. Generic signature: {}", component, entity.getSimpleName(), GenericTypeResolver.resolveTypeArguments(serviceClass, abstractClass));
        }else{
            log.info("Found service for {}", entity.getSimpleName());
        }

        if( details.repository == null ){
            String component = "Repository";
            String name = entity.getSimpleName() + component;
            Class<?> abstractClass = ApiResourceRepository.class;
            Class<?> serviceClass = generateTypedInterface(name, abstractClass, mapping.idClass(), mapping.domainClass());

            AbstractBeanDefinition def = BeanDefinitionBuilder.rootBeanDefinition(JpaRepositoryFactoryBean.class)
                    .addPropertyValue("repositoryInterface", serviceClass)
                    .getBeanDefinition();
            registry.registerBeanDefinition( entity.getSimpleName() + "Repository", def);
            details.repository = def;
            log.info("Generated {} for {}. Generic signature: {}", component, entity.getSimpleName(), GenericTypeResolver.resolveTypeArguments(serviceClass, abstractClass));
        }else{
            log.info("Found repository for {}", entity.getSimpleName());
        }

        if( details.controller == null ){
            if( mapping.omitController() ){
                log.info("Ignored controller for {}", entity.getSimpleName());
            }else{
                String component = "Controller";
                String name = entity.getSimpleName() + component;
                Class<?> abstractClass = ApiResourceController.class;
                Class<?> serviceClass = generateTypedSubclass(name, abstractClass, mapping.idClass(), mapping.domainClass(), mapping.createBeanClass(), mapping.readBeanClass(), mapping.updateBeanClass());

                AbstractBeanDefinition def = BeanDefinitionBuilder.rootBeanDefinition(serviceClass)
                        .addConstructorArgValue(mapping)
                        .getBeanDefinition();
                registry.registerBeanDefinition( name, def);
                details.controller = def;
                log.info("Generated {} for {}. Generic signature: {}", component, entity.getSimpleName(), GenericTypeResolver.resolveTypeArguments(serviceClass, abstractClass));
            }
        }else{
            log.info("Found controller for {}", entity.getSimpleName());
            String name = entity.getSimpleName() + "Controller";
            String alias = findBeanDefinitionName(registry, details.controller);
            registry.registerAlias(alias, name);
        }

        if( details.assembler == null){

            String component = "Assembler";
            String name = entity.getSimpleName() + component;
            Class<?> abstractClass = ApiResourceAssembler.class;
            Collection<Class<?>> ifaces = new ArrayList<>();
            ifaces.add(IApiResourceAssembler.class);
            Class<?> serviceClass = generateTypedSubclass(name, abstractClass, mapping.idClass(), mapping.domainClass(), mapping.createBeanClass(), mapping.readBeanClass(), mapping.updateBeanClass());

            AbstractBeanDefinition def = BeanDefinitionBuilder.rootBeanDefinition(serviceClass)
                    .addPropertyValue("mapping", mapping)
                    .getBeanDefinition();
            registry.registerBeanDefinition( name, def);
            details.assembler = def;
            log.info("Generated {} for {}. Generic signature: {}", component, entity.getSimpleName(), GenericTypeResolver.resolveTypeArguments(serviceClass, abstractClass));
        }else{
            log.info("Found assembler for {}", entity.getSimpleName());
        }
    }

    private String findBeanDefinitionName(BeanDefinitionRegistry registry, AbstractBeanDefinition def) {
        for(String name : registry.getBeanDefinitionNames()){
            BeanDefinition bd = registry.getBeanDefinition(name);
            if( bd.equals(def) ){
                return name;
            }
        }
        throw new IllegalStateException("Could not find bean definition " + def);
    }


    // TODO this needs a lot of love!
    @SneakyThrows
    public void populateMap(BeanDefinitionRegistry registry){
        for( String name : registry.getBeanDefinitionNames() ){
            BeanDefinition d = registry.getBeanDefinition(name);

            if( d instanceof AbstractBeanDefinition ){
                AbstractBeanDefinition def = (AbstractBeanDefinition) d;

                if( isBeanType(def, ApiResourceAssembler.class)){
                    Class<?> entity = getEntityType(def, ApiResourceAssembler.class);
                    map.get(entity).assembler = def;
                }

                if( isBeanType( def, ApiResourceController.class )){
                    Class<?> entity = getEntityType(def, ApiResourceController.class);
                    map.get(entity).controller = def;
                }

                if( isBeanType(def, ApiResourceService.class)){
                    Class<?> entity = getEntityType(def, ApiResourceService.class);
                    map.get(entity).service = def;
                }


                if( isBeanType(def, JpaRepositoryFactoryBean.class)){
                    Class<?> repoInterface;
                    Object o = def.getPropertyValues().get("repositoryInterface");
                    if( o instanceof String){
                        repoInterface = Class.forName((String) o);
                    }else if(o instanceof Class){
                        repoInterface = (Class<?>) o;
                    }else{
                        throw new IllegalStateException("Unknown repository interface type encountered: " + o);
                    }
                    if( ApiResourceRepository.class.isAssignableFrom(repoInterface) ){
                        Class<?> entity = GenericTypeResolver.resolveTypeArguments(repoInterface , ApiResourceRepository.class)[1];
                        BeanDefinitionDetails details = map.get(entity);
                        if( details != null ){
                            details.repository = def;
                        }
                    }
                }
            }

        }
    }

    @SneakyThrows
    private boolean isBeanType( AbstractBeanDefinition beanDef, Class<?> targetType){
        if( beanDef.getBeanClassName() != null ){
            Class<?> beanClass = Class.forName( beanDef.getBeanClassName() );
            return targetType.isAssignableFrom(beanClass);
        }
        return false;
    }

    @SneakyThrows
    private Class<?> getEntityType(AbstractBeanDefinition def, Class<?> gen){
        Class<?> c = Class.forName(def.getBeanClassName());
        Class<?>[] types = GenericTypeResolver.resolveTypeArguments(c, gen);
        if( types != null && types.length >= 1 ){
            return types[1];
        }

        MutablePropertyValues pv = def.getPropertyValues();
        Object propValue = pv.get("mapping");
        if( propValue != null && ApiResourceMapping.class.isAssignableFrom(propValue.getClass()) ){
            return ((ApiResourceMapping) propValue).domainClass();
        }

        throw new IllegalStateException("Couldn't determine entity type");
    }


    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory bf) throws BeansException {}

    @Override
    public void afterPropertiesSet() throws Exception {
        entities = ApiResourceUtil.getAllTaggedClasses(basePackage);
        for( Class<?> entity : entities ){
            map.put(entity, new BeanDefinitionDetails());
        }
    }

    @Override
    public int getOrder() {
        return PriorityOrdered.LOWEST_PRECEDENCE - 3;
    }

    class BeanDefinitionDetails {
        AbstractBeanDefinition repository, service, controller, assembler;
    }


    // keep a cache of our generated classes (to prevent frozen class errors)
    private static Map<String, Class<?>> cache = Maps.newHashMap();
    public static Class<?> generateTypedSubclass(String name, Class<?> baseImpl, Class<?>... classes){
        if( !cache.containsKey(name) ){
            cache.put(name, JavassistUtil.generateTypedSubclass(name, baseImpl, classes));
        }
        return cache.get(name);
    }
    public static Class<?> generateTypedInterface(String name, Class<?> baseImpl, Class<?>... classes) throws NotFoundException, CannotCompileException {
        if( !cache.containsKey(name) ){
            cache.put(name, JavassistUtil.generateTypedInterface(name, baseImpl, classes));
        }
        return cache.get(name);
    }
<<<<<<< HEAD
}
=======
}
>>>>>>> 71b04cb1464dbbec72e0f3ad870202a6d4e03ade

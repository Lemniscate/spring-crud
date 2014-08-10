package com.github.lemniscate.spring.crud.mapping;

import com.github.lemniscate.spring.crud.annotation.AssembleWith;
import com.github.lemniscate.spring.crud.web.ApiResourceController;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

// TODO find a way to make this work with controller mappings
@Slf4j
public class ApiResourceControllerHandlerMapping extends RequestMappingHandlerMapping implements
        ApplicationContextAware {

    private Collection<? extends ApiResourceHandlerMapping> endpoints;

    @Getter
    private MultiValueMap<Class<?>, PathPropertyMapping> assembleWith = new LinkedMultiValueMap();

    @Getter @RequiredArgsConstructor
    public static class PathPropertyMapping{
        private final String property;
        private final Class<?> controller;
        private final Method method;
    }

    @Getter
    private Map<Class<?>, String> paths = new HashMap<Class<?>, String>();

    @Getter
    private final String apiPrefix;

    @Setter @Getter
    private boolean disabled = false;

    public ApiResourceControllerHandlerMapping(String apiPrefix){
        this.apiPrefix = apiPrefix;
        setOrder(LOWEST_PRECEDENCE - 2);
    }

    @Override
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        this.endpoints = getApplicationContext().getBeansOfType(ApiResourceHandlerMapping.class).values();

        if (!this.disabled) {
            for (ApiResourceHandlerMapping endpoint : this.endpoints) {
                detectHandlerMethods(endpoint.getController());
            }
        }
    }

    /**
     * Since all handler beans are passed into the constructor there is no need to detect
     * anything here
     */
    @Override
    protected boolean isHandler(Class<?> beanType) {
        return false;
    }

    @Override
    protected void registerHandlerMethod(Object handler, Method method,
                                         RequestMappingInfo mapping) {

        if (mapping == null) {
            return;
        }

        Set<String> defaultPatterns = mapping.getPatternsCondition().getPatterns();
        String[] patterns = new String[defaultPatterns.isEmpty() ? 1 : defaultPatterns.size()];

        String path = "";
        Object bean = handler;
        if (bean instanceof String) {
            bean = getApplicationContext().getBean((String) handler);
        }
        if (bean instanceof ApiResourceController) {
            ApiResourceController endpoint = (ApiResourceController) bean;
            path = endpoint.getMapping().path();
            paths.put(  endpoint.getMapping().domainClass(), path );
        }

        int i = 0;
        String prefix = StringUtils.hasText(this.apiPrefix) ? this.apiPrefix + path : path;
        if (defaultPatterns.isEmpty()) {
            patterns[0] = prefix;
        }
        else {
            for (String pattern : defaultPatterns) {
                patterns[i] = prefix + pattern;
                i++;
            }
        }

        AssembleWith a = AnnotationUtils.findAnnotation(method, AssembleWith.class);
        if( a != null ){
            String property = a.value();
            Class<?> domain = a.domainClass();
            // TODO look it up automagically from controller?

            for(String pattern : patterns){
                this.assembleWith.add(domain, new PathPropertyMapping(property, handler.getClass(), method));
            }
        }

        PatternsRequestCondition patternsInfo = new PatternsRequestCondition(patterns);

        RequestMappingInfo modified = new RequestMappingInfo(patternsInfo,
                mapping.getMethodsCondition(), mapping.getParamsCondition(),
                mapping.getHeadersCondition(), mapping.getConsumesCondition(),
                mapping.getProducesCondition(), mapping.getCustomCondition());

        super.registerHandlerMethod(handler, method, modified);
    }

}

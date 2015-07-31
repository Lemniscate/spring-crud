package com.github.lemniscate.spring.crud.mapping;

import com.github.lemniscate.spring.crud.annotation.AssembleWith;
import com.github.lemniscate.spring.crud.util.ApiResourceRegistry;
import com.github.lemniscate.spring.crud.web.ApiResourceController;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.condition.*;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.inject.Inject;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by dave on 3/27/15.
 */
public class ApiResourceControllerHandlerMapping extends RequestMappingHandlerMapping {


    @Setter
    @Value("${lemniscate.crud.apiPrefix:}")
    private String apiPrefix;

    @Inject
    private ApiResourceRegistry registry;

    @Getter
    private MultiValueMap<Class<?>, PathPropertyMapping> assembleWith = new LinkedMultiValueMap();


    // TODO wire these up on getters
    private boolean useSuffixPatternMatch = true;
    private boolean useRegisteredSuffixPatternMatch = false;
    private boolean useTrailingSlashMatch = true;
    private ContentNegotiationManager contentNegotiationManager = new ContentNegotiationManager();
    private final List<String> fileExtensions = new ArrayList<String>();

    @Override
    protected boolean isHandler(Class<?> beanType) {
        return super.isHandler(beanType)
            || ApiResourceController.class.isAssignableFrom(beanType);
    }

    @Override
    protected void detectHandlerMethods(Object handler) {
        super.detectHandlerMethods(handler);
    }

    @Override
    protected void registerHandlerMethod(Object handler, Method method, RequestMappingInfo mapping) {
        super.registerHandlerMethod(handler, method, mapping);

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
            if( Object.class.equals(domain) ){
                Class<?>[] types = GenericTypeResolver.resolveTypeArguments(handler.getClass(), ApiResourceController.class);
                Assert.notNull(types, "Could not resolve generic ApiResourceController types from " + handler.getClass().getSimpleName());
                Assert.isTrue(types.length >= 1, "Could not resolve generic ApiResourceController types from " + handler.getClass().getSimpleName());
                domain = types[1];
            }

            for(String pattern : patterns){
                this.assembleWith.add(domain, new PathPropertyMapping(property, handler.getClass(), method));
            }
        }


    }

    @Override
    protected RequestMappingInfo getMappingForMethod(Method method, Class<?> handlerType) {
        RequestMappingInfo result;
        if(ApiResourceController.class.isAssignableFrom(handlerType)){
            result = custom(method, handlerType);
        }else{
            result = super.getMappingForMethod(method, handlerType);
        }
        return result;
    }


    private RequestMappingInfo custom(Method method, Class<?> handlerType) {
        RequestMappingInfo info = null;
        RequestMapping methodAnnotation = AnnotationUtils.findAnnotation(method, RequestMapping.class);
        if (methodAnnotation != null) {
            RequestCondition<?> methodCondition = getCustomMethodCondition(method);
            info = customCreateRequestMappingInfo(methodAnnotation, methodCondition, handlerType);
            RequestMapping typeAnnotation = AnnotationUtils.findAnnotation(handlerType, RequestMapping.class);
            if (typeAnnotation != null) {
                RequestCondition<?> typeCondition = getCustomTypeCondition(handlerType);
                info = createRequestMappingInfo(typeAnnotation, typeCondition).combine(info);
            }
        }
        return info;
    }


    protected RequestMappingInfo customCreateRequestMappingInfo(RequestMapping annotation, RequestCondition<?> customCondition, Class<?> handlerType) {
        String[] patterns = customResolveEmbeddedValuesInPatterns(annotation.value(), handlerType);
        return new RequestMappingInfo(
                new PatternsRequestCondition(patterns, getUrlPathHelper(), getPathMatcher(),
                        this.useSuffixPatternMatch, this.useTrailingSlashMatch, this.fileExtensions),
                new RequestMethodsRequestCondition(annotation.method()),
                new ParamsRequestCondition(annotation.params()),
                new HeadersRequestCondition(annotation.headers()),
                new ConsumesRequestCondition(annotation.consumes(), annotation.headers()),
                new ProducesRequestCondition(annotation.produces(), annotation.headers(), this.contentNegotiationManager),
                customCondition);
    }


    protected String[] customResolveEmbeddedValuesInPatterns(String[] patterns, Class<?> handlerType) {
        Class<?>[] types = GenericTypeResolver.resolveTypeArguments(handlerType, ApiResourceController.class);
        Assert.notEmpty(types, "Could not determine types");
        ApiResourceMapping mapping = registry.getMapping(types[1]);

        String path = mapping.path();
        path = getApiPrefix() + (StringUtils.hasText(path) ? "/" + path : "");
        String[] mappings = super.resolveEmbeddedValuesInPatterns(patterns);
        String[] result = new String[mappings.length];
        for(int i = 0; i < mappings.length; i++){
            result[i] = path + mappings[i];
        }
        return result;
    }


    public String getApiPrefix() {
        return StringUtils.hasLength(apiPrefix) ? apiPrefix : "";
    }

    public String getPath(Class<?> domainClass){
        ApiResourceMapping mapping = registry.getMapping(domainClass);
        return mapping == null ? null : mapping.path();
    }


    @Getter @RequiredArgsConstructor
    public static class PathPropertyMapping{
        private final String property;
        private final Class<?> controller;
        private final Method method;
    }
}

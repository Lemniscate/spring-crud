package com.github.lemniscate.spring.crud.annotation;

import com.github.lemniscate.spring.crud.mapping.ApiResourceControllerHandlerMapping;
import com.github.lemniscate.spring.crud.processor.ApiResourcesPostProcessor;
import com.github.lemniscate.spring.crud.view.JsonViewResolver;
import com.github.lemniscate.spring.crud.web.assembler.ApiResourceEntityLinks;
import com.github.lemniscate.spring.crud.web.assembler.ApiResourceLinkBuilderFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.StandardAnnotationMetadata;

import javax.inject.Inject;
import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({EnableApiResources.ApiResourceRegistrar.class, EnableApiResources.ApiResourceConfiguration.class})
public @interface EnableApiResources {
    Class<?> value() default EnableApiResources.class;

    String apiPrefix() default "/api/";

    public class ApiResourceRegistrar implements ImportBeanDefinitionRegistrar {

        @Override
        public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
            AnnotationAttributes attr = AnnotationAttributes.fromMap(metadata.getAnnotationAttributes(EnableApiResources.class.getName(), false));

            Class<?> value = (Class<?>) attr.get("value");

            // if the value class was the default (EnableApiResources), use the calling class' package
            if( EnableApiResources.class.equals(value)){
                value = ((StandardAnnotationMetadata) metadata).getIntrospectedClass();
            }

            // register the annotation post processor
            String basePackage = value.getPackage().getName();
            String apiPrefix = (String) attr.get("apiPrefix");

            registry.registerBeanDefinition("apiResourcePrefix", BeanDefinitionBuilder.rootBeanDefinition(String.class)
                    .addConstructorArgValue(apiPrefix)
                    .getBeanDefinition());

            registry.registerBeanDefinition("apiResourceBasePackage", BeanDefinitionBuilder.rootBeanDefinition(String.class)
                    .addConstructorArgValue(basePackage)
                    .getBeanDefinition());

            AbstractBeanDefinition postProcessorDef = BeanDefinitionBuilder.rootBeanDefinition(ApiResourcesPostProcessor.class)
                    .addConstructorArgValue(basePackage)
                    .getBeanDefinition();
            registry.registerBeanDefinition("apiResourcesPostProcessor", postProcessorDef);
        }
    }

    @Configuration
    public static class ApiResourceConfiguration{

        @Inject
        private String apiResourcePrefix;

        @Bean
        @ConditionalOnMissingBean(JsonViewResolver.class)
        public JsonViewResolver jsonViewResolver(){
            return new JsonViewResolver();
        }

        @Bean
        @ConditionalOnMissingBean(ApiResourceControllerHandlerMapping.class)
        public ApiResourceControllerHandlerMapping apiResourcesHandlerMapping(){
            ApiResourceControllerHandlerMapping result = new ApiResourceControllerHandlerMapping(apiResourcePrefix);
            result.setOrder(-1);
            return result;
        }

        @Bean
        @ConditionalOnMissingBean(ApiResourceLinkBuilderFactory.class)
        public static ApiResourceLinkBuilderFactory apiResourceLinkBuilderFactory(){
            return new ApiResourceLinkBuilderFactory();
        }

        @Bean
        @ConditionalOnMissingBean(ApiResourceEntityLinks.class)
        public static ApiResourceEntityLinks apiResourceEntityLinks(){
            return new ApiResourceEntityLinks();
        }

    }
}

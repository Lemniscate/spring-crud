package com.github.lemniscate.spring.crud.annotation;

import com.github.lemniscate.spring.crud.util.ApiResourceRegistry;
import com.github.lemniscate.spring.crud.web.assembler.ApiResourceAssemblers;
import com.github.lemniscate.spring.crud.web.assembler.ApiResourceEntityLinks;
import com.github.lemniscate.spring.crud.web.assembler.ApiResourceLinkBuilderFactory;
import com.github.lemniscate.spring.crud.mapping.ApiResourceControllerHandlerMapping;
import com.github.lemniscate.spring.crud.processor.ApiResourcesPostProcessor;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.StandardAnnotationMetadata;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(EnableApiResources.ApiResourceRegistrar.class)
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

            AbstractBeanDefinition postProcessorDef = BeanDefinitionBuilder.rootBeanDefinition(ApiResourcesPostProcessor.class)
                    .addConstructorArgValue(basePackage)
                    .getBeanDefinition();
            registry.registerBeanDefinition("apiResourcesPostProcessor", postProcessorDef);

            AbstractBeanDefinition handlerMappingDef = BeanDefinitionBuilder.rootBeanDefinition(ApiResourceControllerHandlerMapping.class)
                    .addConstructorArgValue(apiPrefix)
                    .addPropertyValue("order", -1)
                    .getBeanDefinition();
            registry.registerBeanDefinition("apiResourcesHandlerMapping", handlerMappingDef);


            registry.registerBeanDefinition("apiResourcesLinkBuilderFactory", new RootBeanDefinition(ApiResourceLinkBuilderFactory.class));
            registry.registerBeanDefinition("apiResourcesEntityLinks", new RootBeanDefinition(ApiResourceEntityLinks.class));

            registry.registerBeanDefinition("apiResourceRegistry", new RootBeanDefinition(ApiResourceRegistry.class));
            registry.registerBeanDefinition("apiResourceAssemblers", new RootBeanDefinition(ApiResourceAssemblers.class));
        }
    }
}

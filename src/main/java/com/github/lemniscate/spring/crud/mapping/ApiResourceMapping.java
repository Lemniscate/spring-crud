package com.github.lemniscate.spring.crud.mapping;

import com.github.lemniscate.spring.crud.util.ApiResourceUtil;
import lombok.Setter;
import org.springframework.hateoas.Identifiable;

import java.io.Serializable;

/**
 * @Author dave 8/8/14 9:25 PM
 */
public interface ApiResourceMapping<ID extends Serializable, E extends Identifiable<ID>, CB, RB extends Identifiable<ID>, UB> {

    Class<ID> idClass();
    Class<E> domainClass();
    Class<CB> createBeanClass();
    Class<RB> readBeanClass();
    Class<UB> updateBeanClass();
    String path();
    boolean omitController();

    public static class SimpleApiResourceMapping<ID extends Serializable, E extends Identifiable<ID>, CB, RB extends Identifiable<ID>, UB>
        implements ApiResourceMapping<ID, E, CB, RB, UB>{

        private final Class<ID> idClass;
        private final Class<E> domainClass;
        private final boolean omitController;

        @Setter
        private String path;

        public SimpleApiResourceMapping(Class<ID> idClass, Class<E> domainClass, boolean omitController) {
            this.idClass = idClass;
            this.domainClass = domainClass;
            this.path = ApiResourceUtil.getPath(domainClass);
            this.omitController = omitController;
        }

        @Override
        public Class<ID> idClass() {
            return idClass;
        }

        @Override
        public Class<E> domainClass() {
            return domainClass;
        }

        @Override
        public Class<CB> createBeanClass() {
            return (Class<CB>) domainClass;
        }

        @Override
        public Class<RB> readBeanClass() {
            return (Class<RB>) domainClass;
        }

        @Override
        public Class<UB> updateBeanClass() {
            return (Class<UB>) domainClass;
        }

        @Override
        public String path() {
            return path;
        }

        @Override
        public boolean omitController() {
            return omitController;
        }
    }

    public static class ComplexApiResourceMapping<ID extends Serializable, E extends Identifiable<ID>, CB, RB extends Identifiable<ID>, UB>
            implements ApiResourceMapping<ID, E, CB, RB, UB>{

        private final Class<ID> idClass;
        private final Class<E> domainClass;
        private final Class<CB> createBeanClass;
        private final Class<RB> readBeanClass;
        private final Class<UB> updateBeanClass;
        private final boolean omitController;

        @Setter
        private String path;

        public ComplexApiResourceMapping(Class<ID> idClass, Class<E> domainClass, Class<CB> createBeanClass, Class<RB> readBeanClass, Class<UB> updateBeanClass, boolean omitController) {
            this.idClass = idClass;
            this.domainClass = domainClass;
            this.createBeanClass = createBeanClass;
            this.readBeanClass = readBeanClass;
            this.updateBeanClass = updateBeanClass;

            this.path = ApiResourceUtil.getPath(domainClass);
            this.omitController = omitController;
        }

        @Override
        public Class<ID> idClass() {
            return idClass;
        }

        @Override
        public Class<E> domainClass() {
            return domainClass;
        }

        @Override
        public Class<CB> createBeanClass() {
            return createBeanClass;
        }

        @Override
        public Class<RB> readBeanClass() {
            return readBeanClass;
        }

        @Override
        public Class<UB> updateBeanClass() {
            return updateBeanClass;
        }

        @Override
        public String path() {
            return path;
        }

        @Override
        public boolean omitController() {
            return omitController;
        }
    }

}

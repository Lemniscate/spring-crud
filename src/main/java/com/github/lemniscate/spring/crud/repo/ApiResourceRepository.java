package com.github.lemniscate.spring.crud.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.hateoas.Identifiable;

import java.io.Serializable;

/**
 * @Author dave 8/8/14 9:34 PM
 */
@NoRepositoryBean
public interface ApiResourceRepository<ID extends Serializable, E extends Identifiable<ID>>
        extends JpaRepository<E, ID>, JpaSpecificationExecutor<E> {

}

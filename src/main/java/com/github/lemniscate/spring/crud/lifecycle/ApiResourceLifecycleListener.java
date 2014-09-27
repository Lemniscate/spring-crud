package com.github.lemniscate.spring.crud.lifecycle;

public interface ApiResourceLifecycleListener<E> {


    void afterDelete(E entity);
    E beforeDelete(E entity);

    E afterSave(E entity);
    E beforeSave(E entity);

    E beforeCreate(E entity);
    E afterCreate(E entity);

}

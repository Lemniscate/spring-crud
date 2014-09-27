package com.github.lemniscate.spring.crud.lifecycle;

public class AbstractApiResourceLifecycleListener<E> implements ApiResourceLifecycleListener<E> {

    @Override
    public void afterDelete(E entity) {}

    @Override
    public E beforeDelete(E entity) {
        return entity;
    }

    @Override
    public E afterSave(E entity) {
        return entity;
    }

    @Override
    public E beforeSave(E entity) {
        return entity;
    }

    @Override
    public E beforeCreate(E entity) {
        return entity;
    }

    @Override
    public E afterCreate(E entity) {
        return entity;
    }
}

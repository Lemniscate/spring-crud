package com.github.lemniscate.spring.crud.svc;

import com.github.lemniscate.spring.crud.util.ApiResourceRegistry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.Identifiable;
import org.springframework.util.MultiValueMap;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @Author dave 1/10/15 9:54 AM
 */
public class ApiResourceServices {

    @Inject
    private ApiResourceRegistry registry;

    public <E extends Identifiable<ID>, ID extends Serializable> E findOne(Class<E> type, ID id){
        return (E) resolve(type).findOne(id);
    }

    public <E extends Identifiable<?>> Page<E> find(Class<E> type, Pageable p){
        return resolve(type).find(p);
    }

    public <E extends Identifiable<?>, RB> Page<RB> findForRead(Class<E> type, Pageable p){
		return resolveByReadBean(type).find(p);
	}

    public <E extends Identifiable<?>> Page<E> query(Class<E> type, MultiValueMap<String, String> params, Pageable p){
		return resolve(type).query(params, p);
	}

    public <RB> Page<RB> queryForRead(Class<RB> type, MultiValueMap<String, String> params, Pageable p){
		return resolveByReadBean(type).queryForRead(params, p);
	}

    public <E extends Identifiable<ID>, ID extends Serializable> List<E> findByIds(Class<E> type, Iterable<ID> ids){
		return resolve(type).findByIds(ids);
	}

    public <E extends Identifiable<?>> E save(E entity){
		return (E) resolve(entity.getClass()).save(entity);
	}

    public <E extends Identifiable<?>, CB> E create(CB bean){
        return (E) resolveByCreateBean(bean.getClass()).create(bean);
	}

    public <CB, RB> RB createForRead(CB bean){
		return (RB) resolveByCreateBean(bean.getClass()).createForRead(bean);
	}

    public <RB, ID extends Serializable> RB read(Class<RB> type, ID id){
		return (RB) resolveByReadBean(type).read(id);
	}

    public <E extends Identifiable<ID>, ID extends Serializable, UB>  E update(ID id, UB bean){
		return (E) resolveByUpdateBean(bean.getClass()).update(id, bean);
	}

    public <ID extends Serializable, RB, UB> RB updateForRead(ID id, UB bean){
        return (RB) resolveByUpdateBean(bean.getClass()).update(id, bean);
	}

    public <E extends Identifiable<ID>, ID extends Serializable> void delete(Class<E> type, ID id){
        resolve(type).delete(id);
	}

    public <E extends Identifiable<ID>, ID extends Serializable> void delete(Class<E> type, Iterable<ID> ids){
        resolve(type).delete(ids);
	}

    public <E extends Identifiable<?>> void delete(E entity){
        resolve(entity.getClass()).delete(entity);
	}

    public <E extends Identifiable<ID>, ID extends Serializable> Page<E> search(Class<E> type, Map<String, Object> search, Pageable pageable){
		return resolve(type).search(search, pageable);
	}

    public <ID extends Serializable, RB> Page<RB> searchForRead(Class<RB> type, Map<String, Object> search, Pageable pageable){
		return resolveByReadBean(type).search(search, pageable);
	}

    // Resolver methods -- for finding the right service

    protected <E extends Identifiable<?>> ApiResourceService resolve(Class<E> type){
        return registry.getService(type);
    }

    protected ApiResourceService resolveByCreateBean(Class<?> type){
        Class<?> e = registry.findDomainByCreateType(type);
        return registry.getService(e);
    }

    protected ApiResourceService resolveByReadBean(Class<?> type){
        Class<?> e = registry.findDomainByReadType(type);
        return registry.getService(e);
    }

    protected ApiResourceService resolveByUpdateBean(Class<?> type){
        Class<?> e = registry.findDomainByUpdateType(type);
        return registry.getService(e);
    }

}

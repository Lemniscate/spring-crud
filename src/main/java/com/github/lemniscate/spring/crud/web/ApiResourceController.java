package com.github.lemniscate.spring.crud.web;

import com.github.lemniscate.spring.crud.mapping.ApiResourceMapping;
import com.github.lemniscate.spring.crud.svc.ApiResourceService;
import com.github.lemniscate.spring.crud.util.ApiResourceSupport;
import com.github.lemniscate.spring.crud.util.ApiResourceUtil;
import com.github.lemniscate.spring.crud.util.ApiResourceTypeHintResolver;
import com.github.lemniscate.spring.crud.view.JsonViewResolver;
import com.github.lemniscate.spring.crud.web.assembler.IApiResourceAssembler;
import com.github.lemniscate.spring.jsonviews.client.BaseView;
import com.github.lemniscate.spring.typehint.annotation.TypeHints;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.Identifiable;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.Map;

/**
* @Author dave 8/8/14 9:20 PM
*/
@Transactional
public class ApiResourceController<ID extends Serializable, E extends Identifiable<ID>, CB, RB extends Identifiable<ID>, UB>
        extends ApiResourceSupport {

    @Inject
    protected JsonViewResolver jsonViewResolver;

    @Inject
    @TypeHints(resolver = ApiResourceTypeHintResolver.class)
    protected ApiResourceService<ID, E, CB, RB, UB> service;

    @Inject
    @TypeHints(resolver = ApiResourceTypeHintResolver.class)
    protected IApiResourceAssembler<ID, E, CB, RB, UB> assembler;

    @Getter
    protected final ApiResourceMapping<ID, E, CB, RB, UB> mapping;

    @Inject
    public ApiResourceController(ApiResourceMapping<ID, E, CB, RB, UB> mapping) {
        this.mapping = mapping;
    }

    public ApiResourceController() {
        this((ApiResourceMapping<ID, E, CB, RB, UB>) ApiResourceUtil.generateMapping(3, ApiResourceController.class));
    }

    public ApiResourceMapping<ID, E, CB, RB, UB> getMapping() {
        return mapping;
    }

    @RequestMapping(value="", method= RequestMethod.GET)
    public ResponseEntity<Page<Resource<RB>>> getAll(@RequestParam MultiValueMap<String, String> params, Pageable p){
        Page<RB> entities = service.findForRead(p);
        return assemblers.respondWithView(mapping.readBeanClass(), view(ControllerMethod.GET_ALL), entities, p, HttpStatus.OK);
    }

    @RequestMapping(value="/{id}", method=RequestMethod.GET)
    public ResponseEntity<Resource<RB>> getOne(@PathVariable("id") ID id){
        RB entity = service.read(conversionService.convert(id, mapping.idClass()));
        return assemblers.respondWithView(entity, view(ControllerMethod.GET_ONE), entity == null ? HttpStatus.NOT_FOUND : HttpStatus.OK);
    }

    @RequestMapping(value="/{id}", method=RequestMethod.DELETE)
    public ResponseEntity<?> deleteOne(@PathVariable("id") ID id){
        service.delete(conversionService.convert(id, mapping.idClass()));
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @RequestMapping(value="", method=RequestMethod.POST)
    public ResponseEntity<Resource<RB>> postOne(@RequestBody CB bean){
        RB entity = service.createForRead(bean);
        return assemblers.respondWithView(entity, view(ControllerMethod.POST), HttpStatus.CREATED);
    }

    @RequestMapping(value="/{id}", method=RequestMethod.PUT)
    public ResponseEntity<Resource<RB>> putOne(@PathVariable("id") ID id, @RequestBody UB bean){
        RB entity = service.updateForRead( id, bean );
        return assemblers.respondWithView(entity, view(ControllerMethod.PUT), entity == null ? HttpStatus.NOT_FOUND : HttpStatus.OK);
    }

    @RequestMapping(value="/searches", method=RequestMethod.POST)
    public ResponseEntity<Page<Resource<RB>>> search(@RequestBody Map<String, Object> search, Pageable pageable){
        Page<RB> entities = service.searchForRead(search, pageable);
        return assemblers.respondWithView( mapping.readBeanClass(), view(ControllerMethod.SEARCH), entities, pageable, HttpStatus.OK);
    }

    private Class<? extends BaseView> view(ControllerMethod method){
        return jsonViewResolver.resolve(method, mapping.readBeanClass());
    }


}

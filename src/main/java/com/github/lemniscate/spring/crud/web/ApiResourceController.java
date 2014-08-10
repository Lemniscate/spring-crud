package com.github.lemniscate.spring.crud.web;

import com.github.lemniscate.spring.crud.web.assembler.ApiResourceAssembler;
import com.github.lemniscate.spring.crud.mapping.ApiResourceMapping;
import com.github.lemniscate.spring.crud.svc.ApiResourceService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.Identifiable;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.List;

/**
* @Author dave 8/8/14 9:20 PM
*/
public class ApiResourceController<ID extends Serializable, E extends Identifiable<ID>, CB, RB extends Identifiable<ID>, UB> {

    public static final String X_SELF_HREF = "X-SELF-HREF";

    @Inject
    protected ApiResourceService<ID, E, CB, RB, UB> service;

    @Inject
    protected ApiResourceAssembler<ID, E, CB, RB, UB> assembler;

    @Getter
    protected final ApiResourceMapping<ID, E, CB, RB, UB> mapping;

    @Inject
    protected ConversionService conversionService;

    @Inject
    public ApiResourceController(ApiResourceMapping<ID, E, CB, RB, UB> mapping) {
        this.mapping = mapping;
    }
//
//    public ApiResourceController() {
//        this((ApiResourceMapping<ID, E, CB, RB, UB>) generateMapping());
//    }
//
//    private static <ID extends Serializable, E extends Identifiable<ID>, CB, RB extends Identifiable<ID>, UB> ApiResourceMapping.ComplexApiResourceMapping<ID, E, CB, RB, UB> generateMapping(){
//        int index = 4;
//        Class<ID> id = (Class<ID>) determineParam(index, 0);
//        Class<E> domain = (Class<E>) determineParam(index, 1);
//        Class<CB> cb = (Class<CB>) determineParam(index, 2);
//        Class<RB> rb = (Class<RB>) determineParam(index, 3);
//        Class<UB> ub = (Class<UB>) determineParam(index, 4);
//
//        ApiResourceMapping.ComplexApiResourceMapping<ID, E, CB, RB, UB> result = new ApiResourceMapping.ComplexApiResourceMapping<ID, E, CB, RB, UB>(id, domain, cb, rb, ub, false);
//        return result;
//    }

    @RequestMapping(value="", method= RequestMethod.GET)
    public ResponseEntity<Page<Resource<RB>>> getAll(@RequestParam MultiValueMap<String, String> params, Pageable p){
        Page<RB> entities = service.findForRead(p);
        List<Resource<RB>> resources = assembler.toResources(entities);
        Page<Resource<RB>> pagedResources = new PageImpl<Resource<RB>>(resources, p, entities.getTotalElements());
        ResponseEntity<Page<Resource<RB>>> response = new ResponseEntity<Page<Resource<RB>>>(pagedResources, HttpStatus.OK);
        return response;
    }

    @RequestMapping(value="/{id}", method=RequestMethod.GET)
    public ResponseEntity<Resource<RB>> getOne(@PathVariable ID id){
        RB entity = service.read(conversionService.convert(id, mapping.idClass()));
        Resource<RB> resource = assembler.toResource(entity);
        ResponseEntity<Resource<RB>> response = new ResponseEntity<Resource<RB>>(resource, HttpStatus.OK);
        return response;
    }

    @RequestMapping(value="/{id}", method=RequestMethod.DELETE)
    public ResponseEntity<Void> deleteOne(@PathVariable ID id){
        service.delete(conversionService.convert(id, mapping.idClass()));
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @RequestMapping(value="", method=RequestMethod.POST)
    public ResponseEntity<Resource<RB>> postOne(@RequestBody CB bean){
        RB entity = service.createForRead(bean);
        Resource<RB> resource = assembler.toResource(entity);
        MultiValueMap<String,String> headers = new LinkedMultiValueMap<String, String>();
        headers.add(X_SELF_HREF, resource.getLink("self").getHref() );
        ResponseEntity<Resource<RB>> response = new ResponseEntity<Resource<RB>>(resource, headers, HttpStatus.CREATED);
        return response;
    }

    @RequestMapping(value="/{id}", method=RequestMethod.PUT)
    public ResponseEntity<Resource<RB>> putOne(@PathVariable ID id, @RequestBody UB bean){
        RB entity = service.updateForRead( bean );
        Resource<RB> resource = assembler.toResource(entity);
        ResponseEntity<Resource<RB>> response = new ResponseEntity<Resource<RB>>(resource, HttpStatus.OK);
        return response;
    }

    public ApiResourceMapping<ID, E, CB, RB, UB> getMapping() {
        return mapping;
    }

    // TODO implement searches
//    @RequestMapping(value="/searches", method=RequestMethod.POST)
//    public ResponseEntity<Page<Resource<RB>>> search(@RequestBody Map<String, Object> search, Pageable pageable){
//        Page<RB> entities = service.search(search, pageable);
//        List<Resource<RB>> resources = assembler.toResources(entities.getContent());
//        Page<Resource<RB>> pagedResources = new PageImpl<Resource<RB>>(resources, pageable, entities.getTotalElements());
//        ResponseEntity<Page<Resource<RB>>> response = new ResponseEntity<Page<Resource<RB>>>(pagedResources, HttpStatus.OK);
//        return response;
//    }


    // Shield your eyes: this nastiness gets us to have default constructors for concrete Assemblers.
    private static Class<?>  determineParam(int callStackIndex, int paramIndex){
        try {
            StackTraceElement[] st = Thread.currentThread().getStackTrace();
            Assert.isTrue(st.length >= callStackIndex, "CallStack didn't contain enough elements");
            // the fourth entry should be our concrete class (unless we have some base-classes... crap)
            String name = st[callStackIndex].getClassName();
            Class<?> clazz = Class.forName(name);
            Type result = GenericTypeResolver.resolveTypeArguments(clazz, ApiResourceController.class)[paramIndex];
            return result.getClass();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}

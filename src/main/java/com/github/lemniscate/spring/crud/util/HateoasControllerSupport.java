package com.github.lemniscate.spring.crud.util;

import com.github.lemniscate.spring.crud.web.assembler.ApiResourceAssembler;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.Identifiable;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.Serializable;
import java.util.List;

@RequiredArgsConstructor
public class HateoasControllerSupport<ID extends Serializable, E extends Identifiable<ID>, CB, RB extends Identifiable<ID>, UB> {

    protected final ApiResourceAssembler<ID, E, CB, RB, UB> assembler;

    public ResponseEntity<Resource<RB>> toResponse(RB entity) {
        return HateoasControllerSupport.toResponse(assembler, entity);
    };

    public ResponseEntity<Page<Resource<RB>>> toResponse(Page<RB> entities, Pageable pageInfo) {
        List<RB> content = entities.getContent();
        List<Resource<RB>> resources = assembler.toResources(content);
        Page<Resource<RB>> pagedResources = new PageImpl<Resource<RB>>(resources, pageInfo, entities.getTotalElements());
        ResponseEntity<Page<Resource<RB>>> response = new ResponseEntity<Page<Resource<RB>>>(pagedResources, HttpStatus.OK);
        return response;
    };

    public static <ID extends Serializable, E extends Identifiable<ID>, CB, RB extends Identifiable<ID>, UB> ResponseEntity<Resource<RB>> toResponse(ApiResourceAssembler<ID, E, CB, RB, UB> assembler, RB entity) {
        if( entity == null ){
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        Resource<RB> resource = assembler.toResource(entity);
        ResponseEntity<Resource<RB>> response = new ResponseEntity<Resource<RB>>(resource, HttpStatus.OK);
        return response;
    };

    public static <ID extends Serializable, E extends Identifiable<ID>, CB, RB extends Identifiable<ID>, UB> ResponseEntity<Page<Resource<RB>>> toResponse(ApiResourceAssembler<ID, E, CB, RB, UB> assembler, Page<RB> entities, Pageable pageInfo) {
        List<RB> content = entities.getContent();
        List<Resource<RB>> resources = assembler.toResources(content);
        Page<Resource<RB>> pagedResources = new PageImpl<Resource<RB>>(resources, pageInfo, entities.getTotalElements());
        ResponseEntity<Page<Resource<RB>>> response = new ResponseEntity<Page<Resource<RB>>>(pagedResources, HttpStatus.OK);
        return response;
    };

}
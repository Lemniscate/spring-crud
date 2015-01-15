package com.github.lemniscate.spring.crud.util;

import com.github.lemniscate.spring.crud.svc.ApiResourceServices;
import com.github.lemniscate.spring.crud.web.assembler.ApiResourceAssemblers;
import org.springframework.core.convert.ConversionService;

import javax.inject.Inject;

/**
 * @Author dave 1/14/15 8:03 PM
 */
public class ApiResourceSupport {

    @Inject
    protected ConversionService conversionService;

    @Inject
    protected ApiResourceServices services;

    @Inject
    protected ApiResourceAssemblers assemblers;

}

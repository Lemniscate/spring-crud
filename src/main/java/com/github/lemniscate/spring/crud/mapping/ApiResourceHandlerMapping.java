package com.github.lemniscate.spring.crud.mapping;

import com.github.lemniscate.spring.crud.web.ApiResourceController;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ApiResourceHandlerMapping {
    private final ApiResourceMapping mapping;
    private final ApiResourceController controller;
}

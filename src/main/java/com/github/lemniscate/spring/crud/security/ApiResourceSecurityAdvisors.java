package com.github.lemniscate.spring.crud.security;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * Created by dave on 2/11/15.
 */
public class ApiResourceSecurityAdvisors {

    @Autowired(required=false)
    protected List<ApiResourceSecurityAdvisor> advisors = Lists.newArrayList();
    
    protected MultiValueMap<Class<?>, ApiResourceSecurityAdvisor> map = new LinkedMultiValueMap<>();

    @PostConstruct
    public void init(){
        for(ApiResourceSecurityAdvisor a : advisors) {
            map.add(a.getDomainClass(), a);
        }
    }

}
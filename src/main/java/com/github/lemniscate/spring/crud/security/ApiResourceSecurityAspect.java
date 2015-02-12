package com.github.lemniscate.spring.crud.security;

import com.github.lemniscate.spring.crud.mapping.ApiResourceMapping;
import com.github.lemniscate.spring.crud.web.ApiResourceController;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.Identifiable;
import org.springframework.hateoas.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by dave on 2/11/15.
 */
@Aspect
@Slf4j
public class ApiResourceSecurityAspect {

    @Inject
    private ApiResourceSecurityAdvisors advisors;
    
    @Getter @Setter
    private boolean ignoreCustomControllerMethods = true;

    @Around("execution(public org.springframework.http.ResponseEntity com.github.lemniscate.spring.crud.web.ApiResourceController+.*(..))")
    public Object aroundAnyController(ProceedingJoinPoint pjp) throws Throwable {
        Object[] args = pjp.getArgs();
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        if( !signature.getDeclaringType().equals(ApiResourceController.class) && ignoreCustomControllerMethods){
            return pjp.proceed(args);
        }

        ApiResourceMapping mapping = ((ApiResourceController) pjp.getTarget()).getMapping();
        Class<?> domain = mapping.domainClass();

        List<ApiResourceSecurityAdvisor> relevantAdvisors = advisors.map.get(domain);

        if( relevantAdvisors == null || relevantAdvisors.isEmpty() ){
            return pjp.proceed(args);
        }else{
            ResultHolder holder = new ResultHolder(pjp);
            for(ApiResourceSecurityAdvisor a : relevantAdvisors){
                switch( signature.getMethod().getName()){
                    case "getAll":
                        a.secureGetAll(domain, holder.proceed(Page.class));
                        break;
                    case "getOne":
                        a.secureGetOne(domain, holder.proceed(Identifiable.class));
                        break;
                    case "deleteOne":
                        a.secureDelete(domain, holder.proceed(Serializable.class));
                        break;
                    case "postOne":
                        a.securePostOne(domain, args[0]);
                        break;
                    case "putOne":
                        a.securePutOne(domain, (Serializable) args[0], args[1]);
                        break;
                    case "search":
                        a.secureSearch(domain, (Map<String, Object>) args[0], holder.proceed(Page.class));
                        break;

                    default:
                        a.secureCatchAll(domain, pjp.getTarget(), mapping.domainClass(), holder.proceed());

                }
            }

            return holder.getResult();
        }
    }

    @RequiredArgsConstructor
    public static class ResultHolder {
        private final ProceedingJoinPoint pjp;

        @Getter
        private ResponseEntity<?> result;

        public <T> T proceed(Class<T> type) throws Throwable {
            if( result == null ){
                result = (ResponseEntity<?>) pjp.proceed(pjp.getArgs());
            }
            Object o = result.getBody();
            // if we have a Hateoas Resource, let's strip it out
            o = o instanceof Resource ? ((Resource) o).getContent() : o;
            return (T) o;
        }

        public Object proceed() throws Throwable {
            return proceed(Object.class);
        }

    }
    
}

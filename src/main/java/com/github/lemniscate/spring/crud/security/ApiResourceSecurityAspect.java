package com.github.lemniscate.spring.crud.security;

import com.github.lemniscate.spring.crud.repo.ApiResourceRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.GenericTypeResolver;
import org.springframework.hateoas.Identifiable;

import javax.inject.Inject;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Collection;
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

    @Around("execution(* com.github.lemniscate.spring.crud.repo.ApiResourceRepository+.*(..))")
    public Object aroundAnyController(ProceedingJoinPoint pjp) throws Throwable {
        Object[] args = pjp.getArgs();
        MethodSignature signature = (MethodSignature) pjp.getSignature();

        Class<?> domain = GenericTypeResolver.resolveTypeArguments(pjp.getTarget().getClass(), ApiResourceRepository.class)[1];

        List<ApiResourceSecurityAdvisor> relevantAdvisors = advisors.map.get(domain);

        if( relevantAdvisors == null || relevantAdvisors.isEmpty() ){
            return pjp.proceed(args);
        }else{
            ResultHolder holder = new ResultHolder(pjp);
            Class[] pTypes = signature.getParameterTypes();
            
            for(ApiResourceSecurityAdvisor a : relevantAdvisors){
                if( hasMethod(signature.getMethod()) ) {
                    switch (signature.getMethod().getName()) {
                        case "findAll":
                            if (args.length > 0 && args[0] != null && Iterable.class.isAssignableFrom(args[0].getClass())) {
                                a.secureFindByIds(domain, (Iterable) args[0], holder.proceed(Collection.class));
                            } else {
                                a.secureFind(domain, holder.proceed(Iterable.class));
                            }
                            break;


                        case "findOne":
                            // intentional fall-through
                        case "getOne":
                            a.secureFindOne(domain, (Serializable) args[0], holder.proceed(Identifiable.class));
                            break;

                        case "saveAndFlush":
                            // intentional fall-through
                        case "save":
                            if (args.length > 0 && args[0] != null && Iterable.class.isAssignableFrom(args[0].getClass())) {
                                a.secureSaveMany(domain, holder.proceed(Iterable.class));
                            } else if (args.length > 0 && args[0] != null && Identifiable.class.isAssignableFrom(args[0].getClass())) {
                                a.secureSave(domain, holder.proceed(Identifiable.class));
                            } else {
                                log.warn("Unhandled save method... ");
                                a.secureCatchAll(domain, pjp.getTarget(), signature, holder.proceed());
                            }

                            break;


                        case "delete":
                            if (args.length > 0 && args[0] != null && Iterable.class.isAssignableFrom(args[0].getClass())) {
                                a.secureDeleteMany(domain, (Iterable) args[0]);
                            } else if (args.length > 0 && args[0] != null && Identifiable.class.isAssignableFrom(args[0].getClass())) {
                                a.secureDelete(domain, (Identifiable) args[0]);
                            } else {
                                log.warn("Unhandled delete method... ");
                                a.secureCatchAll(domain, pjp.getTarget(), signature, holder.proceed());
                            }
                            break;

                        case "deleteInBatch":
                            a.secureDeleteMany(domain, (Iterable) args[0]);
                            break;

                        case "deleteAllInBatch":
                            // intentional fall-through
                        case "deleteAll":
                            a.secureDeleteAll(domain);
                            break;

                        case "search":
                            a.secureSearch(domain, (Map<String, Object>) args[0], holder.proceed(Iterable.class));
                            break;

                        default:
                            // TODO can we not hit this anymore, because of "hasMethod" check?
                            a.secureCatchAll(domain, pjp.getTarget(), signature, holder.proceed());

                    }
                }else{
                    a.secureCatchAll(domain, pjp.getTarget(), signature, holder.proceed());
                }
            }

            return holder.getResult();
        }
    }
    
    // an ugly way to determine if we're in one of our repositories, or a subclass that has the same method name
    private boolean hasMethod(Method method){
        try {
            ApiResourceRepository.class.getMethod( method.getName(), method.getParameterTypes());
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    @RequiredArgsConstructor
    public static class ResultHolder {
        private final ProceedingJoinPoint pjp;

        @Getter
        private Object result;

        public <T> T proceed(Class<T> type) throws Throwable {
            if( result == null ){
                result = pjp.proceed(pjp.getArgs());
            }
            return (T) result;
        }

        public Object proceed() throws Throwable {
            return proceed(Object.class);
        }

    }

}

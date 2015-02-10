package com.github.lemniscate.spring.crud.view;

import com.github.lemniscate.spring.crud.web.ControllerMethod;
import com.github.lemniscate.spring.jsonviews.client.BaseView;

/**
 * Resolves the view to use for a given controller endpoint.
 *
 * @Author dave 2/1/15 1:05 PM
 */
public class JsonViewResolver {

    public Class<? extends BaseView> resolve(ControllerMethod method, Class<?> type){
        return null;
    }

}

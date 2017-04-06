/*
 * Copyright 2017 ELIXIR EBI
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.elixir.ebi.ega.access.config;

import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Peter_Szanto on 1/18/2017.
 */
public class HystrixContextInterceptor extends HandlerInterceptorAdapter {

    static HystrixRequestContext globalSharedContext;
    static {
        HystrixRequestContext.initializeContext();
        globalSharedContext = HystrixRequestContext.getContextForCurrentThread();

    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HystrixRequestContext.setContextOnCurrentThread(globalSharedContext);
        return super.preHandle(request, response, handler);
    }
}
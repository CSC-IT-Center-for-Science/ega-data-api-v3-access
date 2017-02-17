/*
 * Copyright 2016 ELIXIR EBI
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
package eu.elixir.ebi.aga.access.rest;

import eu.elixir.ebi.aga.access.dto.Request;
import eu.elixir.ebi.aga.access.dto.RequestTicket;
import eu.elixir.ebi.aga.access.service.RequestService;
import java.util.List;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 *
 * @author asenf
 */
@RestController
@EnableDiscoveryClient
@RequestMapping("/requests")
public class RequestController {

    @Autowired
    private RequestService requestService;
    
    @RequestMapping(method = GET)
    @ResponseBody
    public Iterable<String> listRequests() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String user_email = auth.getName();
        return requestService.listRequests(user_email);
    }

    @RequestMapping(method = POST)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void newRequest(@RequestBody List<Request> requests) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String ip = "TODO";
        for (Request request:requests) {
            requestService.newRequest(auth, ip, request);
        }
    }

    @RequestMapping(value = "/{request_label}/tickets", method = GET)
    @ResponseBody
    public Iterable<RequestTicket> listRequestTickets(@PathVariable String request_label) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String user_email = auth.getName();
        return requestService.listRequestTickets(user_email, request_label);
    }

    @RequestMapping(value = "/{request_label}", method = DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRequest(@PathVariable String request_label) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String user_email = auth.getName();
        requestService.deleteRequest(user_email, request_label);
    }

    @RequestMapping(value = "/{request_label}/tickets/{ticket}", method = GET)
    @ResponseBody
    public RequestTicket listRequestTicket(@PathVariable String request_label, @PathVariable String ticket) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String user_email = auth.getName();
        return requestService.listOneRequestTicket(user_email, request_label, ticket);
    }

    @RequestMapping(value = "/{request_label}/tickets/{ticket}", method = DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRequestTicket(@PathVariable String request_label, @PathVariable String ticket) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String user_email = auth.getName();
        requestService.deleteOneRequestTicket(user_email, request_label, ticket);
    }
    
}

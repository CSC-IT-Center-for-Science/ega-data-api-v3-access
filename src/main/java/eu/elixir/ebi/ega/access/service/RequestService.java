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
package eu.elixir.ebi.ega.access.service;

import eu.elixir.ebi.ega.access.dto.Request;
import eu.elixir.ebi.ega.access.dto.RequestTicket;

import org.springframework.security.core.Authentication;

/**
 *
 * @author asenf
 */
public interface RequestService {

    public Iterable<String> listRequests(String user_email);
    
    public void newRequest(Authentication auth, String ip, Request request);
    
    public Iterable<RequestTicket> listRequestTickets(String user_email, String request_label);
    
    public void deleteRequest(String user_email, String request_label);
    
    public RequestTicket listOneRequestTicket(String user_email, String request_label, String ticket);
    
    public void deleteOneRequestTicket(String user_email, String request_label, String ticket);
    
}

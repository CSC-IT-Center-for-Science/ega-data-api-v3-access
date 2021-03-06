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
package eu.elixir.ebi.ega.access.service.internal;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import eu.elixir.ebi.ega.access.config.NotFoundException;
import eu.elixir.ebi.ega.access.config.PermissionsException;
import eu.elixir.ebi.ega.access.dto.File;
import eu.elixir.ebi.ega.access.dto.Request;
import eu.elixir.ebi.ega.access.dto.RequestTicket;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import eu.elixir.ebi.ega.access.service.RequestService;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.UUID;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

/**
 *
 * @author asenf
 */
@Service
@Transactional
@EnableDiscoveryClient
public class RemoteRequestServiceImpl implements RequestService {

    private final String SERVICE_URL = "http://DOWNLOADER";
    
    @Autowired
    RestTemplate restTemplate;    
    
    @Override
    @HystrixCommand
    public Iterable<String> listRequests(String email) {
        String[] requests = restTemplate.getForObject(SERVICE_URL + "/request/{email}/", String[].class, email);
        return Arrays.asList(requests);        
    }

    @Override
    @HystrixCommand
    public void newRequest(Authentication auth, String ip, Request request) {
        String email = auth.getName();
        
        // Process: Turn a 'Request' into a List<RequestTicket> ()
        ArrayList<File> requestFiles = new ArrayList<>();

        // Obtain all Authorised Datasets
        HashSet<String> permissions = new HashSet<>();
        Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        while (iterator.hasNext()) {
            GrantedAuthority next = iterator.next();
            permissions.add(next.getAuthority());
        }
        
        // Check Permissions (To Be changed later....)
        if (request.getType().equalsIgnoreCase("file")) {   // Request for 1 File
            ResponseEntity<File[]> forEntity = restTemplate.getForEntity(SERVICE_URL + "/file/{file_id}", File[].class, request.getId());
            File[] forEntityBody = forEntity.getBody();
            if (forEntityBody != null && forEntityBody.length>0) {
                for (int i=0; i<forEntityBody.length; i++) {
                    String dataset_id = forEntityBody[i].getDatasetId();
                    if(permissions.contains(dataset_id)) {
                        requestFiles.add(forEntityBody[i]);
                        break;
                    }
                }
                if (requestFiles==null || requestFiles.size()==0) {
                    throw new PermissionsException("Not Permitted access to Dataset(s) containing File", request.getId());
                }
            } else {
                throw new NotFoundException("File not Found", request.getId());
            }
        } else {    // Request for a Dataset
            
            if(permissions.contains(request.getId())) {
                File[] files = restTemplate.getForObject(SERVICE_URL + "/datasets/{dataset_id}/files/", File[].class, request.getId());
                requestFiles.addAll(Arrays.asList(files));
            } else {
                throw new PermissionsException("Not Permitted access to Dataset", request.getId());
            }
        }
        
        // If there are files to be requested, generate Tickets to be passed on to DOWNLOADER to be saved
        ArrayList<RequestTicket> newRequest = new ArrayList<>();
        for (File f:requestFiles) {
            UUID uuid = UUID.randomUUID();
            RequestTicket t = new RequestTicket();
                t.setDownloadTicket(uuid.toString());
                t.setEncryptionKey(request.getReKey());
                t.setEncryptionType("AES128"); // Default for Distribution via Ticket
                t.setFileId(f.getFileId());
                t.setLabel(request.getLabel());
                t.setEmail(email);
                t.setTicketStatus("ready");
                t.setClientIp(ip);
                t.setCreated(new Timestamp(System.currentTimeMillis()));
                t.setStartCoordinate(Long.parseLong(request.getStart()));
                t.setEndCoordinate(Long.parseLong(request.getEnd()));
            newRequest.add(t);
        }

        // Send newly created tickets to DOWNLOADER service to be stores in DB
        restTemplate.postForEntity(SERVICE_URL + "/request/", newRequest, String.class);
    }

    @Override
    @HystrixCommand
    public Iterable<RequestTicket> listRequestTickets(String email, String request_label) {
        RequestTicket[] tickets = restTemplate.getForObject(SERVICE_URL + "/request/{email}/{request_label}/", RequestTicket[].class, email, request_label);
        return Arrays.asList(tickets);        
    }

    @Override
    @HystrixCommand
    public void deleteRequest(String email, String request_label) {
        restTemplate.delete(SERVICE_URL + "/request/{email}/{label}/", email, request_label);
    }

    @Override
    @HystrixCommand
    public RequestTicket listOneRequestTicket(String email, String request_label, String ticket) {
        // label not used at the moment
        RequestTicket ticketObject = restTemplate.getForObject(SERVICE_URL + "/request/ticket/{ticket}/", RequestTicket.class, ticket);
        return ticketObject;
    }

    @Override
    @HystrixCommand
    public void deleteOneRequestTicket(String email, String request_label, String ticket) {
        restTemplate.delete(SERVICE_URL + "/request/{email}/ticket/{ticket}/", email, ticket);
    }
 
}

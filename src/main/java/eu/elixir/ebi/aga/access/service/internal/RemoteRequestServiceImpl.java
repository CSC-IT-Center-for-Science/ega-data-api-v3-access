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
package eu.elixir.ebi.aga.access.service.internal;

import eu.elixir.ebi.aga.access.config.NotFoundException;
import eu.elixir.ebi.aga.access.config.PermissionsException;
import eu.elixir.ebi.aga.access.dto.File;
import eu.elixir.ebi.aga.access.dto.Request;
import eu.elixir.ebi.aga.access.dto.RequestTicket;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import eu.elixir.ebi.aga.access.service.RequestService;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

/**
 *
 * @author asenf
 */
@Service
@Transactional
@EnableDiscoveryClient
public class RemoteRequestServiceImpl implements RequestService {

    private final String DATA_URL = "http://DATA";
    private final String SERVICE_URL = "http://DOWNLOADER";
    
    @Autowired
    RestTemplate restTemplate;    
    
    @Override
    public Iterable<String> listRequests(String user_email) {
        String[] requests = restTemplate.getForObject(SERVICE_URL + "/request/{user_email}/", String[].class, user_email);
        return Arrays.asList(requests);        
    }

    @Override
    public void newRequest(String user_email, String ip, Request request) {
        // Process: Turn a 'Request' into a List<RequestTicket> ()
        ArrayList<File> requestFiles = new ArrayList<>();

        // Check Permissions (To Be changed later....)
        if (request.getType().equalsIgnoreCase("file")) {   // Request for 1 File
            ResponseEntity<File[]> forEntity = restTemplate.getForEntity(DATA_URL + "/file/{file_id}", File[].class, request.getId());
            File[] forEntityBody = forEntity.getBody();
            if (forEntityBody != null && forEntityBody.length>0) {
                for (int i=0; i<forEntityBody.length; i++) {
                    String dataset_id = forEntityBody[i].getDatasetStableId();
                    String permission = restTemplate.getForObject(DATA_URL + "/user/{user_email}/datasets/{dataset_id}/", String.class, user_email, dataset_id);
                    if(permission!=null && permission.equalsIgnoreCase("approved")) {
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
            String permission = restTemplate.getForObject(DATA_URL + "/user/{user_email}/datasets/{dataset_id}/", String.class, user_email, request.getId());
            if(permission!=null && permission.equalsIgnoreCase("approved")) {
                File[] files = restTemplate.getForObject(DATA_URL + "/datasets/{dataset_id}/files/", File[].class, request.getId());
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
                t.setFileStableId(f.getStableId());
                t.setLabel(request.getLabel());
                t.setUserEmail(user_email);
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
    public Iterable<RequestTicket> listRequestTickets(String user_email, String request_label) {
        RequestTicket[] tickets = restTemplate.getForObject(SERVICE_URL + "/request/{user_email}/{request_label}/", RequestTicket[].class, user_email, request_label);
        return Arrays.asList(tickets);        
    }

    @Override
    public void deleteRequest(String user_email, String request_label) {
        restTemplate.delete(SERVICE_URL + "/request/{user_email}/{label}/", user_email, request_label);
    }

    @Override
    public RequestTicket listOneRequestTicket(String user_email, String request_label, String ticket) {
        // label not used at the moment
        RequestTicket ticketObject = restTemplate.getForObject(SERVICE_URL + "/request/ticket/{ticket}/", RequestTicket.class, ticket);
        return ticketObject;
    }

    @Override
    public void deleteOneRequestTicket(String user_email, String request_label, String ticket) {
        restTemplate.delete(SERVICE_URL + "/request/{user_email}/ticket/{ticket}/", user_email, ticket);
    }
    
}

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

import eu.elixir.ebi.ega.access.dto.File;
import eu.elixir.ebi.ega.access.service.FileService;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

/**
 *
 * @author asenf
 */
@Service
@Transactional
@EnableDiscoveryClient
public class RemoteFileServiceImpl implements FileService {

    private final String SERVICE_URL = "http://DOWNLOADER";
    
    @Autowired
    RestTemplate restTemplate;

    @Override
    public File getFile(Authentication auth, String file_id) {
        ResponseEntity<File[]> forEntity = restTemplate.getForEntity(SERVICE_URL + "/file/{file_id}", File[].class, file_id);

        // Obtain all Authorised Datasets
        HashSet<String> permissions = new HashSet<>();
        Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        while (iterator.hasNext()) {
            GrantedAuthority next = iterator.next();
            permissions.add(next.getAuthority());
        }
        
        // Is this File in at least one Authoised Dataset?
        File[] body = forEntity.getBody();
        if (body!=null) {
            for (File f:body) {
                String dataset_id = f.getDatasetStableId();
                if (permissions.contains(dataset_id)) {
                    return f;
                }
            }
        }
        
        return (new File());
    }
    
    @Override
    public Iterable<File> getDatasetFiles(String dataset_id) {
        File[] response = restTemplate.getForObject(SERVICE_URL + "/datasets/{dataset_id}/files", File[].class, dataset_id);
        return Arrays.asList(response);
    }
    
}

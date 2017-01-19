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

import eu.elixir.ebi.aga.access.dto.File;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import eu.elixir.ebi.aga.access.service.FileService;

/**
 *
 * @author asenf
 */
@Service
@Transactional
@EnableDiscoveryClient
public class RemoteFileServiceImpl implements FileService {

    private final String SERVICE_URL = "http://DATA";
    
    @Autowired
    RestTemplate restTemplate;

    @Override
    public File getFile(String user_email, String file_id) {
        ResponseEntity<File[]> forEntity = restTemplate.getForEntity(SERVICE_URL + "/file/{file_id}", File[].class, file_id);

        File[] body = forEntity.getBody();
        if (body!=null) {
            for (File f:body) {
                String dataset_id = f.getDatasetStableId();
                String permission = restTemplate.getForObject(SERVICE_URL + "/user/{user_email}/datasets/{dataset_id}/", String.class, user_email, dataset_id);
                if (permission.equalsIgnoreCase("approved")) {
                    return f;
                }
            }
        }
        return (new File());
    }
    
}

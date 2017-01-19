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
import org.springframework.stereotype.Service;
import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import eu.elixir.ebi.aga.access.service.DatasetService;

/**
 *
 * @author asenf
 */
@Service
@Transactional
@EnableDiscoveryClient
public class RemoteDatasetServiceImpl implements DatasetService {

    private final String SERVICE_URL = "http://DATA";
    
    @Autowired
    RestTemplate restTemplate;

    @Override
    public Iterable<String> getDatasets(String user_email) {
        String[] response = restTemplate.getForObject(SERVICE_URL + "/user/{user_email}/datasets/", String[].class, user_email);
        return Arrays.asList(response);
    }

    @Override
    public Iterable<File> getDatasetFiles(String user_email, String dataset_id) {
        String permission = restTemplate.getForObject(SERVICE_URL + "/user/{user_email}/datasets/{dataset_id}/", String.class, user_email, dataset_id);
        File[] response = permission.equalsIgnoreCase("approved")?restTemplate.getForObject(SERVICE_URL + "/datasets/{dataset_id}/files/", File[].class, dataset_id):(new File[]{});
        return Arrays.asList(response);
    }
    
}

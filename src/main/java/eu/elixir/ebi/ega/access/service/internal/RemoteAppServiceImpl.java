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
import eu.elixir.ebi.ega.access.dto.Dataset;
import eu.elixir.ebi.ega.access.dto.File;
import eu.elixir.ebi.ega.access.service.AppService;

import java.util.ArrayList;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author asenf
 */
@Service
@Transactional
@EnableDiscoveryClient
public class RemoteAppServiceImpl implements AppService {

    private final String RES_URL = "http://RES";
    private final String SERVICE_URL = "http://DATA";
    
    @Autowired
    RestTemplate restTemplate;

    @Override
    @HystrixCommand
    public Iterable<String> getPublicKeyOrgs() {
        String[] response = restTemplate.getForObject(RES_URL + "/file/availableformats/", String[].class);
        
        ArrayList<String> result = new ArrayList<>();
        for (String key:response) {
            if (key.toLowerCase().startsWith("publicgpg_")) {
                result.add(key);
            }
        }
        
        return result;
    }
    
    @Override
    @HystrixCommand
    public Iterable<String> getDatasets() {
        Dataset[] response = restTemplate.getForObject(SERVICE_URL + "/datasets/", Dataset[].class);

        ArrayList<String> datasetIds = new ArrayList<>();
        for (Dataset dataset:response) {
            datasetIds.add(dataset.getStableId());
        }
        
        return datasetIds;
    }
    
    @Override
    @HystrixCommand
    public Iterable<String> getDatasetsByOrg(String org) {
        String[] response = restTemplate.getForObject(SERVICE_URL + "/datasets/org/{org}", String[].class, org);

        ArrayList<String> datasetIds = new ArrayList<>();
        for (String dataset:response) {
            datasetIds.add(dataset);
        }
        
        return datasetIds;
    }

    @Override
    @HystrixCommand
    public Iterable<File> getDatasetFiles(String dataset_id) {
        File[] response = restTemplate.getForObject(SERVICE_URL + "/datasets/{dataset_id}/files/", File[].class, dataset_id);

        return Arrays.asList(response);
    }
    
    @Override
    @HystrixCommand
    public Iterable<String> getDacDatasets(String dac_id) {
        Dataset[] response = restTemplate.getForObject(SERVICE_URL + "/dac/" + dac_id + "/datasets/", Dataset[].class);

        ArrayList<String> datasetIds = new ArrayList<>();
        for (Dataset dataset:response) {
            datasetIds.add(dataset.getStableId());
        }
        
        return datasetIds;
    }

    @Override
    @HystrixCommand
    public Iterable<String> getElixirDatasets(String user_id) {
        // Get EGA for ELIXIR User
        // TODO: Use hard coded map for now!
        String ega_user = user_id;
        if (user_id.equalsIgnoreCase("juha.tornroos@gmail.com")) {
            ega_user = "...";
        }        
        
        // Perform lookup for EGA User
        Dataset[] response = restTemplate.getForObject(SERVICE_URL + "/user/" + ega_user + "/datasets/", Dataset[].class);

        ArrayList<String> datasetIds = new ArrayList<>();
        for (Dataset dataset:response) {
            datasetIds.add(dataset.getStableId());
        }
        
        return datasetIds;
    }
}

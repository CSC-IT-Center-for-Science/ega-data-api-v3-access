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

import eu.elixir.ebi.aga.access.dto.File;
import eu.elixir.ebi.aga.access.service.FileService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 *
 * @author asenf
 */
@RestController
@EnableDiscoveryClient
@RequestMapping("/datasets")
public class DatasetController {
    
    @Autowired
    private FileService fileService;
    
    @RequestMapping(method = GET)
    public @ResponseBody Iterable<String> list() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        ArrayList<String> result = new ArrayList<>();
        while (iterator.hasNext()) {
            GrantedAuthority next = iterator.next();
            result.add(next.getAuthority());
        }
        return result; // List of datasets authorized for this user
    }
        
    @RequestMapping(value = "/{dataset_id}/files", method = GET)
    public @ResponseBody Iterable<File> getDatasetFiles(@PathVariable String dataset_id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Validate Dataset Access
        boolean permission = false;
        Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        while (iterator.hasNext()) {
            GrantedAuthority next = iterator.next();
            if (dataset_id.equalsIgnoreCase(next.getAuthority())) {
                permission = true;
                break;
            }
        }
        
        return permission?(fileService.getDatasetFiles(dataset_id)):(new ArrayList<>());
    }

}

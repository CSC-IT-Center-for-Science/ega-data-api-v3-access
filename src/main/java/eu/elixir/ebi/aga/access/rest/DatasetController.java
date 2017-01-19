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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import org.springframework.web.bind.annotation.ResponseBody;
import eu.elixir.ebi.aga.access.service.DatasetService;
import java.security.Principal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 *
 * @author asenf
 */
@RestController
@EnableDiscoveryClient
@RequestMapping("/datasets")
public class DatasetController {
    
    @Autowired
    private DatasetService datasetService;

    @RequestMapping(method = GET)
    public @ResponseBody Iterable<String> list() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String user_email = auth.getName();
        return datasetService.getDatasets(user_email);
    }
    
    @RequestMapping(value = "/{dataset_id}/files", method = GET)
    public @ResponseBody Iterable<File> getDatasetFiles(@PathVariable String dataset_id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String user_email = auth.getName();
        return datasetService.getDatasetFiles(user_email, dataset_id);
    }
    
    // Test Code Below ----
    @RequestMapping("/unprotected")
    public String unprotected() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName(); //get logged in username
        boolean a = auth.isAuthenticated();
        return "Should be unprotected, no sign-in needed " + name + ", " + a;
    }
    @RequestMapping("/protected")
    public String yesProtected(Principal principal) {
        
        return "In order to see this, you should have had to authenticate via an OAuth2 Authorization Server, " + principal.toString() + "  \n";
    }
    
}

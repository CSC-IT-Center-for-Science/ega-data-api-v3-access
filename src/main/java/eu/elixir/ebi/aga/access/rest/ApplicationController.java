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
import eu.elixir.ebi.aga.access.service.AppService;
import java.util.Collection;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author asenf
 */
@RestController
@EnableDiscoveryClient
@RequestMapping("/app")
public class ApplicationController {
    
    @Autowired
    private AppService appService;
    
    @Autowired
    private OAuth2ClientContext oAuth2ClientContext;    
    
    @RequestMapping(value = "/orgs", method = GET)
    public @ResponseBody Iterable<String> list() {
        return appService.getPublicKeyOrgs();
    }
        
    @RequestMapping(value = "/datasets", method = GET)
    public @ResponseBody Iterable<String> listDatasets() {
        return appService.getDatasets();
    }
    
    @RequestMapping(value = "/datasets/{org}", method = GET)
    public @ResponseBody Iterable<String> listDatasets(@PathVariable String org) {
        return appService.getDatasetsByOrg(org);
    }
    
    @RequestMapping(value = "/datasets/{dataset_id}/files", method = GET)
    public @ResponseBody Iterable<File> getDatasetFiles(@PathVariable String dataset_id) {
        return appService.getDatasetFiles(dataset_id);
    }
    
    @RequestMapping(value = "/dac/{dac_id}/datasets", method = GET)
    public @ResponseBody Iterable<String> listDacDatasets(@PathVariable String dac_id) {
        return appService.getDacDatasets(dac_id);
    }
    
    // Test Code Below -----
    @RequestMapping("/unprotected")
    public String unprotected() {

        OAuth2Authentication auth = (OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication();
        String name = auth!=null?auth.getName():"null"; //get logged in username
        boolean a = auth!=null?auth.isAuthenticated():false;
        Collection<GrantedAuthority> authorities = auth.getAuthorities();
        OAuth2Request oAuth2Request = auth.getOAuth2Request();
        System.out.println("333 " + oAuth2Request.toString());
        Set<String> scope = oAuth2Request.getScope();
        System.out.println("444 " + scope.toString());
        Object principal = auth.getPrincipal();
        System.out.println("555 " + principal.toString());
        Authentication userAuthentication = auth.getUserAuthentication();
        System.out.println("666 " + userAuthentication.toString());
        
        return "Should be unprotected, no sign-in needed " + name + ", " + a;
    }

}

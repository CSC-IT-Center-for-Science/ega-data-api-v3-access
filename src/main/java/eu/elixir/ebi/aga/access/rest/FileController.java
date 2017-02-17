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
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import eu.elixir.ebi.aga.access.service.FileService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 *
 * @author asenf
 */
@RestController
@EnableDiscoveryClient
@RequestMapping("/files")
public class FileController {

    @Autowired
    private FileService fileService;
    
    @RequestMapping(value = "/{file_id}", method = GET)
    @ResponseBody
    public File getFile(@PathVariable String file_id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // I don't know the dataset ID yet - pass on auth object to implementation for access control
        return fileService.getFile(auth, file_id);
    }

}

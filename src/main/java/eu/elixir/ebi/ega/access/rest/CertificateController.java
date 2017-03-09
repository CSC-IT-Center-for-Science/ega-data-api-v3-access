/*
 * Copyright 2017 ELIXIR EBI
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
package eu.elixir.ebi.ega.access.rest;

import eu.elixir.ebi.ega.access.config.PermissionsException;
import java.io.IOException;
import javax.security.cert.Certificate;
import javax.servlet.http.HttpServletRequest;
import static org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers.x509Certificate;
import org.bouncycastle.cert.X509CertificateHolder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
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
@RequestMapping("/certs")
public class CertificateController {
    
    // Autowired Service
    // TODO
    
    @RequestMapping(value = "/exchange", method = GET)
    public @ResponseBody String list(HttpServletRequest servletRequest) throws IOException {
        Certificate[] certs = 
            (Certificate[]) servletRequest.getAttribute("javax.servlet.request.X509Certificate");
        
        if (certs != null) {
            // Validate Certificate?
            // Exchange for EGA AAI Token?
            
            
            //convert to bouncycastle if you want
            X509CertificateHolder x509CertificateHolder = new X509CertificateHolder(x509Certificate.getEncoded());
        
        } else {
            throw new PermissionsException("Not Permitted to obtain Access Token", "name?");
        }
        
        return "MyAccessToken";
    }
}

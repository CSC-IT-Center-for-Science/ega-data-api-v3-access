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
package eu.elixir.ebi.ega.access.dto;

import java.sql.Timestamp;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author asenf
 */
@NoArgsConstructor
//@AllArgsConstructor
@Setter
@Getter
public class RequestTicket {
    
        private String userEmail;
        private String downloadTicket;
        private String clientIp;
        private String fileStableId;
        private String encryptionKey;
        private String encryptionType;
        private String ticketStatus;
        private String label;
        private Timestamp created;
        private long startCoordinate;
        private long endCoordinate;
        
        public String toString() {
            return userEmail+":"+downloadTicket+":"+clientIp+":"+fileStableId+":"+encryptionKey+":"+encryptionType+":"+ticketStatus+":"+label+":"+startCoordinate+":"+endCoordinate;
        }
        
        public RequestTicket(String userEmail,
                             String downloadTicket,
                             String clientIp,
                             String fileStableId,
                             String encryptionKey,
                             String encryptionType,
                             String ticketStatus,
                             String label,
                             Timestamp created,
                             long startCoordinate,
                             long endCoordinate) {
            
            this.userEmail = userEmail;
            this.downloadTicket = downloadTicket;
            this.clientIp = clientIp;
            this.fileStableId = fileStableId;
            this.encryptionKey = "***"; // Hide this information - it does not leave the EGA Vault!
            this.encryptionType = encryptionType;
            this.ticketStatus = ticketStatus;
            this.label = label;
            this.created = created;
            this.startCoordinate = startCoordinate;
            this.endCoordinate = endCoordinate;
        }
}

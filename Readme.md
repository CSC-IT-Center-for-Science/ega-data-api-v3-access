# EGA.Data.API.v3.ACCESS

This is an Edge Server (ACCESS). It enforces user authentication by requiring an EGA Bearer Token for API access. ACCESS provides access to authorised metadata and allows to request authorised data files to be downloaded.

Dependency: 
* CONFIG (`https://github.com/elixir-europe/ega-data-api-v3-config`). The `'bootstrap-blank.properties'` file must be modified to point to a running configuration service, which will be able to serve the `application.properties` file for this service `ACCESS`
* EUREKA (`https://github.com/elixir-europe/ega-data-api-v3-eureka`). ACCESS service will contact the other services via EUREKA and registers itself with it.
* DATA (``). This service provides metadata regarding the archive. Users can list authorised content in ACCESS; ACCESS obtains the content from DATA.
* DOWNLOADER (``). This service handles download requests and logging. Users can request authorised data for download. Requests are tracked and logged in a database that is handled by the DOWNLOADER service.
* EGA AAI. This is an Authentication and Authorisation Infrastructure service (OpenID Connect IdP) available at Central EGA.

### Documentation

ACCESS is an Edge Service - it is the API for end users (such as the Download Client). At Central EGA this API is exposed via an ssl-terminating load balancer. Without that, this service will have to be configured to accept only SSL secured https connections. The service runs as HTTP service; the load balancer requires HTTPS from external users and has a proper certificate for EBI installed. ACCESS is exposed on https://ega.ebi.ac.uk:8051/

ACCESS is secured by the EGA AAI; there are two types of access tokens used.
Valid access tokens can be obtained with a direct user login (e.g. grant_type=password) and are required for all but one URL on ACCESS. The user is obtained from the access token, so each URL returns user-specific results, subject to user authorization.
An access token obtained with client credentials (grant_type=client_credentials) and a scope of “fuse” is required for the ‘/app’ endpoint. This is what is used at Central EGA by the FUSE layer backend (which is used by GridFTP). End users will never directly interact with this endpoint.

[GET] `/stats/load` (not secured by token; used by the load balancer heartbeat.)
[GET] `/datasets`
[GET] `/datasets/{dataset_id}/files`
[GET] `/files/{file_id}`
[GET] `/requests`
[POST] `/requests`
[GET] `/requests/{request_label}/tickets`
[GET] `/requests/{request_label}/tickets/{ticket}`
[DELETE] `/requests/{request_label}/tickets/{ticket}`
[DELETE] `/requests/{request_label}`

Only at Central EGA:
[GET]	`/app/orgs`
[GET]	`/app/datasets`
[GET]	`/app/datasets/{dataset_id}/files`
[GET]	`/app/dac/{dac_id}/datasets`

### Usage Examples


### Todos

 - Write Tests
 - Develop GA4GH Functionality


## Deployment
!!! - This deployment resolves on host.docker.internal (ports 45000 and 52000), so if you cannot resolve that in your browser after deploying it, try assigning host.docker.internal to 127.0.0.1 in your hosts file.

### Using Docker Compose

First you need to pull the current folder locally..

### Deploy the database
1. **Navigate to `postgres` directory**

2. **Edit the `.env` file:**
```txt
   POSTGRES_TAG=16-alpine              # The version of PostgreSQL
   DBPORT=45432                        # External port in which the postgres server will be deployed
   DBUSER=postgres                     # The name of the postgres admin
   DBPASS=admin                        # The password of the postgres admin

   MAINPATH=.                          # The base location for the PostgreSQL mounts

   DOCKER_USER=                        # The UID for the user that will deploy the container (cat /etc/passwd)
   DOCKER_GROUP=                       # The GID for the user that will deploy the container (cat /etc/passwd)
```
3. **Deploy PostgreSQL:**
- Create the two necessary docker external networks by running
```bash
   docker network create ewb-postgres-shared-network
   docker network create ewb-keycloak-shared-network
```
- Make sure the `deployment/postgres/db_init.sql` is executable and has the correct permissions in order to be run by the container. This sql script creates the two databases `keycloak` and `ewb` when the container is deployed
- Run the following command to start all services defined in the Docker Compose file:
```bash
   docker-compose up -d
```

4. **Initialize databases:**
   - Login as admin in the database
   - Run the `/db/00.00.01.seed.sql` script on the ewb database to initialize it

### Deploy Keycloak

1. **Navigate to `keycloak` directory**
2. **Edit the `.env` file:**
```txt
   MAINPATH=.                          # The base location for the Keycloak mounts
   KEYCLOAK_TAG=25.0.6                 # The version of Keycloak

   DOCKER_USER=                        # The UID for the user that will deploy the container (cat /etc/passwd)
   DOCKER_GROUP=                       # The GID for the user that will deploy the container (cat /etc/passwd)
```

3. **Edit the `keycloak.env` file:**
```txt
   ######################### DB Configuration ############################

   KC_DB=postgres                          # Name of the postgres admin db
   KC_DB_URL_HOST=ewb-postgres             # Postgres container name
   KC_DB_SCHEMA=public                     # Keycloak DB schema
   KC_DB_PORT=5432                         # The (internal) DB port
   KC_DB_URL_DATABASE=keycloak             # Keycloak DB name
   KC_DB_USERNAME=postgres                 # Username of the keycloak DB owner
   KC_DB_PASSWORD=admin                    # Password of the Keycloak DB owner

   ##################### Keycloak Configuration #########################

   KEYCLOAK_ADMIN=admin                    # Admin user for Keycloak
   KEYCLOAK_ADMIN_PASSWORD=admin           # Keycloak admin password

   KC_HOSTNAME=http://host.docker.internal:52000/        # Full URL for the endpoint of keycloak
   KC_HOSTNAME_ADMIN=http://host.docker.internal:52000/  # Full URL for the endpoint of the keycloak admin panel
   KC_PROXY_HEADERS=xforwarded                           # The proxy headers that should be accepted by the server
   KC_HEALTH_ENABLED=true                                # Health checks are available at /health, /health/ready and /health/live endpoints.
   KC_METRICS_ENABLED=true                               # Metrics are available at the /metrics endpoint.
   KC_HOSTNAME_BACKCHANNEL_DYNAMIC=false                 # Enables dynamic resolving of backchannel URLs, including hostname, scheme, port and context path.
   KC_HTTP_ENABLED=true                                  # Enable HTTP (since we are deploying the dev version of keycloak... for production this must be set to false)
```
4. **Deploy Keycloak:**
- Run the following command to start all services defined in the Docker Compose file:
```bash
   docker-compose up -d
```

5. **Initialize Keycloak**
- Follow the Instructions of [Setting up Keycloak](/README.md#setting-up-keycloak) in order to be able to run EWB.
- After that you can modify your keycloak however you like to access the UI. (At least a User that can login to the service and has access to all the components).

### Deploy EWB

1. **Navigate to `ewb` directory**

2.  **Edit the `.env` file:**
```txt
   DOCKER_REGISTRY=citesa/            # The Docker registry that is used to pull the EWB images
   MAINPATH=.                          # The base location for the EWB mounts

   WORKBENCH_TAG=1.0.2                # The Version of EWB
   PROFILE=test                       # The environment used for this deployment 
```
3. **Edit the `config-files/api/config/app.env` file:**
Set the following options with the appropriate from the Keycloak configuration.
```txt
  ...
   IDP_APIKEY_CLIENT_SECRET=<ewb_web_client_secret>
   KEYCLOAK_API_PASSWORD=<ewb_api_user_password>
   KEYCLOAK_API_CLIENT_SECRET=<ewb_web_client_secret>
  ...

```

4. **Edit the `config-files/api/config/keycloak-*.yml` file:**
Set the following options with the appropriate from the Keycloak configuration.
```txt
keycloak-resources:
  authorities:
    user:
      groupId: <user_group_keycloak_id>
    admin:
      groupId: <admin_group_keycloak_id>
```

5. **Edit the `config-files/webapp/config.json` file:**
Set the following options.
```txt
...
   "flow": "standard",
   "scope": "openid profile email address phone ewb_web"
...
```
Edit the following options for the EWB backend service:

```json
{
  	"recommendationTool": {
  	  	"fundingCallCorpus": "cordis",
  	  	  	"fundingCallModel": "cordis_tm_50tpc",
  	  	  	"getCallsSimilarToResearcherCollectionName": "cordis",
  	  	  	"getMetdataDocByIdCorpusCollection": "cordis",
  	  	  	"getThetasResearcherByIDForChart1": "papers",
  	  	  	"getThetasResearcherByIDForChart1Model": "cordis_tm_50tpc",
  	  	  	"getThetasResearcherByIDForChart2": "corpus",
  	  	  	"getThetasResearcherByIDForChart2Model": "cordis_tm_50tpc",
  	  	  	"totalResearchersDisplayed": 30,
  	  	  	"researchersDisplayedPerPage": 5,
  	  	  	"totalRGsDisplayed": 10,
  	  	  	"rgsDisplayedPerPage": 5,
  	  	  	"totalCallsDisplayed": 10,
  	  	  	"callsDisplayedPerPage": 5
    },
  	"knowledgeMap": {
  	  	  	"totalResearchersDisplayed": 30,
  	  	  	"researchersDisplayedPerPage": 5,
  	  	  	"totalRGsDisplayed": 10,
  	  	  	"rgsDisplayedPerPage": 5
  	}
}
```
Replace the version text to be displayed in the application:
```json
{
  	"version": "v1.0.4"
}
```

6. **Start the services:**
- Run the following command to start all services defined in the Docker Compose file:
```bash
   docker-compose up -d
```

   This command will start both the backend and frontend services as defined in the Docker Compose configurations. Ensure that Docker is running on your machine before executing this command.

7. **Access URLs:**
- EWB: http://host.docker.internal:45000/
- Keycloak: http://host.docker.internal:52000/

## License

This project is licensed under the European Union Public Licence (EUPL) v1.2. Please see the [LICENSE](/LICENSE.md) file for more details.

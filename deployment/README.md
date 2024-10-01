## Deployment
- This deployment resolves on host.docker.internal (ports 45000 and 52000), so if you cannot resolve that in your browser after deploying it, try assigning host.docker.internal to 127.0.0.1 in your hosts file.

### Using Docker Compose

First you want to deploy the database, so navigate to `deployment/postgres`.

1. **Edit the `.env` file:**
```txt
   POSTGRES_TAG=16-alpine              # The version of PostgreSQL
   DBPORT=45432                        # External port in which the postgres server will be deployed
   DBUSER=postgres                     # The name of the postgres admin
   DBPASS=admin                        # The password of the postgres admin

   MAINPATH=.                          # The base location for the PostgreSQL mounts

   DOCKER_USER=                        # The UID for the user that will deploy the container (cat /etc/passwd)
   DOCKER_GROUP=                       # The GID for the user that will deploy the container (cat /etc/passwd)
```
2. **Deploy PostgreSQL:**
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

3. **Initialize databases:**
   - Login as admin in the database
   - Run the `/db/00.00.01.seed.sql` script on the ewb database to initialize it

Next, you need to deploy keycloak, so navigate to `deployment/keycloak`.

4. **Edit the `.env` file:**
```txt
   MAINPATH=.                          # The base location for the Keycloak mounts
   KEYCLOAK_TAG=25.0.6                 # The version of Keycloak

   DOCKER_USER=                        # The UID for the user that will deploy the container (cat /etc/passwd)
   DOCKER_GROUP=                       # The GID for the user that will deploy the container (cat /etc/passwd)
```

5. **Edit the `keycloak.env` file:**
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
6. **Deploy Keycloak:**
- Run the following command to start all services defined in the Docker Compose file:
```bash
   docker-compose up -d
```

7. **Initialize Keycloak**
- Follow the Instructions of [Setting up Keycloak](/README.md#setting-up-keycloak) in order to be able to run EWB.
- After that you can modify your keycloak however you like to access the UI. (At least a User that can login to the service and has access to all the components).

Finally, you need to deploy EWB, so navigate to `deployment/ewb`.

8.  **Edit the `.env` file:**
```txt
   DOCKER_REGISTRY=citesa/            # The Docker registry that is used to pull the EWB images
   MAINPATH=.                          # The base location for the EWB mounts

   WORKBENCH_TAG=1.0.0                # The Version of EWB
   PROFILE=test                       # The environment used for this deployment 
```

9. **Start the services:**
- Run the following command to start all services defined in the Docker Compose file:
```bash
   docker-compose up -d
```

   This command will start both the backend and frontend services as defined in the Docker Compose configurations. Ensure that Docker is running on your machine before executing this command.

10. **Access URLs:**
- EWB: http://host.docker.internal:45000/
- Keycloak: http://host.docker.internal:52000/

## License

This project is licensed under the European Union Public Licence (EUPL) v1.2. Please see the [LICENSE](/LICENSE.md) file for more details.

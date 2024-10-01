
# Evaluation Workbench

Welcome to the Evaluation Workbench repository! This project consists of a comprehensive tool designed to assist officials at funding agencies in the evaluation of proposals for funding in programs supporting science, technology and innovation activities. Streamlining and standardizing the evaluation process, it not only enhances efficiency but also ensures a user-friendly experience, even for non-experts. This tool offers a variety of features such as document topic analysis, similarity search, and automatic classification.

## Project Components

- **Backend:** Written in Java using the Spring Boot framework, designed to handle all business logic and data interactions securely and efficiently.
- **Frontend:** Developed in TypeScript utilizing the Angular framework, offering a responsive and intuitive user interface.
- **Database:** Utilizes PostgreSQL for reliable data persistence and management.
- **Authentication:** Secured with Keycloak to ensure protected access and user management.

## Simple Deployment

Deployment instructions and Docker Compose files are provided to simplify the deployment process. All necessary files and instructions can be found in the [deployment](deployment/deployment.md) folder of this repository.

## Prerequisites

Before you start, make sure you have the following installed:
- Java JDK 21 or newer
- IntelliJ IDEA for backend setup
- Node.js and npm
- PostgreSQL
- Keycloak 25

## Getting Started

## Setting Up the Database

1. **Install PostgreSQL** and create a database named `ewb`.
2. **Initialize the database:**
   - Run the SQL script located at `db/00.00.01.seed.sql` to set up your initial database schema and data. You can do this via a command line or a database management tool:
   
```bash
psql -d ewb -U your_username -f db/00.00.01.seed.sql
```

   Replace `your_username` with your PostgreSQL username.

### Setting Up Keycloak

1. Install and run Keycloak.
2. Login with the admin user.
3. Create a new realm with the name `EWB`.
4. Inside the EWB realm, go to **Clients** and create a new client.
   - *Client Type*: `OpenID CONNECT`
   - *Client ID*: `ewb_frontend`
   - *Name*: `ewb_frontend`
   - *Always display in UI*: `Off`
   - *Client authentication*: `Off`
   - *Standard flow*: `On`
   - *Implicit flow*: `On`
   - *Direct access grants*: `On`
   - *Home URL*: `http://<APP_URL>:<APP_PORT>`
   - *Valid redirect URIs*: `http://<APP_URL>:<APP_PORT>/*`
   - *Valid post logout redirect URIs*: `+`
   - *Web origins*: `http://<APP_URL>:<APP_PORT>`
5. Click **Save**.
6. Configure the realm, client, and roles according to your authentication needs.

## Running the Backend

### Setup in IntelliJ IDEA

1. **Open the project in IntelliJ IDEA:**
   - Choose `File > Open` and navigate to the backend project directory.
   - Select the project folder and open it as a project.

2. **Configure the JDK:**
   - Go to `File > Project Structure > Project`.
   - Set the "Project SDK" to JDK 21, apply, and close the dialog.

3. **Configure Environment Variables:**
   - Navigate to `backend/evaluation-workbench-web/src/main/resources/config/`.
   - Open the `app.env` file and set the following properties:

```plaintext
DB_CONNECTION_STRING=jdbc:postgresql://localhost:5432/ewb
DB_USER=your_username
DB_PASSWORD=your_password
IDP_JWT_ISSUER_URI=https://your-keycloak-domain/realms/your_realm
EWB_TM_HOST=http://your-tm-api-host
EWB_CLASSIFICATION_HOST=http://your-classification-api-host
```

   Replace the placeholders with your actual database credentials, Keycloak issuer URI, and the URLs for the EWB's Topic Modeling and Classifier APIs.

   - Set the Spring profile to `dev`:
     - Go to `Run > Edit Configurations`.
     - In the configuration for your Spring Boot application, add `--spring.profiles.active=dev` to the "Program arguments" section.

4. **Run the application:**
   - Find the main application class with `@SpringBootApplication`.
   - Right-click on it and select `Run`.

### Running the Frontend

#### Configure Keycloak in Frontend

1. **Edit `src/assets/config.json`:**
   - Navigate to the `src/assets` directory within the frontend project.
   - Open the `config.json` file.
   - Update the Keycloak configuration section with the appropriate values for your setup. The configuration should look like this:

```json
{
  "idp_service": {
    "address": "http://your-keycloak-address",
    "realm": "YourRealm",
    "clientId": "YourClientID"
  }
}
```

   Replace `"address"`, `"realm"`, and `"clientId"` with the actual Keycloak address, realm name, and client ID.


#### Install Dependencies

Navigate to the frontend directory:

```bash
npm install
```

#### Serve the Application

Start the Angular development server:

```bash
ng serve
```

Visit `http://localhost:4200` in your browser to view the application.

## Docker Images

The Docker images for the Evaluation Workbench project are available on Docker Hub. You can pull the images using the following commands:

- For the backend API:
```bash
  docker pull citesa/ai4university-evaluation-workbench-api
```

- For the frontend Webapp:
```bash
  docker pull citesa/ai4university-evaluation-workbench-webapp
```


## License

This project is licensed under the European Union Public Licence (EUPL) v1.2. Please see the [LICENSE](LICENSE.md) file for more details.


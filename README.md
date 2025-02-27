## Camel-Spring-boot with Hashicorp Vault

In this sample you'll use the Hashicorp Vault Properties Source and run locally with Camel Spring Boot

## Setting up Hashicorp Vault instance

We are going to use the Hashicorp vault docker image for this purpose

```
docker run --cap-add=IPC_LOCK -e 'VAULT_DEV_ROOT_TOKEN_ID=myroot' hashicorp/vault
```

Run the following command to get the IP address

```
docker inspect -f '{{range.NetworkSettings.Networks}}{{.IPAddress}}{{end}}' <container_id>
```

## Setting up Database

This example uses a PostgreSQL database. 

```
docker run --name some-postgres -e POSTGRES_PASSWORD=password1234 -d postgres
```

Now we need to recover the IP Address of the container

```
docker inspect   -f '{{range.NetworkSettings.Networks}}{{.IPAddress}}{{end}}' some-postgres
```

You'll get an address and take note of it.

```
docker run -it postgres psql -h <postgres_ip_address> -U postgres
```

Now let's create the database

```
Password for user postgres: 
psql (17.4 (Debian 17.4-1.pgdg120+2))
Type "help" for help.

postgres=# CREATE DATABASE test;
```

We now need to create the table and some data

```
postgres-# \c test
You are now connected to database "test" as user "postgres".
```

Now we need to create the content and the SQL Table

```
test=# CREATE TABLE test (data TEXT PRIMARY KEY);
INSERT INTO test(data) VALUES ('hello'), ('world');
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO postgres;
```

Take note of username and host, while the password is 'password1234'

Now we need to create the secret payload. You should have your Hashicorp Vault instance container running:

We need to connect to the Vault instance container:

```
docker exec -it <container_id> -- /bin/sh
```

While you are inside the container command line create the secret for authenticating with the Database

```
/ # export VAULT_TOKEN=myroot
/ # vault kv put -address http://127.0.0.1:8200 secret/authsecdbref username="postgres" password="password1234" host="<postgres_ip>" 
====== Secret Path ======
secret/data/authsecdbref

======= Metadata =======
Key                Value
---                -----
created_time       2025-02-27T10:44:55.868382272Z
custom_metadata    <nil>
deletion_time      n/a
destroyed          false
version            1
```

Exit from the container command line.

This complete the Database setup in combination with the Hashicorp Vault instance secrets.

## Add the properties for Hashicorp Properties function

In the application.properties file add the following field:

```
camel.vault.hashicorp.host=<hashicorp_vault_ip>
camel.vault.hashicorp.port=8200
camel.vault.hashicorp.token=myroot
camel.vault.hashicorp.scheme=http
```

## Run

Run the following command

```
./mvnw clean compile spring-boot:run
```


```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/

 :: Spring Boot ::                (v3.3.6)

2025-02-27T12:00:17.097+01:00  INFO 38661 --- [           main] o.e.project.sqltolog.CamelApplication    : Starting CamelApplication using Java 17.0.8 with PID 38661 (/home/oscerd/workspace/redhat/camel-spring-boot-hashicorp-vault-kaoto/target/classes started by oscerd in /home/oscerd/workspace/redhat/camel-spring-boot-hashicorp-vault-kaoto)
2025-02-27T12:00:17.102+01:00  INFO 38661 --- [           main] o.e.project.sqltolog.CamelApplication    : No active profile set, falling back to 1 default profile: "default"
2025-02-27T12:00:17.930+01:00  INFO 38661 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat initialized with port 8080 (http)
2025-02-27T12:00:17.943+01:00  INFO 38661 --- [           main] o.apache.catalina.core.StandardService   : Starting service [Tomcat]
2025-02-27T12:00:17.943+01:00  INFO 38661 --- [           main] o.apache.catalina.core.StandardEngine    : Starting Servlet engine: [Apache Tomcat/10.1.33]
2025-02-27T12:00:17.989+01:00  INFO 38661 --- [           main] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring embedded WebApplicationContext
2025-02-27T12:00:17.990+01:00  INFO 38661 --- [           main] w.s.c.ServletWebServerApplicationContext : Root WebApplicationContext: initialization completed in 851 ms
2025-02-27T12:00:18.604+01:00  INFO 38661 --- [           main] o.s.b.a.e.web.EndpointLinksResolver      : Exposing 1 endpoint beneath base path '/actuator'
2025-02-27T12:00:18.667+01:00  INFO 38661 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port 8080 (http) with context path '/'
2025-02-27T12:00:19.031+01:00  INFO 38661 --- [           main] o.a.c.impl.engine.AbstractCamelContext   : Apache Camel 4.8.0.redhat-00017 (camel-1) is starting
2025-02-27T12:00:19.178+01:00  INFO 38661 --- [           main] o.a.c.impl.engine.AbstractCamelContext   : Routes startup (total:1 started:1 kamelets:1)
2025-02-27T12:00:19.178+01:00  INFO 38661 --- [           main] o.a.c.impl.engine.AbstractCamelContext   :     Started route1 (kamelet://postgresql-source)
2025-02-27T12:00:19.178+01:00  INFO 38661 --- [           main] o.a.c.impl.engine.AbstractCamelContext   : Apache Camel 4.8.0.redhat-00017 (camel-1) started in 145ms (build:0ms init:0ms start:145ms)
2025-02-27T12:00:19.180+01:00  INFO 38661 --- [           main] o.e.project.sqltolog.CamelApplication    : Started CamelApplication in 2.32 seconds (process running for 2.53)
2025-02-27T12:00:20.269+01:00  INFO 38661 --- [%20from%20test;] route1                                   : {"data":"hello"}
2025-02-27T12:00:20.271+01:00  INFO 38661 --- [%20from%20test;] route1                                   : {"data":"world"}
2025-02-27T12:00:25.277+01:00  INFO 38661 --- [%20from%20test;] route1                                   : {"data":"hello"}
2025-02-27T12:00:25.278+01:00  INFO 38661 --- [%20from%20test;] route1                                   : {"data":"world"}
2025-02-27T12:00:30.284+01:00  INFO 38661 --- [%20from%20test;] route1                                   : {"data":"hello"}
2025-02-27T12:00:30.286+01:00  INFO 38661 --- [%20from%20test;] route1                                   : {"data":"world"}
2025-02-27T12:00:35.292+01:00  INFO 38661 --- [%20from%20test;] route1                                   : {"data":"hello"}
```



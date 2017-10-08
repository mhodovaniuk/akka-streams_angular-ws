## Access
There are 3 preregistered users: admin with password admin, user1 with password user1 and user2 with password user2. See V2__data.sql migration file. 
## Server
### How To Run
In order to run server locally please type "sbt run" command from the server directory.
Checked with the latest SBT
````
    [mhodovaniuk@zenbook server]$ sbt sbtVersion
    [info] Loading global plugins from /home/mhodovaniuk/.sbt/0.13/plugins
    [info] Loading project definition from /home/mhodovaniuk/WorkSpace/Scala/eg_scala_assignment/server/project
    [info] Set current project to ta-server (in build file:/home/mhodovaniuk/WorkSpace/Scala/eg_scala_assignment/server/)
    [info] 0.13.13
    [mhodovaniuk@zenbook server]$ sbt run
    22:55:28.843 [run-main-0] INFO  o.f.c.internal.util.VersionPrinter - Flyway 4.1.2 by Boxfuse
    22:55:29.066 [run-main-0] INFO  o.f.c.i.dbsupport.DbSupportFactory - Database: jdbc:h2:mem:eg_test_assignment (H2 1.4)
    22:55:29.141 [run-main-0] INFO  o.f.core.internal.command.DbValidate - Successfully validated 2 migrations (execution time 00:00.008s)
    22:55:29.149 [run-main-0] INFO  o.f.c.i.m.MetaDataTableImpl - Creating Metadata table: "PUBLIC"."schema_version"
    22:55:29.172 [run-main-0] INFO  o.f.core.internal.command.DbMigrate - Current version of schema "PUBLIC": << Empty Schema >>
    22:55:29.173 [run-main-0] INFO  o.f.core.internal.command.DbMigrate - Migrating schema "PUBLIC" to version 1 - init
    22:55:29.189 [run-main-0] INFO  o.f.core.internal.command.DbMigrate - Migrating schema "PUBLIC" to version 2 - data
    22:55:29.197 [run-main-0] INFO  o.f.core.internal.command.DbMigrate - Successfully applied 2 migrations to schema "PUBLIC" (execution time 00:00.049s).
    H2 console is available at http://localhost:8082
    Server online at http://localhost:8080/
    Press RETURN to stop...
````
### Tech Stack
* Akka Http
* Slick
* Guice
* FlywayDB

## Client
### How To Run
In order to run client locally please type "ng serve --host 0.0.0.0 --port 4201" command from the client directory.
Node, npm and angular-cli are required.
````
    [mhodovaniuk@zenbook client]$ node -v
    v7.8.0
    [mhodovaniuk@zenbook client]$ npm -v
    4.2.0
    [mhodovaniuk@zenbook client]$ ng -v
        _                      _                 ____ _     ___
       / \   _ __   __ _ _   _| | __ _ _ __     / ___| |   |_ _|
      / △ \ | '_ \ / _` | | | | |/ _` | '__|   | |   | |    | |
     / ___ \| | | | (_| | |_| | | (_| | |      | |___| |___ | |
    /_/   \_\_| |_|\__, |\__,_|_|\__,_|_|       \____|_____|___|
                   |___/
    @angular/cli: 1.0.0
    node: 7.8.0
    os: linux x64
    @angular/common: 4.0.2
    @angular/compiler: 4.0.2
    @angular/core: 4.0.2
    @angular/forms: 4.0.2
    @angular/http: 4.0.2
    @angular/platform-browser: 4.0.2
    @angular/platform-browser-dynamic: 4.0.2
    @angular/router: 4.0.2
    @angular/cli: 1.0.0
    @angular/compiler-cli: 4.0.2
    [mhodovaniuk@zenbook client]$ ng serve --host 0.0.0.0 --port 4201
    ** NG Live Development Server is running on http://0.0.0.0:4201 **
````
### Tech Stack
* Angular 4
* Bootstrap
## REG
### Docker Support
#### Server
Build Docker Image:
````
    sbt docker:publishLocal
````
Run Docker Image
````
    docker run -td -p 8080:8080 akkas-treams_angular-ws_server:0.1.0
````
#### Client
Build Docker Image
````
    npm run docker:publishLocal
````
Run Docker Image
````
    docker run -td -p 80:80 akkas-treams_angular-ws_client:0.1.0
````
#### Deploy
Copy images via ssh:
````
    docker save akkas-treams_angular-ws_server:0.1.0 | bzip2 | pv | ssh $USER@$IP 'bunzip2 | docker load'
    docker save akkas-treams_angular-ws_client:0.1.0 | bzip2 | pv | ssh $USER@$IP 'bunzip2 | docker load'
````
# Armadillo suite

[![CircleCI](https://circleci.com/gh/molgenis/molgenis-service-armadillo.svg?style=shield)](https://circleci.com/gh/molgenis/molgenis-service-armadillo)
[![Build Status](https://dev.azure.com/molgenis/molgenis-emx2/_apis/build/status/molgenis.molgenis-service-armadillo?branchName=master)](https://dev.azure.com/molgenis/molgenis-service-armadillo/_build/latest?definitionId=1&branchName=master)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=org.molgenis%3Aarmadillo-service&metric=alert_status)](https://sonarcloud.io/dashboard?id=org.molgenis%3Aarmadillo-service)

# What is Armadillo?

The Armadillo data portal can be used by data stewards to share datasets on a server. Researchers can then analyse these datasets and datasets shared on other
servers using the DataSHIELD analysis tools. Researchers will only be able to access aggregate information and cannot see individual rows.

## Analyse data

The Armadillo uses the [DataSHIELD](https://datashield.org) platform to facilitate analysis. It contains a variety of statistical packages applicable to
different research areas. There are DataSHIELD packages for [standard statistical analysis](https://github.com/datashield/dsBaseClient)
, [exposome studies](https://github.com/isglobal-brge/dsExposomeClient)
, [survival studies](https://github.com/neelsoumya/dsSurvivalClient)
, [microbiome studies](https://github.com/StuartWheater/dsMicrobiomeClient)
and [analysis tools for studies that are using large genetic datasets](https://github.com/isglobal-brge/dsomicsclient)
. These packages can all be installed in the Armadillo suite.

How does it work? A researcher connects from an [R client](https://molgenis.github.io/molgenis-r-datashield) to one or multiple Armadillo servers. The data is
loaded into an R session on the Armadillo server specifically created for the researcher. Analysis requests are sent to the R session on each Armadillo server.
There the analysis is performed and aggregated results are sent back to the client.

![DataSHIELD overview](https://raw.githubusercontent.com/molgenis/molgenis-service-armadillo/master/doc/img/overview-datashield.png)

## Share data

Data stewards can use the Armadillo web user interface or [MolgenisArmadillo R client](https://molgenis.github.io/molgenis-r-armadillo)
to manage their data on the Armadillo file server. Data is stored in parquet format that supports fast selections of the columns (variables)
you need for analysis. Data stewards can manage the uploaded data in the web browser. The data can be stored encrypted on the Armadillo file server. When using
the web user interface you must first convert your data into parquet. (CSV uploads will be supported in the near future).

## Access data

Everybody logs in via single sign on using an OIDC central authentication server such as KeyCloack or Fusion auth that federates to authentication systems of
connected institutions, ideally using a federated AAI such as LifeScience AAI.

# Getting started

## Prerequisites

Armadillo 3 can be installed on any flavor of linux OS or modern Unix-based Mac OS. To run armadillo 3 you need the following dependencies.

* Java 17 JRE or JDK
* Docker (for profiles)

To spin up your own server on a laptop, you first need
the [application.yml](https://raw.githubusercontent.com/molgenis/molgenis-service-armadillo/master/scripts/install/conf/application-local.yml)
and edit for your needs. Then you can run:

```
export SPRING_CONFIG_LOCATION=<location to>/application-local.yml

java -jar armadillo-3.x.x.jar

```

## Systemd Service

Armadillo 3 is tested on latest Ubuntu-LTS based servers. To run armadillo 3 as service please follow this
guide: https://github.com/molgenis/molgenis-service-armadillo/blob/master/scripts/install/README.md

## Docker images

Armadillo 3 docker images are available

- release at https://hub.docker.com/r/molgenis/molgenis-armadillo
- snapshot builds from pull requests at https://hub.docker.com/r/molgenis/molgenis-armadillo-snapshot

For example, you can use docker as follows

```
mkdir data
docker run molgenis/molgenis-armadillo-snapshot \
-mount type=bind,source="$(pwd)"/data,target=/data
```

## armadillo 2

For armadillo 2.x you can follow instructions at

* for testing we use docker compose at https://github.com/molgenis/molgenis-service-armadillo/tree/armadillo-service-2.2.3
* for production we are using Ansible at https://galaxy.ansible.com/molgenis/armadillo

## What to do next

You can explore the User interface endpoints at `localhost:8080/ui`

Here you will find user interfaces for:

* defining projects and their data
* defining users and their project authorizations
* defining and managing datashield profiles

You can also explore the API endpoints at `localhost:8080/swagger-ui/index.html`

Finally, you can download the R client.

Of course the next step would be to use a DataSHIELD client to connect to Armadillo for analysis.

# Development

## Develop from commandline

To build run following command in the github root:

```./gradlew build```

To execute in 'dev' run following command in the github root:

```./gradlew run```

To create the binary and run that

```
./gradlew shadowJar
export SPRING_CONFIG_LOCATION=armadillo/src/main/resources/application.yml
java -jar build/libs/molgenis-armadillo-[version].jar  
```

## Setting up development tools

This repository uses `pre-commit` to manage commit hooks. An installation guide can be found
[here](https://pre-commit.com/index.html#1-install-pre-commit). To install the hooks, run `pre-commit install` once in the root folder of this repository. Now
your code will be automatically formatted whenever you commit.

### Storage

For local storage, you don't need to do anything. Data is automatically stored in the `data/` folder in this repository. You can choose another location
in `application.yml` by changing the `storage.root-dir`
setting.

If you want to use MinIO as storage (including the test data), do the following:

1. Start the container with `docker-compose --profile minio up`
2. In your browser, go to `http://localhost:9090`
3. Log in with _molgenis_ / _molgenis_
4. Add a bucket `shared-lifecyle`
5. Copy the folders in `data/shared-lifecycle` in this repository to the bucket
6. In `application.yml`, uncomment the `minio` section.
7. Now Armadillo will automatically connect to MinIO at startup.

> **_Note_**: When you run Armadillo locally for the first time, the `lifecycle` project has not been
> added to the system metadata yet. To add it automatically, see [Application properties](#application-properties).
> Or you can add it manually:
> - Go to the Swagger UI (`http://localhost:8080/swagger-ui/index.html`)
> - Go to the `PUT /access/projects` endpoint
> - Add the project `lifecycle`
>
> Now you're all set!

### Application properties

You can configure the application in `application.yml`. During development however, it is more convenient to override these settings in a local .yml file that
you do not commit to git. Here's how to set that up:

- Next to `application.yml`, create a file `application-local.yml` (this file is ignored by git)
- Give it the following content:

```
armadillo:
  oidc-permission-enabled: false
  docker-management-enabled: true
  oidc-admin-user: <your OIDC email>

spring:
  security:
    oauth2:
      client:
        registration:
          molgenis:
            client-id: <OIDC client ID>
            client-secret: <OIDC client secret>
```

> Note: If can't configure an oauth2 client for any reason, just remove the `spring` section.

- Now, in the Run Configuration for the DatashieldServiceApplication, add the following program argument:

```--spring.config.additional-location=file:armadillo/src/main/resources/application-local.yml```

Now the lifecycle test project (including its data) will work out of the box, and you will be able to log in with your OIDC account immediately.

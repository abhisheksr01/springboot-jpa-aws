# sample-app-with-db-connection

This is a simple Spring boot app to store and retrieve user data from a postgres sql database.

```shell
docker run --name postgres -e POSTGRES_PASSWORD=secret -d -p 5432:5432 postgres
```
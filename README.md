# Dropwizard JDBI Testing

## Overview

*Dropwizard JDBI Testing* is a library for simplyfing of integration
testing of [*JDBI*](http://jdbi.org/) data access objects in
[*Dropwizard*](http://dropwizard.io/) applications.

## What it does

The library does the following:

* Starts in-memory *[HSQLDB](http://hsqldb.org/)* database;
* Creates a DBI instance with the same configuration as in 
Dropwizard's `DBIFactory` (support of Guava `Optional`, `ImmutableList`,
`ImmutableSet`, JodaTime `DateTime`, logging of a SQL Object name);
* Logs SQL quieres to *Logback* with *INFO* level;
* Optionally migrates a user-defined sql schema; 
* The database and the DBI instance are shared between the tests, so they are
performed quickly;
* Provides a *JUnit* runner for running DBI-related tests;
* Supports injecting a DBI DAO or a SQL object to the current test
by annotating a tested instance;
* Supports injecting a `DBI` instance or an `Handle` instance to the 
current test for performing SQL requests against the database;
* Sweeps data from the database and reset sequences after every test.
All data changes performed in tests are discarded (but the schema
remains), so the database is in the clean state before every test.
It affords to tests to be independent and don't impact to each other.
* Supports executing of an arbitrary SQL script before every test
(or set of tests) by specifying an annotation on a test method or
test class.

## Getting started

### Add a Maven dependency


```xml
<dependency>
    <groupId>com.github.arteam</groupId>
    <artifactId>dropwizard-jdbi-testing</artifactId>
    <version>0.1-rc1</version>
</dependency>
```

### Define a simple SQL Object to test


````java
public interface PlayerDao {

    @GetGeneratedKeys
    @SqlUpdate("insert into players(first_name, last_name, birth_date, weight, height)"
            + " values (:first_name, :last_name, :birth_date, :weight, :height)")
    Long createPlayer(@Bind("first_name") String firstName,
                      @Bind("last_name") String lastName,
                      @Bind("birth_date") Date birthDate,
                      @Bind("height") int height, @Bind("weight") int weight);

    @SqlQuery("select last_name from players order by last_name")
    List<String> getPlayerLastNames();

    @SqlQuery("select count(*) from players where year(birth_date) = :year")
    int getAmountPlayersBornInYear(@Bind("year") int year);

    @SqlQuery("select * from players where first_name=:first_name and " +
              "last_name=:last_name")
    @SingleValueResult
    Optional<Player> findPlayer(@Bind("first_name") String firstName,
                                @Bind("last_name") String lastName);
}
````

### Create a test resources directory

Create a test resource directory. Say, on a path '*src/test/resources*'.

Set it in Maven as a test resources directory in the *build* section:

````xml
<build>
    <testResources>
        <testResource>
           <directory>src/test/resources</directory>
        </testResource>
    </testResources>
</build>
````

### To be continued...
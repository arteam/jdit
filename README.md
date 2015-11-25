# JDIT [![Build Status](https://travis-ci.org/arteam/jdit.svg?branch=master)](https://travis-ci.org/arteam/jdit)

## Overview

*JDIT* is a library for simplifying of integration
testing of [*JDBI*](http://jdbi.org/) data access objects in
[*Dropwizard*](http://dropwizard.io/) applications.

## What it does

The library does the following things:

* Starts in-memory *[HSQLDB](http://hsqldb.org/)* database;
* Creates a DBI instance with the same configuration as in 
Dropwizard's `DBIFactory` (support of Guava's `Optional`, `ImmutableList`,
`ImmutableSet`, JodaTime's `DateTime`, logging of a SQL Object name);
* Logs SQL queries to *Logback* with *INFO* level;
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

### Add Maven dependency


```xml
<dependency>
    <groupId>com.github.arteam</groupId>
    <artifactId>jdit</artifactId>
    <version>0.3</version>
    <scope>test</scope>
</dependency>
```

### Create a test resources directory

You need to create a test resource directory to host resources.
Let it be, say, on the path '*src/test/resources*'.

Don't forget to set it in Maven as a test resources directory in
the *build* section:

````xml
<build>
    <testResources>
        <testResource>
           <directory>src/test/resources</directory>
        </testResource>
    </testResources>
</build>
````

### Define a database schema

Add a file with a database schema to your test resources directory.
By default it should have name *schema.sql*.

````sql
create table players(
    id  identity,
    first_name varchar(128) not null,
    last_name varchar(128) not null,
    birth_date date not null,
    weight int not null,
    height int not null
);
````

### Write a test

````java
@RunWith(DBIRunner.class)
public class PlayerDaoTest {

    @TestedSqlObject
    PlayerDao playerDao;

    @DBIHandle
    Handle handle;

    @Test
    public void testCreatePlayer() {
        Long playerId = playerDao.createPlayer("Vladimir", "Tarasenko", date("1991-12-13"), 184, 90);
        List<Map<String,Object>> rows = handle.select("select * from players where id=?", playerId);
        assertFalse(rows.isEmpty());

        Map<String, Object> row = rows.get(0);
        assertEquals(0, row.get("id"));
        assertEquals("Vladimir", row.get("first_name"));
        assertEquals("Tarasenko", row.get("last_name"));
        assertEquals(date("1991-12-13"), row.get("birth_date"));
        assertEquals(184, row.get("height"));
        assertEquals(90, row.get("weight"));
    }

    private static Date date(String textDate) {
        return ISODateTimeFormat.date().parseDateTime(textDate).toDate();
    }
}
````

You should see  something like this in output:

````
23:57:30.091 [main] INFO  DBI - Handle [org.skife.jdbi.v2.BasicHandle@18cc8e9] obtained in 783 millis
23:57:30.157 [main] INFO  DBI - batch:[[create table players(     id  identity,     first_name varchar(128) not null,     last_name varchar(128) not null,     birth_date date not null,     weight int not null,     height int not null )]] took 3 millis
23:57:30.158 [main] INFO  DBI - Handle [org.skife.jdbi.v2.BasicHandle@18cc8e9] released
23:57:30.159 [main] INFO  DBI - Handle [org.skife.jdbi.v2.BasicHandle@3dacfa] obtained in 0 millis
23:57:30.639 [main] INFO  DBI - statement:[/* PlayerDao.createPlayer */ insert into players(first_name, last_name, birth_date, weight, height) values (?, ?, ?, ?, ?)] took 0 millis
23:57:30.664 [main] INFO  DBI - statement:[select * from players where id=?] took 0 millis
23:57:30.676 [main] INFO  DBI - statement:[TRUNCATE SCHEMA public RESTART IDENTITY AND COMMIT] took 0 millis
23:57:30.679 [main] INFO  DBI - Handle [org.skife.jdbi.v2.BasicHandle@3dacfa] released
````

Things to notice:
* Annotation `@RunWith` is crucial.  It makes the test aware of
a DBI context. Without it nothing will work.
* Annotation `@TestedSqlObject` is used for marking a tested SQL object.
* Annotation `@DBIHandle` is used for obtaining a reference to a handle
to the active database for performing queries.
* During a first invocation the schema has been migrated to the database.
It happens only once for all tests.
* As you see from the logs, data was swept from the database after
the completion of the test. But the schema wasn't removed.

### Load data before a test

Write a SQL DML script that populates the DB with needed data for testing.

Give it a name, say, *playerDao/players.sql* and place it into the test resources
directory.

````sql
insert into players(first_name, last_name, birth_date, weight, height)
values ('Vladimir','Tarasenko', '1991-12-13', 99, 184);
insert into players(first_name, last_name, birth_date, weight, height)
values ('Tyler','Seguin', '1992-01-30', 88, 185);
insert into players(first_name, last_name, birth_date, weight, height)
values ('Ryan','Ellis', '1991-01-03', 79, 176);
insert into players(first_name, last_name, birth_date, weight, height)
values ('John','Tavares', '1990-09-20', 93, 185);
````

Load this script before the test execution.

````java
@DataSet("playerDao/players.sql")
@Test
public void testGetPlayerListNames(){
    List<String> playerLastNames = playerDao.getPlayerLastNames();
    assertEquals(playerLastNames, ImmutableList.of("Ellis", "Seguin", "Tarasenko", "Tavares"));
}
````

Annotation `@DataSet` is used for marking a script that should be loaded
before a test.

If you find that you reuse the same data set for different tests, consider
to place this annotation on a class level.

````java
@RunWith(DBIRunner.class)
@DataSet("playerDao/players.sql")
public class PlayerDaoTest {
````

In this mode a script will be loaded for every method in the test.
Nevertheless, this script can be overridden by a method level annotation.

## Configuration

*JDIT* reads a configuration file of the following format:

````properties
db.url=jdbc:hsqldb:mem:jdbi-testing
db.username=sa
db.password=

schema.migration.enabled=true
schema.migration.location=schema.sql

dbi.factory=com.github.arteam.jdit.DropwizardDBIFactory
````

* _db.url_ - Database URL;
* _db.username_ - Database username;
* _db.password_ - Database password;
* _schema.migration.enabled_ - Whether schema migration is enabled;
* _schema.migration.location_ - Location of the database schema in
resources;  If it's a directory, then all .sql files in the directory
are processed as the schema;
* _dbi.factory_ - Implementation of a factory for creating DBI instances.

If you need to override this configuration, you should place the
*jdit.properties* file in your test resources directory with needed
changes.

For example, for overriding the schema location you should create a file
with following content:

````properties
schema.migration.location=db/migration
````

If you need to specify properties for a specific test you can do it
with the `@JditProperties` annotation on the the test class level.

## Examples

More examples available in a separate [repository](https://github.com/arteam/jdit-examples).

## Dependencies

* [JDBI](http://jdbi.org/) 2.80
* [HSQLDB](http://hsqldb.org/) 2.3.3
* [Dropwizard](http://dropwizard.io/) 0.9.1
* [JUnit](http://junit.org/) 4.12

## Compatability

Version 0.1 is compatible with [Dropwizard](http://dropwizard.io/) 0.7.1 and 0.8.0

Version 0.2 is compatible with [Dropwizard](http://dropwizard.io/) 0.8.1

Version 0.3 is compatible with [Dropwizard](http://dropwizard.io/) 0.9.0

## Availability

Artifact are available in [JCenter] (https://bintray.com/bintray/jcenter) repository

````xml
<repositories>
        <repository>
            <id>jcenter</id>
            <name>bintray</name>
            <url>http://jcenter.bintray.com</url>
        </repository>
</repositories>
````

# JDIT

## Overview

*JDIT* is a library for simplyfing of integration
testing of [*JDBI*](http://jdbi.org/) data access objects in
[*Dropwizard*](http://dropwizard.io/) applications.

## What it does

The library does the following things:

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

### Add a Maven dependency


```xml
<dependency>
    <groupId>com.github.arteam</groupId>
    <artifactId>jdit</artifactId>
    <version>0.1-rc3</version>
</dependency>
```

### Create a test resources directory

You need to create a test resource directory to host resource.
Let it be, say, on path '*src/test/resources*'.

Don't forget to set it in Maven as a test resources directory in
*build* section:

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
By default it's should have name *schema.sql*

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

You should see output something like that:

````
23:57:30.091 [main] INFO  org.skife.jdbi.v2.DBI - Handle [org.skife.jdbi.v2.BasicHandle@18cc8e9] obtained in 783 millis
23:57:30.157 [main] INFO  org.skife.jdbi.v2.DBI - batch:[[create table players(     id  identity,     first_name varchar(128) not null,     last_name varchar(128) not null,     birth_date date not null,     weight int not null,     height int not null )]] took 3 millis
23:57:30.158 [main] INFO  org.skife.jdbi.v2.DBI - Handle [org.skife.jdbi.v2.BasicHandle@18cc8e9] released
23:57:30.159 [main] INFO  org.skife.jdbi.v2.DBI - Handle [org.skife.jdbi.v2.BasicHandle@3dacfa] obtained in 0 millis
23:57:30.639 [main] INFO  org.skife.jdbi.v2.DBI - statement:[/* PlayerDao.createPlayer */ insert into players(first_name, last_name, birth_date, weight, height) values (?, ?, ?, ?, ?)] took 0 millis
23:57:30.664 [main] INFO  org.skife.jdbi.v2.DBI - statement:[select * from players where id=?] took 0 millis
23:57:30.676 [main] INFO  org.skife.jdbi.v2.DBI - statement:[TRUNCATE SCHEMA public RESTART IDENTITY AND COMMIT] took 0 millis
23:57:30.679 [main] INFO  org.skife.jdbi.v2.DBI - Handle [org.skife.jdbi.v2.BasicHandle@3dacfa] released
````

Things to notice:
* Annotation `@RunWith` is crucial.  It makes the test aware of
a DBI context. Without it nothing will work.
* Annotation `@TestedSqlObject` is used for marking a tested SQL object
* Annotation `@DBIHandle` is used for obtaining a reference to a handle
to the active database for performing queries
* During a first invocation a schema has been migrated to the database.
It happens only once for all tests.
* As you see from the logs, data has been swept from the database after
completion of the test. But the schema remained.

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

JDIT reads a configuration file the following format:

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
* _db.password - Database password;
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


## Dependencies

* [JDBI](http://jdbi.org/) 2.59
* [HSQLDB](http://hsqldb.org/) 2.3.2
* [Dropwizard](http://dropwizard.io/) 0.8-rc2
* [JUnit](http://junit.org/) 4.12
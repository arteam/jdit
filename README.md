## metrics-jdbi3
A Dropwizard module for instrumenting JDBI3

# Description
This module provides a way to instrument JDBI3 applications and report metrics to a ``MetricRegistry`` from 
[dropwizard-metrics](http://metrics.dropwizard.io/3.1.0/).

The main abstraction is the `InstrumentedTimingCollector` class that extends JDBI's `TimingCollector` and accepts
`MetricRegistry`. It registers a `Timer` with a name provided by the user specified `StatementNameStrategy`. 
The user can choose several strategies:

* `NaiveNameStrategy` uses the name of the SQL query (`select name from users`).
* `BasicSqlNameStrategy` uses the name of the SQL object under which it was executed (`com.acme.UserDao.getUsers`)
* `ContextNameStrategy` uses the name name of the context group and statement name.
* `SmartNameStrategy` uses the `ContextNameStrategy` strategy, if not applicable then `BasicSqlNameStrategy`, if it's
not, then uses the constant `sql.raw`.

# Use

```java
Jdbi jdbi = Jdbi.create(dataSource);
jdbi.setTimingCollector(new InstrumentedTimingCollector(new MetricRegistry(), new SmartNameStrategy()));
```

# Maven

```xml
<dependency>
     <groupId>com.github.arteam</groupId>
     <artifactId>metrics-jdbi3</artifactId>
     <version>0.2</version>
</dependency>
```

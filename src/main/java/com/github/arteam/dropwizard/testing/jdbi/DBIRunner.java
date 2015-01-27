package com.github.arteam.dropwizard.testing.jdbi;

import com.github.arteam.dropwizard.testing.jdbi.annotations.DataSet;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

/**
 * Date: 1/22/15
 * Time: 8:55 PM
 *
 * @author Artem Prigoda
 */
public class DBIRunner extends BlockJUnit4ClassRunner {

    private static SchemaMigration schemaMigration = new SchemaMigration();

    private DBI dbi;
    private Handle handle;

    private TestObjectsInjector injector;

    public DBIRunner(Class<?> klass) throws InitializationError {
        super(klass);
    }

    @Override
    protected Object createTest() throws Exception {
        Object test = super.createTest();
        injector.injectTestedObjects(test);
        return test;
    }

    @Override
    protected Statement classBlock(RunNotifier notifier) {
        final Statement statement = super.classBlock(notifier);
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                dbi = DBIContext.createDBI();
                handle = dbi.open();

                injector = new TestObjectsInjector(dbi, handle);
                try {
                    schemaMigration.migrateSchema(handle);
                    statement.evaluate();
                } finally {
                    handle.close();
                }
            }
        };
    }

    @Override
    protected Statement methodBlock(final FrameworkMethod method) {
        final Statement statement = super.methodBlock(method);
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                DataSet dataSet = method.getAnnotation(DataSet.class);
                if (dataSet != null) {
                    String scriptLocation = dataSet.value();
                    schemaMigration.executeScript(handle, scriptLocation);
                }
                try {
                    statement.evaluate();
                } finally {
                    handle.execute("TRUNCATE SCHEMA public AND COMMIT");
                }
            }
        };
    }
}

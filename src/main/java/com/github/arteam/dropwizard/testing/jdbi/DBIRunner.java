package com.github.arteam.dropwizard.testing.jdbi;

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

    private DataMigration dataMigration;
    private TestObjectsInjector injector;
    private DataSetInjector dataSetInjector;

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
                DBI dbi = DBIContext.getDBI();
                Handle handle = dbi.open();

                injector = new TestObjectsInjector(dbi, handle);
                dataMigration = new DataMigration(handle);
                dataSetInjector = new DataSetInjector(dataMigration);
                try {
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
                try {
                    dataSetInjector.injectData(method.getMethod());
                    statement.evaluate();
                } finally {
                    dataMigration.sweepData();
                }
            }
        };
    }
}

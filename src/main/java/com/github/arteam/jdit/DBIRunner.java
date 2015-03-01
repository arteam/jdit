package com.github.arteam.jdit;

import com.github.arteam.jdit.annotations.JditProperties;
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
 * <p/>
 * A tests runner that:
 * <ul>
 * <li>Injects DBI related tested instances to the tests.
 * <p/>
 * Supports {@link Handle}, {@link DBI}, SQLObject and DBI DAO.</li>
 * <li>Injects data to the DB from a script for a specific
 * method or a test</li>
 * <li>Sweeps data from the DB after every tests, so every
 * test starts with an empty schema</li>
 * </ul>
 *
 * @author Artem Prigoda
 */
public class DBIRunner extends BlockJUnit4ClassRunner {

    private DataMigration dataMigration;
    private TestObjectsInjector injector;
    private DataSetInjector dataSetInjector;
    private Class<?> klass;

    public DBIRunner(Class<?> klass) throws InitializationError {
        super(klass);
        this.klass = klass;
    }

    @Override
    protected Object createTest() throws Exception {
        Object test = super.createTest();
        // Now we can inject tested instances to the current test
        injector.injectTestedInstances(test);
        return test;
    }

    @Override
    protected Statement classBlock(RunNotifier notifier) {
        final Statement statement = super.classBlock(notifier);
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                // Open a new handle for every test
                // It affords to avoid creating a static state that makes tests more independent
                JditProperties jditProperties = klass.getAnnotation(JditProperties.class);
                DBI dbi = jditProperties != null ? DBIContextFactory.getDBI(jditProperties.value()) : DBIContextFactory.getDBI();
                try (Handle handle = dbi.open()) {
                    injector = new TestObjectsInjector(dbi, handle);
                    dataSetInjector = new DataSetInjector(dataMigration = new DataMigration(handle));
                    statement.evaluate();
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
                    // Sweep event if there is an error during injecting data
                    dataMigration.sweepData();
                }
            }
        };
    }
}

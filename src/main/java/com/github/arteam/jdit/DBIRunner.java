package com.github.arteam.jdit;

import com.github.arteam.jdit.annotations.JditProperties;
import com.github.arteam.jdit.maintenance.DatabaseMaintenance;
import com.github.arteam.jdit.maintenance.DatabaseMaintenanceFactory;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

/**
 * <p>
 * Tests runner which:
 * <ul>
 * <li>Injects DBI related tested instances to the tests.
 * <p>Supports {@link Handle}, {@link Jdbi}, SQLObject and DBI DAO.
 * </li>
 * <li>Injects data to the DB from a script for a specific
 * method or a test</li>
 * <li>Sweeps data from the DB after every tests, so every
 * test starts with an empty schema</li>
 * </ul>
 */
public class DBIRunner extends BlockJUnit4ClassRunner {

    private DatabaseMaintenance databaseMaintenance;
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
                // It affords to avoid creating a static state which makes tests more independent
                JditProperties jditProperties = klass.getAnnotation(JditProperties.class);
                Jdbi dbi = jditProperties != null ? DBIContextFactory.getDBI(jditProperties.value()) : DBIContextFactory.getDBI();
                try (Handle handle = dbi.open()) {
                    injector = new TestObjectsInjector(dbi, handle);
                    databaseMaintenance = DatabaseMaintenanceFactory.create(handle);
                    dataSetInjector = new DataSetInjector(new DataMigration(handle));
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
                    databaseMaintenance.sweepData();
                }
            }
        };
    }
}

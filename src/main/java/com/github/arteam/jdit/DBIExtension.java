package com.github.arteam.jdit;

import com.github.arteam.jdit.annotations.JditProperties;
import com.github.arteam.jdit.maintenance.DatabaseMaintenance;
import com.github.arteam.jdit.maintenance.DatabaseMaintenanceFactory;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;

/**
 * A JUnit5 test extension that:
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
public class DBIExtension implements TestInstancePostProcessor, BeforeAllCallback, BeforeEachCallback, AfterEachCallback, AfterAllCallback {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(DBIExtension.class);

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        // Open a new handle for every test
        // It allows us to avoid creating a static state which makes tests independent from each other
        Class<?> testClass = context.getRequiredTestClass();
        JditProperties jditProperties = testClass.getAnnotation(JditProperties.class);
        Jdbi dbi = jditProperties != null ? DBIContextFactory.getDBI(jditProperties.value()) : DBIContextFactory.getDBI();
        Handle handle = dbi.open();
        TestObjectsInjector injector = new TestObjectsInjector(dbi, handle);
        DatabaseMaintenance databaseMaintenance = DatabaseMaintenanceFactory.create(handle);
        DataSetInjector dataSetInjector = new DataSetInjector(new DataMigration(handle));
        context.getStore(NAMESPACE).put(testClass + ".handle", handle);
        context.getStore(NAMESPACE).put(testClass + ".injector", injector);
        context.getStore(NAMESPACE).put(testClass + ".databaseMaintenance", databaseMaintenance);
        context.getStore(NAMESPACE).put(testClass + ".dataSetInjector", dataSetInjector);
    }

    @Override
    public void postProcessTestInstance(Object testInstance, ExtensionContext context) throws Exception {
        TestObjectsInjector testObjectsInjector = (TestObjectsInjector) context.getStore(NAMESPACE).get(context.getRequiredTestClass() + ".injector");
        testObjectsInjector.injectTestedInstances(testInstance);
    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        DataSetInjector dataSetInjector = (DataSetInjector) context.getStore(NAMESPACE).get(context.getRequiredTestClass() + ".dataSetInjector");
        dataSetInjector.injectData(context.getRequiredTestMethod());
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        // Sweep event if there is an error during injecting data
        DatabaseMaintenance databaseMaintenance = (DatabaseMaintenance) context.getStore(NAMESPACE).get(context.getRequiredTestClass() + ".databaseMaintenance");
        databaseMaintenance.sweepData();
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        Class<?> testClass = context.getRequiredTestClass();
        Handle handle = (Handle) context.getStore(NAMESPACE).get(testClass + ".handle");
        handle.close();

        context.getStore(NAMESPACE).remove(testClass + ".handle");
        context.getStore(NAMESPACE).remove(testClass + ".injector");
        context.getStore(NAMESPACE).remove(testClass + ".databaseMaintenance");
        context.getStore(NAMESPACE).remove(testClass + ".dataSetInjector");
    }
}

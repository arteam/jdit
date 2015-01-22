package com.github.arteam.dropwizard.testing.jdbi;

import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

/**
 * Date: 1/22/15
 * Time: 8:55 PM
 *
 * @author Artem Prigoda
 */
public class DBIRunner extends BlockJUnit4ClassRunner {

    public DBIRunner(Class<?> klass) throws InitializationError {
        super(klass);
    }

    @Override
    protected Object createTest() throws Exception {
        Object test = super.createTest();
        // TODO inject DBI, Handle
        return test;
    }

    @Override
    protected Statement classBlock(RunNotifier notifier) {
        final Class<?> testClass = getTestClass().getJavaClass();
        final Statement statement = super.classBlock(notifier);
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                // TODO Create database, DBI and handle, migrate populate schema
                statement.evaluate();
                // TODO Cleanup handle
            }
        };
    }

    @Override
    protected Statement methodBlock(final FrameworkMethod method) {
        final Statement statement = super.methodBlock(method);
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                // TODO Load data from method annotations
                statement.evaluate();
                // TODO Cleanup database
            }
        };
    }
}

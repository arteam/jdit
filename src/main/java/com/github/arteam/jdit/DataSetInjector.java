package com.github.arteam.jdit;

import com.github.arteam.jdit.annotations.DataSet;

import java.lang.reflect.Method;

/**
 * Date: 2/1/15
 * Time: 3:24 PM
 * <p/>
 * Component that responsible for injecting test data to methods.
 * It analyzes test method and classes for {@link DataSet} annotation
 * and dispatches locations of the data to {@link DataMigration}
 * component.
 *
 * @author Artem Prigoda
 */
public class DataSetInjector {

    private DataMigration dataMigration;

    public DataSetInjector(DataMigration dataMigration) {
        this.dataMigration = dataMigration;
    }

    /**
     * Inject test data to a method.
     *
     * If the method or class has {@link} DataSet annotation, data
     * from script from the location specified in the annotation
     * will be injected to the DB
     *
     * @param method current method
     */
    public void injectData(Method method) {
        DataSet classLevelDataSet = method.getDeclaringClass().getAnnotation(DataSet.class);
        DataSet methodDataSet = method.getAnnotation(DataSet.class);
        DataSet actualDataSet = methodDataSet != null ? methodDataSet : classLevelDataSet;
        if (actualDataSet != null) {
            String scriptLocation = actualDataSet.value();
            dataMigration.executeScript(scriptLocation);
        }
    }

}

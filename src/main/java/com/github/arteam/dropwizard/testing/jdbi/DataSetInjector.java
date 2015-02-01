package com.github.arteam.dropwizard.testing.jdbi;

import com.github.arteam.dropwizard.testing.jdbi.annotations.DataSet;

import java.lang.reflect.Method;

/**
 * Date: 2/1/15
 * Time: 3:24 PM
 *
 * @author Artem Prigoda
 */
public class DataSetInjector {

    private DataMigration dataMigration;

    public DataSetInjector(DataMigration dataMigration) {
        this.dataMigration = dataMigration;
    }

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

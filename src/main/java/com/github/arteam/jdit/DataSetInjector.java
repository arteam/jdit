package com.github.arteam.jdit;

import com.github.arteam.jdit.annotations.DataSet;

import java.lang.reflect.Method;

/**
 * Component which is responsible for injecting test data to methods.
 * It analyzes test methods and classes for the {@link DataSet} annotation
 * and dispatches locations of the data to the {@link DataMigration}
 * component.
 */
class DataSetInjector {

    private DataMigration dataMigration;

    DataSetInjector(DataMigration dataMigration) {
        this.dataMigration = dataMigration;
    }

    /**
     * Injects test data to a method.
     * <p>
     * If the method or class has the {@link DataSet} annotation, data from the scripts of the locations,
     * specified in the annotation, will be injected to the DB
     *
     * @param method the current method
     */
    void injectData(Method method) {
        DataSet classLevelDataSet = method.getDeclaringClass().getAnnotation(DataSet.class);
        DataSet methodDataSet = method.getAnnotation(DataSet.class);
        DataSet actualDataSet = methodDataSet != null ? methodDataSet : classLevelDataSet;
        if (actualDataSet != null) {
            String[] scriptLocations = actualDataSet.value() != null ? actualDataSet.value() : new String[]{};
            for (String location : scriptLocations) {
                dataMigration.executeScript(location);
            }
        }
    }

}

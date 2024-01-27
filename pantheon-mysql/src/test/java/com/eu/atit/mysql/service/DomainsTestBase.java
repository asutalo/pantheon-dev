package com.eu.atit.mysql.service;

import com.eu.atit.mysql.service.annotations.MySqlField;
import com.mysql.cj.MysqlType;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

public class DomainsTestBase {
    static final int START_VALUE = 1;

    String getAlias(Field testField, String testTable) {
        return testTable + "_" + testField.getName();
    }

    Field getField() {
        Field testField = SimpleTestClass.class.getDeclaredFields()[0];
        testField.setAccessible(true);
        return testField;
    }

    Constructor<SimpleTestClass> getTestClassDefaultConstructor() throws NoSuchMethodException {
        Constructor<SimpleTestClass> declaredConstructor = SimpleTestClass.class.getDeclaredConstructor();
        declaredConstructor.setAccessible(true);
        return declaredConstructor;
    }

    Constructor<DoomedToFailSimpleTestClass> getDoomedToFailDefaultConstructor() throws NoSuchMethodException {
        return DoomedToFailSimpleTestClass.class.getDeclaredConstructor();
    }

    static class SimpleTestClass {

        @MySqlField(type = MysqlType.INT, primary = true)
        private int val = START_VALUE;

        SimpleTestClass() {
        }

        SimpleTestClass(String s) {
            //ignore
        }

        int getVal() {
            return val;
        }

        //to prevent intellij from adding "final" on the var...
        void setVal() {
            val = 0;
        }
    }

    static class DoomedToFailSimpleTestClass extends SimpleTestClass {
        DoomedToFailSimpleTestClass() {
            throw new RuntimeException();
        }
    }
}

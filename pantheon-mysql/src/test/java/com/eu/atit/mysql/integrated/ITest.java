package com.eu.atit.mysql.integrated;

import com.eu.atit.mysql.integrated.itestbase.ITestBaseStudent;
import com.eu.atit.mysql.integrated.itestbase.ITestBaseType;
import com.eu.atit.mysql.integrated.model.no_column_names.Course;
import com.eu.atit.mysql.integrated.model.no_column_names.Diploma;
import com.eu.atit.mysql.integrated.model.no_column_names.Student;
import com.eu.atit.mysql.integrated.model.no_column_names.Type;
import com.eu.atit.mysql.integrated.model.with_column_names.CourseCN;
import com.eu.atit.mysql.integrated.model.with_column_names.DiplomaCN;
import com.eu.atit.mysql.integrated.model.with_column_names.StudentCN;
import com.eu.atit.mysql.integrated.model.with_column_names.TypeCN;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.sql.SQLException;

public class ITest {
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("No column names - Student")
    @Nested
    class NoColumnNamesStudentTests extends ITestBaseStudent<Student, Type, Diploma, Course> {
        @BeforeAll
        void setUp() throws SQLException, URISyntaxException, IOException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
            super.setUp(Student.class, Type.class, Diploma.class, Course.class);
        }


        @Test
        @Override
        public void instanceOfT_shouldConvertMapToInstanceOfModel() {
            super.instanceOfT_shouldConvertMapToInstanceOfModel();
        }

        @Test
        @Override
        public void filteredSelect_provideBasicQueryBuilder() {
            super.filteredSelect_provideBasicQueryBuilder();
        }

        @Test
        @Override
        public void save_shouldInsertNewRecord() throws SQLException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
            super.save_shouldInsertNewRecord();
        }

        @Test
        @Override
        public void update_shouldUpdateExistingSpecificRecord() {
            super.update_shouldUpdateExistingSpecificRecord();
        }

        @Test
        @Override
        public void delete_shouldDeleteSpecificRecord() {
            super.delete_shouldDeleteSpecificRecord();
        }

        @Test
        @Override
        public void get_shouldFetchSpecificRecord_withQueryBuilder() {
            super.get_shouldFetchSpecificRecord_withQueryBuilder();
        }

        @Test
        @Override
        public void get_shouldSpecificRecord_withFilter() {
            super.get_shouldSpecificRecord_withFilter();
        }

        @Test
        @Override
        public void getAll_shouldFetchAllRecords() {
            super.getAll_shouldFetchAllRecords();
        }

        @Test
        @Override
        public void getAll_shouldFetchAllRecords_withQueryBuilder() {
            super.getAll_shouldFetchAllRecords_withQueryBuilder();
        }

        @Test
        @Override
        public void getAll_shouldFetchAllRecords_withFilter() {
            super.getAll_shouldFetchAllRecords_withFilter();
        }

        @Test
        public void shouldFetchStudentWithDiploma() throws SQLException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
            super.shouldFetchStudentWithDiploma();
        }
    }

    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("With column names - Student")
    @Nested
    class WithColumnNamesStudentTests extends ITestBaseStudent<StudentCN, TypeCN, DiplomaCN, CourseCN> {
        @BeforeAll
        void setUp() throws SQLException, URISyntaxException, IOException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
            super.setUp(StudentCN.class, TypeCN.class, DiplomaCN.class, CourseCN.class);
        }

        @Test
        public void shouldInsertNewStudent_withDiploma() throws SQLException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
            super.shouldFetchStudentWithDiploma();
        }

        @Test
        @Override
        public void instanceOfT_shouldConvertMapToInstanceOfModel() {
            super.instanceOfT_shouldConvertMapToInstanceOfModel();
        }

        @Test
        @Override
        public void filteredSelect_provideBasicQueryBuilder() {
            super.filteredSelect_provideBasicQueryBuilder();
        }

        @Test
        @Override
        public void save_shouldInsertNewRecord() throws SQLException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
            super.save_shouldInsertNewRecord();
        }

        @Test
        @Override
        public void update_shouldUpdateExistingSpecificRecord() {
            super.update_shouldUpdateExistingSpecificRecord();
        }

        @Test
        @Override
        public void delete_shouldDeleteSpecificRecord() {
            super.delete_shouldDeleteSpecificRecord();
        }

        @Test
        @Override
        public void get_shouldFetchSpecificRecord_withQueryBuilder() {
            super.get_shouldFetchSpecificRecord_withQueryBuilder();
        }

        @Test
        @Override
        public void get_shouldSpecificRecord_withFilter() {
            super.get_shouldSpecificRecord_withFilter();
        }

        @Test
        @Override
        public void getAll_shouldFetchAllRecords() {
            super.getAll_shouldFetchAllRecords();
        }

        @Test
        @Override
        public void getAll_shouldFetchAllRecords_withQueryBuilder() {
            super.getAll_shouldFetchAllRecords_withQueryBuilder();
        }

        @Test
        @Override
        public void getAll_shouldFetchAllRecords_withFilter() {
            super.getAll_shouldFetchAllRecords_withFilter();
        }
    }

    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("No column names - Type")
    @Nested
    class NoColumnNamesTypeTests extends ITestBaseType<Type> {
        @BeforeAll
        void setUp() throws SQLException, URISyntaxException, IOException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
            super.setUp(Type.class);
        }

        @Test
        @Override
        public void instanceOfT_shouldConvertMapToInstanceOfModel() {
            super.instanceOfT_shouldConvertMapToInstanceOfModel();
        }

        @Test
        @Override
        public void filteredSelect_provideBasicQueryBuilder() {
            super.filteredSelect_provideBasicQueryBuilder();
        }

        @Test
        @Override
        public void save_shouldInsertNewRecord() throws SQLException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
            super.save_shouldInsertNewRecord();
        }

        @Test
        @Override
        public void update_shouldUpdateExistingSpecificRecord() {
            super.update_shouldUpdateExistingSpecificRecord();
        }

        @Test
        @Override
        public void delete_shouldDeleteSpecificRecord() {
            super.delete_shouldDeleteSpecificRecord();
        }

        @Test
        @Override
        public void get_shouldFetchSpecificRecord_withQueryBuilder() {
            super.get_shouldFetchSpecificRecord_withQueryBuilder();
        }

        @Test
        @Override
        public void get_shouldSpecificRecord_withFilter() {
            super.get_shouldSpecificRecord_withFilter();
        }

        @Test
        @Override
        public void getAll_shouldFetchAllRecords() {
            super.getAll_shouldFetchAllRecords();
        }

        @Test
        @Override
        public void getAll_shouldFetchAllRecords_withQueryBuilder() {
            super.getAll_shouldFetchAllRecords_withQueryBuilder();
        }

        @Test
        @Override
        public void getAll_shouldFetchAllRecords_withFilter() {
            super.getAll_shouldFetchAllRecords_withFilter();
        }
    }

    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("With column names - Type")
    @Nested
    class WithColumnNamesTypeTests extends ITestBaseType<Type> {
        @BeforeAll
        void setUp() throws SQLException, URISyntaxException, IOException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
            super.setUp(Type.class);
        }

        @Test
        @Override
        public void instanceOfT_shouldConvertMapToInstanceOfModel() {
            super.instanceOfT_shouldConvertMapToInstanceOfModel();
        }

        @Test
        @Override
        public void filteredSelect_provideBasicQueryBuilder() {
            super.filteredSelect_provideBasicQueryBuilder();
        }

        @Test
        @Override
        public void save_shouldInsertNewRecord() throws SQLException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
            super.save_shouldInsertNewRecord();
        }

        @Test
        @Override
        public void update_shouldUpdateExistingSpecificRecord() {
            super.update_shouldUpdateExistingSpecificRecord();
        }

        @Test
        @Override
        public void delete_shouldDeleteSpecificRecord() {
            super.delete_shouldDeleteSpecificRecord();
        }

        @Test
        @Override
        public void get_shouldFetchSpecificRecord_withQueryBuilder() {
            super.get_shouldFetchSpecificRecord_withQueryBuilder();
        }

        @Test
        @Override
        public void get_shouldSpecificRecord_withFilter() {
            super.get_shouldSpecificRecord_withFilter();
        }

        @Test
        @Override
        public void getAll_shouldFetchAllRecords() {
            super.getAll_shouldFetchAllRecords();
        }

        @Test
        @Override
        public void getAll_shouldFetchAllRecords_withQueryBuilder() {
            super.getAll_shouldFetchAllRecords_withQueryBuilder();
        }

        @Test
        @Override
        public void getAll_shouldFetchAllRecords_withFilter() {
            super.getAll_shouldFetchAllRecords_withFilter();
        }
    }

}

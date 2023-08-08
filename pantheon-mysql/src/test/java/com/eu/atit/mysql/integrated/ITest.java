package com.eu.atit.mysql.integrated;

import com.eu.atit.mysql.integrated.model.no_column_names.Diploma;
import com.eu.atit.mysql.integrated.model.no_column_names.Student;
import com.eu.atit.mysql.integrated.model.no_column_names.Type;
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
    class NoColumnNamesStudentTests extends ITestBase<Student, Type, Diploma> {
        @BeforeAll
        void setUp() throws SQLException, URISyntaxException, IOException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
            super.setUp(Student.class, Type.class, Diploma.class);
        }
        @Test
        void shouldInsertNewStudent_withoutDiploma() throws SQLException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
            super.shouldInsertNewStudent_withoutDiploma();
        }
    }
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("With column names - Student")
    @Nested
    class WithColumnNamesStudentTests extends ITestBase<StudentCN, TypeCN, DiplomaCN> {
        @BeforeAll
        void setUp() throws SQLException, URISyntaxException, IOException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
            super.setUp(StudentCN.class, TypeCN.class, DiplomaCN.class);
        }
        @Test
        void shouldInsertNewStudent_withoutDiploma() throws SQLException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
            super.shouldInsertNewStudent_withoutDiploma();
        }
    }

}

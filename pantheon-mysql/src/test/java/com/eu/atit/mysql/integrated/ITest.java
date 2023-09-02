package com.eu.atit.mysql.integrated;

import com.eu.atit.mysql.integrated.itestbase.ITestBase;
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
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.concurrent.Callable;
import java.util.stream.Stream;

public class ITest {

    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("Common tests")
    @Nested
    class CommonTests {
        @ParameterizedTest
        @MethodSource("commonTestsProvider")
        void commonTests(Callable<? extends ITestBase> iTestBase) throws Exception {
            ITestBase testBase = iTestBase.call();

//        testBase.instanceOfT_shouldConvertMapToInstanceOfModel();
//        testBase.filteredSelect_provideBasicQueryBuilder();
            testBase.save_shouldInsertNewRecord();
            testBase.update_shouldUpdateExistingSpecificRecord();
            testBase.delete_shouldDeleteSpecificRecord();
//        testBase.get_shouldFetchSpecificRecord_withQueryBuilder();
//        testBase.get_shouldSpecificRecord_withFilter();
            testBase.getAll_shouldFetchAllRecords();
//        testBase.getAll_shouldFetchAllRecords_withQueryBuilder();
//        testBase.getAll_shouldFetchAllRecords_withFilter();
        }

        private static Stream<Callable<? extends ITestBase>> commonTestsProvider() {
            return Stream.of(
                    new Callable<ITestBaseStudent<Student, Type, Diploma, Course>>() {
                        @Override
                        public ITestBaseStudent<Student, Type, Diploma, Course> call() throws Exception {
                            ITestBaseStudent<Student, Type, Diploma, Course> iTestBaseStudent = new ITestBaseStudent<>();
                            iTestBaseStudent.setUp(Student.class, Type.class, Diploma.class, Course.class);
                            return iTestBaseStudent;
                        }

                        @Override
                        public String toString() {
                            return "ITestBaseStudent<Student, Type, Diploma, Course>";
                        }
                    },
                    new Callable<ITestBaseStudent<StudentCN, TypeCN, DiplomaCN, CourseCN>>() {
                        @Override
                        public ITestBaseStudent<StudentCN, TypeCN, DiplomaCN, CourseCN> call() throws Exception {
                            ITestBaseStudent<StudentCN, TypeCN, DiplomaCN, CourseCN> iTestBaseStudent = new ITestBaseStudent<>();
                            iTestBaseStudent.setUp(StudentCN.class, TypeCN.class, DiplomaCN.class, CourseCN.class);
                            return iTestBaseStudent;
                        }

                        @Override
                        public String toString() {
                            return "ITestBaseStudent<StudentCN, TypeCN, DiplomaCN, CourseCN>";
                        }
                    },
                    new Callable<ITestBaseType<Type>>() {
                        @Override
                        public ITestBaseType<Type> call() throws Exception {
                            ITestBaseType<Type> iTestBaseType = new ITestBaseType<>();
                            iTestBaseType.setUp(Type.class);
                            return iTestBaseType;
                        }

                        @Override
                        public String toString() {
                            return "ITestBaseType<Type>";
                        }
                    },
                    new Callable<ITestBaseType<TypeCN>>() {
                        @Override
                        public ITestBaseType<TypeCN> call() throws Exception {
                            ITestBaseType<TypeCN> iTestBaseType = new ITestBaseType<>();
                            iTestBaseType.setUp(TypeCN.class);
                            return iTestBaseType;
                        }

                        @Override
                        public String toString() {
                            return "ITestBaseType<TypeCN>";
                        }
                    }
            );
        }
    }

    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("No column names - Student")
    @Nested
    class NoColumnNamesStudentTests extends ITestBaseStudent<Student, Type, Diploma, Course> {
        @BeforeAll
        void setUp() throws SQLException, URISyntaxException, IOException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
            super.setUp(Student.class, Type.class, Diploma.class, Course.class);
        }

        @Test
        @Disabled
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
        @Disabled
        public void shouldFetchStudentWithDiploma() throws SQLException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
            super.shouldFetchStudentWithDiploma();
        }
    }

}

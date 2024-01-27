package com.eu.atit.mysql.integrated;

import com.eu.atit.mysql.integrated.itestbase.ITestBase;
import com.eu.atit.mysql.integrated.itestbase.ITestBaseCourse;
import com.eu.atit.mysql.integrated.itestbase.ITestBaseDiploma;
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
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class ITest {
    @BeforeAll
    static void setUp() throws IOException, InterruptedException {
        new ProcessBuilder("cmd.exe", "/C", "docker-compose up --detach").start().waitFor();

        Thread.sleep(5000);
    }

    @AfterAll
    static void cleanUp() throws IOException, InterruptedException {
        new ProcessBuilder("cmd.exe", "/C", "docker-compose down").start().waitFor();
    }

    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("Common tests")
    @Nested
    class CommonTests {
        @ParameterizedTest
        @MethodSource("commonTestsProvider")
        void instanceOfT_shouldConvertMapToInstanceOfModel(Callable<? extends ITestBase> iTestBase) throws Exception {
            Assertions.fail();
            ITestBase testBase = iTestBase.call();
            testBase.instanceOfT_shouldConvertMapToInstanceOfModel();
        }

        @ParameterizedTest
        @MethodSource("commonTestsProvider")
        void filteredSelect_provideBasicQueryBuilder(Callable<? extends ITestBase> iTestBase) throws Exception {
            ITestBase testBase = iTestBase.call();
            testBase.filteredSelect_provideBasicQueryBuilder();
        }

        @ParameterizedTest
        @MethodSource("commonTestsProvider")
        void save_shouldInsertNewRecord(Callable<? extends ITestBase> iTestBase) throws Exception {
            ITestBase testBase = iTestBase.call();
            testBase.save_shouldInsertNewRecord();
        }

        @ParameterizedTest
        @MethodSource("commonTestsProvider")
        void update_shouldUpdateExistingSpecificRecord(Callable<? extends ITestBase> iTestBase) throws Exception {
            ITestBase testBase = iTestBase.call();

            testBase.update_shouldUpdateExistingSpecificRecord();
        }

        @ParameterizedTest
        @MethodSource("commonTestsProvider")
        void delete_shouldDeleteSpecificRecord(Callable<? extends ITestBase> iTestBase) throws Exception {
            ITestBase testBase = iTestBase.call();

            testBase.delete_shouldDeleteSpecificRecord();
        }

        @ParameterizedTest
        @MethodSource("commonTestsProvider")
        void get_shouldFetchSpecificRecord_withQueryBuilder(Callable<? extends ITestBase> iTestBase) throws Exception {
            ITestBase testBase = iTestBase.call();
            testBase.get_shouldFetchSpecificRecord_withQueryBuilder();
        }

        @ParameterizedTest
        @MethodSource("commonTestsProvider")
        void get_shouldSpecificRecord_withFilter(Callable<? extends ITestBase> iTestBase) throws Exception {
            Assertions.fail();
            ITestBase testBase = iTestBase.call();
            testBase.get_shouldSpecificRecord_withFilter();
        }

        @ParameterizedTest
        @MethodSource("commonTestsProvider")
        void getAll_shouldFetchAllRecords(Callable<? extends ITestBase> iTestBase) throws Exception {
            ITestBase testBase = iTestBase.call();

            testBase.getAll_shouldFetchAllRecords();
        }

        @ParameterizedTest
        @MethodSource("commonTestsProvider")
        void getAll_shouldFetchAllRecords_withQueryBuilder(Callable<? extends ITestBase> iTestBase) throws Exception {
            ITestBase testBase = iTestBase.call();
            testBase.getAll_shouldFetchAllRecords_withQueryBuilder();
        }

        @ParameterizedTest
        @MethodSource("commonTestsProvider")
        void getAll_shouldFetchAllRecords_withFilter(Callable<? extends ITestBase> iTestBase) throws Exception {
            Assertions.fail();
            ITestBase testBase = iTestBase.call();

            testBase.getAll_shouldFetchAllRecords_withFilter();
        }

        private static Stream<Callable<? extends ITestBase>> commonTestsProvider() {
            return Stream.of(
                    callable(ITestBaseStudent.class, Student.class, Type.class, Diploma.class, Course.class),
                    callable(ITestBaseStudent.class, StudentCN.class, TypeCN.class, DiplomaCN.class, CourseCN.class),
                    callable(ITestBaseType.class, Type.class),
                    callable(ITestBaseType.class, TypeCN.class),
                    callable(ITestBaseCourse.class, Course.class),
                    callable(ITestBaseCourse.class, CourseCN.class),
                    callable(ITestBaseDiploma.class, Diploma.class, Student.class, Type.class),
                    callable(ITestBaseDiploma.class, DiplomaCN.class, StudentCN.class, TypeCN.class)
            );
        }
    }
    private static <T extends ITestBase> Callable <T> callable(Class<T> tClass, Class<?>... setUpParams) {
        return new Callable<>() {
            @Override
            public T call() throws Exception {
                Constructor<T> declaredConstructor = tClass.getDeclaredConstructor();
                T obj = declaredConstructor.newInstance();
                Method setUp = tClass.getDeclaredMethod("setUp", Arrays.stream(setUpParams).map(x -> Class.class).toArray(Class[]::new));
                setUp.setAccessible(true);
                setUp.invoke(obj, (Object[]) setUpParams);

                return obj;
            }

            @Override
            public String toString() {
                StringBuilder sb = new StringBuilder();
                sb.append(tClass.getSimpleName());
                sb.append(": ");
                Iterator<Class<?>> iterator = Arrays.stream(setUpParams).iterator();

                while (iterator.hasNext()) {
                    sb.append(iterator.next().getSimpleName());
                    if(iterator.hasNext()){
                        sb.append(", ");
                    }
                }

                return sb.toString();
            }
        };
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

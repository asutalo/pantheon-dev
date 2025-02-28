package com.eu.atit.mysql.integrated;

import com.eu.atit.mysql.integrated.itestbase.*;
import com.eu.atit.mysql.integrated.model.no_column_names.*;
import com.eu.atit.mysql.integrated.model.with_column_names.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.stream.Stream;

public class ITest {
    @BeforeAll
    static void setUp() throws IOException, InterruptedException {
        //linux
//        new ProcessBuilder("/usr/bin/bash", "-c", "docker-compose up --detach").start().waitFor();
//        win
        new ProcessBuilder("cmd.exe", "/C", "docker-compose up --detach").start().waitFor();
//        mac
//        new ProcessBuilder("sh",  "-c", "docker-compose up --detach").start().waitFor();
        Thread.sleep(5000);
    }

    @AfterAll
    static void cleanUp() throws IOException, InterruptedException {
        //linux
//        new ProcessBuilder("/usr/bin/bash", "-c", "docker-compose down").start().waitFor();
        //win
        new ProcessBuilder("cmd.exe", "/C", "docker-compose down").start().waitFor();
        //mac
//        new ProcessBuilder("sh", "-c", "docker-compose down").start().waitFor();

    }

    private static <T extends ITestBase> Callable<T> callable(Class<T> tClass, Class<?>... setUpParams) {
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
                    if (iterator.hasNext()) {
                        sb.append(", ");
                    }
                }

                return sb.toString();
            }
        };
    }

    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("Common tests")
    @Nested
    class CommonTests {
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
        void get_shouldFetchSpecificRecord_withFilter(Callable<? extends ITestBase> iTestBase) throws Exception {
            ITestBase testBase = iTestBase.call();
            testBase.get_shouldFetchSpecificRecord_withFilter();
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
            ITestBase testBase = iTestBase.call();
            testBase.getAll_shouldFetchAllRecords_withFilter();
        }
    }

    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("No column names - Student")
    @Nested
    class NoColumnNamesStudentTests extends ITestBaseStudent<Student, Type, Diploma, Course> {
        @BeforeAll
        void setUp() throws SQLException, URISyntaxException, IOException {
            super.setUpDb(Student.class, Type.class, Diploma.class, Course.class);
            super.initMySqlService(StudentCourse.class);
        }

        //todo dedupe in shouldFetchStudentWithDiploma method
        @Test
        public void shouldFetchFullStudentObject() throws SQLException {
            Type testType = new Type("some type");
            List<Course> testCourses = List.of(new Course("some course"), new Course("some other course"));
            ITestBase.insert(testType, Type.class);
            Student testStudent = new Student("some student", testType, null, null);
            ITestBase.insert(testStudent, Student.class);

            for (Course course : testCourses) {
                ITestBase.insert(course, Course.class);
                // facilitate 1:N connection
                ITestBase.insert(new StudentCourse(testStudent.getId(), course.getId()), StudentCourse.class);
            }

            Diploma testDiploma = new Diploma(testStudent, true);
            ITestBase.insert(testDiploma, Diploma.class);

            //we initialised the student object with empty courses and diploma as they didn't exist at time of insertion
            //existing courses will come back in reverse due to joining table not being sorted by keys so needs to be reversed to match 
            testStudent.setCourses(testCourses.reversed());
            testStudent.setDiploma(testDiploma);

            List<Student> all = ITestBase.getAll(Student.class);
            Assertions.assertEquals(1, all.size());

            Student actual = all.getFirst();
            Assertions.assertEquals(testStudent, actual);
        }

        @Test
        public void instanceOfT_shouldConvertMapToInstanceOfModel() {
            Type someT = new Type(11, "testTypeName");
            Course someC = new Course(33, "someCourse");
            Course someOtherC = new Course(44, "someOtherCourse");
            String someName = "someName";
            int someId = 112;
            Student expected = new Student(someId, someName, someT, null, List.of(someC, someOtherC));
            Diploma diploma = new Diploma(expected, Boolean.TRUE);
            expected.setDiploma(diploma);

            Map<String, Object> fieldsAndValues = Map.of(
                    "id", someId,
                    "name", someName,
                    "type", Map.of(
                            "id", someT.getId(),
                            "name", someT.getName()),
                    "diploma", Map.of(
                            "student", Map.of(
                                    "id", someId
                            ),
                            "obtained", diploma.obtained()
                    ),
                    "courses", List.of(
                            Map.of(
                                    "id", someC.getId(),
                                    "name", someC.getName()
                            ),
                            Map.of(
                                    "id", someOtherC.getId(),
                                    "name", someOtherC.getName()
                            )
                    )
            );

            Student actual = ITestBase.init(Student.class, fieldsAndValues);
            Assertions.assertEquals(expected, actual);
        }
    }

    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("With column names - Student")
    @Nested
    class WithColumnNamesStudentTests extends ITestBaseStudent<StudentCN, TypeCN, DiplomaCN, CourseCN> {
        @BeforeAll
        void setUp() throws SQLException, URISyntaxException, IOException {
            super.setUpDb(StudentCN.class, TypeCN.class, DiplomaCN.class, CourseCN.class);
            super.initMySqlService(StudentCourseCN.class);
        }

        @Test
        public void shouldFetchFullStudentObject() throws SQLException {
            TypeCN testType = new TypeCN("some type");
            List<CourseCN> testCourses = List.of(new CourseCN("some course"), new CourseCN("some other course"));
            ITestBase.insert(testType, TypeCN.class);
            StudentCN testStudent = new StudentCN("some student", testType, null, null);
            ITestBase.insert(testStudent, StudentCN.class);

            for (CourseCN course : testCourses) {
                ITestBase.insert(course, CourseCN.class);
                // facilitate 1:N connection
                ITestBase.insert(new StudentCourseCN(testStudent.getId(), course.getId()), StudentCourseCN.class);
            }

            DiplomaCN testDiploma = new DiplomaCN(testStudent, true);
            ITestBase.insert(testDiploma, DiplomaCN.class);

            //we initialised the student object with empty courses and diploma as they didn't exist at time of insertion
            //existing courses will come back in reverse due to joining table not being sorted by keys so needs to be reversed to match 
            testStudent.setCourses(testCourses.reversed());
            testStudent.setDiploma(testDiploma);

            List<StudentCN> all = ITestBase.getAll(StudentCN.class);
            Assertions.assertEquals(1, all.size());

            StudentCN actual = all.getFirst();
            Assertions.assertEquals(testStudent, actual);
        }

        @Test
        public void instanceOfT_shouldConvertMapToInstanceOfModel() {
            TypeCN someT = new TypeCN(11, "testTypeName");
            CourseCN someC = new CourseCN(33, "someCourse");
            CourseCN someOtherC = new CourseCN(44, "someOtherCourse");
            String someName = "someName";
            int someId = 112;
            StudentCN expected = new StudentCN(someId, someName, someT, null, List.of(someC, someOtherC));
            DiplomaCN diploma = new DiplomaCN(expected, Boolean.TRUE);
            expected.setDiploma(diploma);

            Map<String, Object> fieldsAndValues = Map.of(
                    "i", someId,
                    "n", someName,
                    "t", Map.of(
                            "i", someT.getId(),
                            "n", someT.getName()),
                    "diplomaCN", Map.of(
                            "s", Map.of(
                                    "i", someId
                            ),
                            "o", diploma.obtained()
                    ),
                    "courses", List.of(
                            Map.of(
                                    "i", someC.getId(),
                                    "n", someC.getName()
                            ),
                            Map.of(
                                    "i", someOtherC.getId(),
                                    "n", someOtherC.getName()
                            )
                    )
            );

            StudentCN actual = ITestBase.init(StudentCN.class, fieldsAndValues);
            Assertions.assertEquals(expected, actual);
        }
    }

}

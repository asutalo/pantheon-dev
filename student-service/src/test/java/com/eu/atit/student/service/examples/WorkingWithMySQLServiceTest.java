package com.eu.atit.student.service.examples;

import com.eu.atit.mysql.client.MySqlClient;
import com.eu.atit.mysql.query.QueryBuilder;
import com.eu.atit.student.service.model.Course;
import com.eu.atit.student.service.model.Diploma;
import com.eu.atit.student.service.model.Student;
import com.eu.atit.student.service.model.Type;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.inject.Named;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WorkingWithMySQLServiceTest {
    @Mock
    private MySqlClient mockMySqlClient;
    @InjectMocks
    private WorkingWithMySQLService workingWithMySQLService;
    @Captor
    private ArgumentCaptor<QueryBuilder> queryBuilderCaptor;

    private static final String STUDENT_ID_COLUMN_ALIAS = getAlias(Student.class, "id");
    private static final String STUDENT_NAME_COLUMN_ALIAS = getAlias(Student.class, "name");
    private static final String TYPE_ID_COLUMN_ALIAS = getAlias(Type.class, "id");
    private static final String TYPE_NAME_COLUMN_ALIAS = getAlias(Type.class, "name");
    private static final String COURSE_ID_COLUMN_ALIAS = getAlias(Course.class, "id");
    private static final String COURSE_NAME_COLUMN_ALIAS = getAlias(Course.class, "name");
    private static final String DIPLOMA_OBTAINED_COLUMN_ALIAS = getAlias(Diploma.class, "obtained");

    private static final Type STUDENT_TYPE = new Type(1, "student");
    private static final Type ALUMNI_TYPE = new Type(2, "alumni");

    private static final Course ALGBR101 = new Course(1, "algebra 101");
    private static final Course STAT_101 = new Course(2, "statistics 101");
    private static final Course COMP_SCI_102 = new Course(3, "computer science 102");
    private static final Student STUDENT_1 = new Student(1, "student 1", STUDENT_TYPE, null, List.of(COMP_SCI_102, STAT_101, ALGBR101));
    private static final Diploma STUDENT_1_DIPLOMA = getDiplomaFor(STUDENT_1, false);
    private static final Student STUDENT_2 = new Student(3, "student 2", STUDENT_TYPE, null, List.of(COMP_SCI_102, STAT_101));
    private static final Diploma STUDENT_2_DIPLOMA = getDiplomaFor(STUDENT_2, false);
    private static final Student ALUMNI_1 = new Student(2, "alumni 1", ALUMNI_TYPE, null, List.of(new Course(null, null)));
    private static final Diploma ALUMNI_1_DIPLOMA = getDiplomaFor(ALUMNI_1, true);

    @Named("Working with Student class")
    @Nested
    class StudentTests {
        @Test
        void getAll() throws SQLException {
            String expectedQuery = "SELECT diploma.obtained AS diploma_obtained, course.id AS course_id, type.id AS type_id, type.name AS type_name, course.name AS course_name, student.id AS student_id, student.name AS student_name FROM Student AS student LEFT JOIN Type AS type ON student.type_id = type.id LEFT JOIN Diploma AS diploma ON student.id = diploma.id LEFT JOIN Student_Course AS student_course ON student.id = student_course.student_id LEFT JOIN Course AS course ON student_course.course_id = course.id;";
            List<Student> expectedStudents = List.of(
                    STUDENT_1, ALUMNI_1, STUDENT_2
            );

            when(mockMySqlClient.executeSelectQuery(any())).thenReturn(
                    List.of(
                            getRowFor(COMP_SCI_102, STUDENT_1),
                            getRowFor(STAT_101, STUDENT_1),
                            getRowFor(ALGBR101, STUDENT_1),
                            getRowFor(null, null, ALUMNI_1),
                            getRowFor(COMP_SCI_102, STUDENT_2),
                            getRowFor(STAT_101, STUDENT_2)
                    ));

            List<Student> actualStudents = (List<Student>) workingWithMySQLService.getAll(Student.class);

            verify(mockMySqlClient).executeSelectQuery(queryBuilderCaptor.capture());
            assertEquals(expectedQuery, getActualQuery(queryBuilderCaptor.getValue()));
            assertEquals(expectedStudents.toString(), actualStudents.toString());
        }
    }

    @Named("Working with Type class")
    @Nested
    class TypeTests {
        @Test
        void getAll() throws SQLException {
            String expectedQuery = "SELECT type.id AS type_id, type.name AS type_name FROM Type AS type;";
            List<Type> expectedTypes = List.of(
                    STUDENT_TYPE, ALUMNI_TYPE
            );

            when(mockMySqlClient.executeSelectQuery(any())).thenReturn(
                    List.of(
                            getRowFor(STUDENT_TYPE),
                            getRowFor(ALUMNI_TYPE)
                    ));

            List<Type> actualTypes = (List<Type>) workingWithMySQLService.getAll(Type.class);

            verify(mockMySqlClient).executeSelectQuery(queryBuilderCaptor.capture());
            assertEquals(expectedQuery, getActualQuery(queryBuilderCaptor.getValue()));
            assertEquals(expectedTypes.toString(), actualTypes.toString());
        }
    }

    @Named("Working with Diploma class")
    @Nested
    class DiplomaTests {
        @Test
        void getAll() throws SQLException {
            String expectedQuery = "SELECT diploma.obtained AS diploma_obtained, course.id AS course_id, type.id AS type_id, type.name AS type_name, course.name AS course_name, student.id AS student_id, student.name AS student_name FROM Diploma AS diploma LEFT JOIN Student AS student ON diploma.id = student.id LEFT JOIN Type AS type ON student.type_id = type.id LEFT JOIN Student_Course AS student_course ON student.id = student_course.student_id LEFT JOIN Course AS course ON student_course.course_id = course.id;";
            List<Diploma> expectedDiplomas = List.of(
                    ALUMNI_1_DIPLOMA, STUDENT_1_DIPLOMA, STUDENT_2_DIPLOMA
            );

            when(mockMySqlClient.executeSelectQuery(any())).thenReturn(
                    List.of(
                            getRowFor(null, null, ALUMNI_1),
                            getRowFor(COMP_SCI_102, STUDENT_1),
                            getRowFor(STAT_101, STUDENT_1),
                            getRowFor(ALGBR101, STUDENT_1),
                            getRowFor(COMP_SCI_102, STUDENT_2),
                            getRowFor(STAT_101, STUDENT_2)
                    ));

            List<Diploma> actualDiplomas = (List<Diploma>) workingWithMySQLService.getAll(Diploma.class);

            verify(mockMySqlClient).executeSelectQuery(queryBuilderCaptor.capture());
            assertEquals(expectedQuery, getActualQuery(queryBuilderCaptor.getValue()));
            assertEquals(expectedDiplomas.toString(), actualDiplomas.toString());
        }
    }

    @Named("Working with Course class")
    @Nested
    class CourseTests {
        @Test
        void getAll() throws SQLException {
            String expectedQuery = "SELECT course.id AS course_id, course.name AS course_name FROM Course AS course;";
            List<Course> expectedCourses = List.of(
                    ALGBR101, COMP_SCI_102, STAT_101
            );

            when(mockMySqlClient.executeSelectQuery(any())).thenReturn(
                    List.of(
                            getRowFor(ALGBR101),
                            getRowFor(COMP_SCI_102),
                            getRowFor(STAT_101)
                    ));

            List<Course> actualCourses = (List<Course>) workingWithMySQLService.getAll(Course.class);

            verify(mockMySqlClient).executeSelectQuery(queryBuilderCaptor.capture());
            assertEquals(expectedQuery, getActualQuery(queryBuilderCaptor.getValue()));
            assertEquals(expectedCourses.toString(), actualCourses.toString());
        }
    }

    private String getActualQuery(QueryBuilder actualQueryBuilder) {
        return actualQueryBuilder.buildQueryString().replaceAll(System.lineSeparator(), " ").replaceAll("\\t", " ").replaceAll(" +", " ");
    }

    private static String getAlias(Class<?> testClass, String fieldName) {
        return testClass.getSimpleName().toLowerCase().concat("_").concat(fieldName);
    }

    private static Diploma getDiplomaFor(Student student, boolean obtained) {
        Diploma diploma = new Diploma(student, obtained);
        student.setDiploma(diploma);
        return diploma;
    }

    private Map<String, Object> getRowFor(Type type) {
        return new HashMap<>() {{
            put(TYPE_ID_COLUMN_ALIAS, type.getId());
            put(TYPE_NAME_COLUMN_ALIAS, type.getName());
        }};
    }

    private Map<String, Object> getRowFor(Course course) {
        return new HashMap<>() {{
            put(COURSE_ID_COLUMN_ALIAS, course.getId());
            put(COURSE_NAME_COLUMN_ALIAS, course.getName());
        }};
    }

    private Map<String, Object> getRowFor(Course course, Student student) {
        return getRowFor(course.getId(), course.getName(), student);
    }

    private Map<String, Object> getRowFor(Object courseId, Object courseName, Student student) {
        return new HashMap<>() {{
            put(COURSE_ID_COLUMN_ALIAS, courseId);
            put(STUDENT_NAME_COLUMN_ALIAS, student.getName());
            put(TYPE_NAME_COLUMN_ALIAS, student.getType().getName());
            put(COURSE_NAME_COLUMN_ALIAS, courseName);
            put(DIPLOMA_OBTAINED_COLUMN_ALIAS, student.getDiploma().getObtained());
            put(TYPE_ID_COLUMN_ALIAS, student.getType().getId());
            put(STUDENT_ID_COLUMN_ALIAS, student.getId());
        }};
    }
}
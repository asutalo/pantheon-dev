package com.eu.atit.mysql.integrated.itestbase;

import com.eu.atit.mysql.integrated.model.base.BaseCourse;
import com.eu.atit.mysql.integrated.model.base.BaseDiploma;
import com.eu.atit.mysql.integrated.model.base.BaseStudent;
import com.eu.atit.mysql.integrated.model.base.BaseType;
import org.junit.jupiter.api.Assertions;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;

public class ITestBaseStudent<S extends BaseStudent, T extends BaseType, D extends BaseDiploma, C extends BaseCourse> extends ITestBase<S, T, D, C> {

    public void shouldInsertNewStudent_withoutDiploma() throws SQLException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {

        int startingStudentCount = studentMySQLService.getAll().size();

        S testStudent = sClass.getDeclaredConstructor(String.class, tClass, dClass, List.class).newInstance("testStudentName", TEST_TYPE, null, List.of());

        studentMySQLService.save(testStudent);

        Assertions.assertTrue(testStudent.getId() > 0);
        List<S> actualStudents = studentMySQLService.getAll();
        Assertions.assertTrue(actualStudents.size() > startingStudentCount);

        List<S> matchingStudents = actualStudents.stream().filter(s -> s.getId() == testStudent.getId()).toList();
        Assertions.assertEquals(1, matchingStudents.size());
        Assertions.assertNull(matchingStudents.get(0).getDiploma().obtained());

    }

    public void shouldFetchStudentWithDiploma() throws SQLException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        int startingStudentCount = studentMySQLService.getAll().size();

        S testStudent = sClass.getDeclaredConstructor(String.class, tClass, dClass, List.class).newInstance("testStudentName", TEST_TYPE, null, List.of());

        studentMySQLService.save(testStudent);
        Assertions.assertTrue(testStudent.getId() > 0);

        D testDiploma = dClass.getDeclaredConstructor(sClass, Boolean.class).newInstance(testStudent, Boolean.TRUE);

        insert(testDiploma, dClass);

        Assertions.assertTrue(studentMySQLService.getAll().size() > startingStudentCount);
    }
}

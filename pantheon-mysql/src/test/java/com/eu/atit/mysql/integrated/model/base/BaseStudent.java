package com.eu.atit.mysql.integrated.model.base;

import java.util.List;

public interface BaseStudent extends WithId, WithName, BaseCourse {
    BaseDiploma getDiploma();

    <D extends BaseDiploma> void setDiploma(D diploma);

    <C extends BaseCourse> void setCourses(List<C> courses);
}

create table Course
(
    id   int auto_increment,
    name varchar(255) not null,
    constraint course_id_uindex
        unique (id),
    constraint course_name_uindex
        unique (name)
);

alter table Course
    add primary key (id);

create table Type
(
    id   int auto_increment,
    name varchar(255) not null,
    constraint type_id_uindex
        unique (id),
    constraint type_name_uindex
        unique (name)
);

alter table Type
    add primary key (id);

create table Student
(
    id      int auto_increment,
    name    varchar(255) not null,
    type_id int          not null,
    constraint student_id_uindex
        unique (id),
    constraint student_type_id_fk
        foreign key (type_id) references Type (id)
);

alter table Student
    add primary key (id);

create table Diploma
(
    id       int                  not null,
    obtained tinyint(1) default 0 null,
    constraint diploma_id_uindex
        unique (id),
    constraint diploma_student_id_fk
        foreign key (id) references Student (id)
);

alter table Diploma
    add primary key (id);

create table Student_Course
(
    student_id int not null,
    course_id  int not null,
    constraint student_course_course_id_fk
        foreign key (course_id) references Course (id),
    constraint student_course_student_id_fk
        foreign key (student_id) references Student (id)
);
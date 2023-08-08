create table course
(
    id   int auto_increment,
    name varchar(255) not null,
    constraint course_id_uindex
        unique (id),
    constraint course_name_uindex
        unique (name)
);

alter table course
    add primary key (id);

create table type
(
    id   int auto_increment,
    name varchar(255) not null,
    constraint type_id_uindex
        unique (id),
    constraint type_name_uindex
        unique (name)
);

alter table type
    add primary key (id);

create table student
(
    id      int auto_increment,
    name    varchar(255) not null,
    type_id int          not null,
    constraint student_id_uindex
        unique (id),
    constraint student_type_id_fk
        foreign key (type_id) references type (id)
);

alter table student
    add primary key (id);

create table diploma
(
    id       int                  not null,
    obtained tinyint(1) default 0 null,
    constraint diploma_id_uindex
        unique (id),
    constraint diploma_student_id_fk
        foreign key (id) references student (id)
);

alter table diploma
    add primary key (id);

create table student_course
(
    student_id int not null,
    course_id  int not null,
    constraint student_course_course_id_fk
        foreign key (course_id) references course (id),
    constraint student_course_student_id_fk
        foreign key (student_id) references student (id)
);
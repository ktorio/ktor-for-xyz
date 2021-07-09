drop table if exists tasks;

create table tasks
(
    id        integer primary key autoincrement,
    name      text not null,
    completed bit  not null
)
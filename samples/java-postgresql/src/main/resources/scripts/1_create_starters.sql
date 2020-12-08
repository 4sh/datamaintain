create table starters {
    name text not null,
    type text not null,
    hp numeric default 100,
    attack numeric default 50,
    defense numeric default 50,
    special numeric default 50,
    speed numeric default 50,
    constraint starters_pk primary key name
}
create table if not exists users (
    id uuid primary key,
    created_at timestamp not null default current_timestamp,
    updated_at timestamp not null default current_timestamp,
    email varchar(254) not null unique,
    password_hash varchar(255) not null,
    role varchar(32) not null,
    profile_completed boolean not null default false
);

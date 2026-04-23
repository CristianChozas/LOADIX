create table if not exists warehouse_profiles (
    id uuid primary key,
    created_at timestamp not null default current_timestamp,
    updated_at timestamp not null default current_timestamp,
    user_id uuid not null unique,
    company_name varchar(255),
    cif varchar(50),
    address varchar(255),
    postal_code varchar(20),
    city varchar(100),
    phone varchar(20),
    contact_person varchar(100),
    cargo_type varchar(50),
    constraint fk_warehouse_profiles_user_id foreign key (user_id) references users (id) on delete cascade
);
create index if not exists idx_warehouse_profiles_user_id on warehouse_profiles (user_id);
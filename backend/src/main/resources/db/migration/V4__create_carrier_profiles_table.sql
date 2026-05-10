create table if not exists carrier_profiles (
    id uuid primary key,
    created_at timestamp not null default current_timestamp,
    updated_at timestamp not null default current_timestamp,
    user_id uuid not null unique,
    name varchar(100),
    last_name varchar(100),
    phone varchar(20),
    vehicle_type varchar(32),
    license_place varchar(20),
    carnet varchar(20),
    constraint fk_carrier_profiles_user_id foreign key (user_id) references users (id) on delete cascade
);
create index if not exists idx_carrier_profiles_user_id on carrier_profiles (user_id);

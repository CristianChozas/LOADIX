create table if not exists loads (
    id uuid primary key,
    created_at timestamp not null default current_timestamp,
    updated_at timestamp not null default current_timestamp,
    warehouse_user_id uuid not null,
    created_by_user_id uuid not null,
    origin_address varchar(255) not null,
    origin_city varchar(100) not null,
    origin_postal_code varchar(20) not null,
    destination_address varchar(255) not null,
    destination_city varchar(100) not null,
    destination_postal_code varchar(20) not null,
    cargo_type varchar(50) not null,
    weight_kg numeric(10,2) not null,
    pickup_date date not null,
    base_price_amount numeric(12,2) not null,
    notes text,
    special_requirements text,
    status varchar(32) not null,
    constraint fk_loads_warehouse_user_id foreign key (warehouse_user_id) references users (id) on delete cascade,
    constraint fk_loads_created_by_user_id foreign key (created_by_user_id) references users (id) on delete cascade
);

create index if not exists idx_loads_warehouse_user_id on loads (warehouse_user_id);
create index if not exists idx_loads_created_by_user_id on loads (created_by_user_id);
create index if not exists idx_loads_status on loads (status);

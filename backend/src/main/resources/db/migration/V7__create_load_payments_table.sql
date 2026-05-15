create table if not exists load_payments (
    id uuid primary key,
    created_at timestamp not null default current_timestamp,
    updated_at timestamp not null default current_timestamp,
    load_id uuid not null,
    carrier_user_id uuid not null,
    provider varchar(32) not null,
    provider_payment_id varchar(128) not null,
    amount numeric(12,2) not null,
    currency varchar(3) not null,
    status varchar(32) not null,
    constraint fk_load_payments_load_id foreign key (load_id) references loads (id) on delete cascade,
    constraint fk_load_payments_carrier_user_id foreign key (carrier_user_id) references users (id) on delete cascade
);

create unique index if not exists uq_load_payments_provider_payment_id on load_payments (provider_payment_id);
create index if not exists idx_load_payments_load_carrier_status on load_payments (load_id, carrier_user_id, status);

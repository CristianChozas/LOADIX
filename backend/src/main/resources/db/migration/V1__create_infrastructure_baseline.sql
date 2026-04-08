create table if not exists infrastructure_baseline (
    id bigint primary key,
    created_at timestamp not null default current_timestamp
);

insert into infrastructure_baseline (id)
values (1)
on conflict (id) do nothing;

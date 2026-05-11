alter table loads
    add column if not exists load_quantity integer;

alter table loads
    add column if not exists load_unit_type varchar(32);

update loads
set load_quantity = 1,
    load_unit_type = 'PALLETS'
where load_quantity is null
   or load_unit_type is null;

alter table loads
    alter column load_quantity set not null;

alter table loads
    alter column load_unit_type set not null;

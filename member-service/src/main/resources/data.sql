insert into member.role (name) values ('USER'), ('SELLER'), ('ADMIN') on conflict do nothing;

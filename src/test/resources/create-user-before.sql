delete
from users;



insert into users(id_user, email, first_name, last_name, password, status, username, avatar, role)
values ('1',
        'test@mail.com',
        'test',
        'test',
        '$2a$10$trZ2KInnDx0/4u6/ABMuL.1t70/QXk/FUHFtJYHlv.WlHsIR3OVk.',
        'ACTIVE',
        'test',
        false,
        'USER');

insert into users(id_user, email, first_name, last_name, password, status, username, role)
values ('2',
        'test2@mail.com',
        'test',
        'test',
        '$2a$10$trZ2KInnDx0/4u6/ABMuL.1t70/QXk/FUHFtJYHlv.WlHsIR3OVk.',
        'NOT_ACTIVE',
        'test2',
        'USER');

insert into users(id_user, email, first_name, last_name, password, status, username, role)
values ('3',
        'test3@mail.com',
        'test',
        'test',
        '$2a$10$trZ2KInnDx0/4u6/ABMuL.1t70/QXk/FUHFtJYHlv.WlHsIR3OVk.',
        'BLOCKED',
        'test3',
        'USER');

insert into users(id_user, email, first_name, last_name, password, status, username, avatar,role)
values ('4',
        'test4@mail.com',
        'test',
        'test',
        '$2a$10$trZ2KInnDx0/4u6/ABMuL.1t70/QXk/FUHFtJYHlv.WlHsIR3OVk.',
        'ACTIVE',
        'test4',
        true,
        'USER');



insert into  spring_security.sys_user values (1,'admin','{noop}12345','{
    "Statement": [
        {
            "Action": "admin:*",	# /admin/**
            "Effect": "Allow",
            "Resource": "*"
        }
    ],
    "Version": "1"
}');
insert into  spring_security.sys_user values (2,'用户a1','{noop}12345', '{
    "Statement": [
        {
            "Action": "oms:*",	# /oms/**
            "Effect": "Allow",
            "Resource": "001:*"
        }
    ],
    "Version": "1"
}');
insert into  spring_security.sys_user values (3,'用户a2','{noop}12345','{
    "Statement": [
        {
            "Action": "oms:books:*",	# /books/**
            "Effect": "Allow",
            "Resource": "001:*"
        }
    ],
    "Version": "1"
}');
insert into  spring_security.sys_user values (4,'用户b1','{noop}12345','{
    "Statement": [
        {
            "Action": "oms:items:*",	# /oms/items/**
            "Effect": "Allow",
            "Resource": "002:b01"
        }
    ],
    "Version": "1"
}');
insert into  spring_security.sys_user values (5,'用户b2','{noop}12345','{
    "Statement": [
        {
            "Action": "oms:items:*",	# /oms/items/**
            "Effect": "Allow",
            "Resource": "002:b01"
        }
    ],
    "Version": "1"
}');
insert into  spring_security.sys_user values (6,'用户b3','{noop}12345','{
    "Statement": [
        {
            "Action": "oms:*",	# /oms/**
            "Effect": "Allow",
            "Resource": "002:*"
        }
    ],
    "Version": "1"
}');
insert into  spring_security.sys_projects values (1, '001', '武汉P4项目');
insert into  spring_security.sys_projects values (2, '002', '上海P3项目');
insert into  spring_security.oms_books values (1, '001', '哈利波特');
insert into  spring_security.oms_books values (2, '002', '绿野仙踪');
insert into  spring_security.oms_books values (3, '002', '不可思议的爱丽丝');
insert into  spring_security.oms_items values (1, '001', 'a01', '医用口罩');
insert into  spring_security.oms_items values (2, '002', 'b01', '3M 7501');
insert into  spring_security.oms_items values (3, '002', 'b02', '松重 UK2');
insert into  spring_security.oms_items values (4, '002', 'b01', '3M 5502');
insert into  spring_security.oms_items values (5, '002', 'b03', '3M 7502QL');
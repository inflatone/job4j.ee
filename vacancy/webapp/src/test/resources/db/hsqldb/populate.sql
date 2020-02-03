INSERT INTO task(id, keyword, city, scan_limit, repeat_rule, scan_source_id, user_id)
VALUES (100, 'java', 'Moscow', '2020-01-01 10:00:00', 'manually', 1, 10),
       (101, 'java', 'Samara', '2020-01-10 16:00:00', 'daily', 1, 10),
       (102, 'kotlin', 'New York', '2020-01-13 00:00:00', 'monthly', 2, 10),
       (103, 'frontend', 'Tolyatti', '2020-02-01 02:00:00', 'weekly', 3, 11),
       (104, 'javascript', null, '2019-11-28 11:00:00', 'manually', 2, 11);

INSERT INTO vacancy(id, task_id, title, description, url, date)
VALUES (1000, 100, 'Java programmer (Moscow)', 'Description', 'url', '2020-01-25 16:12:00'),
       (1001, 100, 'Fullstack programmer (Samara)', 'Description', 'url', '2020-01-27 13:00:00'),
       (1002, 100, 'Java programmer (relocation)', 'Description', 'url', '2020-01-27 16:33:00'),
       (1003, 100, 'Android programmer (full-day)', 'Description', 'url', '2020-01-30 08:12:00'),
       (1004, 101, 'Java programmer (Moscow)', 'Description', 'url', '2020-01-27 06:00:00'),
       (1005, 101, 'Scala programmer (visa sponsorship)', 'Description', 'url', '2020-01-28 14:13:00'),
       (1006, 101, 'Fullstack programmer (Java, Kotlin)', 'Description', 'url', '2020-01-30 07:58:00'),
       (1007, 103, 'JS-react programmer', 'Ajax description', 'url', '2020-01-30 8:34:00')

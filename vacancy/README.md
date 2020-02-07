# Web-приложение «Граббер вакансий»

[![Codacy Badge](https://api.codacy.com/project/badge/Grade/df661ff60cef47ceb6e566e9e207150c?branch=task_1731)](https://www.codacy.com/manual/inflatone/job4j.ee?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=inflatone/job4j.ee&amp;utm_campaign=Badge_Grade)
[![Build Status](https://api.travis-ci.org/inflatone/job4j.ee.svg?branch=task_1731)](https://travis-ci.org/inflatone/job4j.ee/branches)
[![codecov](https://codecov.io/gh/inflatone/job4j.ee/branch/task_1731/graph/badge.svg)](https://codecov.io/gh/inflatone/job4j.ee/branch/task_1731)
[![Heroku](https://heroku-badge.herokuapp.com/?app=saneseeker)](http://saneseeker.herokuapp.com/)

Реализация многопользовательского приложения (c авторизацией и правами доступа на основе ролей) по автоматическому отслеживанию и сбору информации
об имеющихся вакансиях на профильных интернет-ресурсах. Каждый пользователь имеет возможность создавать неограниченное кол-во заданий с разными настройками.

Доступные режимы: 
* автоматический: <a href="https://en.wikipedia.org/wiki/Cron">cron</a>-выражение строится на основе времени следующего старта (по-умолчанию старт происходит сразу после добавления задачи, также можно задать вручную) и выбранного интервала между запусками
* ручной (force): запуск сбора вакансий right now (не влияет на заданные параметры автоматического сбора)

Возможно временная остановка запусков (пауза), редактирование некоторых настроек уже существующей задачи: время следующего запуска, периодичность, начальное ограничение по дате публикации вакансии — совмещено с полем последнего запуска,
при повторном нахождении вакансии дубликат не добавляется, но поля обновляются (например, дата публикации — for HR sake :-) ).

На данный момент поддерживается парсинг с сайтов <a href="https://hh.ru/">hh.ru</a>, <a href="https://career.habr.com/">career.habr.com</a>, форума <a href="https://www.sql.ru/forum/job-offers/">sql.ru</a>.

### При реалиазации использовано:
* JDK 11, Tomcat 9, Maven 3
* Maven plugins: <a href="https://www.liquibase.org/documentation/maven/index.html">Liquibase</a>, <a href="https://github.com/heroku/webapp-runner">WebApp runner</a> (via <a href="https://maven.apache.org/plugins/maven-dependency-plugin/">Dependency plugin</a>), <a href="https://maven.apache.org/enforcer/maven-enforcer-plugin/">Enforcer</a> (dependency convergence)
* <a href="https://github.com/google/guice">Google Guice</a> (lightweight dependency injection framework)
* <a href="http://www.quartz-scheduler.org/">Quartz Enterprise Job Scheduler</a>
* <a href="https://jdbi.org/">Jdbi 3</a> (higher level library over JDBC to access the database)
* PostgreSQL, <a href="http://hsqldb.org/">HSQLDB</a> (for test and demo modes)
* <a href="https://projectlombok.org/">Project Lombok</a>
* JUnit 5 with Extension integration (<a href="https://site.mockito.org/">Mockito</a>, <a href="https://github.com/JeffreyFalgout/junit5-extensions/tree/master/guice-extension">Guice</a>), <a href="http://www.vogella.com/tutorials/AssertJ/article.html">AssertJ</a>, <a href="https://github.com/skyscreamer/JSONassert">JSONassert</a>
* Front-end (all provided via <a href="https://www.webjars.org/">Webjars</a> and located with <a href="https://github.com/webjars/webjars-taglib">Taglib</a>):
    * <a href="https://jquery.com/">jQuery</a>, <a href="https://jqueryvalidation.org/">jQuery Validation Plugin</a>, <a href="https://github.com/phstc/jquery-dateFormat">jquery-dateFormat</a>, <a href="https://datatables.net/examples/data_sources/ajax">DataTables</a>
    * <a href="https://getbootstrap.com/">Bootstrap</a>, <a href="https://eonasdan.github.io/bootstrap-datetimepicker/">Bootstrap DateTimePicker</a>
    * <a href="https://fontawesome.com/">Font Awesome</a> icons
    * <a href="https://ned.im/noty/#/">Noty</a> notification library
* <a href="https://docs.travis-ci.com/user/tutorial/">Travis CI</a>, <a href="http://https://codecov.io/">Codecov</a>,  <a href="https://www.codacy.com/product">Codacy</a>
* <a href="https://devcenter.heroku.com/categories/java-support">Heroku</a> deployed
### Чтобы запустить:
* Build and run `executable war` on the command line:
    * launch `demo-vacancy.bat` from root (installed Maven/JRE required)
* After the server is running, go to `localhost:8080`
* Press `Ctrl+C` on the command line to stop the server
###### ...or visit demo on <a href="http://saneseeker.herokuapp.com/">heroku</a> (a first access may take some time because of application redeploying)

### Схема

#### Технические замечания
* Первоначально проект вырос из задания курса **"Парсер вакансий на sql.ru"** (здесь как модуль `engine`),
где предполагалось создание консольного приложения, работающего лишь с форумом sql.ru, собирающее найденные java-вакансии в БД раз в день.
Ещё тогда же была добавлена поддержка кастомизации под _hh.ru_ и _habr.career.com_ для отягощения.
* Для превращения в полноценное многопользовательское web-приложение взял за основу ранее написанный <a href="https://github.com/sane5ever/job4j.ee/tree/task_2512/users">user-app</a> проект.
Оптимизировал хранение данных авторизованного юзера, вынеся их в session-scope объекты класса `AuthManager`. Обмен датами между сервером и клиентом переделал в виде timestamp, чтобы гарантировать корректное отображение в разных часовых поясах.
* Практически целиком перевёл приложение на использование HSQLDB (поддержка PostgreSQL сохраняется), пока без PostgreSQL  не будет функционирует только базовое консольное приложение (модуль `engine`), из-за чего упадёт и сборка web-приложения (hsqldb-mode) при неотключённых тестах.
Переключение между базами (встроенная HSQLDB, и PostgreSQL, локальная и heroku) осуществляется за счёт maven-профилирования (hsqldb, local, heroku).
* **Maven.** Использую преимущества многомодульного проекта: **parent/parent-web** — родители для обычного/web модулей с общими build-плагинами, property-константами и необходимыми тестовыми библиотеками; **common** — набор утилитных библиотек — <a href="https://github.com/google/guava">Guava</a>, <a href="https://github.com/amaembo/streamex">StreamEx</a> (enhancing Java Stream API), <a href="https://github.com/qos-ch/logback">logback</a> via <a href="https://github.com/qos-ch/slf4j">SLF4J</a>.
    * <a href="https://maven.apache.org/enforcer/maven-enforcer-plugin/">Maven Enforcer Plugin</a> — "конвергенция зависимостей", проводит проверку не находятся ли в проекте конфликтующие зависимост, т.е. одна и та же библиотека с разными версиями, как правило за счёт «скрытых» зависимостей зависимостей (<a href="https://www.baeldung.com/maven-dependency-scopes#transitive-dependency">transitive dependency</a>), в случае обнаружения — роняет сборку.
    * <a href="https://www.liquibase.org/documentation/maven/index.html">Maven Liquibase Plugin</a> — накатывает скрипты для PostgreSQL для local и <a href="https://docs.travis-ci.com/user/database-setup/#postgresql">CI</a> запусков.
    * <a href="">Heroku Webapp Runner</a> — с помощью Maven dependency plugin собирает executable web-jar c встроенным контейнером сервлетов (Tomcat 9.27), первоначально использовался только для деплоя приложения на Heroku, в последующем с помощью него же сделал развёртывание локально через исполняемые .bat (maven-команда для сборки + java-запуск получившегося jar).
* **Quartz.** планировщик задач по расписанию, запуск настраивается с помощью конкретной даты (`nextLaunch`) и <a href="https://en.wikipedia.org/wiki/Cron">cron</a>-выражения.
В заготовку cron-выражения вставляются необходимые «куски» от `nextLaunch` в зависимости от выбранной периодичности (daily, weekly, monthly, etc) — выполнено с помощью переопределния методов экземпляров `enum RepeatRule`.
Чтобы предусмотреть возможность остановки/перезапуска сервера, реализовал автосохранение параметров задач в БД (с помощью предоставленного самим Quartz механизма выбора JDBC-based хранилища для планировщика).
* Добавил использование доп. **jQuery-based** библиотек на клиенте: 
    * **jquery-dateFormat** для упрощения конвертации timestamp в текстовые даты, в некоторых таблицах использовал "<a href="https://github.com/phstc/jquery-dateFormat#pretty-date-formatting">pretty-date formatting</a>" для отрисовки и поиска. 
    * **jquery-validation** для доп. условий валидации форм (поля времени before/after в форме задач)
    * **dataTables** по-прежнему используется для отрисовки/обновления всех динамических таблиц (списки юзеров/тасок/вакансий, данные профиля/таски), также обеспечивает логику _сортировки_/_поиска_ (корректно сортирует и в случае колонок со временем: вчера, три дня назад, etc.), в таблице с вакансиями плюсом включил _paging_.
    * **bootstrap**, **noty**, **font awesome** по-прежнему используются для наведения лёгкого марафета на клиенте.

#### Планы по улучшению
* переделать с помощью Spring Framework (Boot + MVC/JPA/Security)
* попробовать порезать приложение на сервисы
* ввести иерархию исключений, добавить их user-friendly обработку (сейчас местами на клиент в уведомления уходят «полотна» из эксепшенов с технической информацией)
* перевести полностью на HSQLDB модуль `engine`
* добавить отображение на клиенте лога запусков таска (когда, сколько, статус — серверная часть уже имеется)
* починить информацию о кол-ве найденных/добавленных во время запуска вакансий (сейчас "добавляются" все найденные,
из-за чего счётчик вакансий сбивается, хотя на самом деле дубликаты лишь обновляются:
 `INSERT ... ON CONFLICT DO UPDATE SET ...` (Postgres) и `MERGE INTO ... WHEN MATCHED ... WHEN NOT MATCHED ...` (Hsqldb)
* добавить поддержку доп. ресурсов (avito.ru, vc.ru, rabota.ru, etc)
* улучшить алгоритм выборки, опираясь на особенности каждого используемого ресурса вакансий
* предусмотреть возможность нескольких попыток переподключения к html-странице при обрыве соединения
* реализовать мульти-ресурсный сбор вакансий (несколько интернет-ресурсов в одном таске)
* добавить парсинг дополнительных колонок (з/п, адрес, условия работы, etc)
* добавить фильтрацию по городу на sql.ru (например, поиск упоминаний города внутри самой темы), либо закрыть эту графу в форме при выборе sql.ru (сейчас она молча игнорируется)
* функционал для администрирования приложения, возможность ручного добавления cron-выражения (для продвинутых юзеров)


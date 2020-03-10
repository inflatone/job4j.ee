# Web-приложение для управления пользователями

[![Codacy Badge](https://api.codacy.com/project/badge/Grade/df661ff60cef47ceb6e566e9e207150c?branch=task_2512)](https://www.codacy.com/manual/sane5ever/job4j.ee?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=sane5ever/job4j.ee&amp;utm_campaign=Badge_Grade)
[![Build Status](https://travis-ci.org/sane5ever/job4j.svg?branch=task_2512)](https://travis-ci.org/sane5ever/job4j.ee/branches)
[![codecov](https://codecov.io/gh/sane5ever/job4j.ee/branch/task_2512/graph/badge.svg)](https://codecov.io/gh/sane5ever/job4j.ee/branch/task_2512)
[![Heroku](https://heroku-badge.herokuapp.com/?app=usersane)](http://usersane.herokuapp.com/)

Представляет собой заготовку многопользовательского web-приложения (c авторизацией и правами доступа на основе ролей).
Имеются страницы профилей с возможностью просмотра и редактирования своих данных и загрузки фотографии профиля,
а так же «админка», доступная только для администраторов, позволяющая создавать/редактировать/удалять профили пользователей, 
управлять возможностью их доступа в систему (enable/disable).
Реализована возможность самостоятельной регистрации новых пользователей.

Используется мною как основа для всех последующих проектов web-приложений.

### При реалиазации использовано:
* JDK 11, Tomcat 9, Maven 3
* Maven plugins: <a href="https://www.liquibase.org/documentation/maven/index.html">Liquibase</a>, <a href="https://github.com/heroku/webapp-runner">WebApp runner</a> (via <a href="https://maven.apache.org/plugins/maven-dependency-plugin/">Dependency plugin</a>), <a href="https://maven.apache.org/enforcer/maven-enforcer-plugin/">Enforcer</a> (dependency convergence)
* <a href="https://github.com/google/guice">Google Guice</a> (lightweight dependency injection framework)
* <a href="https://jdbi.org/">Jdbi 3</a> (higher level library over JDBC to access the database)
* PostgreSQL
* JUnit 5, <a href="https://site.mockito.org/">Mockito</a>, <a href="http://www.vogella.com/tutorials/AssertJ/article.html">AssertJ</a>
* Front-end:
    * <a href="https://jquery.com/">jQuery</a>, <a href="https://datatables.net/examples/data_sources/ajax">DataTables</a>
    * <a href="https://getbootstrap.com/">Bootstrap</a>, <a href="https://eonasdan.github.io/bootstrap-datetimepicker/">Bootstrap DateTimePicker</a>
    * <a href="https://fontawesome.com/">Font Awesome</a> icons
    * <a href="https://ned.im/noty/#/">Noty</a> notification library
* <a href="https://github.com/qos-ch/logback">Logback</a> via <a href="https://github.com/qos-ch/slf4j">SLF4J</a>
* <a href="https://docs.travis-ci.com/user/tutorial/">Travis CI</a>, <a href="http://https://codecov.io/">Codecov</a>,  <a href="https://www.codacy.com/product">Codacy</a>
* <a href="https://devcenter.heroku.com/categories/java-support">Heroku</a> deployed
### Чтобы запустить:
* Build and run `executable war` on the command line:
    * launch `demo-users.bat` from root (installed Maven/JRE required, using a PostgreSQL database from heroku — no need it locally)
* After the server is running, go to `localhost:8080`
* Press `Ctrl+C` on the command line to stop server

###### ...or visit demo on <a href="http://usersane.herokuapp.com/">heroku</a> (a first access may take some time because of application redeploying)

#### Технические замечания
* **Jdbi**: в предверии Hibernate для расширения кругозора захотелось работать не с голым JDBC, как раньше, а использовать что-то чуть выше уровнем.
 Это далеко не Hibernate, но упрощает формирование запросов, управление коннекшенами и транзакциями, так же обеспечивает практически автоматический мапинг result set'ов в java-объекты (при JOIN'ах и сохрании/вытягивании картинок приходилось «допиливать» то, что получалось из коробки)
* **Connection pool**: поднимаю на уровне Tomcat, получаю с помощью JNDI
(в данном проекте в этом нет необходимости, тк доступ к БД не share'ится между несколькими "приёмниками", но уже в следующем проекте это пригодилось при включении автосохранения в БД параметров тасков для Quartz).
Для этого подкладываю context.xml c описанием нужного ресурса в war'ник, properties-зависимые поля вставляю в него чз `maven resource plugin (filtering=true)`, чтобы креденшелы к БД задавались только в одном месте (блок `<properties>` в pom.xml)
* **Guice**: решил попробовать, тк не захотел для подмены в тестах статических getInstance() в синглтонах снова мучиться с Powermock  (тем более, не был уверен, что JUnit 5, на кот. я перешёл с этого проекта, совместим с ним).
Также перенёс на него и диспетчиризацию сервлетов.
* **Auth**: сделал отдельный SecurityService для авторизации, в кот. для разнообразия реализовал валидацию юзера по нескольким условиям: существует ли вообще, корректный ли пароль, не забанен ли — по примеру из <a href="https://github.com/peterarsentev/code_quality_principles#3-if-else-throw-statements">If-else-throw statements</a>, только выкидывание исключений тоже «обобщил» с помощью функционального интерфейса с default-методом плюсом, тк меняется только передаваемый месседж ошибки.
* **WebMock** (самописный): написал простенький шаблонизатор для интеграционного тестирования контроллеров (по примеру `MockMVC` в spring-test).
Тестирую в т.ч. диспетчеризацию запросов, указывая в тестах url'ы, кот. уходят в Guice filter, вместо того, чтоб явно дёргать методы контроллеров.
* **DB mock rollback** (самописный): На уровне сервисов тестирую и JDBI, и in-memory репозитории (_«заменить БД-репозитории на реализованные с помощью Java Collections»_ — часть первоначального задания), чтобы контролировать соответствие основных JDBI-версий и in-memory аналогов.
Для тестов, кот. работают с реальной БД, сделал mock на `DataSource` таким образом, чтобы откатывать/закрывать соединения только в самих тестах (в самой программе все места, где фиксируются изменения `commit()` и `close()` — игнорируются), для добавления этого функционала лишь в отдельные тесты спроектировал тестовый интерфейс с mock'ером и `@Before`/`@After`-аннотациями. Не класс, тк позволяет обойти двойное наследование (у тестовых классов уже есть "родитель"), в следующем проекте сообразил, что более простой способ — переделать на JUnit5 extension.
#### Планы по улучшению
* заменить клиентские зависимости (jQuery, Bootstrap, etc) с ссылок на внешние ресурсы на внутренние <a href="https://www.webjars.org/">webjars</a>
* добавить thumbnail-версии аватаров (показывать их в "админке") — использовать <a href="https://github.com/coobird/thumbnailator">Thumbnailator library</a>
* перевести тесты на Hsqldb/H2 (может, и сам проект тоже)
* переделать обмен датами между сервером и клиентом cо строкового представления на timestamp (решит проблему часовых поясов) — использовать <a href="https://github.com/phstc/jquery-dateFormat">jquery-dateFormat</a>
* разнообразить валидацию данных на клиенте

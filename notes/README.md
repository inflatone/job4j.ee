# Web-приложение «TODO list»

[![Codacy Badge](https://api.codacy.com/project/badge/Grade/df661ff60cef47ceb6e566e9e207150c?branch=task_3786)](https://www.codacy.com/manual/inflatone/job4j.ee?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=inflatone/job4j.ee&amp;utm_campaign=Badge_Grade)
[![Build Status](https://api.travis-ci.org/inflatone/job4j.ee.svg?branch=task_3786)](https://travis-ci.org/inflatone/job4j.ee/branches)
[![codecov](https://codecov.io/gh/inflatone/job4j.ee/branch/task_3786/graph/badge.svg)](https://codecov.io/gh/inflatone/job4j.ee/branch/task_3786)

Является простым однопользовательским приложением для управления заметками, хранящихся в базе данных. 
Для работы с ними используется Hibernate.
<strong>Находится в разработке. На данный момент не представляет особого интереса в плане функциональности.</strong>

### При реализации использовано:
* JDK 11, Tomcat 9, Maven 3
* Maven plugins: <a href="https://www.liquibase.org/documentation/maven/index.html">Liquibase</a>, <a href="https://github.com/heroku/webapp-runner">WebApp runner</a> (via <a href="https://maven.apache.org/plugins/maven-dependency-plugin/">Dependency plugin</a>), <a href="https://maven.apache.org/enforcer/maven-enforcer-plugin/">Enforcer</a> (dependency convergence)
* Hibernate
* PostgreSQL, <a href="http://hsqldb.org/">HSQLDB</a> (for test and demo modes)
* JUnit 5 with Extension integration (<a href="https://site.mockito.org/">Mockito</a>, <a href="https://github.com/JeffreyFalgout/junit5-extensions/tree/master/guice-extension">Guice</a>), <a href="http://www.vogella.com/tutorials/AssertJ/article.html">AssertJ</a>, <a href="https://github.com/skyscreamer/JSONassert">JSONassert</a>
* Front-end 
    * <a href="https://jquery.com/">jQuery</a>, <a href="https://datatables.net/examples/data_sources/ajax">DataTables</a>
    * <a href="https://getbootstrap.com/">Bootstrap</a>, <a href="https://fontawesome.com/">Font Awesome</a> icons, <a href="https://ned.im/noty/#/">Noty</a> notification library
* <a href="https://docs.travis-ci.com/user/tutorial/">Travis CI</a>, <a href="http://https://codecov.io/">Codecov</a>,  <a href="https://www.codacy.com/product">Codacy</a>

#### Планы по улучшению
* написать тесты :-(
* заменить синглтоны на контекст (same old Google Guice required)
* заменить PostreSQL на in-memory DB (H2, HSQLDB)
* переделать работу с Hibernate через JPA
* добавить внешнее управление транзакциями (Bitronix)
* попробовать прикрутить сервисы (например, заметочник рассылает уведомления на email чз другой сервис)
* добавить многопользовательский режим

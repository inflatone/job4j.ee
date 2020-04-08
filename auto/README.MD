# Web-приложение «Площадка по продаже машин»

[![Codacy Badge](https://api.codacy.com/project/badge/Grade/df661ff60cef47ceb6e566e9e207150c?branch=task_2092)](https://www.codacy.com/manual/inflatone/job4j.ee??bid=17350698)
[![Build Status](https://api.travis-ci.org/inflatone/job4j.ee.svg?branch=task_2092)](https://travis-ci.org/inflatone/job4j.ee/branches)
[![codecov](https://codecov.io/gh/inflatone/job4j.ee/branch/task_2092/graph/badge.svg)](https://codecov.io/gh/inflatone/job4j.ee/branch/task_2092)
[![Heroku](https://heroku-badge.herokuapp.com/?app=autosane)](http://autosane.herokuapp.com/)

Осуществляет многопользовательский доступ к объявлениям о продаже машин (c авторизацией и правами доступа на основе ролей).
Позволяет пользователям просматривать все доступные объявления, а также фильтровать их по выбранным критериям. 
Имеется возможность по созданию/редактированию/удалению собственных объявлений.
Администраторы имеют возможность работать с объявлениями любого пользователя.

### При реалиазации использовано:
* JDK 11, Tomcat 9, Maven 3
* Maven plugins: <a href="https://github.com/heroku/webapp-runner">WebApp runner</a> (via <a href="https://maven.apache.org/plugins/maven-dependency-plugin/">Dependency plugin</a>), <a href="https://maven.apache.org/enforcer/maven-enforcer-plugin/">Enforcer</a> (dependency convergence)
* Spring MVC (REST via Jackson) / Spring Security
* Spring ORM (Hibernate vendor), Hibernate validator
* PostreSQL / HSQLDB (default)
* JUnit 5, Spring (Security) Test
* Front-end (all provided via <a href="https://www.webjars.org/">Webjars</a> and located with Spring <a href="https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/servlet/resource/WebJarsResourceResolver.html">WebJarsResourceResolver</a>):
    * <a href="https://jquery.com/">jQuery</a>, <a href="https://github.com/phstc/jquery-dateFormat">jquery-dateFormat</a>, <a href="https://datatables.net/examples/data_sources/ajax">DataTables</a>
    * <a href="https://getbootstrap.com/">Bootstrap</a>, <a href="https://eonasdan.github.io/bootstrap-datetimepicker/">Bootstrap DateTimePicker</a>
    * <a href="https://fontawesome.com/">Font Awesome</a> icons
    * <a href="https://ned.im/noty/#/">Noty</a> notification library
* <a href="https://github.com/qos-ch/logback">Logback</a> via <a href="https://github.com/qos-ch/slf4j">SLF4J</a>
* <a href="https://docs.travis-ci.com/user/tutorial/">Travis CI</a>, <a href="http://https://codecov.io/">Codecov</a>,  <a href="https://www.codacy.com/product">Codacy</a>
* <a href="https://devcenter.heroku.com/categories/java-support">Heroku</a> deployed
### Чтобы запустить:
* Build and run `executable war` on the command line:
    * launch `demo-auto.bat` from root (installed Maven/JRE required)
* After the server is running, go to `localhost:8080`
* Press `Ctrl+C` on the command line to stop server

###### ...or visit demo on <a href="http://autosane.herokuapp.com/">heroku</a> (a first access may take some time because of application redeploying)

#### Технические замечания
* При разработке постарался применять базовые принципы REST:
    * обмен сообщениями между сервером и клиентом как JSON
    * использование существительных в названии ресурсов, выстраивание из них иерархии
    * PUT/DELETE наравне с GET/POST по необходимости
    * максимальное разделение функций сервера и клиента, в т.ч. динамическое формирование ссылок для управления ресурсами на стороне сервера только при наличии соотв. прав доступа
* Сделал динамическое добавление фильтров к запросам (CriteriaQuery's Predicate) при наличии переданных с клиента параметров
* Для добавления ссылок на ресурсы использую Transfer Object,
результат работы обработчиков ошибок (@ExceptionHandler) так же заворачиваю в специальный TO
#### Планы по улучшению
* поменять Spring ORM на Spring Data JPA
* добавить кеширование
* окончательно отделить серверную часть от клиентской
* добавить автоматическую фильтрацию неактуальных (=completed) постов для страницы объявлений
* сделать возможность быстрого сброса фильтров по-отдельности;
добавить возможность выбора нескольких элементов в списочных фильтрах (либо ни одного, по-умолчанию)
* разобраться с ошибкой при загрузке изображений > 5MB (должен возвращать ошибку валидации, сейчас на Хероку уходит в 503)
* добавить в Domain города и страны, сделать фильтрацию по таким кейсам
* поправить вёрстку при изменении размера экрана

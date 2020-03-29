call mvn -B -P !main,!liquibase,production,hsqldb -DskipTests=true -pl vacancy/webapp -am clean package
call java -cp vacancy/webapp/target/dependency/* webapp.runner.launch.Main vacancy/webapp/target/*.war --enable-naming


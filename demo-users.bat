call mvn -B -s settings.xml -DskipTests=true clean package
call java -cp users/target/dependency/* webapp.runner.launch.Main users/target/*.war --enable-naming
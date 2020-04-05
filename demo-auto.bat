call mvn -B -s settings.xml -DskipTests=true clean package
call java -cp auto/target/dependency/* webapp.runner.launch.Main auto/target/*.war --enable-naming
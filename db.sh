docker run --name mysql-container \
  -e MYSQL_ROOT_PASSWORD=password \
  -e MYSQL_DATABASE=grep_db \
  -p 3306:3306 \
  -d mysql:latest
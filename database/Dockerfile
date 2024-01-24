FROM mariadb:latest

COPY setup.sql /docker-entrypoint-initdb.d/
ENV MARIADB_ROOT_PASSWORD=123
EXPOSE 3306
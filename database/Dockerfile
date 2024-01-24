FROM mariadb:latest

COPY setup.sql /docker-entrypoint-initdb.d/
ENV MARIADB_ROOT_PASSWORD=""
EXPOSE 3306
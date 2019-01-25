FROM maven
COPY ./src /app/src
COPY ./migrations /app/migrations
COPY ./mybatis-migrations-3.3.1 /usr/local/mybatis/mybatis-migrations-3.3.1
COPY ./migrations/environments/development.docker.properties /app/migrations/environments/development.properties 
COPY ./src/main/webapp/WEB-INF/spring-config/jdbc.docker.properties /app/src/main/webapp/WEB-INF/spring-config/jdbc.properties 
COPY ./src/main/webapp/WEB-INF/log4j.docker.properties /app/src/main/webapp/WEB-INF/log4j.properties
COPY ./pom.xml /app
COPY ./wait_for_it.sh /app
COPY ./build.sh /app
RUN chmod 775 /app/build.sh
ENV MIGRATIONS_HOME=/usr/local/mybatis/mybatis-migrations-3.3.1
ENV MIGRATIONS=$MIGRATIONS_HOME/bin
ENV PATH=$MIGRATIONS:$PATH
WORKDIR /app
CMD mvn tomcat7:run
EXPOSE 8080

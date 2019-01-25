# 编译运行

## 数据库配置

数据库使用 Mybatis migrations 进行管理，相应文件存放目录为 migrations。

1. 创建数据库

    数据库编码请使用：`utf8_general_ci`，数据库引擎请使用：`InnoDB`

2. 安装 Mybatis migrations，能运行 `migrate` 命令

    <http://www.mybatis.org/migrations/installation.html>

3. 将连接mysql的jar包复制到目录 migrations/drivers/ 下
4. 配置数据库连接信息，配置文件为： migrations/environments/development.properties
5. 进入 migrations 目录，执行相应的 mybatis migrations 命令创建或更新数据库
    - migrate bootstrap
    - migrate up

**配置注意事项**

mysql 默认不允许执行多条语句，配置 mysql 可执行多条语句或 migrations 不要一次执行。

mysql 允许多条语句执行，连接配置方式：

    jdbc:mysql://ip:port/dbname?useUnicode=true&characterEncoding=UTF-8&useSSL=false&allowMultiQueries=true

mybatis migrations 不要批量执行 SQL 配置（development.properties）：

    send_full_script=false

## 数据库连接配置

    数据库连接配置文件：`src/main/webapp/WEB-INF/spring-config/jdbc.properties`

## 日志配置

需要将 log4j 日志文件中的日志目录配置为运行机器上对应的目录：

    log4j日志配置文件：`src/main/webapp/WEB-INF/log4j.properties`

## 管理后台 UI 编译

管理后台 UI 使用了 vue + webpack，因此需要 node.js 环境，安装 node.js 之后进入管理后台 UI 目录 `adminui`，运行安装编译命令：

    cd adminui/
    npm install
    npm run build

管理后台编译之后，发布目录为：`src/main/webapp/page`

## 运行

使用 tomcat7-maven-plugin 插件来运行

    mvn tomcat7:run

# 目录说明

- `docs` 设计文档
- `migrations` mybatis-migrations目录
- `adminui` 管理后台UI
- `src` 源码目录
    - `main`
        - `java` java源码
        - `resources` 资源文件
        - `webapp` web目录
    - `test`
        - `java` 测试用例
        - `resources` 测试用资源文件
- `README.md`
- `pom.xml`

**src/main/java 源码目录 Java 包名空间说明**

- com.sdk4.biz.aote 常量/配置等
- com.sdk4.biz.aote.api 对 APP 端提供的 HTTP API 接口
- com.sdk4.biz.aote.bean 数据库实体类
- com.sdk4.biz.aote.controller 后台管理WEB及API接入层
- com.sdk4.biz.aote.dao 数据库操作DAO层
- com.sdk4.biz.aote.service 服务接口层
- com.sdk4.biz.aote.service.impl 服务接口实现
- com.sdk4.biz.aote.timer 定时任务
- com.sdk4.biz.aote.util 工具类
- com.sdk4.biz.aote.var 枚举类

**src/main/resources/sqlmap/aote/mysql/*.xml**

DAO层对应的 MyBatis Mapper xml 文件

**src/main/webapp web目录结构说明**

- static
    - libs （此目录下为 jQuery 等第三方js/css库）
    - css
    - js
    - images
- pages vue编译后UI
- WEB-INF
    - lib 依赖jar包，请根据 pom.xml 自行下载 `mvn dependency:copy-dependencies`
    - conf 系统参数配置（运行参数等配置信息）
    - spring-config spring配置文件(spring bean/数据连接等)
    - views 后台运营velocity模板文件
    - web.xml

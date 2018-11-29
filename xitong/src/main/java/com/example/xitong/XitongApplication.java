package com.example.xitong;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@MapperScan("com.example.xitong.common.dao")
@EnableCaching/*使用缓存默认配置*/
public class XitongApplication {

	public static void main(String[] args) {
		SpringApplication.run(XitongApplication.class, args);
	}
}
/*MySql and orcal pagerHelper：MySQL use limit，later folowing startNum and totalNum，orcal is use rownum byself,count use startNum and endNum
*Java reflection，program can use a  class's method and property when this program is working;
*transaction's delimit and realize machine-processed,a series unitions thing operation ,must be   by all do or must be by all not do;
* Spring's declarative transaction management is based on AOP.
* The essence is to intercept the method before and after,
* then create or join a transaction before the target method starts,
* submit or roll back the transaction according to the execution after the target method is executed.
*
* Common collection and their respective characteristics.
The two top-level interfaces of the collection are: Collection and Map
There are two commonly used interfaces under the Collection, List and set, where List can store repeating elements, elements are ordered (the access order is the same), and the specified elements can be obtained through the List footer; Set cannot have repeating elements, and elements are unordered.
In the List interface, there are three more commonly used classes: ArrayList, Vactor, and LinkedList.
ArrayList : Threads are not safe, and the query on elements is fast.
Vector : Thread-safe, there is a way to extract elements: Enumeration, but has been replaced by ArrayList
LinkedList: A linked list structure that adds and removes elements quickly.
In the Set interface, there are two commonly used classes: HashSet, TreeSet:
HashSet: To ensure the uniqueness of the element, you need to override the equals and hashCode methods in the Object.
TreeSet: Stores elements in a binary tree structure, which sorts the elements.
The Map interface is characterized by the fact that elements appear in pairs, in the form of keys and values, and keys must be unique: common classes are: HashMap, Hashtable, TreeMap
HashMap: Threads are not safe, etc., allowing the null value of the null key to be stored.
Hashtable: thread-safe, does not allow null keys to be stored.
TreeMap: You can sort keys (to achieve the same sorting method as TreeSet).
What is the difference between an interface and an abstract class?
Abstract classes and interfaces cannot be instantiated directly. To instantiate, abstract class variables must point to subclass objects that implement all abstract methods. Interface variables must point to class objects that implement all interface methods.
Abstract classes are to be inherited by subclasses, and interfaces are implemented by classes.
The interface can only be declared as a method, and the method can be declared in the abstract class, or it can be implemented as a method.
The variables defined in the interface can only be public static constants, and the variables in the abstract class are ordinary variables.
Abstract methods can only be declared, can not be achieved, the interface is the result of the design, the abstract class is the result of the refactoring
There can be no abstract methods in abstract classes
What are the two cores of Spring?
IOC (DI)
In fact, IOC and DI are two different expressions of the same concept. The application depends on the external object provided by the container.
 The container injects its dependent external resources into the application at runtime;
  when an object is called, its The dependent objects are injected by the container.
advantage
First, centralized management of resources, configurable and manageable resources, and reduced complexity of object relationship maintenance.
Second, it reduces the degree of dependence on both sides of the resource, which is what we call coupling.
AOP
AOP uses a technique called "crosscutting" to deconstruct the inside of a packaged object,
encapsulate the common behavior that affects multiple classes into a reusable module,
and name it Aspect. The so-called "aspect", in a nutshell,
 is the part that encapsulates the logic that is not related to the business but is called by the business module.
 In order to reduce the system's repetitive code, reduce the coupling between modules, and help system maintenance.
Spring transaction management uses aop
Mybatis # and $ difference
# Treat the incoming data as a string, and add a double quote to the automatically passed data.
#Methods can greatly prevent sql injection.
The $ method does not prevent Sql injection.
The $ method is generally used to pass in database objects, such as incoming table names.
Jdbc steps
You can follow the steps below when doing JDBC operations:
1, load the database driver, the driver needs to be configured into the classpath when loading
2, connect to the database, through the Connection interface and DriverManager class
3, the operation of the database, through the Statement, PreparedStatement, ResultSet three interfaces
4, close the database, in the actual development of the database resources are very limited, must be closed after the operation

throwable:1、Error (jvm error or ThreadDeath)
			2、Exception
				RuningTime Exception and checked exception(IOException and SQL exception)
			 1.	NullPointerException 2.FormatDateException 3.ArraryIndexOfBoundException 4.ClassCastException 5.ArithmeticException
			 6.FileNotFoundException 7.NoSuchMethodException 8.OutOfMemoryError
			 垃圾回收可以有效的防止内存泄露，有效的使用空闲的内存。
			算法：引入计数法，根搜索法，标记清楚法（产生断续的储存空间）、复制算法（解决标记清除法的漏洞，但是效率低），标记整理、分代收集

			final static :use final xiushi de can not  change ;static bianliang shuxing fa fang daimakuai  neibu class;

RabbitQM
	1.Vhost
	2.Binding
	3.ExChange
	4.Queues

	1．SVN
1、 管理方便，逻辑明确，符合一般人思维习惯。
2、 易于管理，集中式服务器更能保证安全性。
3、 代码一致性非常高。
4、 适合开发人数不多的项目开发。

1、 服务器压力太大，数据库容量暴增。
2、 如果不能连接到服务器上，基本上不可以工作，看上面第二步，如果服务器不能连接上，就不能提交，还原，对比等等。
3、 不适合开源开发（开发人数非常非常多，但是Google app engine就是用svn的）。但是一般集中式管理的有非常明确的权限管理机制（例如分支访问限制），可以实现分层管理，从而很好的解决开发人数众多的问题。

2．Git

1、适合分布式开发，强调个体。
2、公共服务器压力和数据量都不会太大。
3、速度快、灵活。
4、任意两个开发者之间可以很容易的解决冲突。
5、离线工作。

1、学习周期相对而言比较长。
2、不符合常规思维。
3、代码保密性差，一旦开发者把整个库克隆下来就可以完全公开所有代码和版本信息
* */

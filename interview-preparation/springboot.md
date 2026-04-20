# Spring Boot

## Spring ioc

In a traditional application, we manually create objects and manage their dependencies using the new keyword. This cause tight coupling. With Spring IoC, the control is inverted: the framework takes over the responsibility of object management. Code doesn't create dependencies; instead, the Spring container "injects" them when an object is created. This process is known as Dependency Injection (DI), which is a implementation of the IoC principle

spring container init bean by scanning annotation, store in container and wherever need dependency, it inject instance bean from container to

2 type of container, BeanFactory and ApplicationConext, ApplicationContext is prefer because it include all BeanFactory's features and some more like ApplicationEvent or i18

Benefits: Decoupling components, which allows developers to focus on writing business logic while the framework handles the boilerplate task of wiring components 

3 DI types:
- Constructor injection: dependencies are provided through class constructor. Ensures required dependencies are initialized, supports final field for immutability. Best for mandatory dependencies
- Setter injection: setter method is automatically called to inject by Spring container immediately after a bean instance is initialized. Object in an incomplete state during the short period between its instantiation and the completion of all dependency injections, fields cannot be final because setter require mutable object. Best for optional dependencies
- Field injection: dependencies are injected directly into fields (@Autowired). Hard to test because require reflection, specifically Mockito must use Java Reflection to break encapsulation, changing the private field's accessibility to public temporarily (Mockito set the value of a field (the mock object) directly on the target object without relying on standard methods like constructors or setters) + cannot final. Best for quick prototyping

spring bean là các đối tượng được quản lý bởi spring container. Bean scopes:
- Singleton (default): best for stateless instance like service or repository
- Prototype: new bean instance is created every time the bean is requested. Best for stateful bean
- Request, Session, WebSocket: new bean instance is created for each HTTP request, HTTP session, WebSocket session
- Application: similar to singleton but Application scoped bean is singleton per ServletContext, singleton scoped bean is singleton per ApplicationContext. There can be multiple application contexts for single application

Ways to get correct bean of same type:
- @Qualifier
- Field name match bean name

## Spring aop

while spring ioc seperate component dependency, aop seperate concerns

Spring AOP enable Aspect-Oriented Programming to handle cross-cutting concerns (e.g., logging, security, transaction management) separately from the core business logic and allow reusability. 
For ex, instead of putting the logging code directly in a service function, we create a logging aspect and declare where it should be applied => make the code for this concern reusable and not disrupt business logic

Concepts:
- Aspect: unit that encapsulates a concern, specifically a class annotated with @Aspect that contains advice and pointcuts
- Join Point: A specific point during the execution of a program where an action can be applied. In Spring AOP a join point is always the execution of a method
- Advice: piece of code that you want to execute at a specific point 
- Pointcut: an expression that tells where to apply a piece of code

```
@Around("execution(* com.app.service.*.*(..))")
public Object measureTime(ProceedingJoinPoint joinPoint) throws Throwable {
    System.out.println("Method: " + joinPoint.getSignature().getName());
    System.out.println("Args: " + Arrays.toString(joinPoint.getArgs()));

    long start = System.currentTimeMillis();
    Object result = joinPoint.proceed();
    System.out.println("Time: " + (System.currentTimeMillis() - start));
    return result;
}
```

Here, the advice defines what code runs and when (before the method)

Pointcut expression defines which methods are affected: ```execution(* com.app.service.*.*(..))```

JoinPoint represents the actual runtime context to give details like method name, arguments, ...

JoinPoint → read-only
ProceedingJoinPoint → used in @Around, allows proceed()

types of Advice:

- Before Advice: Runs before the join point (method execution). It can’t prevent the method from executing, unless it throws an exception.

- After Advice: Runs after the method execution, regardless of the outcome (normal or exception). Example: Cleaning up resources after method execution.

- After Returning Advice: Runs after the method completes normally (no exception thrown). Example: Updating the cache with method return value.

- After Throwing Advice: Runs if the method throws an exception. Example: Logging error details when an exception occurs.

- Around Advice: The most powerful type of advice. It surrounds the method execution. It can modify the method’s input or output, and even prevent the method from being called. Example: Transaction management

Spring AOP is proxy-based AOP implementation in Spring Framework, and it only supports method execution join points, while AspectJ is a comprehensive AOP framework that supports more advanced AOP features

## Spring JPA

Hibernate is an implementation of JPA specifications that provides the actual code to perform database operations, while JPA defines the rules and interfaces for high-level abstraction

types of jpa query:

- JPQL (Java Persistence Query Language): uses Entity and field names
  ```
  @Query("SELECT u FROM User u WHERE u.name = :name")
  List<User> findByName(String name);
  ```

- Native query: use raw sql
  ```
  @Query(value = "SELECT * FROM users WHERE name = :name", nativeQuery = true)
  List<User> findByNameNative(String name);
  ```

- Query Method
  ```
  List<User> findByName(String name);
  ```

- Criteria API: programmatic way to build queries dynamically

## Transaction

Spring uses proxy-based AOP to start transaction, commit if success and rollback if exception

@Transactional can be applied at class level or method level, and method-level always overrides class-level

@Transactional does not work on private or protected methods because Spring uses proxy-based AOP, which can only intercept public methods

If we use self invocation, inner transaction will behave non-transactionally because Spring uses proxies for @Transactional, and calling a method inside the same class bypasses the proxy, so the transaction logic is never applied

### Propagation types

defines behavior when a transactional method is called within another transactional method

```
@Transactional
public void serviceA() {
    serviceB();
}

@Transactional(propagation = Propagation.REQUIRED)
public void serviceB() {
    // 
} 
```

#### REQUIRED (default)

Joins the existing transaction if one exists. If no transaction exists, a new one is created

If create new transaction, exception in inner method will only roll back inner transaction, outer method behave non-transactionally

If join existing transaction, even inner or outer fails, the entire transaction roll back but Spring may throw UnexpectedRollbackException if we dont manage properly.

For example, inner method fails and outer method wrap inner method inside a try-catch and no rethrow. Because when inner method fail, Spring marks the transaction as rollback-only so even outer method catches the exception and try to commit, Spring detect this and throws UnexpectedRollbackException. So to avoid exception, just don't catch it

#### REQUIRES_NEW

Always starts a new transaction, the existing transaction (if any) is suspended. Once the method completes, the original transaction is resumed

rollback in nested function affects only that transaction, not the outer one

If inner function is not wrapped in try-catch, exception is propagated to the outer method and outer transaction will roll back as well, so we need to catch and avoid rethrowing

commonly used for audit logging or external calls, where failure in a sub-operation should not affect the main business transaction

#### MANDATORY

Requires an existing transaction to join and throw exception if not exist

#### NEVER

Must not have existing transaction, otherwise throw exception and always runs without transaction

#### SUPPORTS

Joins the existing transaction if one exists. If no transaction exists, run without transaction

#### NOT_SUPPORTED

Suspends any active transaction and runs without transaction

#### NESTED

If no transaction exists, creates new one. If a transaction exists, it creates a savepoint within the current transaction.

If the nested method fails, only changes after the savepoint are rolled back, not the entire transaction.

Hibernate does not support savepoints so encounter NestedTransactionNotSupportedException

### Note

When propagation is set to REQUIRES_NEW or NOT_SUPPORTED, suspended transaction is not re-executed but simply paused and resumed later

Spring transactions only roll back for unchecked exceptions (subclasses of RuntimeException or Error). They do not automatically roll back for checked exceptions (subclasses of Exception)

### Isolation level

Dirty read: One transaction reads uncommitted changes from another transaction

Non-repeatable read: A transaction reads a row multiple times and gets different result due to UPDATE or DELETE by other transactions

Phantom read: A transaction reads a set of rows by a range query multiple times and gets different result due to insertions or deletions by other transactions

difference between Phantom read and Non-repeatable read: root cause for Non-repeatable read is modification so value is different, while root cause for Phantom read is insertion or deletion so the number of records is different

DEFAULT: Use the isolation level used by the database itself, for example MySQL use repeatable read, Postgre use read committed

READ_UNCOMMITTED: transaction can read the data of another uncommitted transaction

READ_COMMITTED: transaction cannot read data until another transaction is committed

REPEATABLE_READ: when transaction is opened, modification operations are no longer allowed.

SERIALIZABLE: the highest isolation level. Under this level, transactions are executed sequentially

```
+----------------+------------+---------------------+--------------+
|isolation level | dirty read | non-repeatable read | phantom read |
+----------------+------------+---------------------+--------------+
|Read uncommitted| ×(unsolved)|   ×                 |  ×           |
|Read committed  | √(solved)  |   ×                 |  ×           |
|Repeatable read | √          |   √                 |  ×           |
|Serializable    | √          |   √                 |  √           |
+----------------+------------+---------------------+--------------+
```

### Lock

Optimistic locking assumes conflicts are rare and does not lock data. Instead, it uses a version field to detect conflicts during update. If a conflict occurs, the transaction must retry. Optimistic locking is suitable for low-contention environment, long-running transaction and read-heavy systeam. 

Pessimistic locking assumes conflicts are mostly and locks the data when it is read, preventing other transactions from acquiring lock until the lock is released. Pessimistic locking is suitable for high-contention environment, short-live transaction and critical consistency

NONE: the default used by Spring Data findById() and it is the only lock mode that can be used outside of an transaction

OPTIMISTIC (or READ): Ensures that no other transaction write between the time it is read and the time the current transaction commits

OPTIMISTIC_FORCE_INCREMENT (or WRITE): Similar to OPTIMISTIC, but always increase the @Version column even if row is not modified

PESSIMISTIC_READ: Obtains a shared lock. Other transactions can read the data but cannot write until the lock is released (select for share)

PESSIMISTIC_WRITE: Obtains an exclusive lock . Other transactions cannot read and write until the lock is released (select for no key update)

PESSIMISTIC_FORCE_INCREMENT: Similar to PESSIMISTIC_WRITE but always increase the @Version column if present even if row is not modified

## Transaction manager

Transaction Manager is responsible for managing the lifecycle of a transaction, including starting, committing, and rolling back. Spring abstracts transaction management through PlatformTransactionManager, allowing us to switch between different persistence technologies without changing business logic

interface PlatformTransactionManager include 3 method getTransaction, commit and rollback
When a method with annotation transactional is invoked, Spring use getTransaction to check if a transaction already exists, then based on propagation type, it join the existing or create a new one, then execute method and finally commit or rollback

types of transaction manager:
- DataSourceTransactionManager: work with JDBC
- JpaTransactionManager: work with JPA/Hibernate, manage EntityManager and Persistence context
- JtaTransactionManager: for with multi-database, implement distributed transactions using two-phase commit. However, due to performance and complexity issues, modern systems often prefer patterns like Saga

## Scheduled tasks

Built-in: @EnableScheduling and @Scheduled with cron expression or fixed delay at method level to schedule task. TaskScheduler will manage to run these task in other thread

Quartz: Define task bằng cách tạo 1 class implement Quartz Job và Override method execute, khi muốn schedule task này thì tạo JobDetail và Trigger. Quartz quản lý job bằng cách lưu vào memory hoặc db -> set JDBC trong application.yml. Quarzt hỗ trợ tự động tạo table bằng cách set initialize-schema always nhưng có nhược điểm là mỗi lần restart server sẽ drop và recreate table nên mất hết các data -> Tạo file sql create table if not exist và config quartz point tới file này

## Spring batch

## internationalization and localization

- Define message properties files for different locales, such as messages_en.properties for English, messages_fr.properties for French. Ex: Put messages_en.properties at src/main/resources/i18n
- Config MessageSource, specifying the base name of the message properties files. Ex: i18n/messages
- In code, retrieve the appropriate message based on the current locale. Ex messageSource.getMessage("message", Object... args, Locale.forLanguageTag("en"))

## Junit

Unit test is testing an independent logic like a method or a class. Framework code, external system or simple getter/setter should not be unit tested

@Test: marks a method as a test case

@BeforeEach: A method annotated with @BeforeEach runs before the execution of each test method

@AfterEach: A method annotated with @AfterEach runs after the execution of each test method

Assertions: assertEquals, assertThrows, assertTrue...

For a void method, we do not veriy return value, instead we verify side effects or interactions

When an object has many fields

- Setup expected object with builder pattern, factory methods or use EasyRandom to fill large objects with random data

- don’t assert field-by-field manually, instead use AssertJ for recursive comparison or partial assertions on critical fields
```
assertThat(actual)
    .usingRecursiveComparison()
    .isEqualTo(expected);
```

## Mockito

Mock used to bypass what not need to test. Sample scenario: When testing the OrderService, you want to verify its logic for different payment outcomes, but run test with live payment provider is unreliable, so create a mock of the PaymentGateway to control its responses and bypass the actual external communication

@InjectMocks creates an instance of the class and injects all other dependencies marked with @Mock or @Spy into it 
when().thenReturn() is used to define the behavior of those mocked dependencies

```
@ExtendWith(MockitoExtension.class)
class MyServiceTest {
    @Mock
    private DependencyRepository repo; // 1. Create a Mock

    @InjectMocks
    private MyService service; // 2. Inject 'repo' into 'service'

    @Test
    void testServiceMethod() {
        // 3. Define behavior
        when(repo.getData()).thenReturn("Mock Data"); 
        
        String result = service.process();
        assertEquals("Mock Data", result);
    }
}
```

## Testing mindset

Test pyramid
```
       E2E
    Integration
  Unit (most)
```

Focus on testing behavior, not implementation, meaning validating expected output of a method rather than its internal implementation. This makes tests less dependent on how the code is written and we can refactoring code without breaking tests

Coverage is percentage of test that cover line of written code, but meaningful tests are more important than just high percentage

Naming convention:
- class: use the same class name with suffix Test for unit test and IntegrationTest or IT for integration test
- method: use descriptive name that follow Arrange-Act-Assert or Given-When-Then patterns. Both follow the same idea of separating setup, execution, and verification but AAA is more about technical, while another one is more about business

methodName_condition_expectedResult: withdraw_validAmount_shouldDecreaseBalance, withdraw_invalidAmount_shouldThrow

givenCondition_whenAction_thenExpectedResult: givenValidBalance_whenWithdraw_thenBalanceDecreases, givenInsufficientBalance_whenWithdraw_thenThrowException

When test async code, we should be careful because async code runs in another thread so test may finish before async logic completes

For method that does return data, best practice is to return CompletableFuture and we can call get() to block current thread until result is return

It's harder for method that not return data, should use tool like Awaitility to wait with timeout
```
await().atMost(5, TimeUnit.SECONDS).until(() -> conditionIsTrue());
```

integration test is testing interaction between components like a restapi endpoint. Choice between local/dev db and TestContainer is a tradfe off. dev db is not isolated, local db may have different timezone config and both require manual cleanup. While TestContainer guarantee similarity, isolation and no munual cleanup but it's slightly slower and may need to setup data

### Testing inside CI environment

Consider a project of multiple microservices, some depending on 2 or 3 other services.

spin up the virtual service on-demand only when a test starts and destroy it immediately after. This approach ensure clean state and cost-effective because we only pay for computing resources during the minutes tests are running

For 3rd-party APIs we don't control, package virtual services like a WireMock configuration as a Docker container. This only simulate the chaos of a real network like latency or 503 error but not simulate real interaction. That's why we should have an end-to-end test which runs immediately after service is deployed to the development environment to verify real interaction

For internal service, spin up the actual Docker image of the dependency to verify real interaction but this is heavy

## Other

@Bean is only recognized when their containing class is registered in the Spring context with annotation @Configuration

Tomcat is default embedded web server. When run a Spring Boot application, it starts up an instance of Tomcat within the same process. spring-boot-starter-web dependency automatically includes spring-boot-starter-tomcat

@Bean only for method-level, @Component for class-level, @Autowired for both field and method-level 

@SpringBootApplication annotation is the combination of @Configuration , @EnableAutoConfiguration và @ComponentScan

Spring Boot handle dependency management by providing starter dependencies and these starter already include necessary library and config

override the default properties in Spring Boot by using command-line arguments, environment variables or application.yml. Priority will be command-line arguments/environment variables > application.yml > default values

profile allow to define different config for different envinroment like dev, stg, ...

To enable a specific profile, use `spring.profiles.active=development,production`

Spring Actuator enable production-ready by providing monitor function with built-in endpoint like /health , /info , /metrics, ...

config caching with annotation like @EnableCaching and @Cacheable, @CachePut, @CacheEvict to specify caching behavior

Composite key are used in many-many relationship, combine annotation @Embeddable with @EmbeddedId

## Liquidbase

use a master changelog file to manage all migrations in one place
```
databaseChangeLog:
  - includeAll:
      path: db/changelog/migrations/
```

use command mvn liquibase:diff to generate diff files and it will be located at db/changelog/migrations/changelog.yaml

Then modify it manually if need and rename as 02_add_user_table.yaml for example

Then apply changes: mvn liquidbase:update

For local development we can use liquibase.properties but for other env, inject env var

Note that each time update liquibase.properties or pom.xml file, need to re run mvn clean install

## Scenario Based Interview Questions

what if the 3th party provider use different case (like snake_case) for their apis

I wouldn’t change my Java naming conventions but instead handling at the Jackson layer using naming strategy. Specifically, we can use global config or per-dto config to keep internal APIs unaffected. Sample:

application.yml for global
```
spring:
  jackson:
    property-naming-strategy: SNAKE_CASE
```

per-dto:
```
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserDto {
    private String firstName;
    private String lastName;
}
```

For complex systems, I’d keep third-party DTOs separate from internal models and map between them using Mapstruct to avoid tight coupling

```
External API DTO (snake_case)
        ↓
     Mapper
        ↓
Internal Domain Model (camelCase)
```
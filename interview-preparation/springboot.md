# Spring Boot

## Spring ioc

In a traditional application, we manually create objects and manage their dependencies using the new keyword. This cause tight coupling. With Spring IoC, the control is inverted: the framework takes over the responsibility of object management. Code doesn't create dependencies; instead, the Spring container "injects" them when an object is created. This process is known as Dependency Injection (DI), which is a implementation of the IoC principle

spring container khởi tạo các bean bằng cách quét các annotation, lưu vào container và ở chỗ nào có dependency thì sẽ lấy instance bean trong đó ra để inject vào (singleton).
Có 2 loại là BeanFactory và ApplicationConext, ApplicationContext thường dc sử dụng nhiều hơn vì nó bao gồm toàn bộ các chức năng của BeanFactory và các chức năng mở rộng như quốc tế hóa (i18), ApplicationEvent, …

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

1. What is the difference between Spring and Spring Boot?

  Spring là 1 java framework bao gồm nhiều module và thư viện như spring core, spring mvc, spring data, …
  Spring Boot là sub project của spring framework, tập trung vào convention over configuration bằng cách cung cấp sẵn các default và auto-configuration

2. What is the role of the @SpringBootApplication annotation?

  Là sự kết hợp của 3 annotation @Configuration , @EnableAutoConfiguration và @ComponentScan giúp enables auto configuration, component scanning và application context 

3. Explain auto-configuration in Spring Boot ?

  Auto config cho các common component như web framework, security, ... Hoạt động bằng cách analyze classpath và quyết định bean nào được tạo. We can define more config and enable or disable it by using @Conditional or @ConditionalOnProperty (Only create this bean if a specific property exists and/or has a certain value)

4. How does Spring Boot handle dependency management?

  Bằng cách cung cấp sẵn các starter dependencies. Các starter dependencies này include sẵn các thư viện cần thiết và config

5. How can you override the default properties in Spring Boot?

  Specify value các properties muốn overide trong file application.properties hoặc application.yml. Ngoài ra còn có thể dùng command-line arguments hoặc environment variables. Thứ tự ưu tiên là command-line arguments hoặc environment variables > application.properties hoặc application.yml > default values.

6. Explain profiles in Spring Boot.

  Profile cho phép define các config khác nhau tương ứng với các môi trường dev, stg, prod, ... và mình có thể active profile dựa trên các điều kiện

7. How can you enable a specific profile in Spring Boot?

  - set spring.profiles.active=development,production in application.properties hoặc application.yml
  - command-line argument: --spring.profiles.active=development
  - env variable: SPRING_PROFILES_ACTIVE

8. What is Spring Data JPA
  là sub project của spring data provide 1 lớp abstract on top của JPA giúp simplify database access

9. How can you handle exceptions in a Spring Boot application?

  - Controller-Level Exception Handling: annotate @ExceptionHandler 1 method trong controller class và specify loại exception, khi exception đó occur trong controller đó, cái hàm được annotated để handle exception sẽ được invoke
  - Global Exception Handling: Tạo 1 class được annotate @ControllerAdvice và implement các method với annotation @ExceptionHandler để handle exception

10. How does Spring Boot support database migrations?

  Spring cung cấp các db migration tool như flyway hoặc liquidbase. Define script bằng sql hoặc xml và spring sẽ tự động detect được các script đó và execute nó during application startup để đảm bảo db schema được sync up

11. What is the role of Spring Boot Actuator?

  Giúp ensure springboot app của mình production-ready bằng cách cung cấp các chức năng monitor và troubleshoot. Spring Boot Actuator offer các built-in endpoint để monitor application health, metrics, logging, tracing, ... như /health , /info , /metrics , /env , /loggers, ... Ngoài ra nó còn cho phép mình enxtend các chức năng bằng cách custom thêm endpoint, health indicator và metric collector, ...

12. How can you configure caching in a Spring Boot application?

  Springboot cung cấp annotation giúp tích hợp caching: @EnableCaching để enable caching support, @Cacheable, @CachePut và @CacheEvict ở method level để specify caching behavior như thêm, sửa và xóa cache

13. @Value

  Mình nên avoid dùng @Value để inject config từ file application vì nó k có tính chất quản lý tập trung, Vd như mình có thể inject aws key id ở nhiều file và khi mình rename thành aws key secret thì mình phải chỉnh lại ở nhiều file. Ngoài ra @Value cũng k có support built-in validation. Thay vào đó mình nên dùng @ConfigurationProperties và @Validated ở class-level

14. @Autowired

  - Trong unit test, khó inject vào private field để mock
  - Field được inject bởi @Autowired thì mutable nên có thể bị modify accidentally
  - ...
  Nên dùng constructor injection vì giúp mock injection dễ dàng bằng test constructor, constructor kết hợp với final field enforce immutable, ...

15. What is the purpose of the @Async annotation in Spring Boot?

  dùng để indicate 1 method được execute asynchronously ở 1 thread khác để k block calling thread

16. How can you schedule tasks in a Spring Boot application?

  Built-in: Combine annotation @EnableScheduling và @Scheduled ở method level để schedule task sử dụng cron expression, fixed delay, ... Spring run task bằng 1 thread khác được quản lý bởi TaskScheduler

  Quartz: Define task bằng cách tạo 1 class implement Quartz Job và Override method execute, khi muốn schedule task này thì tạo JobDetail và Trigger. Quartz quản lý job bằng cách lưu vào memory hoặc db -> set JDBC trong application.ymk. Quarzt hỗ trợ tự động tạo table bằng cách set initialize-schema always nhưng có nhược điểm là mỗi lần restart server sẽ drop và recreate table nên mất hết các data -> Tạo file sql create table if not exist và config quartz point tới file này

17. Connection pool

  reuse db connection instead of creating new one for each request

18. How does Spring Boot support internationalization and localization?

  - Define message properties files for different locales, such as messages_en.properties for English, messages_fr.properties for French. Ex: Put messages_en.properties at src/main/resources/i18n
  - Config MessageSource, specifying the base name of the message properties files. Ex: i18n/messages
  - In code, retrieve the appropriate message based on the current locale. Ex messageSource.getMessage("message", Object... args, Locale.forLanguageTag("en"))

19. How can you enable request logging in a Spring Boot application?

  logging.level.org.springframework.web=DEBUG 

20. Composite key

  in many many relationship, combine annotation @Embeddable with @EmbeddedId

## Junit
@Test: marks a method as a test case

@BeforeEach: A method annotated with @BeforeEach runs before the execution of each test method

@AfterEach: A method annotated with @AfterEach runs after the execution of each test method

Assertions: assertEquals, assertThrows, assertTrue...

## Mockito

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
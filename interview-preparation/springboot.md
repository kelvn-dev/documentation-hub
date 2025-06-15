# Spring Boot

spring container khởi tạo các bean bằng cách quét các annotation, lưu vào container và ở chỗ nào có dependency thì sẽ lấy instance bean trong đó ra để inject vào (singleton).
Có 2 loại là BeanFactory và ApplicationConext, ApplicationContext thường dc sử dụng nhiều hơn vì nó bao gồm toàn bộ các chức năng của BeanFactory và các chức năng mở rộng như quốc tế hóa (i18), ApplicationEvent, …

spring bean là các đối tượng được quản lý bởi spring container

Scenario Based Interview Questions

## Questions
1. What is the difference between Spring and Spring Boot?
2. What is the role of the @SpringBootApplication annotation?
3. Explain auto-configuration in Spring Boot ?
4. How does Spring Boot handle dependency management?
5. How can you override the default properties in Spring Boot?
6. Explain the concept of profiles in Spring Boot.
7. How can you enable a specific profile in Spring Boot?
8. What is Spring Data JPA
9. How can you handle exceptions in a Spring Boot application?
10. How does Spring Boot support database migrations?
11. What is the role of Spring Boot Actuator?
12. How can you configure caching in a Spring Boot application?
13. @Value
14. @Autowired
15. What is the purpose of the @Async annotation in Spring Boot?
16. How can you schedule tasks in a Spring Boot application?
17. Connection pool
18. How does Spring Boot support internationalization and localization?
19. How can you enable request logging in a Spring Boot application?
20. Composite key

## Answer
1. What is the difference between Spring and Spring Boot?

  Spring là 1 java framework bao gồm nhiều module và thư viện như spring core, spring mvc, spring data, …
  Spring Boot là sub project của spring framework, tập trung vào convention over configuration bằng cách cung cấp sẵn các default và auto-configuration

2. What is the role of the @SpringBootApplication annotation?

  Là sự kết hợp của 3 annotation @Configuration , @EnableAutoConfiguration và @ComponentScan giúp enables auto configuration, component scanning và application context 

3. Explain auto-configuration in Spring Boot ?

  Auto config cho các common component như web framework, security, ... Hoạt động bằng cách analyze classpath và quyết định bean nào được tạo. Nó dựa trên annotation @Conditional để enable hoặc disable các config. 

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
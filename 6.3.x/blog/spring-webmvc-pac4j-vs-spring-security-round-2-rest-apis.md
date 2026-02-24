---
layout: blog
title: spring-webmvc-pac4j vs Spring Security&#58; Round 2, REST APIs
author: Jérôme LELEU
date: February 2019
---

## 1) Introduction

Last year, I wrote a blog post on the [Spring Boot security and why you should choose the spring-webmvc-pac4j implementation over the Spring Security library](/blog/spring-boot-security-choose-spring-webmvc-pac4j.html). It was greatly inspired by a request from a customer looking for the right security library for his Spring Boot project.
It was really focused on UI authentication with the CAS protocol in action. The advantage was for the pac4j implementation.  
The funny thing is that the same customer asked me the same question to secure his REST APIs and I must admit that this new comparison would have been in favor of Spring Security without the latest spring-webmvc-pac4j evolutions.

First of all, I'd like to come back to definitions just to be sure UI and web services authentications mean the same to everybody:
- a UI authentication happens once, is made by a human and is saved in the web session
- a web service authentication happens for every request, is made by another application and is only saved in the current request.

These are core concepts in pac4j as web authentication methods (called clients) are indirect (UI authentications) or direct (web services authentications).

In any case, whether you deal with UI authentications or web services authentications, things are always easier in pac4j than in Spring Security because pac4j has only one mandatory concept for the authentication process (the client) while Spring Security has several: filter, provider, token, entry point...
pac4j has a lot of concepts too, but by default, you don't need to known them or implement them (you can still do that for customisations). And that's the huge difference!

## 2) Basic auth

Let's start by an easy example with the basic auth authentication:

With Spring Security ([https://www.baeldung.com/spring-security-basic-authentication](https://www.baeldung.com/spring-security-basic-authentication)):

```java
@Configuration
@EnableWebSecurity
public class CustomWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {
 
    @Autowired
    private MyBasicAuthenticationEntryPoint authenticationEntryPoint;
 
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
          .withUser("user1").password(passwordEncoder().encode("user1Pass"))
          .authorities("ROLE_USER");
    }
 
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
          .antMatchers("/securityNone").permitAll()
          .anyRequest().authenticated()
          .and()
          .httpBasic()
          .authenticationEntryPoint(authenticationEntryPoint);
 
        http.addFilterAfter(new CustomFilter(),
          BasicAuthenticationFilter.class);
    }
 
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

With spring-webmvc-pac4j:

```java
@Configuration
public class Pac4jConfig {

    @Bean
    public Config config() {
        DirectBasicAuthClient directBasicAuthClient = new DirectBasicAuthClient(new SimpleTestUsernamePasswordAuthenticator());
        Clients clients = new Clients(directBasicAuthClient);
        return new Config(clients);
    }
}
```

and

```java
@Configuration
@ComponentScan(basePackages = "org.pac4j.springframework.web")
public class SecurityConfig extends WebMvcConfigurerAdapter {

    @Autowired
    private Config config;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SecurityInterceptor(config, "DirectBasicAuthClient")).addPathPatterns("/basicauth/*");
    }
}
```

Configurations are quite similar.

## 3) JWT

Things get really bad for Spring Security when it comes to JWT (a very common authentication mechanism!) as there is no out-of-the-box support for it and
you must create your own provider, filter, etc. See: [https://www.codementor.io/hantsy/protect-rest-apis-with-spring-security-and-jwt-ms5uu3zd6](https://www.codementor.io/hantsy/protect-rest-apis-with-spring-security-and-jwt-ms5uu3zd6)

With spring-webmvc-pac4j, it remains very easy:

```java
@Configuration
public class Pac4jConfig {

    @Value("${salt}")
    private String salt;

    @Bean
    public Config config() {
 
        SecretSignatureConfiguration secretSignatureConfiguration = new SecretSignatureConfiguration(salt);
        SecretEncryptionConfiguration secretEncryptionConfiguration = new SecretEncryptionConfiguration(salt);
        JwtAuthenticator authenticator = new JwtAuthenticator();
        authenticator.setSignatureConfiguration(secretSignatureConfiguration);
        authenticator.setEncryptionConfiguration(secretEncryptionConfiguration);
        HeaderClient jwtClient = new HeaderClient("Authorization", "Bearer", authenticator);

        Clients clients = new Clients(jwtClient);
        return new Config(clients);
    }
}
```

## 4) REST APIs specificities

Nonetheless, there is something really different related to RESTful architecture and REST APIs: the same URL can be used for several operations, each associated with an HTTP method (GET, POST, DELETE...)  
While you can also do that in regular web applications, it's not so common.

For example ([https://spring.io/guides/tutorials/bookmarks/](https://spring.io/guides/tutorials/bookmarks/)):

```java
@RestController
class EmployeeController {

    private final EmployeeRepository repository;

    EmployeeController(EmployeeRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/employees")
    List<Employee> all() {
        return repository.findAll();
    }

    @PostMapping("/employees")
    Employee newEmployee(@RequestBody Employee newEmployee) {
        return repository.save(newEmployee);
    }

    @GetMapping("/employees/{id}")
    Employee one(@PathVariable Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new EmployeeNotFoundException(id));
    }

    ...
}
```

In *pac4j*, you could apply a different security mechanism for the same URL using the appropriate `Matcher` concept, but it made URLs definitions really cumbersome!

## 5) Security at the method level

Eventually, we copied Spring Security and its security at the method level. In Spring Security, you have several annotations to check security: `@Secured`, `@RolesAllowed`, `@PreAuthorize` and so on.

We chose to remain easier by providing only two annotations so far and without any expression language capabilities: `@RequireAnyRole` and `@RequireAllRoles`.

Like in Spring Security (and its `@EnableGlobalMethodSecurity`), you must not forget to enable these annotations by importing the appropriate configuration classes: `@Import({ComponentConfig.class, AnnotationConfig.class})`.

But then, you're good for your REST API:

```java
@GetMapping("/employees")
@RequireAnyRole("ROLE_ADMIN")
List<Employee> all() {
    return repository.findAll();
}
```

## 6) Conclusion

In its latest versions, spring-webmvc-pac4j is as ready as Spring Security to secure REST APIs.  
While remaining much easier.  
And we love to Keep It Simple Stupid!

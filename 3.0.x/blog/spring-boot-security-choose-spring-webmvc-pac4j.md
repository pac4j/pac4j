---
layout: blog
title: Spring Boot Security&colon; choose spring-webmvc-pac4j over Spring Security
author: Jérôme LELEU
date: January 2018
---

Recently, a client asked me to write a secured Spring Boot webapp sample to interact with his CAS server.
He requested me to use Spring Security and I proposed him to test _pac4j_ as well. He was reluctant first, but accepted to give it a try.

_pac4j_ is not only a security library, it’s a security framework/engine implemented for many environments.
Generally, for a Spring Boot webapp, the first insight is to use the [spring-security-pac4j](https://github.com/pac4j/spring-security-pac4j) library, which is the pac4j security capabilities adapted to Spring Security.
But it’s not the right choice for a new webapp, it only makes sense for legacy purposes.

A Spring Boot app is a Spring MVC webapp so it’s much better to use the [spring-webmvc-pac4j](https://github.com/pac4j/spring-webmvc-pac4j) library, which is the pac4j security capabitilites adapted to Spring Web MVC.
It's as powerful as _spring-security-pac4j_, but it's much easier.

I hadn’t planned to write a post to compare both solutions, but the end result was so significant that it’s worth the deal talking about it.

For sure, Spring Security is more popular than _pac4j_ and many people know about it (or at least they think so). Spring is a big company, which makes people reassured about the future of their products.
And they even provide commercial support if you really need that. That said, I'd be more than happy to be paid to support you with _pac4j_ :-)

But let's talk about the two Spring Boot sample webapps, the one using Spring Security and the other one using _spring-webmvc-pac4j_.

The startup class is similar in both cases:

```java
@SpringBootApplication
public class AppXDemo extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(AppXDemo.class);
    }

    public static void main(final String[] args) {
        SpringApplication.run(AppXDemo.class, args);
    }
}
```

The Thymeleaf templates and the controller also:

```java
@Controller
public class Application {

    @RequestMapping("/")
    public String root(Map<String, Object> map) {
        return index(map);
    }

    @RequestMapping("/index.html")
    public String index(Map<String, Object> map) {
        map.put("profile", getProfile());
        return "index";
    }

    @RequestMapping("/protected/index.html")
    public String protectedIndex(Map<String, Object> map) {
        map.put("profile", getProfile());
        return "protectedIndex";
    }
}
```

The difference is how you get the current authenticated user. In Spring Security, you have an authentication with a principal:

```java
private Authentication getProfile() {
   return SecurityContextHolder.getContext().getAuthentication();
}
```

In _pac4j_, you get a profile (in fact, you could get several profiles as several authentications can exist at the same time):

```java
private CommonProfile getProfile(final WebContext context) {
    final ProfileManager manager = new ProfileManager(context);
    return (CommonProfile) manager.get(true).orElse(null);
}
```

So far, so good.

But when it comes to the security configuration (the goal was to secure the `/protected` URL with the CAS server and a specific role retrieved from the CAS authenticated user),
the difference between both security libraries is startling.

I think it mainly comes from the different philosophies proposed by the two libraries: while Spring Security provides a set of concepts (authentication filter, token, provider...),
_pac4j_ focuses on use cases: either you want to login for a UI or for a web service (it's an indirect or a direct client = authentication mechanism). And this makes things incredibly easier!

For Spring Security:

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private static final String APP1_CALLBACK_URL = "http://myappserver/app1/callback";
    private static final String CAS_SERVER_PREFIX_URL = "http://mycasserver/cas/";
    private static final String CAS_SERVER_LOGIN_URL = CAS_SERVER_PREFIX_URL + "login";

    @Bean
    public ServiceProperties serviceProperties() {
        ServiceProperties sp = new ServiceProperties();
        sp.setService(APP1_CALLBACK_URL);
        return sp;
    }

    @Bean
    public Cas30ServiceTicketValidator cas30ServiceTicketValidator() {
        return new Cas30ServiceTicketValidator(CAS_SERVER_PREFIX_URL);
    }

    @Bean
    public CasAuthenticationFilter casAuthenticationFilter() throws Exception {
        CasAuthenticationFilter casAuthenticationFilter = new CasAuthenticationFilter();
        casAuthenticationFilter.setFilterProcessesUrl("/callback");
        casAuthenticationFilter.setAuthenticationManager(authenticationManager());
        casAuthenticationFilter.setSessionAuthenticationStrategy(new SessionFixationProtectionStrategy());
        return casAuthenticationFilter;
    }

    @Bean
    public CasAuthenticationEntryPoint casAuthenticationEntryPoint() {
        CasAuthenticationEntryPoint casAuthenticationEntryPoint = new CasAuthenticationEntryPoint();
        casAuthenticationEntryPoint.setLoginUrl(CAS_SERVER_LOGIN_URL);
        casAuthenticationEntryPoint.setServiceProperties(serviceProperties());
        return casAuthenticationEntryPoint;
    }

    @Bean
    public CasAuthenticationProvider casAuthenticationProvider() {
        CasAuthenticationProvider casAuthenticationProvider = new CasAuthenticationProvider();
        casAuthenticationProvider.setAuthenticationUserDetailsService(customUserDetailsService());
        casAuthenticationProvider.setServiceProperties(serviceProperties());
        casAuthenticationProvider.setTicketValidator(cas30ServiceTicketValidator());
        casAuthenticationProvider.setKey("an_id_for_this_auth_provider_only");
        return casAuthenticationProvider;
    }

    @Bean
    public AuthenticationUserDetailsService<CasAssertionAuthenticationToken> customUserDetailsService() {
        return token -> {
            AttributePrincipal principal = token.getAssertion().getPrincipal();
            String name = principal.getName();
            Object roles = principal.getAttributes().get("ROLES");
            List<GrantedAuthority> authorities = new ArrayList<>();
            if (roles instanceof List) {
                List<String> list = (List<String>) roles;
                list.forEach(role -> {
                    GrantedAuthority authority = new SimpleGrantedAuthority(role);
                    authorities.add(authority);
                });
            }
            return new User(name, "pwd", authorities);
        };
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/protected/**").hasRole("USER_APP1")
                .and()
                .addFilterAt(casAuthenticationFilter(), CasAuthenticationFilter.class)
                .authenticationProvider(casAuthenticationProvider())
                .exceptionHandling()
                .authenticationEntryPoint(casAuthenticationEntryPoint());
    }
}
```

For _spring-webmvc-pac4j_:

```java
@Configuration
@ComponentScan(basePackages = "org.pac4j.springframework.web")
public class SecurityConfig extends WebMvcConfigurerAdapter {

    private static final String APP2_CALLBACK_URL = "http://myappserver/app2/callback";
    private static final String CAS_SERVER_LOGIN_URL = "http://mycasserver/cas/login";

    @Bean
    public Config config() {

        final CasClient casClient = new CasClient(new CasConfiguration(CAS_SERVER_LOGIN_URL));
        cas.setName("cas");

        final Clients clients = new Clients(APP2_CALLBACK_URL, casClient);
        clients.setAuthorizationGenerator((ctx, profile) -> {
            Object roles = profile.getAttribute("ROLES");
            if (roles instanceof List) {
                List<String> list = (List<String>) roles;
                list.forEach(role -> profile.addRole(role));
            }
            return profile;
        });

        final Config config = new Config(clients);
        config.addAuthorizer("user", new RequireAnyRoleAuthorizer("ROLE_USER_APP2"));
        return config;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SecurityInterceptor(config(), "cas", "user")).addPathPatterns("/protected/*");
    }
}
```

No comment.

Check out by yourself: see all the power of this _pac4j_ library in the [spring-webmvc-pac4j-boot-demo](https://github.com/pac4j/spring-webmvc-pac4j-boot-demo) demo...

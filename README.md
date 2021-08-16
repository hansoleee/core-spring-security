# Core-Spring-Security
### [Inflearn 정수원님의 강의 보러가기](https://www.inflearn.com/course/%EC%BD%94%EC%96%B4-%EC%8A%A4%ED%94%84%EB%A7%81-%EC%8B%9C%ED%81%90%EB%A6%AC%ED%8B%B0/dashboard)
***
## 프로젝트 시작일 : 2021.08.14
## 프로젝트 종료일 : -
***
## 학습 내용
### 실전 프로젝트 - 인증 프로세스 Form 인증 구현
#### 2021.08.14 1)실전 프로젝트 생성

#### 2021.08.14 2) 정적 자원 관리 - WebIgnore 설정
- 아래의 코드를 WebSecurityConfigurerAdapter를 상속받은 class에 작성한다.
```java
@Override
public void configure(WebSecurity web) throws Exception {
    web
        .ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
}
```
> 위 코드는 Matchers()에 등록된 경로의 자원을 Spring Security Filter에서 검사하지 않도록 해준다.
- 아래의 코드와 차이점을 생각해보자
```java
@Override
protected void configure(HttpSecurity http) throws Exception {
    http
        .authorizeRequests()
        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll();
}
```
> 위 코드는 Matchers()에 등록된 경로의 자원을 Spring Security Filter에서 인가된 사용자의 접근인지 확인하고 적절한 응답을 내려주는 역할을 한다.

> 첫 번째 코드와는 달리 두 번째 코드에서는 Spring Security Filter에서 검사를 수행한다.   
> 정적 자원과 같이 특별한 검사가 필요하지 않다면 WebIgnore 설정에 등록하는 것을 권장한다. ~~검사하는 것도 비용이 들어가니까~~   
> 예를 들어 local 환경에서 Spring project를 생성하다보면 h2 database를 이용하게 되는 경우가 많다. 이때 web console도 같이 사용하게 되면 WebIgnore 설정에 .antMatchers("/h2-console/**") 코드를 이용하면 될 것이다.

#### 2021.08.14 3) 사용자 DB 등록 및 PasswordEncoder
- PasswordEncoder의 역할
```text
비밀번호를 안전하게 암호화하는 역할을 한다.   
```
- PasswordEncoder 생성 방법
```java
PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder()
```
- 암호화 포멧
```text
형식 : {id}encodedPassword
예(Bcrypt 방식을 사용) : {bcrypt}$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG
알고리즘의 종류 : bcrypt, noop, pbkdf2, scrypt, sha256
```
- 인터페이스
```text
1. encode(password) // 패스워드 암호화
2. matches(rawPassword, encodedPassword) // 패스워드 비교
```
- ModelMapper 사용
```text
ModelMapper를 사용할 때 기본 설정을 사용하면 Domain 객체에 Setter가 필요하다.
Setter가 없다면 .map()을 사용했을 때 field 이름이 같아도 domain 객체의 field에는 null 값이 들어간다.
```
##### 추가 학습 필요
- PasswordEncoder를 사용해야되는 이유 => 보안을 위해서 사용
- ModelMapper를 사용하면서 Domain 객체에 Setter 코드를 작성하지 않는 방법 => [lokie님의 tistory 보러가기](https://lokie.tistory.com/26)
> DTO 객체와 domain 객체 mapping을 지원해주는 라이브러리는 여러가지가 있다.   
> [MapStruct](https://mapstruct.org)   
> [ModelMapper](http://modelmapper.org/)   
> 두 라이브러리의 가장 큰 차이는 리플렉션 발생의 유무이다.   
> MapStruct의 경우 컴파일 시점에서 Annotation을 읽고 구현체를 만들어서 리플렉션이 발생하지 않는다.   
> 반면에 ModelMapper는 modelMapper.map(member, MemberDTO.class) 호출될 때 리플렉션이 발생한다.   
> [성능 비교글 보러가기(mangchhe님의 글)](https://mangchhe.github.io/spring/2021/01/25/ModelMapperAndMapStruct/)

- 암호화 된 문자열에 {id}가 붙는 이유 => 
[spring-security 공식 문서 보기](https://docs.spring.io/spring-security/site/docs/current/reference/html5/#authentication-password-storage)
- 암호화 된 문자열이 {id}encodedPassword 형식이 아닌 {id}가 빠진 문자열인 이유 => ~~아직 찾지 못했다.~~

#### 2021.08.14 4) DB 연동 인증 처리(1): CustomUserDetailsService
- 로그인 요청하는 사용자 정보를 DB에서 조회하도록 기능 구현
```text
아래의 코드를 WebSecurityConfigurerAdapter를 상속받는 class에 작성한다.
(필자는 lombok을 사용하기 때문에 @RequiredArgsConstructor Annotation을 통한 의존성 주입이 가능하다.)
```
```java
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService);
    }
}
```
```text
매우 간단한 코드이기에 매우 간단하게 코드 설명을 해보겠다.
내가 정의한 UserDetailsService Bean을 사용하겠다는 설정 코드이다.
```
- UserDetailsService 설명
> UserDetailsService는 저장된 UserDetails 객체를 반환해주는 인터페이스이다.   
> UserDetails 객체를 반환을 위해 DaoAuthenticationProvider와 협력한다.      
> DaoAuthenticationProvider는 요청받은 유저의 ID, Password와 저장된 ID, Password의 검증을 담당한다.   
> 
> ![DaoAuthenticationProvider와 UserDetailsService가 협력하는 과정](./image/daoauthenticationprovider.png)   
> <DaoAuthenticationProvider와 UserDetailsService가 협력하는 과정>

#### 2021.08.15 5) DB 연동 인증 처리(2): CustomAuthenticationProvider
- CustomAuthenticationProvider 객체를 생성하고 인증 처리에 사용
```java
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // CustomUserDetailsService를 이용한 인증 방식 사용
        // auth.userDetailsService(userDetailsService);
        
        // CustomAuthenticationProvider를 이용한 인증 방식 사용
        auth.authenticationProvider(authenticationProvider());
    }
}
```
- AuthenticationProvider 설명
> UserDetailsService에서 UserDetails 객체를 받아오고 사용자가 입력한 Password와 UserDetails 객체의 Password가 일치하는지 비교하고 Authentication 객체를 반환하는 역할을 수행한다.

#### 2021.08.15 6) 커스텀 로그인 페이지 생성하기

#### 2021.08.15 7) 로그아웃 및 인증에 따른 화면 보안 처리
- Logout 기능 적용

#### 2021.08.15 8) 인증 부가 기능 - WebAuthenticationDetails, AuthenticationDetailsSource
- WebAuthenticationDetails의 역할
```text
WebAuthenticationDetails는 인증 과정 중 전달된 데이터를 저장하는 역할을 한다.
```
- AuthenticationDetailsSource의 역할
```text
AuthenticationDefailtsSource는 WebAuthenticationDetails를 생성하는 역할을 한다.
```
-WebAuthenticationDetails의 활용 방안
```markdown
로그인 할 때 보안문자 입력을 하고 보안문자 입력이 맞다면 로그인 성공을 할 때 사용할 수 있을 것 같다. ~~조사는 해보고 말한 거야?~~
```

#### 2021.08.15 9) 인증 성공 핸들러: CustomAuthenticationSuccessHandler
- AuthenticationSuccessHandler의 역할
```text
FormLogin 인증 필터가 로그인에 성공한 후에 필요한 로직을 수행해주는 역할을 한다.
```
- AuthenticationSuccessHandler의 활용 방안
```text
로그인 하지 않은 사용자가 특정 url 바로 접근하려 했을 때 먼저 로그인 페이지로 이동하고 로그인 정보를 입력한 뒤에 요청했던 url로 이동시켜 줄 수 있다. 
```

#### 2021.08.15 10) 인증 실패 핸들러: CustomAuthenticationFailureHandler
- AuthenticationFailureHandler의 역할
```text
FormLogin 인증 필터가 로그인에 실패한 후에 필요한 로직을 수행해주는 역할을 한다.
```
- AuthenticationFailureHandler의 활용 방안
```text
실패 원인을 Login 화면에 보여줄 수 있다. 
```

#### 2021.08.15 11) 인증 거부 처리 - Access Denied
- AccessDeniedException(인가 예외)을 발생시키는 Filter와 예외 처리 과정
```text
FilterSecurityInterceptor가 AccessDeniedException을 발생시키고 던진다.
그러면 ExceptionTranslationFilter가 AccessDeniedException을 받고 accessDeniedHandler에게 처리를 위임한다.
```

### 인증 프로세스 Ajax 인증 구현
#### 2021.08.15 1) 흐름 및 개요

#### 2021.08.15 2) 인증 필터 - AjaxAuthenticationFilter
- AbstractAuthenticationProcessingFilter 구현과 적용 과정
```text
1. AbstractAuthenticationProcessingFilter 상속한 클래스 구현
2. 필터 동작 조건 설정
3. AjaxAuthenticationToken 생성과 AuthenticationManager를 통한 인증 처리
4. Filter 등록
```

#### 2021.08.15 3) 인증 처리자 - AjaxAuthenticationProvider
- Ajax 처리를 위한 WebSecurityConfigurerAdapter 상속 클래스 생성
- AjaxAuthenticationProvider 클래스 생성과 적용

#### 2021.08.15 4) 인증 핸들러 - AjaxAuthenticationSuccessHandler, AjaxAuthenticationFailureHandler
- Ajax 인증 성공과 실패 이후 작업을 담당하는 SuccessHandler, FailureHandler 클래스 생성

##### 추가 학습 필요
- 같은 타입의 Bean이 여러개 등록되어 있을 때 Bean 주입 시 발생하는 문제와 해결 방안
###### 발생 문제
```text
AuthenticationSuccessHandler와 AuthenticationFailureHandler가 각각 2개씩 등록된 상황이다.

등록한 Bean
    AuthenticationSuccessHandler:
        1. FormAuthenticationSuccessHandler
        2. AjaxAuthenticationSuccessHandler
    AuthenticationFailureHandler:
        1. FormAuthenticationFailureHandler
        2. AjaxAuthenticationFailureHandler
        
이때 WebSecurityConfigurerAdapter를 상속한 클래스에서 

@Slf4j
@EnableWebSecurity
@RequiredArgsConstructor
@Order(1)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final AuthenticationSuccessHandler authenticationSuccessHandler;
    private final AuthenticationFailureHandler authenticationFailureHandler;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .(생략)
            .successHandler(authenticationSuccessHandler)
            .failureHandler(authenticationFailureHandler)
            .(생략)
    }
}

위와 같이 사용자 인증이 성공/실패했을 때 처리 로직을 담당하는 Handler를 등록하기 위한 Bean을 주입에서 문제가 발생했다.
```
###### 해결방안
```text
Form 인증 방식에서 FormAuthenticationSuccessHandler와 FormAuthenticationFailureHandler Bean을 주입하는 방법

1. @Autowired 필드명 매칭
        private final AuthenticationSuccessHandler authenticationSuccessHandler;
        -> private final AuthenticationSuccessHandler formAuthenticationSuccessHandler;
        
        private final AuthenticationFailureHandler authenticationFailureHandler;
        -> private final AuthenticationFailureHandler formAuthenticationFailureHandler;
    위와 같이 필드명을 특정 빈 이름으로 지정한다.

2. @Qualifier -> @Qualifier끼리 매칭 -> 빈 이름 매칭

    @Component
    @Qualifier("formAuthenticationSuccessHandler") // 추가 작성
    public class FormAuthenticationSuccessHandler implements SimpleUrlAuthenticationSuccessHandler {
        ...
    }
    
    public FormSecurityConfig (@Qualifier("formAuthenticationSuccessHandler") AuthenticationSuccessHandler authenticationSuccessHandler) {
        ....
    }
    
    사용하기 위해서는 생성자를 코드로 작성하고 매개변수에 @Qualifier Annotation과 같이 사용해야한다. ~~Lombok @RequiredArgsContructor를 포기해야한다니..ㅜㅜ~~
    

3. @Primary 사용

    @Component
    @Primary
    public class FormAuthenticationSuccessHandler implements SimpleUrlAuthenticationSuccessHandler {
        ...
    }
    이 경우는 여러개의 Bean 중에서 특정 Bean이 다른 Bean보다 매우 빈번하게 사용되는 경우에 적합하다. ~~스프링 핵심 원리 - 기본편 중에서~~

각 해결방법에 대한 개인적인 생각
1번 방법의 경우 나중에 코드를 쓰윽 봤을 때 분명 놓칠게 뻔해보인다.
2번 방법의 경우 "해당 타입의 Bean이 여러 개있고 이번엔 이걸 쓰고있어요"라고 너무 드러내 놓고있다. ~~1번 보단 이 방식이 좋은 것 같다고 생각한다.~~
3번 방법의 경우는 위에 작성한 내용대로 특정 하나의 Bean이 자주 사용될 때 적용하기에 딱이다.
```
#### 2021.08.15 5) 인증 및 인가 예외 처리 - AjaxLoginUrlAuthenticationEntryPoint, AjaxAccessDeniedHandler
- AuthenticationEntryPoint의 역할
```text
인증을 받지 못한 사용자 인증이 필요한 자원에 접근할 경우 사용자가 인증을 받을 수 있도록 인도하는 역할 (보통 로그인 페이지로 이동)
```
- AccessDeninedHandler의 역할
```text
인가받지 못한 사용자가 자원에 접근할 경우에 대한 처리를 담당하는 역할 (보통 접근 권한이 없다는 페이지로 이동)
```
##### 추가 학습 필요
```text
Ajax 방식으로 UserDetailsService에서 사용자를 조회하고 Authentication 객체를 SecurityContext에 저장하는 것은 이해가 된다.
그 이후 요청에서 JSESSIONID와 같은 쿠키를 보내지 않고도 인가된 사용자라는 것을 어떻게 알수 있는 것일까?

Curl을 이용한 테스트에서는 401, Unauthorized 응답이 내려온다.
```

#### 2021.08.15 6) Ajax Custom DSLs 구현하기
- Custom DSLs 구현 및 적용 방법
```text
AbstractHttpConfigurer 상속을 통해 구현할 수 있다.

*DSL이란: Domain Specific Language (도메인 특화 언어)
```
-Custom DSLs 사용
```text
시스템의 규모가 크고 복잡한 경우에 구조화된 아키텍처 구성에 도움이 될 수 있을 것이다. 
```

#### 2021.08.15 7) Ajax 로그인 구현 & CSRF 설정
- Ajax 요청&응답을 위한 설정
```text
1. 전송 방식이 Ajax 인지의 여부를 위한 헤더 설정
    xhr.setRequestHeader("X-Request-With", "XMLHttpRequest");

2. HTML에서 CSRF 헤더 설정과 Javascript에서 사용
<meta id="_csrf" name="_csrf" th:content="${_csrf.token}"/>
<meta id="_csrf_header" name="_csrf_header" th:content="${_csrf.headerName}"/>

let csrfHeader = $('meta[name="_csrf_header"]').attr('content');
let csrfToken = $('meta[name="_csrf"]').attr('content');
xhr.setRequestHeader(csrfHeader, csrfToken);

<form></form>을 이용할 때는 Thymeleaf가 csrf 값을 요청에 담아서 전송한다.
하지만 Ajax 방식을 이용한 요청에는 Header에 값을 넣어주는 코드를 작성하여 보내야한다. 
```

### 인가 프로세스 DB 연동 웹 계층 구현 
#### 2021.08.15 1) 스프링 시큐리티 인가 개요
- 동적 권한 관리가 가능한 기능 구현 (DB 연동, 권한 설정 클래스에서 코드 작성을 통한 권한 관리는 하지않음)
```text
관리자 시스템 구축
    1. 회원 관리 - 권한 부여
    2. 권한 관리 - 권한 생성, 삭제, 계층 설정
    3. 자원 관리 - 자원 생성, 삭제, 수정, 권한 매핑
    
인가 방식
    1. URL
    2. Method
```
#### 2021.08.16 2) 관리자 시스템 - 권한 도메인, 서비스, 리포지토리 구성

#### 2021.08.16 3) 웹 기반 인가처리 DB 연동 - 주요 아키텍처 이해
- 스프링 시큐리티의 인가를 위한 필요 정보
```text
http.antMatchers("/user").access("hasRole('UESR')")

**사용자**가 **/user 자원**에 접근하기 위해서는 **ROLE_USER 권한**이 필요하다

인증 대상 : 사용자
요청 자원 : /user
권한 정보 : ROLE_USER

인가 처리를 담당하는 Filter(SecurityInterceptor)가 인가 처리를 위임하는 AccessDecisionManager한테 위 3가지 정보를 전달한다.
AccessDecisionManager는 전달받은 3가지 정보를 통해 인증 대상이 갖고 있는 권한으로 요청하는 자원에 접근이 가능한지 판단하게 된다.
```
- 스프링 시큐리티가 초기화되는 과정&사용자의 자원 요청에 대한 인가 처리 과정
```text
ExpressionBasedFilterInvocationSecurityMetadataSource.class 가 Key-Value(자원-권한) Map을 갖고 있다.
부모 클래스인 DefaultFilterInvocationSecurityMetadataSource에서 사용자의 요청과 요청에 해당하는 접근 권한을 찾는다.
그리고 인증 정보, 요청 정보, 권한 정보를 AccessDecisionManger 객체에 넘겨 처리를 맡긴다.

너무 많은 과정이 있는데 정리하기가 힘들다... 이유는 아마 해당 기능을 비슷하게라도 구현해본 경험이 없어서 흐름 파악에 중점을 두었기 때문인것으로 생각된다. 꼭 다시 정리하도록 하자
~~그냥 Security가 어렵다 포기하지 말고 계속 파보자~~
```

#### 2021.08.16 4) 웹 기반 인가처리 DB 연동 - FilterInvocationSecurityMetadataSource(1)
- DB 연동 인가 처리를 위한 Interface 구현
```text
Interface 구현 내용
1. 사용자가 접근하고자 하는 URL 자원에 대한 권한 정보 추출 -> DB select
2. AccessDecisionManger에게 전달하여 인가처리 수행
3. 사용자의 요청마다 요청 정보에 매핑된 권한 정보 확인
```
- 구현한 Interface를 사용한 인가 처리의 전체 흐름
```text
user - GET /admin -> 
FilterSecurityInterceptor.class - 권한 정보 조회 -> 
FilterInvocationSecurityMetadataSource.class - 요청 정보와 매칭되는 권한 정보 추출 ->
        {"/admin": [
                "ROLE_ADMIN", "ROLE_MANAGER", "ROLE_USER"
            ],
            "/user": [
                "ROLE_USER"
            ]}
권한 목록 존재 - NO -> 인가 처리를 하지 않음
             - YES -> AccessDecisionManager.class로 처리 위임 
```
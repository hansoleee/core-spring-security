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
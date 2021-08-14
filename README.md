# Core-Spring-Security
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
> ModelMapper를 사용할 때 기본 설정을 사용하면 Domain 객체에 Setter가 필요하다.
##### 추가 학습 필요
- PasswordEncoder를 사용해야되는 이유
- ModelMapper를 사용하면서 Domain 객체에 Setter 코드를 작성하지 않는 방법
- 암호화 된 문자열이 {id}encodedPassword 형식이 아닌 encodedPassword 문자열인 이유
 
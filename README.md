# Bank-side-project

## 객체에 PS를 붙이는 경우
객체가 영속성 컨텍스트에 들어간 경우 객체명뒤에 PS를 붙입니다.
(persistence의 약자)

## Jpa LocalDateTime 자동생성하는법
- @EnableJpaAuditing (Main 클래스)
- @EntityListeners(AuditingEntityListener.class) (Entity 클래스)
```java
    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime createAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updateAt;
```

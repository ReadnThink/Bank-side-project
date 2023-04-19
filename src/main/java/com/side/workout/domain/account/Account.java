package com.side.workout.domain.account;

import com.side.workout.domain.user.User;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true,nullable = false, length = 20)
    private Long account_number;
    @Column(nullable = false, length = 4)
    private Long password;
    @Column(nullable = false)
    private Long balance;

    //항상 ORM에서 fk의 주인은 Many Entity 쪽입니다.
    @ManyToOne(fetch = FetchType.LAZY) // account.getUser().필드명 == 직접 조회할때 Lazy 발동이 됩니다.
    private User users; // user_id 로 테이블에 생성이 됩니다.

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime createAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updateAt;
}

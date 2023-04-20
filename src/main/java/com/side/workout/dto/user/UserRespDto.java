package com.side.workout.dto.user;

import com.side.workout.domain.user.User;
import com.side.workout.util.CustomDateUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

public class UserRespDto {
    @Setter
    @Getter
    public static class LoginRespDto{
        private Long id;
        private String username;
        private String createAt;

        public LoginRespDto(User user) {
            this.id = user.getId();
            this.username = user.getUsername();
            this.createAt = CustomDateUtil.toStringFormat(user.getCreateAt());
        }
    }
    @ToString
    @Setter
    @Getter
    public static class JoinRespDto{
        private Long id;
        private String username;
        private String fullname;

        public JoinRespDto(User user) {
            this.id = user.getId();
            this.username = user.getUsername();
            this.fullname = user.getFullname();
        }
    }
}

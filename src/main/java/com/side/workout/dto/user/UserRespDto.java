package com.side.workout.dto.user;

import com.side.workout.domain.user.User;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

public class UserRespDto {
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

package com.example;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
class UserTeamId implements Serializable {

    @Serial
    private static final long serialVersionUID = 4133724209349774583L;

    @Column(name = "user_login_name")
    private String userLoginName;

    @Column(name = "team_id")
    private Long teamId;

    public UserTeamId(String userLoginName, Long teamId) {
        this.userLoginName = userLoginName;
        this.teamId = teamId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        UserTeamId that = (UserTeamId) o;
        return Objects.equals(userLoginName, that.userLoginName) &&
                Objects.equals(teamId, that.teamId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userLoginName, teamId);
    }

}

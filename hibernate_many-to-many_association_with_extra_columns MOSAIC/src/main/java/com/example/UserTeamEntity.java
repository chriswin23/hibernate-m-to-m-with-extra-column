package com.example;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.type.YesNoConverter;

import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor()
public class UserTeamEntity {

    @EmbeddedId
    private UserTeamId id;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("userLoginName")
    private UserEntity user;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("teamId")
    private TeamEntity team;

    @Column(name = "is_team_leader")
    @Convert(converter = YesNoConverter.class)
    private boolean isTeamLeader = false;

    @Column(name = "is_user_primary_team")
    @Convert(converter = YesNoConverter.class)
    private boolean isUserPrimaryTeam = false;

    public UserTeamEntity(UserEntity user, TeamEntity team, boolean isTeamLeader, boolean isUserPrimaryTeam) {
        this.id = new UserTeamId(user.getLoginName(), team.getId());
        this.user = user;
        this.team = team;
        this.isTeamLeader = isTeamLeader;
        this.isUserPrimaryTeam = isUserPrimaryTeam;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        UserTeamEntity that = (UserTeamEntity) o;
        return Objects.equals(user, that.user) &&
                Objects.equals(team, that.team);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

}

package com.example;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;
import java.util.stream.Collectors;

@Entity
@NoArgsConstructor
public class UserEntity {

    public UserEntity(String loginName) {
        this.loginName = loginName;
    }

    @Id
    @Getter
    @Setter
    @Column(name = "login_name")
    private String loginName;

    @OneToMany(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER
    )
    private Set<UserTeamEntity> teams = new HashSet<>();

    public Set<TeamEntity> getAllTeams() {
        List<TeamEntity> allTeams = new ArrayList<>();
        allTeams.addAll(getPrimaryTeams());
        allTeams.addAll(getNonPrimaryTeams());
        return Set.copyOf(allTeams);
    }

    public List<TeamEntity> getPrimaryTeams() {
        return teams.stream()
                .filter(UserTeamEntity::isUserPrimaryTeam)
                .map(UserTeamEntity::getTeam)
                .sorted(Comparator.comparing(TeamEntity::getDisplayName, Comparator.nullsLast(String::compareTo)))
                .toList();
    }

    public List<TeamEntity> getNonPrimaryTeams() {
        return teams.stream()
                .filter(userTeam -> !userTeam.isUserPrimaryTeam())
                .map(UserTeamEntity::getTeam)
                .sorted(Comparator.comparing(TeamEntity::getDisplayName, Comparator.nullsLast(String::compareTo)))
                .toList();
    }

    public Set<TeamEntity> getTeamsLed() {
        return Set.copyOf(
                teams.stream().filter(UserTeamEntity::isTeamLeader).map(UserTeamEntity::getTeam).collect(Collectors.toSet()));
    }

    public void addTeam(TeamEntity team, boolean isTeamLeader, boolean isPrimaryTeam) {
        Optional<UserTeamEntity> optionalUserTeam = teams.stream().filter(userTeamEntity -> userTeamEntity.getTeam().equals(team))
                .findFirst();

        if (optionalUserTeam.isPresent()) {
            optionalUserTeam.get().setTeamLeader(isTeamLeader);
            optionalUserTeam.get().setUserPrimaryTeam(isPrimaryTeam);
        } else {
            UserTeamEntity userTeamEntity = new UserTeamEntity(this, team, isTeamLeader, isPrimaryTeam);
            teams.add(userTeamEntity);
            team.getUserTeams().add(userTeamEntity);
        }
    }

    public void removeTeam(TeamEntity teamEntity) {
        UserTeamEntity userTeam = new UserTeamEntity(this, teamEntity, false, false);
        teams.remove(userTeam);
        teamEntity.removeUser(this);
    }

    Set<UserTeamEntity> getUserTeams() {
        return teams;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(loginName);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (!(obj instanceof UserEntity other)) {
            return false;
        } else {
            return Objects.equals(loginName, other.getLoginName());
        }
    }

}

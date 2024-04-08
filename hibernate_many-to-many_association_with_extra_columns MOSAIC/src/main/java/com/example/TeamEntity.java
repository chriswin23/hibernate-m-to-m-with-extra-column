package com.example;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Setter
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class TeamEntity {

    public TeamEntity(String displayName) {
        this.id = null;
        this.displayName = displayName;
        this.users = new HashSet<>();
    }

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @GenericGenerator(name = "native")
    private Long id;

    @Column(name = "display_name")
    private String displayName;

    @OneToMany(
            mappedBy = "team",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER
    )
    private Set<UserTeamEntity> users = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (!(o instanceof TeamEntity teamEntity)) {
            return false;
        } else {
            return id != null && id.equals(teamEntity.getId());
        }
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    public void addUser(UserEntity userEntity, boolean isPrimaryTeam, boolean isLeader) {
        Optional<UserTeamEntity> optionalUserTeam = users.stream().filter(userTeamEntity -> userTeamEntity.getUser().equals(userEntity))
                .findFirst();

        if (optionalUserTeam.isPresent()) {
            optionalUserTeam.get().setTeamLeader(isLeader);
            optionalUserTeam.get().setUserPrimaryTeam(isPrimaryTeam);
        } else {
            UserTeamEntity userTeamEntity = new UserTeamEntity(userEntity, this, false, false);
            users.add(userTeamEntity);
            userEntity.getUserTeams().add(userTeamEntity);
        }
    }

    public Set<UserEntity> getLeaderUsers() {
        Set<UserEntity> userEntities = users.stream().filter(UserTeamEntity::isTeamLeader).map(UserTeamEntity::getUser)
                .collect(Collectors.toSet());
        return Set.copyOf(userEntities);
    }

    public Set<UserEntity> getPrimaryUsers() {
        Set<UserEntity> userEntities = users.stream().filter(UserTeamEntity::isUserPrimaryTeam).map(UserTeamEntity::getUser)
                .collect(Collectors.toSet());
        return Set.copyOf(userEntities);
    }

    public Set<UserEntity> getAllUsers() {
        Set<UserEntity> userEntities = users.stream().map(UserTeamEntity::getUser).collect(Collectors.toSet());
        return Set.copyOf(userEntities);
    }

    public void removeUser(UserEntity userEntity) {
        for (Iterator<UserTeamEntity> iterator = users.iterator(); iterator.hasNext(); ) {
            UserTeamEntity userTeamEntity = iterator.next();

            if (userTeamEntity.getTeam().equals(this) && userTeamEntity.getUser().equals(userEntity)) {
                iterator.remove();
                userTeamEntity.getUser().getUserTeams().remove(userTeamEntity);
                userTeamEntity.setTeam(null);
                userTeamEntity.setUser(null);
            }
        }
    }

    Set<UserTeamEntity> getUserTeams() {
        return users;
    }

}

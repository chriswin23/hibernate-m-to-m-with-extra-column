package com.example;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@RequiredArgsConstructor
@Service
public class UserTeamService {

    private final TeamRepository teamRepository;
    private final UserRepository userRepository;

    @Transactional
    public UserEntity findById(String loginName) {
        return userRepository.findById(loginName).orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    @Transactional
    public TeamEntity findById(Long id) {
        return teamRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Team not found"));
    }

    @Transactional
    public TeamEntity createTeam(TeamEntity team) {
        return teamRepository.save(team);
    }

    @Transactional
    public UserEntity createUser(UserEntity user) {
        return userRepository.save(user);
    }

    @Transactional
    public TeamEntity updateTeam(@NonNull TeamEntity teamEntity) {
        TeamEntity savedTeam = findById(teamEntity.getId());
        Collection<UserEntity> userEntities = userRepository.findAll();

        savedTeam.setDisplayName(teamEntity.getDisplayName());

        savedTeam.getAllUsers().stream()
                .filter(userEntity -> !teamEntity.getAllUsers().contains(userEntity))
                .forEach(userEntity -> {
                    findByUserLoginName(userEntities, userEntity.getLoginName()).removeTeam(savedTeam);
                    savedTeam.removeUser(userEntity);
                });

        teamEntity.getAllUsers().forEach(user -> {
            UserEntity persistedUser = findByUserLoginName(userEntities, user.getLoginName());
            boolean isPrimaryTeam = user.getPrimaryTeams().stream().map(TeamEntity::getId).toList()
                    .contains(teamEntity.getId());
            boolean isTeamLeader = user.getTeamsLed().stream().map(TeamEntity::getId).toList()
                    .contains(teamEntity.getId());
            savedTeam.addUser(persistedUser, isPrimaryTeam, isTeamLeader);
        });

        return teamRepository.save(savedTeam);
    }

    @Transactional
    public UserEntity updateUser(UserEntity userEntity) {
        String loginName = userEntity.getLoginName().toLowerCase();
        UserEntity savedUser = findById(loginName);
        Collection<TeamEntity> teamEntities = teamRepository.findAll();

        // remove teams
        savedUser.getAllTeams().stream()
                .filter(teamEntity -> !userEntity.getAllTeams().contains(teamEntity))
                .forEach(savedUser::removeTeam);

        userEntity.getAllTeams().forEach(team -> {
            TeamEntity persistedTeam = findTeamById(teamEntities, team.getId());
            boolean isPrimaryTeam = team.getPrimaryUsers().stream().map(UserEntity::getLoginName).toList()
                    .contains(userEntity.getLoginName());
            boolean isTeamLeader = team.getLeaderUsers().stream().map(UserEntity::getLoginName).toList()
                    .contains(userEntity.getLoginName());
            savedUser.addTeam(persistedTeam, isTeamLeader, isPrimaryTeam);
        });

        return userRepository.save(savedUser);
    }

    @Transactional
    public UserEntity updateUser(String loginName, Collection<Long> allTeams, Collection<String> primaryTeams, Collection<String> teamLeaders) {
        UserEntity savedUser = findById(loginName);
        Collection<TeamEntity> teamEntities = teamRepository.findAll();

        // remove teams
        savedUser.getAllTeams().stream()
                .filter(teamEntity -> !allTeams.contains(teamEntity.getId()))
                .forEach(savedUser::removeTeam);

        allTeams.forEach(teamId -> {
            TeamEntity persistedTeam = findTeamById(teamEntities, teamId);
            boolean isPrimaryTeam = primaryTeams.contains(loginName);
            boolean isTeamLeader = teamLeaders.contains(loginName);
            savedUser.addTeam(persistedTeam, isTeamLeader, isPrimaryTeam);
        });

        return userRepository.save(savedUser);
    }

    private UserEntity findByUserLoginName(Collection<UserEntity> userEntities, String loginName) {
        return userEntities.stream().filter(userEntity -> userEntity.getLoginName().equals(loginName)).findFirst()
                .orElseThrow(() -> new EntityNotFoundException("User with id " + loginName + " not found for update"));
    }

    private TeamEntity findTeamById(Collection<TeamEntity> teamEntities, Long id) {
        return teamEntities.stream().filter(teamEntity -> teamEntity.getId().equals(id)).findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Team with id " + id + " not found for update"));
    }

}

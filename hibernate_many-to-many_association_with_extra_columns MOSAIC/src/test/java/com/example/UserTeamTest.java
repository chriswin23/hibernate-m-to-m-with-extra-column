package com.example;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class UserTeamTest {

    private final static String TEAM_DISPLAY_NAME = "display name";
    private final static String USER_LOGIN_NAME = "login name";

    @Autowired
    UserTeamService userTeamService;

    @Test
    void updateTeamService() {
        // GIVEN
        TeamEntity team = createTeam(TEAM_DISPLAY_NAME);
        createUser(USER_LOGIN_NAME);

        // WHEN
        TeamEntity teamToUpdate = userTeamService.findById(team.getId());
        UserEntity userToAdd = userTeamService.findById(USER_LOGIN_NAME);
        teamToUpdate.addUser(userToAdd, false, false);
        userTeamService.updateTeam(teamToUpdate);

        // THEN
        TeamEntity updatedEntity = userTeamService.findById(team.getId());
        assertEquals(1, updatedEntity.getAllUsers().size());
        assertTrue(updatedEntity.getAllUsers().stream().anyMatch(user -> USER_LOGIN_NAME.equalsIgnoreCase(user.getLoginName())));
    }

    @Test
    void updateUserService() {
        // GIVEN
        TeamEntity team = createTeam(TEAM_DISPLAY_NAME);
        createUser(USER_LOGIN_NAME);

        // WHEN
        TeamEntity teamToAdd = userTeamService.findById(team.getId());
        UserEntity userToUpdate = userTeamService.findById(USER_LOGIN_NAME);
        userToUpdate.addTeam(teamToAdd, false, false);
        userTeamService.updateUser(userToUpdate);

        // THEN
        UserEntity user = userTeamService.findById(USER_LOGIN_NAME);
        assertEquals(1, user.getAllTeams().size());
        assertTrue(user.getAllTeams().stream().anyMatch(teamOnUser -> TEAM_DISPLAY_NAME.equalsIgnoreCase(teamOnUser.getDisplayName())));
    }

    private TeamEntity createTeam(String displayName) {
        return userTeamService.createTeam(new TeamEntity(displayName));
    }

    private void createUser(String displayName) {
        userTeamService.createUser(new UserEntity(displayName));
    }

}

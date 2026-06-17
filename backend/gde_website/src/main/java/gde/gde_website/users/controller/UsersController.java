package gde.gde_website.users.controller;

import gde.gde_website.games.controller.GamesController;
import gde.gde_website.games.model.Games;
import gde.gde_website.games.service.GamesService;
import gde.gde_website.users.model.User;
import gde.gde_website.users.service.UsersService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/auth")
public class UsersController {
    private static final Logger gamesControllerLogger = LoggerFactory.getLogger(GamesController.class);

    private final UsersService userService;

    public UsersController(UsersService userService) {
        this.userService = userService;
    }

    // #TODO: implement this function
    // This function must implement user registration logic,
    // it must save new user to the DB, and return user session token in ideal scenario
    // In the ideal case this function must return ResponseEntity<SessionToken>
    // where SessionToken is a user session token which is provided by the AuthenticationController

//    @PostMapping("/register")
//    public ResponseEntity<> register() {
//        return null;
//    }

    // #TODO: implement this function
    // This function must implement user login logic,
    // it must check requested username/email + passwordHash from DB, and return user session token in ideal scenario
    // In the ideal case this function must return ResponseEntity<SessionToken>
    // where SessionToken is a user session token which is provided by the AuthenticationController

//    @PostMapping("/login")
//    public ResponseEntity<> login() {
//        return null;
//    }
}

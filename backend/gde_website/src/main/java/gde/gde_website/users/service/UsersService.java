package gde.gde_website.users.service;

import gde.gde_website.security.JwtUtils;
import gde.gde_website.users.entity.UserEntity;
import gde.gde_website.users.model.*;
import gde.gde_website.users.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * This class implements business logic of functions which are called inside UsersController
 * @Author: Artemii Gorelov, Egor Grishin
 */
@Service
@RequiredArgsConstructor
public class UsersService {
    private final static Logger userServiceLogger = LoggerFactory.getLogger(UsersService.class);

    private final UsersRepository userRepository;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;

    /**
     *
     * @param request - registration request for a new user
     * It contains following information:
     *                username,
     *                email,
     *                password,
     *                profileImageUrl
     * @return - New registered user session token
     * @throws ResponseStatusException with code 409 if in registration request user include email which is already registered.
     *
     * @Author: Egor Grishin
     */
    public LoginResponse register(RegisterRequest request) {
        userServiceLogger.info("Called UsersService register method");
        if (userRepository.existsByEmail(request.email())) {
            userServiceLogger.error("Entered email is already used");
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User with such email is already registered");
        }

        String hashedPassword = passwordEncoder.encode(request.password());

        UserEntity newUser = new UserEntity(
                request.username(),
                request.email(),
                hashedPassword,
                request.profileImageUrl()
        );

        UserEntity savedUser = userRepository.save(newUser);
        userServiceLogger.info("Successfully saved new user");

        String token = jwtUtils.generateToken(savedUser.getId());
        userServiceLogger.info("Successfully generated new user token");
        return new LoginResponse(token);
    }

    /**
     *
     * @param request - user login request which contains following information:
     *                email,
     *                password
     * @return - session token of successfully log-inned user
     * @throws  ResponseStatusException with:
     * Code 401 if user with requested email does not find in database
     * Code 401 if user requested incorrect password
     *
     * @Author: Egor Grishin
     */
    public LoginResponse login(LoginRequest request) {
        userServiceLogger.info("Called UsersService login method");
        UserEntity user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No user with such email exists"));
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            userServiceLogger.error("Entered password is incorrect");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Incorrect password");
        }

        String token = jwtUtils.generateToken(user.getId());
        userServiceLogger.info("Successfully log-inned");
        return new LoginResponse(token);
    }

    /**
     *
     * @param userId - id of the user, information about we want to get
     * @return - user with requested id information which contains:
     * id
     * username
     * email
     * profileImageUrl
     *
     * @throws ResponseStatusException with status 401 if user with requested id does not exist in database
     * @Author: Artemii Gorelov
     */
    public MeResponse me(Long userId) {
        userServiceLogger.info("Called UsersService me method");
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User doesnt exists"));

        userServiceLogger.info("Successfully returned required user info");
        return new MeResponse(
                userEntity.getId(),
                userEntity.getUsername(),
                userEntity.getEmail(),
                userEntity.getProfileImageUrl()
        );
    }
}

package gde.gde_website.users.service;

import gde.gde_website.games.entity.GamesEntity;
import gde.gde_website.games.repository.CommentRepository;
import gde.gde_website.games.repository.GamesRepository;
import gde.gde_website.games.service.GamesService;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.awt.desktop.UserSessionEvent;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class implements business logic of functions which are called inside UsersController
 * @Author: Artemii Gorelov, Egor Grishin
 */
@Service
@RequiredArgsConstructor
public class UsersService {
    private final CommentRepository commentRepository;
    private final static Logger userServiceLogger = LoggerFactory.getLogger(UsersService.class);

    private final UsersRepository userRepository;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    private final GamesRepository gamesRepository;
    private final GamesService gamesService;

    /**
     *
     * @param request - registration request for a new user
     * It contains following information:
     *                username,
     *                email,
     *                password,
     *                profileImageUrl
     * @return - New registered user session token
     * @throws ResponseStatusException with code 409 if in registration request user include email or username which is already registered.
     *
     * @Author: Egor Grishin
     */
    public LoginResponse register(RegisterRequest request) {
        userServiceLogger.info("Called UsersService register method");
        if (userRepository.existsByEmail(request.email())) {
            userServiceLogger.error("Entered email is already used");
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User with such email is already registered");
        }
        if (userRepository.existsByUsername(request.username())) {
            userServiceLogger.error("Entered username is already used");
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User with such username is already registered");
        }

        String hashedPassword = passwordEncoder.encode(request.password());

        UserEntity newUser = new UserEntity(
                request.username(),
                request.email(),
                hashedPassword,
                request.profileImageUrl(),
                request.isFromTatarstan()
        );

        UserEntity savedUser = userRepository.save(newUser);
        userServiceLogger.info("Successfully saved new user");

        String token = jwtUtils.generateToken(savedUser.getId(), savedUser.getRole());
        userServiceLogger.info("Successfully generated new user token");
        return new LoginResponse(token);
    }

    /**
     *
     * @param request - user login request which contains following information:
     *                authInfo,
     *                isEmail,
     *                password
     * authInfo can be either email or username depending on isEmail flag value.
     * @return - session token of successfully log-inned user
     * @throws  ResponseStatusException with:
     * Code 401 if user with requested email does not find in database
     * Code 401 if user with requested username does not find in database
     * Code 401 if user requested incorrect password
     *
     * @Author: Egor Grishin
     */
    public LoginResponse login(LoginRequest request) {
        userServiceLogger.info("Called UsersService login method");

        UserEntity user;

        if (request.isEmail()) {
            user = userRepository.findByEmail(request.authInfo())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No user with such email exists"));
        } else {
            user = userRepository.findByUsername(request.authInfo())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No user with such username exists"));
        }

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            userServiceLogger.error("Entered password is incorrect");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Incorrect password");
        }

        String token = jwtUtils.generateToken(user.getId(), user.getRole());
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
                userEntity.getProfileImageUrl(),
                userEntity.getIsFromTatarstan(),
                userEntity.getRole()
        );
    }

    /**
     * This method is used for getting all users list
     * @param adminUserId - admin who requests getting list of all users
     * @return - list of all users
     * @Author: Artemii Gorelov
     */
    public UserListResponse getAllUsers(Long adminUserId) {
        UserEntity admin = userRepository.findById(adminUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Admin not found"));
        if (admin.getRole() != UserRole.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admins can view user list");
        }

        List<UserEntity> users = userRepository.findAllByOrderByCreatedAtDesc();
        List<UserListResponse.UserInfo> userInfoList = users.stream()
                .map(user -> new UserListResponse.UserInfo(
                        user.getId(), user.getUsername(), user.getEmail(),
                        user.getProfileImageUrl(), user.getRole(), user.getIsFromTatarstan()
                ))
                .collect(Collectors.toList());

        return new UserListResponse(userInfoList);
    }

    /**
     * This method is used for promoting user to admin by admin
     * @param userId - user to be promoted
     * @param adminId - admin who promoting
     * @Author: Artemii Gorelov
     */
    @Transactional
    public void promoteUserToAdmin(Long userId, Long adminId) {
        checkAdminRights(adminId);
        UserEntity targetUser = getUserOrThrow(userId);
        if (targetUser.getRole() == UserRole.ADMIN) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User to be promoted is already an admin");
        if (targetUser.getRole() == UserRole.BANNED) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User to be promoted is banned");
        targetUser.setRole(UserRole.ADMIN);
        userRepository.save(targetUser);
    }

    /**
     * This method is used for demoting admins
     * @param userId - user to be demoted
     * @param adminId - admin who is devoting
     * @Author: Artemii Gorelov
     */
    @Transactional
    public void demoteUserFromAdmin(Long userId, Long adminId) {
        checkAdminRights(adminId);
        UserEntity targetUser = getUserOrThrow(userId);
        if (targetUser.getRole() != UserRole.ADMIN) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User to be promoted is not admin");
        targetUser.setRole(UserRole.DEVELOPER);
        userRepository.save(targetUser);
    }

    /**
     * This method is used for banning users
     * @param userId - user to be banned
     * @param adminId - admin who is banning user
     * @Author: Artemii Gorelov
     */
    @Transactional
    public void banUser(Long userId, Long adminId) {
        checkAdminRights(adminId);
        UserEntity targetUser = getUserOrThrow(userId);
        if (targetUser.getRole() == UserRole.BANNED) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is already banned");
        targetUser.setRole(UserRole.BANNED);
        userRepository.save(targetUser);
        commentRepository.deleteAllByUserId(userId);
    }

    /**
     * This method is used for unbanning users
     * @param userId - user to be unbanned
     * @param adminId - admin who is unbanning user
     * @Author: Artemii Gorelov
     */
    @Transactional
    public void unbanUser(Long userId, Long adminId) {
        checkAdminRights(adminId);
        UserEntity targetUser = getUserOrThrow(userId);
        if (targetUser.getRole() != UserRole.BANNED) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is not banned");
        targetUser.setRole(UserRole.DEVELOPER);
        userRepository.save(targetUser);
    }

    /**
     * This method is used for deleting user
     * @param userId - user to be deleted
     * @param adminId - admin who deleting user
     * @Author: Artemii Gorelov
     */
    @Transactional
    public void deleteUser(Long userId, Long adminId) {
        checkAdminRights(adminId);
        UserEntity targetUser = getUserOrThrow(userId);
        gamesService.deleteGamesByAuthor(targetUser.getId());
        userRepository.delete(targetUser);
    }

    /**
     * This method is used for checking is user have permissions to update or delete games
     * @param adminUserId - requested user id
     * @Author: Artemii Gorelov
     */
    private void checkAdminRights(Long adminUserId) {
        UserEntity admin = userRepository.findById(adminUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Admin not found"));
        if (admin.getRole() != UserRole.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin access required");
        }
    }

    /**
     * This method is used for throwing user not found response status exception
     * @param userId - requested user id
     * @return returns {@code ResponseStatusException with status NOT_FOUND}
     * @Author: Artemii Gorelov
     */
    private UserEntity getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }
}

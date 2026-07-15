package gde.gde_website.users.service;

import gde.gde_website.games.entity.GamesEntity;
import gde.gde_website.games.entity.TagTypeEntity;
import gde.gde_website.games.mapper.GamesMapper;
import gde.gde_website.games.model.GamesPageResponse;
import gde.gde_website.games.repository.GamesRepository;
import gde.gde_website.games.repository.TagTypeRepository;
import gde.gde_website.users.entity.UserEntity;
import gde.gde_website.users.model.ChangePasswordRequest;
import gde.gde_website.users.model.PublicUserProfileResponse;
import gde.gde_website.users.model.UpdateProfileRequest;
import gde.gde_website.users.model.UserProfileUpdateResponse;
import gde.gde_website.users.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

/**
 * This class implements business logic for managing a user's profile:
 * updating profile fields, changing password, deleting account, and
 * retrieving public profile information along with a user's games.
 *
 * @Author: Artemii Gorelov
 */
@Service
@RequiredArgsConstructor
public class UsersProfileService {
    private static final Logger usersProfileServiceLogger = LoggerFactory.getLogger(UsersProfileService.class);

    private final UsersRepository usersRepository;
    private final GamesRepository gamesRepository;
    private final TagTypeRepository tagTypeRepository;
    private final GamesMapper gamesMapper;
    private final PasswordEncoder passwordEncoder;

    /**
     * Updates username, email and/or profile image URL of the specified user.
     * Fields that are null in the request are left unchanged; blank profileImageUrl clears it.
     *
     * @param userId - id of the user whose profile is being updated
     * @param request - profile fields to update
     * @return updated user profile information
     * @throws ResponseStatusException with code {@code 404} if user with given id does not exist
     * @throws ResponseStatusException with code {@code 409} if requested username or email is already taken by another user
     * @Author: Artemii Gorelov
     */
    @Transactional
    public UserProfileUpdateResponse updateProfile(Long userId, UpdateProfileRequest request) {
        usersProfileServiceLogger.info("Called updateProfile from userProfileService method");
        UserEntity user = findByIdOrThrow(userId);

        if (request.username() != null && !request.username().isBlank() && !request.username().equals(user.getUsername())) {
            if (usersRepository.existsByUsername(request.username())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already taken");
            }
            user.setUsername(request.username());
        }

        if (request.email() != null && !request.email().isBlank()
                && !request.email().equalsIgnoreCase(user.getEmail())) {
            if (usersRepository.existsByEmail(request.email())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already taken");
            }
            user.setEmail(request.email());
        }

        if (request.profileImageUrl() != null) {
            user.setProfileImageUrl(request.profileImageUrl().isBlank() ? null : request.profileImageUrl());
        }

        return toProfileResponse(user);
    }

    /**
     * Returns the public profile of a user, including their total game count.
     *
     * @param id - id of the user whose public profile is requested
     * @return public profile information, safe to expose to any visitor
     * @throws ResponseStatusException with code {@code 404} if user with given id does not exist
     * @Author: Artemii Gorelov
     */
    @Transactional(readOnly = true)
    public PublicUserProfileResponse getPublicProfile(Long id) {
        usersProfileServiceLogger.info("Called UsersProfileService getPublicProfile method");
        UserEntity user = usersRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        long gamesCount = gamesRepository.countByAuthorId(id);

        return new PublicUserProfileResponse(
                user.getId(),
                user.getUsername(),
                user.getProfileImageUrl(),
                gamesCount,
                user.getCreatedAt()
        );
    }

    /**
     * Changes the password of the specified user after verifying the old password.
     *
     * @param userId - id of the user whose password is being changed
     * @param request - old and new password values
     * @throws ResponseStatusException with code {@code 404} if user with given id does not exist
     * @throws ResponseStatusException with code {@code 400} if old password is incorrect or new password is null/blank
     * @Author: Artemii Gorelov
     */
    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest request) {
        usersProfileServiceLogger.info("Called UsersProfileService changePassword method");
        UserEntity user = findByIdOrThrow(userId);

        if (!passwordEncoder.matches(request.oldPassword(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Old password is incorrect");
        }
        if (request.newPassword() == null || request.newPassword().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "New password must not be empty");
        }

        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
    }

    /**
     * Deletes the specified user's account along with all games created by them.
     *
     * @param userId - id of the user to be deleted
     * @throws ResponseStatusException with code {@code 404} if user with given id does not exist
     * @Author: Artemii Gorelov
     */
    @Transactional
    public void deleteAccount(Long userId) {
        usersProfileServiceLogger.info("Called UsersProfileService deleteAccount method");
        UserEntity user = findByIdOrThrow(userId);

        gamesRepository.deleteAllByAuthorId(user.getId());
        usersRepository.delete(user);
    }

    /**
     * Returns a paginated list of games created by the specified author, with tags
     * grouped by tag type and author information attached to each game.
     *
     * @param authorId - id of the author whose games are requested
     * @param pageable - pagination information
     * @return page of games belonging to the specified author
     * @throws ResponseStatusException with code {@code 404} if user with given id does not exist
     * @Author: Artemii Gorelov
     */
    @Transactional(readOnly = true)
    public Page<GamesPageResponse> getUserGames(Long authorId, Pageable pageable) {
        usersProfileServiceLogger.info("Called UsersProfileService getUserGames method");
        UserEntity author = usersRepository.findById(authorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Page<GamesEntity> gamesPage = gamesRepository.findAllByAuthorIdAndIsHiddenFalse(authorId, pageable);

        List<String> tagTypesNames = tagTypeRepository.findAll().stream()
                .map(TagTypeEntity::getType)
                .toList();

        Map<Long, UserEntity> authorsMap = Map.of(author.getId(), author);

        return gamesPage.map(game -> gamesMapper.gamesEntityToGamesPageResponse(game, tagTypesNames, authorsMap, List.of()));
    }

    /**
     * Finds a user by id or throws a NOT_FOUND response status exception.
     *
     * @param id - user id to look up
     * @return found user entity
     * @throws ResponseStatusException with code {@code 404} if user with given id does not exist
     * @Author: Artemii Gorelov
     */
    private UserEntity findByIdOrThrow(Long id) {
        return usersRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    /**
     * Maps a user entity to a profile update response.
     *
     * @param user - user entity to transform
     * @return profile update response containing current user data
     * @Author: Artemii Gorelov
     */
    private UserProfileUpdateResponse toProfileResponse(UserEntity user) {
        return new UserProfileUpdateResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getProfileImageUrl()
        );
    }
}
package gde.gde_website.games.mapper;

import gde.gde_website.games.entity.CommentEntity;
import gde.gde_website.games.model.AuthorResponse;
import gde.gde_website.games.model.GamesCommentResponse;
import gde.gde_website.users.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Mapper for converting comment entities into API response DTOs.
 * Supports mapping with either a preloaded authors map for paginated reads
 * or a single preloaded author for comment mutation responses.
 *
 * @Author: Egor Grishin
 */
@Component
@RequiredArgsConstructor
public class GamesCommentsMapper {
    /**
     * Maps a comment entity to response using preloaded authors indexed by user id.
     * Intended for paginated reads where all authors of current page are loaded in batch.
     *
     * @param comment - comment entity to map
     * @param authorsMap - map of loaded authors indexed by user id
     * @return comment response with author data when available
     */
    public GamesCommentResponse commentEntityToCommentResponse(CommentEntity comment, Map<Long, UserEntity> authorsMap) {
        UserEntity author = authorsMap.get(comment.getUserId());

        AuthorResponse authorResp = null;
        if (author != null) { authorResp = getAuthorResponseFromUserEntity(author); }

        return new GamesCommentResponse(
                comment.getId(),
                authorResp,
                comment.getText(),
                comment.getCreatedAt(),
                comment.getUpdatedAt()
        );
    }

    /**
     * Maps a comment entity to response using a single preloaded original author.
     * Intended for create, update and delete responses.
     *
     * @param comment - comment entity to map
     * @param author - preloaded original author of the comment
     * @return comment response with author data when available
     */
    public GamesCommentResponse commentEntityToCommentResponse(CommentEntity comment, UserEntity author) {
        AuthorResponse authorResp = null;
        if (author != null) { authorResp = getAuthorResponseFromUserEntity(author); }

        return new GamesCommentResponse(
                comment.getId(),
                authorResp,
                comment.getText(),
                comment.getCreatedAt(),
                comment.getUpdatedAt()
        );
    }

    private AuthorResponse getAuthorResponseFromUserEntity(UserEntity user) {
        return new AuthorResponse(
                user.getId(),
                user.getUsername(),
                user.getProfileImageUrl(),
                user.getEmail()
        );
    }
}

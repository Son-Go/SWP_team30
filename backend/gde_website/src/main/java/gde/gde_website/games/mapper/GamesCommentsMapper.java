package gde.gde_website.games.mapper;

import gde.gde_website.games.entity.CommentEntity;
import gde.gde_website.games.model.AuthorResponse;
import gde.gde_website.games.model.GamesCommentResponse;
import gde.gde_website.users.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class GamesCommentsMapper {
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

    private AuthorResponse getAuthorResponseFromUserEntity(UserEntity user) {
        return new AuthorResponse(
                user.getUsername(),
                user.getProfileImageUrl(),
                user.getEmail()
        );
    }
}

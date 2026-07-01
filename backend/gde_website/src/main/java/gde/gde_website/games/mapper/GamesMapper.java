package gde.gde_website.games.mapper;

import gde.gde_website.games.entity.*;
import gde.gde_website.games.model.*;
import gde.gde_website.games.repository.GameScreenshotsRepository;
import gde.gde_website.users.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * This is a class responsible for transformations between objects
 * @Author: Artemii Gorelov, Egor Grishin
 */
@Component
@RequiredArgsConstructor
public class GamesMapper {

    private final GameScreenshotsRepository gameScreenshotsRepository;

    /**
     * This method is used to transform game response entity to response
     * @param entity - entity to be transformed
     * @param currentUserId - current user id
     * @param author - game author
     * @param screenshots - list of screenshots links
     * @return returns game response object
     * @Author: Egor Grishin
     */
    public GamesCardResponse entityToResponse(GamesEntity entity, Long currentUserId, AuthorResponse author, List<String> screenshots) {
        boolean isOwner = currentUserId != null && currentUserId.equals(entity.getAuthorId());

        return new GamesCardResponse(
                entity.getId(),
                entity.getAuthorId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getBannerUrl(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                isOwner,
                author,
                entity.getGameTags().stream().map(gameTagEntity ->
                        gameTagEntity.getTag().getName()).toList(),
                screenshots
        );
    }

    /**
     * This method is used to transform entity to games
     * @param entity - entity to be transformed
     * @return new game object
     */
    public Games entityToGames(GamesEntity entity) {

        List<String> screenshots = gameScreenshotsRepository.findAllByGameId(entity.getId())
                .stream()
                .map(GamesScreenshotEntity::getUrl)
                .toList();

        return new Games(
                entity.getId(),
                entity.getAuthorId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getBannerUrl(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getGameTags().stream().
                        map(gameTagEntity -> gameTagEntity.getTag().getName()).toList(),
                screenshots
        );
    }

    /**
     * Builds paginated game list response item with grouped tags by tag type.
     * Every tag type from {@code tagTypesNames} is present in the resulting map,
     * even if the current game has no tags of that type.
     *
     * @param game - game entity to transform
     * @param tagTypesNames - ordered list of all tag type names that must appear in response
     * @return paginated game response item with grouped tags and author info
     */
    public GamesPageResponse gamesEntityToGamesPageResponse(GamesEntity game, List<String> tagTypesNames, Map<Long, UserEntity> authorsMap) {
        List<TagEntity> tags = game.getGameTags().stream()
                .map(GameTagEntity::getTag).toList();

        Map<String, List<String>> separatedTags = new LinkedHashMap<>();

        for (String tagTypeName : tagTypesNames) {
            separatedTags.put(tagTypeName, new ArrayList<>());
        }

        for (TagEntity tag : tags) {
            separatedTags.get(tag.getTagType().getType()).add(tag.getName());
        }

        UserEntity author = authorsMap.get(game.getAuthorId());

        AuthorResponse authorResp = null;
        if (author != null) {
            authorResp = new AuthorResponse(
                    author.getUsername(),
                    author.getProfileImageUrl(),
                    author.getEmail()
            );
        }

        return new GamesPageResponse(
                game.getId(),
                game.getAuthorId(),
                game.getTitle(),
                game.getDescription(),
                game.getBannerUrl(),
                authorResp,
                separatedTags
        );
    }
}

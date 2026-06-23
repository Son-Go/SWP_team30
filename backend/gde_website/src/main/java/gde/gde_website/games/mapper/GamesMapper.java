package gde.gde_website.games.mapper;

import gde.gde_website.games.entity.GamesEntity;
import gde.gde_website.games.model.Games;
import gde.gde_website.games.model.GamesCardResponce;
import org.springframework.stereotype.Component;

/**
 * This is a class responsible for transformations between objects
 * @Author: Artemii Gorelov, Egor Grishin
 */
@Component
public class GamesMapper {

    /**
     * This method is used to transform game response entity to response
     * @param entity - entity to be transformed
     * @param currentUserId - current user id
     * @return returns game response object
     * @Author: Egor Grishin
     */
    public GamesCardResponce entityToResponse(GamesEntity entity, Long currentUserId) {
        boolean isOwner = currentUserId != null && currentUserId.equals(entity.getAuthorId());

        return new GamesCardResponce(
                entity.getId(),
                entity.getAuthorId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getBannerUrl(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                isOwner,
                entity.getGameTags().stream().map(gameTagEntity ->
                        gameTagEntity.getTag().getName()).toList()
        );
    }

    /**
     * This method is used for transforming game to entity
     * @param games - game to be transformed
     * @return new games entity object
     * @Author: Artemii Gorelov
     */
    public GamesEntity gamesToEntity(Games games) {
        return new GamesEntity(
                games.authorId(),
                games.title(),
                games.description(),
                games.bannerUrl()
        );
    }

    /**
     * This method is used to transform entity to games
     * @param entity - entity to be transformed
     * @return new game object
     */
    public Games entityToGames(GamesEntity entity) {
        return new Games(
                entity.getId(),
                entity.getAuthorId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getBannerUrl(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getGameTags().stream().
                        map(gameTagEntity -> gameTagEntity.getTag().getName()).toList()
        );
    }
}

package gde.gde_website.games.mapper;

import gde.gde_website.games.entity.GamesEntity;
import gde.gde_website.games.model.Games;
import gde.gde_website.games.model.GamesResponce;
import org.springframework.stereotype.Component;

@Component
public class GamesMapper {

    public GamesResponce entityToResponce(GamesEntity entity, Long currentUserId) {
        boolean isOwner = currentUserId != null && currentUserId.equals(entity.getAuthorId());

        return new GamesResponce(
                entity.getId(),
                entity.getAuthorId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getBannerUrl(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                isOwner
        );
    }

    public GamesEntity gamesToEntity(Games games) {
        return new GamesEntity(
                games.authorId(),
                games.title(),
                games.description(),
                games.bannerUrl()
        );
    }

    public Games entityToGames(GamesEntity entity) {
        return new Games(
                entity.getId(),
                entity.getAuthorId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getBannerUrl(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}

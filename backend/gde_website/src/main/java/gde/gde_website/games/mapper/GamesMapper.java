package gde.gde_website.games.mapper;

import gde.gde_website.games.entity.GamesEntity;
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
}

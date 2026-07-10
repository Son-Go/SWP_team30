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
    /**
     * This method is used to transform game response entity to response.
     * Groups tags by tag type and keeps all provided tag types in the result map,
     * even when the current game has no tags of some type.
     *
     * @param game - game to be transformed
     * @param currentUserId - current user id
     * @param author - game author
     * @param screenshots - screenshots grouped into {@code videos} and {@code pictures} lists
     * @param tagTypesNames - ordered list of all tag type names that must appear in response
     * @return returns game response object
     * @Author: Egor Grishin
     */
    public GamesCardResponse gamesEntityToGamesCardResponse(GamesEntity game,
                                                            Long currentUserId,
                                                            AuthorResponse author,
                                                            Map<String, List<String>> screenshots,
                                                            List<String> tagTypesNames) {
        boolean isOwner = currentUserId != null && currentUserId.equals(game.getAuthorId());

        boolean isApproved = game.isApproved();

        List<TagEntity> tags = getTagsEntitiesByGamesEntity(game);

        Map<String, List<String>> separatedTags = getSeparatedTags(tagTypesNames, tags);

        return new GamesCardResponse(
                game.getId(),
                game.getAuthorId(),
                game.getTitle(),
                game.getShortDescription(),
                game.getDescription(),
                game.getBannerUrl(),
                game.getCreatedAt(),
                game.getUpdatedAt(),
                isOwner,
                isApproved,
                author,
                separatedTags,
                screenshots
        );
    }

    /**
     * This method is used to transform entity to games.
     * Keeps create/update response contract unchanged and returns tags grouped by tag type
     * together with screenshots grouped into {@code videos} and {@code pictures}.
     *
     * @param game - entity to be transformed
     * @return new game object
     */
    public Games entityToGames(GamesEntity game,
                               List<String> tagTypesNames,
                               Map<String, List<String>> screenshots) {
        List<TagEntity> tags = getTagsEntitiesByGamesEntity(game);

        return new Games(
                game.getId(),
                game.getAuthorId(),
                game.getTitle(),
                game.getShortDescription(),
                game.getDescription(),
                game.getBannerUrl(),
                game.getCreatedAt(),
                game.getUpdatedAt(),
                getSeparatedTags(tagTypesNames, tags),
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
     * @param authorsMap - map of authors indexed by author id for page items on current result page
     * @return paginated game response item with grouped tags and author info
     */
    public GamesPageResponse gamesEntityToGamesPageResponse(GamesEntity game, List<String> tagTypesNames, Map<Long, UserEntity> authorsMap) {
        List<TagEntity> tags = getTagsEntitiesByGamesEntity(game);

        Map<String, List<String>> separatedTags = getSeparatedTags(tagTypesNames, tags);

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
                game.getShortDescription(),
                game.getDescription(),
                game.getBannerUrl(),
                authorResp,
                game.isApproved(),
                separatedTags
        );
    }

    /**
     * Extracts tag entities linked with the provided game entity.
     *
     * @param game - game entity with loaded game-tag relations
     * @return list of tag entities linked with the game
     */
    private List<TagEntity> getTagsEntitiesByGamesEntity(GamesEntity game) {
        return game.getGameTags().stream().map(GameTagEntity::getTag).toList();
    }

    /**
     * Groups provided tags by tag type name and pre-initializes all known tag types with empty lists.
     *
     * @param tagTypesNames - ordered list of all tag type names that must appear in response
     * @param tags - tag entities linked with a game
     * @return ordered map where key is tag type name and value is list of tag names of that type
     */
    private Map<String, List<String>> getSeparatedTags(List<String> tagTypesNames, List<TagEntity> tags) {
        Map<String, List<String>> separatedTags = new LinkedHashMap<>();

        for (String tagTypeName : tagTypesNames) {
            separatedTags.put(tagTypeName, new ArrayList<>());
        }

        for (TagEntity tag : tags) {
            separatedTags.get(tag.getTagType().getType()).add(tag.getName());
        }

        return separatedTags;
    }
}

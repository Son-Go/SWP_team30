package gde.gde_website.games.model;

/**
 * This is a record which represents game tag
 * @param gameId - game id
 * @param tagId - tag id
 * @Author: Artemii Gorelov
 */
public record GameTag(
        Long gameId,
        Integer tagId
) {
}

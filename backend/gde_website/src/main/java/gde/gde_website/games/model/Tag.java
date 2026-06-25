package gde.gde_website.games.model;

/**
 * This is a record which represents game genre
 * @param id - id of tag
 * @param name - name of genre
 * @Author: Artemii Gorelov
 */
public record Tag(
        Integer id,
        GameGanre name
) {
}

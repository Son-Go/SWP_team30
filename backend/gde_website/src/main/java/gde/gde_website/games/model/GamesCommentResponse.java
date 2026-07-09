package gde.gde_website.games.model;

public record GamesCommentResponse(
        Long id,
        AuthorResponse author,
        String text
) {
}

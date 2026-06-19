package gde.gde_website.users.model;

public record MeResponse(
        Long id,
        String username,
        String email,
        String profileImageUrl
) {}
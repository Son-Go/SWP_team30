package gde.gde_website.users.model;

public record RegisterRequest(
        String username,
        String email,
        String password,
        String profileImageUrl
) {}
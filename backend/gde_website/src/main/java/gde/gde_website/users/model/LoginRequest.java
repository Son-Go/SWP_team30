package gde.gde_website.users.model;

public record LoginRequest(
        String email,
        String password
) {
}

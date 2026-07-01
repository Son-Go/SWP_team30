package gde.gde_website.users.model;

import com.nimbusds.openid.connect.sdk.claims.UserInfo;

import java.util.List;

/**
 * This record is used for representing list of all users
 * @param users 0 list of all users from db
 */
public record UserListResponse(List<UserInfo> users) {
    public record UserInfo(
            Long id,
            String username,
            String email,
            String profileImageUrl,
            UserRole role,
            Boolean isFromTatarstan
    ) {}
}
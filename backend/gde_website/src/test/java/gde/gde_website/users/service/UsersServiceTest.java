package gde.gde_website.users.service;

import gde.gde_website.games.repository.CommentRepository;
import gde.gde_website.games.repository.GamesRepository;
import gde.gde_website.games.service.GamesService;
import gde.gde_website.security.JwtUtils;
import gde.gde_website.users.entity.UserEntity;
import gde.gde_website.users.model.UserRole;
import gde.gde_website.users.repository.UsersRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsersServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UsersRepository userRepository;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private GamesRepository gamesRepository;

    @Mock
    private GamesService gamesService;

    @InjectMocks
    private UsersService usersService;

    @Test
    void unbanUserRestoresGamesVisibility() {
        UserEntity admin = new UserEntity("admin", "admin@example.com", "hash", "");
        admin.setRole(UserRole.ADMIN);
        UserEntity bannedUser = new UserEntity("user", "user@example.com", "hash", "");
        bannedUser.setRole(UserRole.BANNED);
        when(userRepository.findById(99L)).thenReturn(Optional.of(admin));
        when(userRepository.findById(42L)).thenReturn(Optional.of(bannedUser));
        when(userRepository.save(bannedUser)).thenReturn(bannedUser);

        usersService.unbanUser(42L, 99L);

        verify(gamesService).restoreGamesByAuthor(42L);
    }
}

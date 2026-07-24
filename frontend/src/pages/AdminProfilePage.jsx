import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import {
    approveGameByAdmin,
    banUser,
    deleteUserByAdmin,
    demoteUser,
    getAdminGames,
    getAdminUsers,
    getGameById,
    promoteUser,
    rejectGameByAdmin,
    restoreUserGames,
    setGameFeaturedState,
    unbanUser,
} from "../api/api";
import ErrorState from "../components/ErrorState";
import Loader from "../components/Loader";
import { useAuth } from "../context/auth-context";

function AvatarPlaceholder({ username, className }) {
    const initial = username?.[0]?.toUpperCase() || "?";

    return <div className={className}>{initial}</div>;
}

function getUsersFromResponse(data) {
    return Array.isArray(data?.users) ? data.users : [];
}

function getGamesFromResponse(data) {
    return Array.isArray(data?.games) ? data.games : [];
}

function getRoleLabel(role) {
    if (role === "ADMIN") return "Администратор";
    if (role === "BANNED") return "Забанен";
    return "Разработчик";
}

function getGameStatus(game) {
    if (game.hidden) {
        return {
            label: "Скрыта",
            className: "admin-game-status-hidden",
        };
    }

    if (game.approved) {
        return {
            label: "Одобрена",
            className: "admin-game-status-approved",
        };
    }

    return {
        label: "На модерации",
        className: "admin-game-status-pending",
    };
}

function getFeaturedState(gameData) {
    const featuredTags = gameData?.gameTags?.featured || [];

    return featuredTags.some(
        (tag) => typeof tag === "string" && tag.toLowerCase() === "featured",
    );
}

function formatDate(value) {
    if (!value) return "Дата не указана";

    return new Date(value).toLocaleDateString("ru-RU", {
        day: "2-digit",
        month: "2-digit",
        year: "numeric",
    });
}

function AdminProfilePage() {
    const { token, user: currentUser } = useAuth();
    const navigate = useNavigate();

    const [activeTab, setActiveTab] = useState("users");

    const [users, setUsers] = useState([]);
    const [usersLoading, setUsersLoading] = useState(true);
    const [usersError, setUsersError] = useState("");

    const [games, setGames] = useState([]);
    const [gamesLoading, setGamesLoading] = useState(false);
    const [gamesError, setGamesError] = useState("");
    const [gamesLoaded, setGamesLoaded] = useState(false);

    const [successMessage, setSuccessMessage] = useState("");
    const [actionKey, setActionKey] = useState("");
    const [banTarget, setBanTarget] = useState(null);

    const [banOptions, setBanOptions] = useState({
        deleteComments: false,
        hideGames: true,
    });

    async function loadUsers() {
        try {
            setUsersLoading(true);
            setUsersError("");

            const data = await getAdminUsers(token);
            setUsers(getUsersFromResponse(data));
        } catch (err) {
            setUsersError(err.message || "Не удалось загрузить пользователей");
        } finally {
            setUsersLoading(false);
        }
    }

    async function loadGames() {
        try {
            setGamesLoading(true);
            setGamesError("");

            const data = await getAdminGames(token);
            const adminGames = getGamesFromResponse(data);
            const hydratedGames = await Promise.all(
                adminGames.map(async (game) => {
                    try {
                        const detailedGame = await getGameById(game.id, token);
                        return {
                            ...game,
                            featured: getFeaturedState(detailedGame),
                        };
                    } catch {
                        return {
                            ...game,
                            featured: false,
                        };
                    }
                }),
            );

            setGames(hydratedGames);
            setGamesLoaded(true);
        } catch (err) {
            setGamesError(err.message || "Не удалось загрузить игры");
        } finally {
            setGamesLoading(false);
        }
    }

    useEffect(() => {
        if (!token) return;

        loadUsers();
    }, [token]);

    useEffect(() => {
        if (activeTab === "games" && !gamesLoaded && token) {
            loadGames();
        }
    }, [activeTab, gamesLoaded, token]);

    function updateUserRole(userId, role) {
        setUsers((previous) =>
            previous.map((item) => (item.id === userId ? { ...item, role } : item)),
        );
    }

    function updateGameApproval(gameId, approved) {
        setGames((previous) =>
            previous.map((item) =>
                item.id === gameId ? { ...item, approved } : item,
            ),
        );
    }

    function updateGameFeaturedState(gameId, featured) {
        setGames((previous) =>
            previous.map((item) =>
                item.id === gameId ? { ...item, featured } : item,
            ),
        );
    }

    async function runAction(key, action, successText) {
        try {
            setActionKey(key);
            setUsersError("");
            setGamesError("");
            setSuccessMessage("");

            await action();
            setSuccessMessage(successText);
        } catch (err) {
            const message = err.message || "Не удалось выполнить действие";

            if (activeTab === "games") {
                setGamesError(message);
            } else {
                setUsersError(message);
            }
        } finally {
            setActionKey("");
        }
    }

    function openBanModal(targetUser) {
        setBanTarget(targetUser);
        setBanOptions({
            deleteComments: false,
            hideGames: true,
        });
    }

    function closeBanModal() {
        if (actionKey) return;

        setBanTarget(null);
    }

    function handleBanConfirm() {
        if (!banTarget) return;

        const targetUser = banTarget;

        runAction(
            `ban-${targetUser.id}`,
            async () => {
                await banUser(targetUser.id, banOptions, token);
                updateUserRole(targetUser.id, "BANNED");
                setBanTarget(null);
            },
            `Пользователь ${targetUser.username} забанен`,
        );
    }

    function handlePromote(targetUser) {
        runAction(
            `promote-${targetUser.id}`,
            async () => {
                await promoteUser(targetUser.id, token);
                updateUserRole(targetUser.id, "ADMIN");
            },
            `Пользователь ${targetUser.username} теперь администратор`,
        );
    }

    function handleDemote(targetUser) {
        const confirmed = window.confirm(
            `Снять права администратора у пользователя ${targetUser.username}?`,
        );

        if (!confirmed) return;

        runAction(
            `demote-${targetUser.id}`,
            async () => {
                await demoteUser(targetUser.id, token);
                updateUserRole(targetUser.id, "DEVELOPER");
            },
            `Права администратора у ${targetUser.username} сняты`,
        );
    }

    function handleUnban(targetUser) {
        runAction(
            `unban-${targetUser.id}`,
            async () => {
                await unbanUser(targetUser.id, token);
                updateUserRole(targetUser.id, "DEVELOPER");
            },
            `Пользователь ${targetUser.username} разбанен`,
        );
    }

    function handleRestoreGames(targetUser) {
        runAction(
            `restore-${targetUser.id}`,
            () => restoreUserGames(targetUser.id, token),
            `Игры пользователя ${targetUser.username} восстановлены`,
        );
    }

    function handleDelete(targetUser) {
        const confirmed = window.confirm(
            `Удалить пользователя ${targetUser.username}? Это действие нельзя отменить.`,
        );

        if (!confirmed) return;

        runAction(
            `delete-${targetUser.id}`,
            async () => {
                await deleteUserByAdmin(targetUser.id, token);

                setUsers((previous) =>
                    previous.filter((item) => item.id !== targetUser.id),
                );
            },
            `Пользователь ${targetUser.username} удалён`,
        );
    }

    function handleApproveGame(game) {
        runAction(
            `approve-${game.id}`,
            async () => {
                await approveGameByAdmin(game.id, token);
                updateGameApproval(game.id, true);
            },
            `Игра «${game.title}» одобрена`,
        );
    }

    function handleRejectGame(game) {
        const confirmed = window.confirm(
            `Отклонить игру «${game.title}»? Она останется не одобренной.`,
        );

        if (!confirmed) return;

        runAction(
            `reject-${game.id}`,
            async () => {
                await rejectGameByAdmin(game.id, token);
                updateGameApproval(game.id, false);
            },
            `Игра «${game.title}» отклонена`,
        );
    }

    function handleToggleFeatured(game) {
        const nextFeatured = !Boolean(game.featured);

        runAction(
            `featured-${game.id}`,
            async () => {
                const currentGame = await getGameById(game.id, token);
                await setGameFeaturedState(game.id, token, currentGame, nextFeatured);
                updateGameFeaturedState(game.id, nextFeatured);
            },
            nextFeatured
                ? `Игра «${game.title}» отмечена как Featured`
                : `Игра «${game.title}» убрана из Featured`,
        );
    }

    return (
        <section className="section-lg">
            <div className="page-header">
                <div className="section">
                    <h1 className="page-title">Панель администратора</h1>

                    <p className="page-subtitle">
                        Управление пользователями и модерация игр.
                    </p>
                </div>

                <button
                    type="button"
                    className="button button-ghost"
                    onClick={activeTab === "users" ? loadUsers : loadGames}
                    disabled={Boolean(actionKey)}
                >
                    Обновить
                </button>
            </div>

            {successMessage ? (
                <div className="state-box admin-success">{successMessage}</div>
            ) : null}

            <div
                className="admin-tabs"
                role="tablist"
                aria-label="Разделы админ-панели"
            >
                <button
                    type="button"
                    className={`admin-tab ${activeTab === "users" ? "active" : ""}`}
                    role="tab"
                    aria-selected={activeTab === "users"}
                    onClick={() => setActiveTab("users")}
                >
                    Пользователи
                </button>

                <button
                    type="button"
                    className={`admin-tab ${activeTab === "games" ? "active" : ""}`}
                    role="tab"
                    aria-selected={activeTab === "games"}
                    onClick={() => setActiveTab("games")}
                >
                    Игры
                </button>
            </div>

            {activeTab === "users" ? (
                <>
                    {usersLoading ? (
                        <Loader text="Загружаем пользователей..." />
                    ) : usersError ? (
                        <ErrorState message={usersError} />
                    ) : !users.length ? (
                        <div className="state-box">Пользователи не найдены.</div>
                    ) : (
                        <div className="admin-users-list">
                            {users.map((targetUser) => {
                                const role = targetUser.role || "DEVELOPER";
                                const isCurrentUser = targetUser.id === currentUser?.id;
                                const isSubmitting = actionKey.includes(`-${targetUser.id}`);

                                return (
                                    <article className="card admin-user-card" key={targetUser.id}>
                                        <div className="admin-user-info">
                                            {targetUser.profileImageUrl ? (
                                                <img
                                                    src={targetUser.profileImageUrl}
                                                    alt={`Аватар ${targetUser.username}`}
                                                    className="admin-user-avatar"
                                                />
                                            ) : (
                                                <AvatarPlaceholder
                                                    username={targetUser.username}
                                                    className="admin-user-avatar-placeholder"
                                                />
                                            )}

                                            <div className="section admin-user-details">
                                                <div className="admin-user-title">
                                                    <h2 className="card-title">
                                                        {targetUser.username}
                                                    </h2>

                                                    {isCurrentUser ? (
                                                        <span className="admin-user-current">Вы</span>
                                                    ) : null}
                                                </div>

                                                <span className="card-text">
                          {targetUser.email || "Почта не указана"}
                        </span>

                                                <span
                                                    className={`admin-user-role admin-user-role-${role.toLowerCase()}`}
                                                >
                          {getRoleLabel(role)}
                        </span>
                                            </div>
                                        </div>

                                        <div className="admin-actions">
                                            {role === "BANNED" ? (
                                                <>
                                                    <button
                                                        type="button"
                                                        className="button button-secondary"
                                                        onClick={() => handleUnban(targetUser)}
                                                        disabled={isSubmitting}
                                                    >
                                                        {isSubmitting ? "Обработка..." : "Разбанить"}
                                                    </button>

                                                    <button
                                                        type="button"
                                                        className="button button-ghost"
                                                        onClick={() => handleRestoreGames(targetUser)}
                                                        disabled={isSubmitting}
                                                    >
                                                        Восстановить игры
                                                    </button>
                                                </>
                                            ) : (
                                                <button
                                                    type="button"
                                                    className="button button-danger"
                                                    onClick={() => openBanModal(targetUser)}
                                                    disabled={isSubmitting || isCurrentUser}
                                                >
                                                    Забанить
                                                </button>
                                            )}

                                            {role === "DEVELOPER" ? (
                                                <button
                                                    type="button"
                                                    className="button button-outline"
                                                    onClick={() => handlePromote(targetUser)}
                                                    disabled={isSubmitting || isCurrentUser}
                                                >
                                                    Сделать админом
                                                </button>
                                            ) : null}

                                            {role === "ADMIN" ? (
                                                <button
                                                    type="button"
                                                    className="button button-ghost"
                                                    onClick={() => handleDemote(targetUser)}
                                                    disabled={isSubmitting || isCurrentUser}
                                                >
                                                    Снять права
                                                </button>
                                            ) : null}

                                            <button
                                                type="button"
                                                className="button button-danger"
                                                onClick={() => handleDelete(targetUser)}
                                                disabled={isSubmitting || isCurrentUser}
                                            >
                                                Удалить
                                            </button>
                                        </div>
                                    </article>
                                );
                            })}
                        </div>
                    )}
                </>
            ) : (
                <>
                    {gamesLoading ? (
                        <Loader text="Загружаем игры..." />
                    ) : gamesError ? (
                        <ErrorState message={gamesError} />
                    ) : !games.length ? (
                        <div className="state-box">Игры не найдены.</div>
                    ) : (
                        <div className="admin-games-list">
                            {games.map((game) => {
                                const status = getGameStatus(game);
                                const isSubmitting = actionKey.includes(`-${game.id}`);

                                return (
                                    <article
                                        className="card admin-game-card admin-game-card-clickable"
                                        key={game.id}
                                        role="button"
                                        tabIndex={0}
                                        onClick={() => navigate(`/games/${game.id}`)}
                                        onKeyDown={(event) => {
                                            if (event.key === "Enter" || event.key === " ") {
                                                event.preventDefault();
                                                navigate(`/games/${game.id}`);
                                            }
                                        }}
                                    >
                                        {game.bannerUrl ? (
                                            <img
                                                src={game.bannerUrl}
                                                alt={`Баннер игры ${game.title}`}
                                                className="admin-game-banner"
                                            />
                                        ) : (
                                            <div className="admin-game-banner admin-game-banner-placeholder">
                                                Нет баннера
                                            </div>
                                        )}

                                        <div className="section admin-game-info">
                                            <div className="admin-game-title-row">
                                                <h2 className="card-title">{game.title}</h2>

                                                <span
                                                    className={`admin-game-status ${status.className}`}
                                                >
                          {status.label}
                        </span>
                                            </div>

                                            {game.shortDescription ? (
                                                <p className="card-text admin-game-description">
                                                    {game.shortDescription}
                                                </p>
                                            ) : null}

                                            <p className="admin-game-meta">
                                                Автор: {game.author?.username || "Неизвестный автор"}
                                            </p>

                                            <p className="admin-game-meta">
                                                Создано: {formatDate(game.createdAt)}
                                            </p>
                                        </div>

                                        <div
                                            className="admin-actions"
                                            onClick={(event) => event.stopPropagation()}
                                        >
                                            <button
                                                type="button"
                                                className={`button ${game.featured ? "button-secondary" : "button-outline"}`}
                                                onClick={() => handleToggleFeatured(game)}
                                                disabled={isSubmitting}
                                            >
                                                {isSubmitting
                                                    ? "Обработка..."
                                                    : game.featured
                                                        ? "Удалить из Featured"
                                                        : "Сделать Featured"}
                                            </button>

                                            {!game.approved ? (
                                                <button
                                                    type="button"
                                                    className="button button-secondary"
                                                    onClick={() => handleApproveGame(game)}
                                                    disabled={isSubmitting}
                                                >
                                                    {isSubmitting ? "Обработка..." : "Одобрить"}
                                                </button>
                                            ) : (
                                                <button
                                                    type="button"
                                                    className="button button-danger"
                                                    onClick={() => handleRejectGame(game)}
                                                    disabled={isSubmitting}
                                                >
                                                    {isSubmitting ? "Обработка..." : "Отклонить"}
                                                </button>
                                            )}
                                        </div>
                                    </article>
                                );
                            })}
                        </div>
                    )}
                </>
            )}

            {banTarget ? (
                <div
                    className="admin-modal-backdrop"
                    role="presentation"
                    onMouseDown={closeBanModal}
                >
                    <section
                        className="card admin-ban-modal"
                        role="dialog"
                        aria-modal="true"
                        aria-labelledby="ban-modal-title"
                        onMouseDown={(event) => event.stopPropagation()}
                    >
                        <div className="admin-modal-header">
                            <div>
                                <h2 id="ban-modal-title" className="card-title">
                                    Заблокировать пользователя
                                </h2>

                                <p className="card-text">
                                    Пользователь: <strong>{banTarget.username}</strong>
                                </p>
                            </div>

                            <button
                                type="button"
                                className="admin-modal-close"
                                onClick={closeBanModal}
                                disabled={Boolean(actionKey)}
                                aria-label="Закрыть окно бана"
                            >
                                ×
                            </button>
                        </div>

                        <div className="section">
                            <p className="card-text">
                                Выберите дополнительные действия для пользователя.
                            </p>

                            <label className="admin-option">
                                <input
                                    type="checkbox"
                                    checked={banOptions.deleteComments}
                                    onChange={(event) =>
                                        setBanOptions((previous) => ({
                                            ...previous,
                                            deleteComments: event.target.checked,
                                        }))
                                    }
                                    disabled={Boolean(actionKey)}
                                />
                                Удалить комментарии пользователя
                            </label>

                            <label className="admin-option">
                                <input
                                    type="checkbox"
                                    checked={banOptions.hideGames}
                                    onChange={(event) =>
                                        setBanOptions((previous) => ({
                                            ...previous,
                                            hideGames: event.target.checked,
                                        }))
                                    }
                                    disabled={Boolean(actionKey)}
                                />
                                Скрыть игры пользователя
                            </label>
                        </div>

                        <div className="admin-modal-actions">
                            <button
                                type="button"
                                className="button button-ghost"
                                onClick={closeBanModal}
                                disabled={Boolean(actionKey)}
                            >
                                Отмена
                            </button>

                            <button
                                type="button"
                                className="button button-danger"
                                onClick={handleBanConfirm}
                                disabled={Boolean(actionKey)}
                            >
                                {actionKey ? "Блокировка..." : "Забанить"}
                            </button>
                        </div>
                    </section>
                </div>
            ) : null}
        </section>
    );
}

export default AdminProfilePage;
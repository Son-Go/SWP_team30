import React from "react";
import { Link } from "react-router-dom";

function NotFoundPage() {
  return (
    <section className="section-lg">
      <article className="card section-lg">
        <div className="section">
          <p className="page-subtitle">Ошибка 404</p>
          <h1 className="page-title">Страница не найдена</h1>
          <p className="card-text">Такой страницы нет или ссылка устарела.</p>
        </div>

        <div className="card-actions">
          <Link to="/games" className="button">
            В каталог
          </Link>

          <Link to="/auth" className="button button-ghost">
            Войти
          </Link>
        </div>
      </article>
    </section>
  );
}

export default NotFoundPage;

import React from "react";
import { Link } from "react-router-dom";

function Footer() {
  function scrollToTop() {
    window.scrollTo({
      top: 0,
      behavior: "smooth",
    });
  }

  return (
    <footer className="footer">
      <div className="footer-content">
        <div className="footer-main">
          <section className="footer-column">
            <p className="footer-label">Почта для связи</p>
            <a className="footer-contact-link" href="mailto:hello@gde.ru">
              hello@gde.ru
            </a>

            <p className="footer-label footer-label-spaced">Адрес</p>
            <p className="footer-text">
              Площадка для разработчиков, игроков и игровых проектов.
            </p>
          </section>

          <section className="footer-column">
            <p className="footer-label">Контакты</p>
            <a className="footer-contact-link" href="mailto:hello@gde.ru">
              Написать нам
            </a>

            <p className="footer-label footer-label-spaced">Социальные сети</p>
            <div className="footer-socials">
              <a
                className="footer-social-link"
                href="https://t.me/"
                target="_blank"
                rel="noreferrer"
                aria-label="Telegram"
              >
                TG
              </a>

              <a
                className="footer-social-link"
                href="https://vk.com/"
                target="_blank"
                rel="noreferrer"
                aria-label="ВКонтакте"
              >
                VK
              </a>
            </div>
          </section>

          <section className="footer-brand">
            <Link to="/" className="footer-logo" aria-label="GDE — на главную">
              GDE
            </Link>
            <p className="footer-brand-description">Игры. Люди. Сообщество.</p>
          </section>
        </div>

        <div className="footer-bottom">
          <span>© {new Date().getFullYear()} GDE. Все права защищены.</span>

          <span>Политика конфиденциальности</span>

          <button
            type="button"
            className="footer-top-button"
            onClick={scrollToTop}
          >
            <span aria-hidden="true">↗</span>
            Наверх
          </button>
        </div>
      </div>
    </footer>
  );
}

export default Footer;

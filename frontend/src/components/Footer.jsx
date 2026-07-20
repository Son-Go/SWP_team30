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
            <p className="footer-label">Почта</p>
            <p className="footer-text">hello@gde.ru</p>

            <p className="footer-label footer-label-spaced">Адрес</p>
            <p className="footer-text">ул. Университетская 1, г. Иннополис</p>
          </section>

          <section className="footer-column">
            <p className="footer-label">Телефон</p>
            <p className="footer-text">8 (800) 555-35-35</p>

            <p className="footer-label footer-label-spaced">Социальные сети</p>
            <div className="footer-socials">
              <a
                className="footer-social-link"
                href="https://t.me/lopufeed"
                target="_blank"
                rel="noreferrer"
                aria-label="Telegram"
              >
                <img
                  src="/tg_icon_wht.svg"
                  alt=""
                  className="footer-social-icon"
                />
              </a>
            </div>
          </section>

          <section className="footer-brand">
            <Link to="/" className="footer-logo" aria-label="GDE — на главную">
              <img
                src="/logo_imp.png"
                alt="GDE"
                className="footer-logo-image"
              />
            </Link>
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

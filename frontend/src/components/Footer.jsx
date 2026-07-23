import React from "react";

function Footer() {
  function scrollToTop() {
    window.scrollTo({ top: 0, behavior: "smooth" });
  }

  return (
      <footer className="footer">
        <div className="footer-content">
          <div className="footer-main">
            <div className="footer-column">
              <p className="footer-label">Контакты</p>

              <a
                  className="footer-contact-link"
                  href="mailto:workspacegde@gmail.com"
              >
                workspacegde@gmail.com
              </a>

              <p className="footer-label footer-label-spaced">Соцсети</p>

              <div className="footer-socials">
                <a
                    className="footer-social-link"
                    href="https://vk.ru/gdevenings"
                    target="_blank"
                    rel="noreferrer"
                    aria-label="ВКонтакте"
                >
                  <img
                      className="footer-social-icon"
                      src="/media/vk.webp"
                      alt="ВКонтакте"
                  />
                </a>

                <a
                    className="footer-social-link"
                    href="https://www.youtube.com/@InnoGameDev"
                    target="_blank"
                    rel="noreferrer"
                    aria-label="YouTube"
                >
                  <img
                      className="footer-social-icon"
                      src="/media/youtube.svg"
                      alt="YouTube"
                  />
                </a>

                <a
                    className="footer-social-link"
                    href="https://t.me/gdevenings"
                    target="_blank"
                    rel="noreferrer"
                    aria-label="Telegram"
                >
                  <img
                      className="footer-social-icon"
                      src="/media/tg_icon_wht.svg"
                      alt="Telegram"
                  />
                </a>
              </div>
            </div>

            <div className="footer-column">
              <p className="footer-label">О сообществе</p>

              <p className="footer-text">
                Сообщество разработчиков игр Иннополиса и Татарстана. Хакатоны,
                практики и совместные проекты с 2022 года.
              </p>
            </div>

            <div className="footer-brand">
              <a className="footer-logo" href="/">
                <img
                    className="footer-logo-image"
                    src="/media/logo_imp.png"
                    alt="GDE Evenings"
                />
              </a>

              <p className="footer-brand-description">Game Dev Evenings</p>
            </div>
          </div>

          <div className="footer-bottom">
            <a className="footer-bottom-link" href="/about-community">
              О нас
            </a>

            <span>© {new Date().getFullYear()} GDE Evenings</span>

            <button
                type="button"
                className="footer-top-button"
                onClick={scrollToTop}
            >
              <span>↑</span> Наверх
            </button>
          </div>
        </div>
      </footer>
  );
}

export default Footer;
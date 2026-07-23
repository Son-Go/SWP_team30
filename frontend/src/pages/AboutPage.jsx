import React from "react";
import Footer from "../components/Footer";

const ACTIVITIES = [
  "Растим разработчиков игр",
  "Проводим практики",
  "Организуем хакатоны",
  "Разрабатываем игры",
  "Создаем геймификацию",
  "Помогаем с грантами и издателями",
];

const CASES = [
  "Game Open Lab",
  "GAICA",
  "Anlaut Jam",
  "Дружный джем",
  "Т-Банк — игра",
  "Полка чудес — игра",
  "Simple Sandbox — игра",
  "Breadborn — игра",
  "Геймификация ОЭЗ",
  "Питч-сессия на DID",
];

// Плоский массив партнёров, собранный из старого объекта
const PARTNERS = [
  { name: "VK", logo: "VK.png" },
  { name: "Т-Банк", logo: "tbank.png" },
  { name: "Сбер", logo: "sber.png" },
  { name: "IXBT", logo: "IXBT.png" },
  { name: "Иннополис", logo: "innopolis_c_logo_2022.jpg" },
  { name: "HSE", logo: "hse.png" },
  { name: "Академия наук", logo: "AkademiyanaukRT.jpg" },
  { name: "Игропром", logo: "igroprom.png" },
  { name: "СЛЕТ", logo: "sljot.jpg" },
  { name: "Игровая индустрия", logo: "game_industry.jpg" },
  { name: "DID", logo: "DID.png" },
];

const EXPERTS = [
  { name: "Василий Скобелев", photo: "vSkobelev.jpg" },
  { name: "Кирилл Золовкин", photo: "kZolovkin.jpg" },
  { name: "Сергей Гимельрейх", photo: "sGimmelreih.jpg" },
  { name: "Алексей Макаренков", photo: "aMakarenkov.jpg" },
  { name: "Илья Крапивников", photo: "iKrapivnikov.jpg" },
  { name: "Александр Шакиров", photo: "aShakirov.png" },
  { name: "Виталий Казунов", photo: "vKazunov.jpg" },
  { name: "Михаил Шкредов", photo: "mShkredov.png" },
  { name: "Слава Грис", photo: "SlavaGris.png" },
  { name: "Денис Русаков", photo: "dRusakov.png" },
  { name: "Александр Артемов", photo: "aArtemov.png" },
  { name: "Владислав Полевик", photo: "vPolevik.jpg" },
];

function AboutList({ items }) {
  return (
      <ul className="about-list">
        {items.map((item) => (
            <li key={item}>{item}</li>
        ))}
      </ul>
  );
}

function PartnersMarquee({ partners }) {
  // Дублируем массив для создания эффекта бесконечной прокрутки
  const loopedPartners = [...partners, ...partners, ...partners];

  return (
      <div className="about-partners-marquee">
        <div className="about-partners-track">
          {loopedPartners.map((partner, index) => (
              <div className="about-partner-logo" key={`${partner.name}-${index}`}>
                <img
                    src={`/media/${partner.logo}`}
                    alt={partner.name}
                    loading="lazy"
                />
              </div>
          ))}
        </div>
      </div>
  );
}

function AboutCommunityPage() {
  return (
      <div className="about-community-layout">
        <main>
          <section className="about-hero">
            <video
                className="about-hero-video"
                autoPlay
                muted
                loop
                playsInline
                poster="/media/core_media.png"
            >
              <source src="/media/AboutUs.mp4" type="video/mp4" />
            </video>

            <div className="about-hero-overlay" />

            <div className="about-hero-content">
              <p className="about-hero-kicker">О нас</p>

              <h1 className="about-hero-title">
                Сообщество разработчиков игр
                <br />
                Иннополиса и Татарстана
              </h1>

              <p className="about-hero-directions">
                Разработка игр • Геймификация • Игровые мероприятия • Образование
              </p>
            </div>

            <div className="about-hero-stats" aria-label="Статистика сообщества">
              <div className="about-hero-stat">
                <strong>750+</strong>
                <span>участников</span>
              </div>

              <div className="about-hero-stat">
                <strong>100+</strong>
                <span>игровых проектов</span>
              </div>

              <div className="about-hero-stat">
                <strong>с 2022</strong>
                <span>года</span>
              </div>
            </div>
          </section>

          <div className="about-community-page">
            <section className="about-section">
              <div className="about-section-heading">
                <p className="about-section-kicker">Разделы</p>
                <h2>Что мы делаем?</h2>
              </div>

              <div className="about-split-grid">
                <img
                    className="about-section-image"
                    src="/media/core_media.png"
                    alt="Участники сообщества на игровом мероприятии"
                />

                <div className="about-section-content">
                  <p>
                    GDE объединяет начинающих и опытных специалистов игровой
                    индустрии. Мы создаём пространство для развития навыков,
                    совместных проектов, практики и профессиональных знакомств.
                  </p>

                  <AboutList items={ACTIVITIES} />
                </div>
              </div>
            </section>

            <section className="about-section">
              <div className="about-section-heading">
                <p className="about-section-kicker">Проекты</p>
                <h2>Наши кейсы</h2>
              </div>

              <div className="about-split-grid about-split-grid-reverse">
                <div className="about-section-content">
                  <p>
                    Мы проводим образовательные программы и джемы, создаём игры
                    и помогаем компаниям использовать игровые механики в
                    продуктах и мероприятиях.
                  </p>

                  <AboutList items={CASES} />
                </div>

                <img
                    className="about-section-image"
                    src="/media/core_media.png"
                    alt="Игровые проекты сообщества"
                />
              </div>
            </section>

            {/* Секция партнёров теперь без карточек-категорий, просто одна строка */}
            <section className="about-section" aria-labelledby="partners-title">
              <div className="about-section-heading">
                <p className="about-section-kicker">Сотрудничество</p>
                <h2 id="partners-title">Наши партнёры</h2>
              </div>

              <PartnersMarquee partners={PARTNERS} />
            </section>

            <section className="about-section" aria-labelledby="experts-title">
              <div className="about-section-heading">
                <p className="about-section-kicker">Сообщество</p>
                <h2 id="experts-title">Среди наших гостей и экспертов</h2>
              </div>

              <div className="about-experts-grid">
                {EXPERTS.map((expert) => (
                    <article className="card about-expert-card" key={expert.name}>
                      <img
                          src={`/media/${expert.photo}`}
                          alt={`Фото ${expert.name}`}
                          className="about-expert-photo"
                          loading="lazy"
                      />

                      <h3>{expert.name}</h3>
                      <p>Гость и эксперт мероприятий сообщества GDE.</p>
                    </article>
                ))}
              </div>
            </section>

            <section className="about-cta">
              <div>
                <p className="about-section-kicker">Присоединяйтесь</p>

                <h2>
                  Хотите провести игровой хакатон, разработать игру или стать
                  частью сообщества?
                </h2>
              </div>

              <div className="about-cta-actions">
                <a
                    className="button button-primary"
                    href="https://t.me/gdevenings"
                    target="_blank"
                    rel="noreferrer"
                >
                  Стать участником
                </a>

                <a
                    className="button button-outline"
                    href="https://t.me/lopufeed"
                    target="_blank"
                    rel="noreferrer"
                >
                  Обсудить сотрудничество
                </a>
              </div>
            </section>
          </div>
        </main>

        <div className="about-footer-divider" />
        <Footer />
      </div>
  );
}

export default AboutCommunityPage;
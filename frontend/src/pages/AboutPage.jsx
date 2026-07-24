import React from "react";
import Footer from "../components/Footer";

const ACTIVITIES = [
  {
    title: "Растим разработчиков игр",
    description:
      "Наше сообщество — это пространство для развития навыков. Мы помогаем начинающим и опытным разработчикам расти через менторство, обучение и совместную работу над проектами.",
  },
  {
    title: "Проводим практики",
    description:
      "Регулярно устраиваем практические занятия, воркшопы и мастер-классы, где участники могут применять знания на реальных задачах и обмениваться опытом с коллегами.",
  },
  {
    title: "Организуем хакатоны",
    description:
      "Проводим игровые джемы и хакатоны, где за ограниченное время команды создают прототипы и полноценные игры, развивая креативность и командную работу.",
  },
  {
    title: "Разрабатываем игры",
    description:
      "Совместно создаём игры — от идеи до релиза. Наши участники работают над реальными проектами, получая практический опыт разработки и издания игр.",
  },
  {
    title: "Создаем геймификацию",
    description:
      "Помогаем компаниям и организациям внедрять игровые механики в продукты, мероприятия и бизнес-процессы, делая их более вовлекающими и запоминающимися.",
  },
  {
    title: "Помогаем с грантами и издателями",
    description:
      "Поддерживаем разработчиков в поиске финансирования и издателей. Помогаем с подготовкой питч-материалов и взаимодействием с индустрией.",
  },
];

const CASES = [
  {
    title: "Game Open Lab",
    tag: "СОБЫТИЯ",
    description: "Описание кейса Game Open Lab. Заполните позже.",
    image: "Game_Open_Lab.jpg",
    link: "https://gameopenlab.ru/",
  },
  {
    title: "GAICA",
    tag: "СОБЫТИЯ",
    description: "Описание кейса GAICA. Заполните позже.",
    image: "GAICA.jpg",
    link: "https://gaica.pro/",
  },
  {
    title: "Anlaut Jam",
    tag: "СОБЫТИЯ",
    description: "Описание кейса Anlaut Jam. Заполните позже.",
    image: "Anlaut_Jam.jpg",
    link: "https://itch.io/jam/anlaut-4",
  },
  {
    title: "Самые спортивные выживатели — игра",
    tag: "СОБЫТИЯ",
    description: "Описание кейса Дружный джем. Заполните позже.",
    image: "Most_sport_survivors.jpg",
    link: "https://store.steampowered.com/app/3032360/SAMYE_SPORTIVNYE_VYZHIVATELI/",
  },
  {
    title: "Т-Банк — игра",
    tag: "ИГРА",
    description: "Описание кейса Т-Банк — игра. Заполните позже.",
    image: "T_bank_game.jpg",
    link: "#",
  },
  {
    title: "Полка чудес — игра",
    tag: "ИГРА",
    description: "Описание кейса Полка чудес — игра. Заполните позже.",
    image: "Miracle_shelf.jpg",
    link: "https://polkachudesgame.github.io/game/",
  },
  {
    title: "Simple Sandbox — игра",
    tag: "ИГРА",
    description: "Описание кейса Simple Sandbox — игра. Заполните позже.",
    image: "Simple_sandbox.jpg",
    link: "https://play.google.com/store/apps/details?id=com.MadnessGames.SimpleSandbox3&hl=ru",
  },
  {
    title: "Breadborn — игра",
    tag: "ИГРА",
    description: "Описание кейса Breadborn — игра. Заполните позже.",
    image: "Breadborn.jpg",
    link: "https://store.steampowered.com/app/2996970/Breadborn/",
  },
  {
    title: "Геймификация ОЭЗ",
    tag: "ГЕЙМИФИКАЦИЯ",
    description: "Описание кейса Геймификация ОЭЗ. Заполните позже.",
    image: "Tech_park_gamification.jpg",
    link: "t.me/oez_loyalty_bot",
  },
  {
    title: "Питч-сессия на DID",
    tag: "СОБЫТИЯ",
    description: "Описание кейса Питч-сессия на DID. Заполните позже.",
    image: "GameDay_2024.jpg",
    link: "#",
  },
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

function ActivityAccordion({ items }) {
  const [activeItem, setActiveItem] = React.useState(null);

  const mid = Math.ceil(items.length / 2);
  const leftItems = items.slice(0, mid);
  const rightItems = items.slice(mid);

  return (
    <div className="activity-accordion">
      <div className="activity-accordion-columns">
        <div className="activity-accordion-col">
          {leftItems.map((item) => (
            <div
              key={item.title}
              className={`activity-accordion-item ${activeItem === item.title ? "active" : ""}`}
              onMouseEnter={() => setActiveItem(item.title)}
              onMouseLeave={() => setActiveItem(null)}
            >
              <div className="activity-accordion-btn">
                {item.title}
              </div>
              <div className="activity-accordion-desc">
                {item.description}
              </div>
            </div>
          ))}
        </div>
        <div className="activity-accordion-col">
          {rightItems.map((item) => (
            <div
              key={item.title}
              className={`activity-accordion-item ${activeItem === item.title ? "active" : ""}`}
              onMouseEnter={() => setActiveItem(item.title)}
              onMouseLeave={() => setActiveItem(null)}
            >
              <div className="activity-accordion-btn">
                {item.title}
              </div>
              <div className="activity-accordion-desc">
                {item.description}
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
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

              <div className="about-section-content about-section-content-wide">
                  <p>
                    GDE объединяет начинающих и опытных специалистов игровой
                    индустрии. Мы создаём пространство для развития навыков,
                    совместных проектов, практики и профессиональных знакомств.
                  </p>

                  <ActivityAccordion items={ACTIVITIES} />
                </div>
            </section>

            <section className="about-section">
              <div className="about-section-heading">
                <p className="about-section-kicker">Проекты</p>
                <h2>Наши кейсы</h2>
              </div>

              <div className="cases-list">
                {CASES.map((c) => (
                  <article className="case-card" key={c.title}>
                    <div className="case-card-content">
                      <h3 className="case-card-title">{c.title}</h3>
                      <span className="case-card-tag">{c.tag}</span>
                      <p className="case-card-desc">{c.description}</p>
                      <a
                        className="case-card-link"
                        href={c.link}
                        target="_blank"
                        rel="noreferrer"
                      >
                        ПОСМОТРЕТЬ КЕЙС
                        <span className="case-card-arrow">↗</span>
                      </a>
                    </div>
                    <div className="case-card-image-wrap">
                      <img
                        className="case-card-image"
                        src={`/media/cases/${c.image}`}
                        alt={c.title}
                        loading="lazy"
                      />
                    </div>
                  </article>
                ))}
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
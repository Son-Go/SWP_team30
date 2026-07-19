import React from "react";
import Footer from "../components/Footer";

const SERVICES = [
  {
    id: "conferences",
    label: "КОНФЕРЕНЦИИ",
    text: "Масштабные профессиональные мероприятия: от нетворкинга до пленарных сессий. Организуем под ключ с полным техническим обеспечением.",
  },
  {
    id: "b2b",
    label: "B2B-СОБЫТИЯ",
    text: "Закрытые деловые встречи, форумы и переговорные сессии для бизнес-аудитории. Фокус на результат и качество контактов.",
  },
  {
    id: "hr",
    label: "HR-ПРОЕКТЫ",
    text: "Корпоративные мероприятия для команд: тимбилдинги, внутренние конференции и программы вовлечённости сотрудников.",
  },
  {
    id: "integrations",
    label: "ИНТЕГРАЦИИ",
    text: "Мероприятия и проекты, которые влюбляют сотрудников в бренд работодателя: корпоративы, митапы, конференции, внутренние и внешние спецпроекты.",
  },
  {
    id: "regional",
    label: "РЕГИОНАЛЬНЫЕ ИВЕНТЫ",
    text: "Организация событий за пределами столицы — знаем специфику регионов и работаем с локальными командами по всей стране.",
  },
];

function ServicesAccordion() {
  return (
    <section className="services-section">
      <h2 className="services-title">
        ИВЕНТЫ ДЛЯ СВЯЗИ
        <br />
        БРЕНДОВ И ЛЮДЕЙ
      </h2>

      <div className="services-pills">
        {SERVICES.map((item) => (
          <div className="services-pill-wrapper" key={item.id}>
            <button
              className="services-pill"
              type="button"
              aria-describedby={`service-description-${item.id}`}
            >
              {item.label}
            </button>

            <div
              className="services-pill-content"
              id={`service-description-${item.id}`}
            >
              <p>{item.text}</p>
            </div>
          </div>
        ))}
      </div>
    </section>
  );
}

function AboutCommunityPage() {
  return (
    <div className="about-community-layout">
      <main className="about-community-page">
        <section className="about-hero">
          <div className="about-hero-media">
            <img src="/media/Alisher_2.jpg" />
          </div>

          <a
            className="about-telegram-button"
            href="https://t.me/lopufeed"
            target="_blank"
            rel="noreferrer"
          >
            Написать в Telegram
          </a>
        </section>

        <ServicesAccordion />
      </main>

      <div className="about-footer-divider" />

      <Footer />
    </div>
  );
}

export default AboutCommunityPage;

import React from "react";
import Footer from "../components/Footer";

const SERVICES = [
  {
    id: "success",
    label: "GDE: путь к успеху",
    text: "Разбираем истории успешных игр: от первых идей и разработки до выхода к широкой аудитории. Обсуждаем решения, которые сделали проект запоминающимся, и роль сообщества в его развитии.",
  },
  {
    id: "gamejams",
    label: "Геймджемы",
    text: "Интенсивные мероприятия, где участники за ограниченное время создают собственные игры. Это возможность найти команду, попробовать новые инструменты, воплотить идею и показать результат сообществу.",
  },
  {
    id: "lections",
    label: "Лекции GDE",
    text: "Встречи со спикерами и участниками сообщества на темы, связанные с разработкой игр. Говорим о геймдизайне, программировании, визуале, звуке, индустрии и работе над проектами.",
  },
  {
    id: "breakfasts",
    label: "Завтраки GDE",
    text: "Неформальные встречи разработчиков за завтраком в кафе. Делимся новостями индустрии, обсуждаем идеи и личные проекты, ищем единомышленников и просто общаемся в приятной компании.",
  },
];

function ServicesAccordion() {
  return (
    <section className="services-section">
      <h2 className="services-title">ИВЕНТЫ СООБЩЕСТВА</h2>

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

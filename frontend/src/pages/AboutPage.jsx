import React from "react";
import Footer from "../components/Footer";

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
      </main>

      <div className="about-footer-divider" />

      <Footer />
    </div>
  );
}

export default AboutCommunityPage;

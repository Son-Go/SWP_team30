import React from "react";

function ErrorState({ message = "Что-то пошло не так..." }) {
  return <div className="state-box error">Ошибка: {message}</div>;
}

export default ErrorState;

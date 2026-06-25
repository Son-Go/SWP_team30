import React from "react";

function EmptyState({ message = "Здесь пока ничего нет..." }) {
  return <div className="state-box">{message}</div>;
}

export default EmptyState;

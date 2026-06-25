import React from "react";

function Loader({ text = "Загрузка..." }) {
  return <div className="state-box">{text}</div>;
}

export default Loader;

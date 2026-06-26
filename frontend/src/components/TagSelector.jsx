import React, { useEffect, useState } from "react";
import { getAllTags } from "../api/api";

function TagSelector({ selected, onChange }) {
  const [allTags, setAllTags] = useState([]);

  useEffect(() => {
    getAllTags()
      .then((data) => setAllTags(data.gameTags || []))
      .catch(() => setAllTags([]));
  }, []);

  function toggleTag(tag) {
    if (selected.includes(tag)) {
      onChange(selected.filter((t) => t !== tag));
    } else {
      onChange([...selected, tag]);
    }
  }

  if (!allTags.length) return null;

  return (
    <div className="tag-selector">
      <div className="tag-selector-all">
        <span className="tag-selector-label">Все теги</span>
        <div className="tag-list">
          {allTags.map((tag) => (
            <span
              key={tag}
              className={`tag-badge tag-badge-selectable ${
                selected.includes(tag) ? "tag-badge-dimmed" : ""
              }`}
              onClick={() => toggleTag(tag)}
            >
              {tag}
            </span>
          ))}
        </div>
      </div>

      <div className="tag-selector-selected">
        <span className="tag-selector-label">Выбранные</span>
        <div className="tag-list">
          {selected.map((tag) => (
            <span
              key={tag}
              className="tag-badge tag-badge-selected"
              onClick={() => toggleTag(tag)}
            >
              {tag} <span className="tag-remove-icon">×</span>
            </span>
          ))}
          {selected.length === 0 && (
            <span className="tag-selector-empty">Ничего не выбрано</span>
          )}
        </div>
      </div>
    </div>
  );
}

export default TagSelector;

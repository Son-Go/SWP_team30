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
    <div className="tag-list">
      {allTags.map((tag) => (
        <span
          key={tag}
          className={`tag-badge tag-badge-selectable ${selected.includes(tag) ? "tag-badge-selected" : ""}`}
          onClick={() => toggleTag(tag)}
        >
          {tag}
        </span>
      ))}
    </div>
  );
}

export default TagSelector;

import React, { useEffect, useState } from "react";
import { getAllTags } from "../api/api";

const CATEGORY_CONFIG = [
  {
    key: "genre",
    label: "Жанр",
    multiple: true,
    colorClass: "tag-badge-genre",
  },
  {
    key: "town",
    label: "Город",
    multiple: false,
    colorClass: "tag-badge-town",
  },
  {
    key: "stage",
    label: "Статус",
    multiple: false,
    colorClass: "tag-badge-stage",
  },
];

const EMPTY_TAGS = { genre: [], town: [], stage: [], featured: [] };

function TagSelector({ selected, onChange }) {
  const [allTags, setAllTags] = useState(EMPTY_TAGS);

  useEffect(() => {
    getAllTags()
      .then((data) => setAllTags({ ...EMPTY_TAGS, ...(data.gameTags || {}) }))
      .catch(() => setAllTags(EMPTY_TAGS));
  }, []);

  function toggleTag(categoryKey, multiple, tag) {
    const current = selected[categoryKey] || [];
    let nextValue;

    if (multiple) {
      nextValue = current.includes(tag)
        ? current.filter((t) => t !== tag)
        : [...current, tag];
    } else {
      nextValue = current.includes(tag) ? [] : [tag];
    }

    onChange({ ...selected, [categoryKey]: nextValue });
  }

  const hasAnyTags = CATEGORY_CONFIG.some(
    ({ key }) => (allTags[key] || []).length > 0,
  );
  if (!hasAnyTags) return null;

  return (
    <div className="tag-selector">
      {CATEGORY_CONFIG.map(({ key, label, multiple, colorClass }) => {
        const options = allTags[key] || [];
        if (!options.length) return null;
        const current = selected[key] || [];

        return (
          <div className="tag-selector-category" key={key}>
            <span className="tag-selector-label">{label}</span>
            <div className="tag-list">
              {options.map((tag) => (
                <span
                  key={tag}
                  className={`tag-badge tag-badge-selectable ${colorClass} ${
                    current.includes(tag) ? "tag-badge-active" : ""
                  }`}
                  onClick={() => toggleTag(key, multiple, tag)}
                >
                  {tag}
                </span>
              ))}
            </div>
          </div>
        );
      })}
    </div>
  );
}

export default TagSelector;

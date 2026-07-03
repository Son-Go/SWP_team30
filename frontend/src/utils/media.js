export function createEmptyMedia() {
  return { videos: [], pictures: [] };
}

export function normalizeMedia(value) {
  return {
    videos: Array.isArray(value?.videos) ? value.videos : [],
    pictures: Array.isArray(value?.pictures) ? value.pictures : [],
  };
}

export function getTotalMediaCount(media) {
  const normalized = normalizeMedia(media);
  return normalized.videos.length + normalized.pictures.length;
}

export function isYoutubeUrl(url) {
  try {
    const parsed = new URL(url);
    const host = parsed.hostname.replace(/^www\./, "");

    return (
      host === "youtube.com" || host === "m.youtube.com" || host === "youtu.be"
    );
  } catch {
    return false;
  }
}

export function isImageUrl(url) {
  try {
    const parsed = new URL(url);
    return /\.(png|jpe?g|webp|gif|bmp|svg|avif)$/i.test(parsed.pathname);
  } catch {
    return false;
  }
}

export function getOrderedMedia(media) {
  const normalized = normalizeMedia(media);
  return [...normalized.videos, ...normalized.pictures];
}

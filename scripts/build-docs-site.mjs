import { copyFileSync, existsSync, mkdirSync, readdirSync, readFileSync, rmSync, statSync, writeFileSync } from "node:fs";
import path from "node:path";

const root = process.cwd();
const outDir = path.join(root, "site");
const repoUrl = "https://github.com/Son-Go/SWP_team30";
const publicUrl = "https://son-go.github.io/SWP_team30/";
const copiedFiles = new Set();

const docs = [
  { source: "README.md", title: "Project Overview", group: "Product" },
  { source: "CHANGELOG.md", title: "Changelog", group: "Product" },
  { source: "ATTRIBUTION.md", title: "Attribution", group: "Product" },
  { source: "docs/user-stories.md", title: "User Stories", group: "Product" },
  { source: "docs/roadmap.md", title: "Roadmap", group: "Product" },
  { source: "docs/development-process.md", title: "Development Process", group: "Process" },
  { source: "docs/definition-of-done.md", title: "Definition of Done", group: "Process" },
  { source: "docs/architecture/README.md", title: "Architecture", group: "Architecture" },
  { source: "docs/architecture/adr/ADR-001.md", title: "ADR-001: JWT Authentication", group: "Architecture Decisions" },
  { source: "docs/architecture/adr/ADR-002.md", title: "ADR-002: Docker Compose", group: "Architecture Decisions" },
  { source: "docs/architecture/adr/ADR-003.md", title: "ADR-003: Nginx Reverse Proxy", group: "Architecture Decisions" },
  { source: "docs/architecture/adr/ADR-004.md", title: "ADR-004: Flyway Migrations", group: "Architecture Decisions" },
  { source: "docs/architecture/adr/ADR-005.md", title: "ADR-005: Layered Architecture", group: "Architecture Decisions" },
  { source: "docs/quality-requirements.md", title: "Quality Requirements", group: "Quality" },
  { source: "docs/quality-requirement-tests.md", title: "Quality Requirement Tests", group: "Quality" },
  { source: "docs/testing.md", title: "Testing", group: "Testing" },
  { source: "docs/endpoint-testing.md", title: "Endpoint Testing", group: "Testing" },
  { source: "docs/user-acceptance-tests.md", title: "User Acceptance Tests", group: "Testing" },
  { source: "docs/endpoint-overview.md", title: "API Endpoint Overview", group: "API" },
  { source: "docs/observability.md", title: "Observability", group: "Operations" },
  { source: "docs/db-backup-vm.md", title: "PostgreSQL Auto Dump For VM", group: "Operations" },
  { source: "frontend/README.md", title: "Frontend README", group: "Component Docs" },
  { source: "scripts/db_scripts/mock_scripts/README.md", title: "Mock Data Scripts", group: "Component Docs" },
  { source: "observability/grafana/dashboards/README.md", title: "Grafana Dashboards", group: "Component Docs" },
].filter((doc) => existsSync(path.join(root, doc.source)));

const pageForSource = new Map(
  docs.map((doc) => [normalize(doc.source), pagePath(doc.source)])
);

rmSync(outDir, { recursive: true, force: true });
mkdirSync(outDir, { recursive: true });
copyAssets();
writeStylesheet();

for (const doc of docs) {
  const sourcePath = path.join(root, doc.source);
  const markdown = readFileSync(sourcePath, "utf8");
  const body = renderMarkdown(markdown, doc.source);
  const outputPath = path.join(outDir, pagePath(doc.source));
  mkdirSync(path.dirname(outputPath), { recursive: true });
  writeFileSync(outputPath, layout(doc.title, body, doc.source));
}

writeFileSync(path.join(outDir, "index.html"), layout(
  "GDE Documentation",
  renderHome(),
  ""
));

writeFileSync(path.join(outDir, ".nojekyll"), "");
console.log(`Built ${docs.length} documentation pages into ${outDir}`);

function pagePath(source) {
  const normalized = normalize(source);
  if (normalized === "README.md") return "project-overview.html";
  return normalized.replace(/\/README\.md$/i, "/index.md").replace(/\.md$/i, ".html");
}

function normalize(value) {
  return value.replaceAll("\\", "/");
}

function copyAssets() {
  const allowed = new Set([".svg", ".png", ".jpg", ".jpeg", ".gif", ".webp", ".ico", ".puml"]);
  for (const dir of ["docs", "frontend", "observability", "scripts"]) {
    const start = path.join(root, dir);
    if (!existsSync(start)) continue;
    walk(start, (file) => {
      if (!allowed.has(path.extname(file).toLowerCase())) return;
      const rel = normalize(path.relative(root, file));
      const target = path.join(outDir, rel);
      mkdirSync(path.dirname(target), { recursive: true });
      copyFileSync(file, target);
      copiedFiles.add(rel);
    });
  }
}

function writeStylesheet() {
  const css = `:root {
  --bg: #f7f8f5;
  --panel: #ffffff;
  --ink: #202421;
  --muted: #626b64;
  --line: #d8ded6;
  --accent: #22695a;
  --accent-2: #9b3d56;
  --code: #eef3ef;
}

* {
  box-sizing: border-box;
}

body {
  margin: 0;
  background: var(--bg);
  color: var(--ink);
  font-family: Inter, ui-sans-serif, system-ui, -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif;
  line-height: 1.6;
}

a {
  color: var(--accent);
  text-decoration-thickness: 0.08em;
  text-underline-offset: 0.18em;
}

.sidebar {
  position: fixed;
  inset: 0 auto 0 0;
  width: 300px;
  overflow-y: auto;
  border-right: 1px solid var(--line);
  background: var(--panel);
  padding: 24px 20px;
}

.brand {
  display: block;
  color: var(--ink);
  font-size: 1.35rem;
  font-weight: 800;
  margin-bottom: 24px;
  text-decoration: none;
}

.nav-group {
  margin: 0 0 22px;
}

.nav-group h2 {
  color: var(--muted);
  font-size: 0.76rem;
  letter-spacing: 0;
  margin: 0 0 8px;
  text-transform: uppercase;
}

.nav-group a {
  display: block;
  border-radius: 6px;
  color: var(--ink);
  padding: 6px 8px;
  text-decoration: none;
}

.nav-group a:hover {
  background: var(--code);
}

.content {
  max-width: 1060px;
  margin-left: 300px;
  padding: 34px 48px 80px;
}

.topline {
  align-items: center;
  color: var(--muted);
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  font-size: 0.9rem;
  margin-bottom: 24px;
}

h1,
h2,
h3,
h4 {
  line-height: 1.25;
}

h1 {
  font-size: clamp(2rem, 4vw, 3.2rem);
  margin: 0 0 18px;
}

h2 {
  border-top: 1px solid var(--line);
  font-size: 1.55rem;
  margin-top: 42px;
  padding-top: 24px;
}

h3 {
  color: #313833;
  font-size: 1.18rem;
  margin-top: 28px;
}

.lead {
  color: var(--muted);
  font-size: 1.12rem;
  max-width: 760px;
}

.cards {
  display: grid;
  gap: 14px;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  margin: 28px 0;
}

.cards article {
  background: var(--panel);
  border: 1px solid var(--line);
  border-radius: 8px;
  padding: 18px;
}

.cards h2 {
  border: 0;
  font-size: 1.05rem;
  margin: 0 0 8px;
  padding: 0;
}

img {
  background: var(--panel);
  border: 1px solid var(--line);
  border-radius: 8px;
  display: block;
  height: auto;
  margin: 18px 0;
  max-width: 100%;
  padding: 8px;
}

table {
  border-collapse: collapse;
  display: block;
  margin: 18px 0;
  overflow-x: auto;
  width: 100%;
}

th,
td {
  border: 1px solid var(--line);
  padding: 9px 11px;
  text-align: left;
  vertical-align: top;
}

th {
  background: #e8efe8;
}

pre,
code {
  background: var(--code);
  border-radius: 5px;
  font-family: "Cascadia Code", "SFMono-Regular", Consolas, monospace;
}

code {
  padding: 0.12rem 0.3rem;
}

pre {
  overflow-x: auto;
  padding: 16px;
}

pre code {
  padding: 0;
}

blockquote {
  border-left: 4px solid var(--accent-2);
  color: var(--muted);
  margin-left: 0;
  padding-left: 16px;
}

@media (max-width: 820px) {
  .sidebar {
    position: static;
    width: auto;
  }

  .content {
    margin-left: 0;
    padding: 26px 20px 56px;
  }
}
`;
  mkdirSync(path.join(outDir, "assets"), { recursive: true });
  writeFileSync(path.join(outDir, "assets", "site.css"), css);
}

function walk(dir, visit) {
  for (const entry of readdirSync(dir)) {
    const full = path.join(dir, entry);
    const stats = statSync(full);
    if (stats.isDirectory()) {
      if (["node_modules", "dist", "coverage", "playwright-report", "test-results", "site", "reports"].includes(entry)) continue;
      walk(full, visit);
    }
    if (stats.isFile()) visit(full);
  }
}

function buildNav(source) {
  const groups = new Map();
  for (const doc of docs) {
    if (!groups.has(doc.group)) groups.set(doc.group, []);
    groups.get(doc.group).push(doc);
  }
  return [...groups.entries()].map(([group, items]) => `
    <section class="nav-group">
      <h2>${escapeHtml(group)}</h2>
      ${items.map((item) => `<a href="${relativeUrl(pagePath(item.source), source)}">${escapeHtml(item.title)}</a>`).join("")}
    </section>
  `).join("");
}

function renderHome() {
  const cards = [
    ["Product", "Current product scope, user stories, roadmap, changelog, and project overview."],
    ["Process", "Development workflow, configuration management, and definition of done."],
    ["Architecture", "Static, dynamic, and deployment views with maintained diagrams and ADRs."],
    ["Quality", "Quality requirements, quality checks, endpoint tests, UAT, and CI coverage."],
    ["Operations", "Observability stack, Grafana dashboards, and VM database backup procedures."],
  ];
  return `
    <h1>GDE Website Documentation</h1>
    <p class="lead">Browsable maintained documentation for the Game Dev Evenings website. This site mirrors the current non-report documentation from the repository and preserves architecture diagrams in context.</p>
    <div class="cards">
      ${cards.map(([title, text]) => `<article><h2>${title}</h2><p>${text}</p></article>`).join("")}
    </div>
    <p><a href="project-overview.html">Start with the project overview</a> or use the navigation to inspect the maintained documentation set.</p>
  `;
}

function layout(title, body, source) {
  const navHtml = buildNav(source);
  const sourceLink = source ? `<a href="${repoUrl}/blob/main/${source}">View source</a>` : `<a href="${repoUrl}">Repository</a>`;
  return `<!doctype html>
<html lang="en">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>${escapeHtml(title)} | GDE Documentation</title>
  <link rel="stylesheet" href="${relativeUrl("assets/site.css", source)}">
</head>
<body>
  <aside class="sidebar">
    <a class="brand" href="${relativeUrl("index.html", source)}">GDE Docs</a>
    <nav>${navHtml}</nav>
  </aside>
  <main class="content">
    <div class="topline">
      <a href="${publicUrl}">Hosted documentation site</a>
      <span>/</span>
      ${sourceLink}
    </div>
    ${body}
  </main>
</body>
</html>`;
}

function renderMarkdown(markdown, source) {
  const lines = markdown.replace(/\r\n/g, "\n").split("\n");
  const html = [];
  let paragraph = [];
  let listStack = [];
  let inCode = false;
  let code = [];
  let table = [];

  const flushParagraph = () => {
    if (!paragraph.length) return;
    html.push(`<p>${inline(paragraph.join(" "), source)}</p>`);
    paragraph = [];
  };
  const flushLists = () => {
    while (listStack.length) html.push(`</${listStack.pop()}>`);
  };
  const flushTable = () => {
    if (table.length < 2) {
      table.forEach((line) => html.push(`<p>${inline(line, source)}</p>`));
      table = [];
      return;
    }
    const rows = table.map(splitTable);
    const headers = rows.shift();
    rows.shift();
    html.push("<table><thead><tr>");
    headers.forEach((cell) => html.push(`<th>${inline(cell.trim(), source)}</th>`));
    html.push("</tr></thead><tbody>");
    rows.forEach((row) => {
      html.push("<tr>");
      row.forEach((cell) => html.push(`<td>${inline(cell.trim(), source)}</td>`));
      html.push("</tr>");
    });
    html.push("</tbody></table>");
    table = [];
  };

  for (const rawLine of lines) {
    const line = rawLine.trimEnd();
    if (line.startsWith("```")) {
      flushParagraph();
      flushLists();
      flushTable();
      if (inCode) {
        html.push(`<pre><code>${escapeHtml(code.join("\n"))}</code></pre>`);
        inCode = false;
        code = [];
      } else {
        inCode = true;
      }
      continue;
    }
    if (inCode) {
      code.push(rawLine);
      continue;
    }
    if (line.includes("|") && /^\s*\|?[^|]+\|/.test(line)) {
      flushParagraph();
      flushLists();
      table.push(line);
      continue;
    }
    flushTable();
    if (!line.trim()) {
      flushParagraph();
      flushLists();
      continue;
    }
    const heading = /^(#{1,6})\s+(.+)$/.exec(line);
    if (heading) {
      flushParagraph();
      flushLists();
      const level = heading[1].length;
      const text = heading[2].replace(/\s+#*$/, "");
      html.push(`<h${level} id="${slug(text)}">${inline(text, source)}</h${level}>`);
      continue;
    }
    if (/^---+$/.test(line.trim())) {
      flushParagraph();
      flushLists();
      html.push("<hr>");
      continue;
    }
    if (/^>\s?/.test(line)) {
      flushParagraph();
      flushLists();
      html.push(`<blockquote>${inline(line.replace(/^>\s?/, ""), source)}</blockquote>`);
      continue;
    }
    const unordered = /^(\s*)[-*]\s+(.+)$/.exec(rawLine);
    const ordered = /^(\s*)\d+[.)]\s+(.+)$/.exec(rawLine);
    if (unordered || ordered) {
      flushParagraph();
      const match = unordered || ordered;
      const tag = unordered ? "ul" : "ol";
      if (listStack[listStack.length - 1] !== tag) {
        flushLists();
        listStack.push(tag);
        html.push(`<${tag}>`);
      }
      html.push(`<li>${inline(match[2], source)}</li>`);
      continue;
    }
    paragraph.push(line.trim());
  }
  flushParagraph();
  flushLists();
  flushTable();
  return html.join("\n");
}

function splitTable(line) {
  return line.replace(/^\s*\|/, "").replace(/\|\s*$/, "").split("|");
}

function inline(text, source) {
  let value = escapeHtml(text);
  value = value.replace(/!\[([^\]]*)\]\(([^)]+)\)/g, (_, alt, href) => `<img src="${rewriteLink(href, source)}" alt="${escapeHtml(alt)}">`);
  value = value.replace(/\[([^\]]+)\]\(([^)]+)\)/g, (_, label, href) => `<a href="${rewriteLink(href, source)}">${label}</a>`);
  value = value.replace(/`([^`]+)`/g, "<code>$1</code>");
  value = value.replace(/\*\*([^*]+)\*\*/g, "<strong>$1</strong>");
  value = value.replace(/\*([^*]+)\*/g, "<em>$1</em>");
  return value;
}

function rewriteLink(href, source) {
  if (/^(https?:|mailto:|#)/.test(href)) return href;
  const [target, hash = ""] = href.split("#");
  const sourceDir = path.posix.dirname(normalize(source));
  const normalizedTarget = normalize(path.posix.normalize(path.posix.join(sourceDir, target)));
  if (pageForSource.has(normalizedTarget)) {
    return `${relativeUrl(pageForSource.get(normalizedTarget), source)}${hash ? `#${hash}` : ""}`;
  }
  const assetPath = target.startsWith("/") ? target.slice(1) : normalizedTarget;
  if (copiedFiles.has(assetPath)) {
    return relativeUrl(assetPath, source);
  }
  return `${repoUrl}/blob/main/${normalizedTarget}${hash ? `#${hash}` : ""}`;
}

function relativeUrl(target, source) {
  const from = source ? path.posix.dirname(pagePath(source)) : ".";
  const rel = path.posix.relative(from, normalize(target));
  return rel.startsWith(".") ? rel : `./${rel}`;
}

function slug(text) {
  return text.toLowerCase()
    .replace(/<[^>]+>/g, "")
    .replace(/[^a-z0-9а-яё\s-]/gi, "")
    .trim()
    .replace(/\s+/g, "-");
}

function escapeHtml(value) {
  return String(value)
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll('"', "&quot;");
}

# Session 5: Practical Project, Best Practices, and Final Checklist

This is the last session. By now you know all the tools. Now it’s time to put them all together in a real project, avoid common mistakes, and have a professional checklist for every document you write.

---

## 1. Practical Project: Documenting a Hypothetical Project

Suppose you have a project called **“Task Management System”** and you want to write its documentation. The final structure is:

```text
task-manager/
├── README.md
├── docs/
│   ├── setup/
│   │   ├── QUICKSTART.md
│   │   └── INSTALLATION.md
│   ├── guides/
│   │   ├── GIT_WORKFLOW.md
│   │   └── DEPLOYMENT.md
│   └── reference/
│       └── API.md
└── images/
    └── architecture.png
```

### Step 1 — `README.md` (the entry point)

````markdown
---
title: "Task Management System"
description: "A lightweight system for team task management"
author: "Development Team"
date: 2026-10-22
tags: [task-management, team-tool]
---

# Task Management System

![version](https://img.shields.io/badge/version-1.2.0-blue)
[![license](https://img.shields.io/badge/license-MIT-green)](LICENSE)
![status](https://img.shields.io/badge/build-passing-brightgreen)

<div dir="rtl">

A lightweight, open-source system for team task management with full support for a Persian/RTL interface.

## Features

- Project and task management
- Right-to-left user interface
- Complete REST API
- Report export to CSV and PDF

## Quick Start

</div>

```bash
git clone https://github.com/example/task-manager.git
cd task-manager
npm install
npm run dev
```

<div dir="rtl">

## Documentation

- [Quick Start](docs/setup/QUICKSTART.md)
- [Installation Guide](docs/setup/INSTALLATION.md)
- [Git Workflow](docs/guides/GIT_WORKFLOW.md)
- [Deployment](docs/guides/DEPLOYMENT.md)
- [API Reference](docs/reference/API.md)

## License

This project is released under the MIT License. See the [LICENSE](LICENSE) file for details.

</div>
````

**Lesson from this step:** README is a “signpost,” not an encyclopedia. The user should understand within 30 seconds what the project is, how to start, and where the rest of the documentation is.

---

### Step 2 — `docs/setup/QUICKSTART.md`

````markdown
---
title: "Quick Start"
author: "Development Team"
date: 2026-10-22
---

# Quick Start

<div dir="rtl">

## Table of Contents
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [First Run](#first-run)

## Prerequisites

| Tool | Version |
|------|---------|
| Node.js | 18+ |
| npm | 9+ |
| Git | 2.30+ |

## Installation

1. Clone the repository:

```bash
git clone https://github.com/example/task-manager.git
```

2. Enter the folder and install dependencies:

```bash
cd task-manager
npm install
```

3. Create the environment file:

```bash
cp .env.example .env
```

> [!IMPORTANT]
> In the `.env` file, make sure to set the `DATABASE_URL` value.

## First Run

```bash
npm run dev
```

Then open `http://localhost:3000` in your browser.

If you get a port error, see the [Troubleshooting section](INSTALLATION.md#troubleshooting).

</div>
````

**Lesson from this step:** Every file should have **Front Matter**, a **table of contents** if it is long, and **links to related sections**. A document should not be an island.

---

### Step 3 — `docs/setup/INSTALLATION.md` (with troubleshooting and `<details>`)

````markdown
---
title: "Detailed Installation Guide"
---

# Detailed Installation Guide

<div dir="rtl">

## Prerequisites

- [ ] Node.js installed
- [ ] Git installed
- [ ] Terminal access

## Installation

```bash
git clone https://github.com/example/task-manager.git
cd task-manager
npm install
```

## Troubleshooting

<details>
<summary>Port is already in use error</summary>

Change the port value in the `.env` file:

```bash
PORT=3001
```

</details>

<details>
<summary>Database access error</summary>

Make sure `DATABASE_URL` is set correctly and the database service is running.

</details>

</div>
````

**Lesson from this step:** The troubleshooting section is one of the **most important parts of any technical document**. Use `<details>` so the document does not become cluttered.

---

### Step 4 — `docs/reference/API.md` (technical reference)

````markdown
---
title: "API Reference"
---

# API Reference

<div dir="rtl">

## Authentication

All requests must include the following header:

```http
Authorization: Bearer <token>
```

## Create Task

```http
POST /api/tasks
Content-Type: application/json
```

Request body:

```json
{
  "title": "Design the home page",
  "assignee": "Ali",
  "priority": "high"
}
```

Successful response:

```json
{
  "id": 123,
  "status": "created",
  "created_at": "2026-10-22T10:00:00Z"
}
```

| Status Code | Description |
|-------------|-------------|
| 201 | Created |
| 400 | Invalid request |
| 401 | Unauthenticated |

</div>
````

**Lesson from this step:** For APIs, always write the **method, path, body, response, and error codes** separately and with code blocks.

---

## 2. Best Practices

### 2.1. The “One Idea per Section” Principle

Each heading should cover one idea. If you have a heading like “Installation and Configuration and Troubleshooting,” split it into three separate headings.

### 2.2. The “Know Your Audience” Principle

- **Beginner audience:** Step by step, explain each command, state prerequisites explicitly.
- **Professional audience:** Concise, direct, without explaining obvious things.

One document cannot be excellent for both at the same time. If necessary, write **two separate documents**: `QUICKSTART.md` for beginners and `REFERENCE.md` for professionals.

### 2.3. The “Real Example” Principle

Nothing is worse than `{{placeholder}}` and `value-here`. Give a real, runnable example:

```bash
# ❌ Bad
your-command --arg=value

# ✅ Good
npm run build -- --production
```

### 2.4. The “Raw File Readability” Principle

Markdown is read both in preview and in the raw file (in Git diff, in a simple editor, in the terminal). The raw file should also be readable:

- Align tables in the code.
- Put blank lines between blocks and paragraphs.
- Separate long links with **reference-style links**.
- Do not break long headings.

### 2.5. The “Link, Don’t Repeat” Principle

If a concept is explained elsewhere, **link to it** instead of explaining it again. Repetition means that with every change, you have to fix it in multiple places.

### 2.6. The “Date and Version” Principle

Always include the **last updated date** (in Front Matter) with every technical document. A document without a date is untrustworthy.

### 2.7. The “Copyable Code” Principle

Code blocks should be **directly copyable**. Do not put explanations inside the code (unless as comments); explain outside the code.

### 2.8. The “Terminology Consistency” Principle

One concept = one term. If you say “user” in one place, do not say “end-user” elsewhere. If you say “task,” do not say “todo.” Keep a **glossary** in the project.

### 2.9. The “Tool-Awareness” Principle

Before using a feature, make sure it is rendered **in the target tool** (GitHub? Obsidian? VS Code?). Remember the compatibility table from Session 3.

---

## 3. Common Mistakes (and Solutions)

### Mistake 1: Skipping heading levels

```markdown
# Title
### Subsection without ##
```

✅ Solution: Always go step by step. `#` → `##` → `###`.

### Mistake 2: Code block without a language

````markdown
```
npm install
```
````

✅ Solution: Write the language: ` ```bash `.

### Mistake 3: Forgetting blank lines around code blocks

````markdown
Text before
```bash
npm install
```
Text after
````

In some parsers, the code block is not closed correctly. ✅ Solution: Put a **blank line before and after** the code block.

### Mistake 4: Wrong relative link

```markdown
[Install](install.md)   ← if the document is in another folder, this breaks
```

✅ Solution: Calculate the path relative to **the location of the file itself**: `[Install](setup/install.md)` or `[Install](../setup/install.md)`.

### Mistake 5: Empty or meaningless image alt text

```markdown
![](image.png)
![image](image.png)
```

✅ Solution: Give **descriptive** alt text: `![System architecture diagram](architecture.png)`.

### Mistake 6: Spaces in file names

```text
Installation Guide.md   ❌
```

Its URL becomes `%20`, and it breaks in some tools. ✅ Solution: `INSTALLATION.md` or `installation-guide.md`.

### Mistake 7: Neglecting RTL in tables

A Persian table in GitHub is rendered left-to-right, and the columns appear swapped.

✅ Solution: For sensitive tables, use HTML with `dir="rtl"`.

### Mistake 8: Footnotes and Alerts in incompatible tools

Footnotes work well in GitHub but are not rendered in some tools. ✅ Solution: Check the compatibility table from Session 3 and use conservative syntax for multi-tool documents.

### Mistake 9: Document without date and version

A document whose date you do not know is worthless. ✅ Solution: In Front Matter, write the last edited date.

### Mistake 10: Repetition instead of linking

Explaining the same concept in three different files. ✅ Solution: One single source of truth + links.

---

## 4. Final Checklist (Before Every Commit)

Keep this checklist nearby. For every Persian Markdown document you write, check these:

### Structure
- [ ] The file has only **one** `#` (document title)
- [ ] The heading hierarchy is step by step (no skipping)
- [ ] Each heading covers one idea
- [ ] A table of contents exists in long documents

### Front Matter
- [ ] Front Matter is on the first line of the file
- [ ] `title`, `author`, `date` are filled in
- [ ] YAML is valid (spaces, not tabs)

### Content
- [ ] Every code block has a **language**
- [ ] There is a **blank line** before and after every code block
- [ ] Code blocks are **copyable**
- [ ] Examples are **real** and runnable
- [ ] A troubleshooting section exists (for technical documents)
- [ ] Terminology is consistent (one concept = one term)

### Links and Images
- [ ] All links use **relative paths**
- [ ] Anchor links (like `#installation`) point to existing headings
- [ ] All images have **alt text**
- [ ] File names have **no spaces**

### RTL (for Persian documents)
- [ ] Persian paragraphs are inside `<div dir="rtl">`
- [ ] Sensitive tables are written with HTML and `dir="rtl"`
- [ ] English terms are inside `` `backticks` ``
- [ ] You have checked the document preview in the **target tool**

### Version and Date
- [ ] The last edited date is recorded
- [ ] The document version is specified (if needed)

### Compatibility
- [ ] You have used only features that work in the **target tool**
- [ ] You have checked the compatibility table for footnotes, Alerts, and checklists

---

## 5. Final Exercise: A Real Project

Choose a project you currently have (or a hypothetical project) and write **all** of its documentation:

1. `README.md` with Front Matter, three Badges, and links to `docs/`
2. `docs/setup/QUICKSTART.md` with a table of contents, prerequisites table, and run commands
3. `docs/setup/INSTALLATION.md` with a troubleshooting section and two `<details>` blocks
4. `docs/reference/API.md` with JSON examples and a status code table
5. All Persian paragraphs inside `<div dir="rtl">`
6. Apply the final checklist to every document

If you complete this project, you will be able to write **any other Markdown document** with confidence.

---

## 6. Summary of All 5 Sessions

| Session | What You Learned |
|---------|------------------|
| 1 | Basics: headings, paragraphs, emphasis, lists, links, blockquotes |
| 2 | Structure: hierarchy, table of contents, tables, images, line breaks |
| 3 | Technical: code, checklists, footnotes, internal links, Alerts, `<details>` |
| 4 | Professional: Front Matter, Badges, folder structure, RTL |
| 5 | Project, best practices, mistakes, checklist |

### Resources for Continued Learning

- [GitHub Flavored Markdown](https://github.github.com/gfm/) — official GitHub syntax reference
- [Markdown Guide](https://www.markdownguide.org/) — the most comprehensive free reference
- [Shields.io](https://shields.io/) — Badge generation
- [CommonMark](https://commonmark.org/) — the base Markdown standard
- [Awesome Markdown](https://github.com/mundimark/awesome-markdown) — tools and resources

### One Last Piece of Advice

Markdown is a **tool**, not a goal. The best document is the one that takes the reader from point A to point B. If a Markdown feature does not help that goal, do not use it. Simplicity is Markdown’s greatest advantage — do not lose it.

---

🎓 **Congratulations!** If you have made it this far, you can now write professional Markdown documents.

If you want, I can give you a **ready-made template** that you just fill in — just say the word and I’ll create it for you.
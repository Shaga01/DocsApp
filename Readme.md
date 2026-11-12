# Docs App

A small collaborative document editor. Users can create, edit, format, import and share documents.

Live app: https://docsapp-cj5o.onrender.com/
Code: https://github.com/Shaga01/DocsApp

Note: the app runs on Render free plan. If nobody used it for some time, the first load can take 30-60 seconds. After that it is fast.

## Test accounts

| Username | Password    |
|----------|-------------|
| shashwat | password123 |
| gautam   | password123 |
| ajaia    | password123 |

## How to test sharing

1. Log in as shashwat and create a document.
2. Write something, use bold or a heading, and click Save.
3. In the "Share this document" box, type gautam and click Share.
4. Log out and log in as gautam. The document is under "Shared with me".
5. Log in as ajaia. ajaia cannot see or open this document.

## Features

- Create, rename, edit and save documents
- Formatting: bold, italic, underline, headings (H1-H3), bullet and numbered lists
- Import a .txt or .md file as a new document (max 1 MB)
- Share a document with another user by username
- "My documents" and "Shared with me" are shown separately
- Data is saved in a database, so it stays after refresh

Supported upload types: only .txt and .md. .docx and .pdf are not supported.

## Tech stack

- Backend: Java 21, Spring Boot, Spring Data JPA
- Frontend: plain HTML, CSS, JavaScript and Quill editor (from CDN)
- Database: H2 file database for local, Postgres (Neon) for the live app
- Hosting: Render

## Run locally

You need Java 21. Nothing else is needed because local mode uses H2.

    git clone https://github.com/Shaga01/DocsApp.git
    cd DocsApp
    ./mvnw spring-boot:run

Open http://localhost:8080

## Run tests

Stop the app first, then run:

    ./mvnw test

## Use Postgres instead of H2 (optional)

Set these environment variables before running:

    DB_URL=jdbc:postgresql://<host>/<db>?sslmode=require
    DB_USERNAME=<user>
    DB_PASSWORD=<password>

## Deployment

The live app is deployed on Render (free plan) using the Dockerfile in this repo. Render builds the app from GitHub on every push to main. The database is a free Postgres on Neon. The database URL, username and password are set as environment variables on Render (DB_URL, DB_USERNAME, DB_PASSWORD), so no password is stored in the code.

## What is working

- Full flow: login, create, edit, format, save, reopen, rename, share, import file
- Access control: only owner and shared users can open a document, only owner can share
- Basic validation and error messages (empty title, wrong file type, unknown user, file too big)

## What is not done

- No real-time editing. If two users edit at the same time, the last save wins.
- Shared users can always edit. There is no view-only option.
- No delete document and no unshare.
- No autosave. User must click Save.
- Passwords are plain text. This is only for demo accounts.

## What I would build next (2-4 hours)

1. Autosave every few seconds and a warning if leaving with unsaved changes
2. View-only vs edit permission when sharing
3. Delete document and remove sharing
4. Better Security

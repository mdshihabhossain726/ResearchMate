<p align="center">
  <img src="docs/banner.svg" alt="ResearchMate banner" width="100%">
</p>

<p align="center">
  <img alt="Java 21" src="https://img.shields.io/badge/Java-21-orange?logo=openjdk&logoColor=white">
  <img alt="Swing" src="https://img.shields.io/badge/UI-Swing%20%2B%20FlatLaf-2563EB">
  <img alt="PostgreSQL" src="https://img.shields.io/badge/Database-PostgreSQL-336791?logo=postgresql&logoColor=white">
  <img alt="Maven" src="https://img.shields.io/badge/Build-Maven-C71A36?logo=apachemaven&logoColor=white">
  <img alt="Tests" src="https://img.shields.io/badge/Tests-JUnit%205-25A162?logo=junit5&logoColor=white">
</p>

<h3 align="center">A desktop assistant that keeps your research papers in one place.</h3>

**ResearchMate** is a Java Swing desktop application. Create an account, save your papers, read the text of a PDF,
find the most important keywords of a paper and see how similar two papers are.

---

## Features

| | |
|---|---|
| 🔐 **Accounts** | Register and sign in. Every user sees only his or her own papers. |
| 📚 **My Papers** | Add, edit, delete and search papers. Export the list to CSV (opens in Excel). |
| 📄 **Import PDF** | Choose a PDF and the text is read for you (in a background thread, so the window never freezes). |
| 🔑 **Keywords** | Shows the words a paper uses most, without common words like *the* and *and*. |
| ⚖️ **Compare** | Similarity of two papers in % (Jaccard or Cosine), with the common and the unique keywords. |

## Screenshots

| Sign in | My Papers |
|:---:|:---:|
| ![Sign in](docs/screenshots/login.png) | ![My Papers](docs/screenshots/papers.png) |
| **Import PDF** | **Keywords** |
| ![Import PDF](docs/screenshots/import.png) | ![Keywords](docs/screenshots/keywords.png) |
| **Compare** | |
| ![Compare](docs/screenshots/compare.png) | |

## Tech stack

| Part | Technology |
|---|---|
| Language | Java 21 |
| User interface | Swing with the FlatLaf look |
| Database | PostgreSQL through JDBC |
| PDF reading | Apache PDFBox |
| Tests | JUnit 5 |
| Build tool | Maven |

## Getting started

**You need:** JDK 21, Maven, and PostgreSQL (running).

```bash
# 1. get the code
git clone https://github.com/mdshihabhossain726/ResearchMate.git
cd ResearchMate

# 2. add your own database password (this file is never uploaded to GitHub)
cp db.properties.example db.properties      # Windows: copy db.properties.example db.properties
#    now open db.properties and write your PostgreSQL password

# 3. run
mvn compile exec:java
```

The database `researchmate` and all tables are created automatically on the first start.
Run the tests with `mvn test` (they do not need the database).

<details>
<summary><b>Running it from VS Code</b></summary>

1. Install the **Extension Pack for Java**.
2. Open the project folder (the one that contains `pom.xml`).
3. Open `src/main/java/com/researchmate/Main.java` and click **Run** above `main`.

</details>

## How the comparison works

- **Keywords:** the text is split into words, common words (stop words) are removed and the rest are counted.
- **Jaccard similarity:** `shared words ÷ all different words` of both papers. How often a word appears does not matter.
- **Cosine similarity:** treats each paper as a list of word counts and measures the angle between the two lists.
  A word that appears often counts more.

> This is a counting method: it compares words, it does not understand meaning.
> PDFs that are only scanned pictures contain no text, so nothing can be read from them.

## Project structure

```
src/main/java/com/researchmate/
├── Main, LoginFrame, MainFrame          start of the program, login window, main window
├── PapersPanel, ImportPanel,
│   KeywordsPanel, ComparePanel          the four tabs
├── Paper, User                          data classes
├── PaperDao, UserDao, Db                all SQL and the database connection
├── Passwords                            password hashing
├── PdfReader, TextAnalyzer              PDF text and keyword counting
├── Similarity (interface),
│   JaccardSimilarity, CosineSimilarity  two ways to compare papers
└── Ui                                   message boxes, input checks, CSV helper
```

Ideas used: encapsulation, an interface with two implementations (polymorphism), the DAO pattern,
collections, exception handling, file handling, JDBC, multithreading (`SwingWorker`) and unit tests.

## Security

- **Passwords are never stored.** Only a salted PBKDF2 hash is saved in the database.
- **No SQL injection.** All queries use `PreparedStatement`.
- **Your database password stays on your computer.** It lives in `db.properties`, which is listed in `.gitignore`.
- **Accounts are separated.** Every query is limited to the logged-in user's own papers.

## Author

**Md Shihab Hossain**, CSE · AI · Data Science
[GitHub](https://github.com/mdshihabhossain726) · [Portfolio](https://mdshihabhossain726.github.io)

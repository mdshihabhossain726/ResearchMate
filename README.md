# ResearchMate Easy

Ekta chhoto Java Swing desktop app: research paper save kora, PDF theke text pora, keyword ber kora, duita paper compare kora.
**Java 21 + Swing + PostgreSQL (JDBC) + Apache PDFBox + JUnit + Maven.** Mot 15 ta source file.

## Run korar niyom
1. PostgreSQL install/start thakte hobe.
2. `db.properties.example` copy kore `db.properties` naam dao, ar nijer postgres password lekho.
3. VS Code theke (Extension Pack for Java lagbe) terminal-e:
   ```
   mvn compile exec:java
   ```
   Test chalate: `mvn test`
4. Prothombar chalale database `researchmate` ar table `papers` nijei toiri hobe.

## File-er kaj
| File | Kaj |
|---|---|
| `Main` | Program shuru; DB connect kore window kholey |
| `MainFrame` | Main window, 4 ta tab |
| `PapersPanel` | Paper list, search, add, edit, delete, CSV export |
| `ImportPanel` | PDF choose kore text pora (SwingWorker thread) ar save |
| `KeywordsPanel` | Ekta paper-er shobcheye beshi use hoa shobdo |
| `ComparePanel` | Duita paper compare: similarity %, common/unique keyword |
| `Paper` | Paper-er data (Encapsulation) |
| `Db` | PostgreSQL connection ar table toiri |
| `PaperDao` | Shob SQL ekhane (PreparedStatement) |
| `PdfReader` | PDFBox diye PDF text pora |
| `TextAnalyzer` | Shobdo alada kora, stop word bad, count |
| `Similarity` | Interface (Abstraction) |
| `JaccardSimilarity`, `CosineSimilarity` | Duita method (Polymorphism) |
| `Ui` | Message box, input check, CSV quoting |

## OOP / syllabus-er shathe mil
| Concept | Kothay |
|---|---|
| Encapsulation | `Paper` (private field + getter/setter) |
| Interface, Abstraction | `Similarity` |
| Polymorphism | `ComparePanel`-e `Similarity` combo |
| Collections | `HashSet` (stop word), `HashMap` (count), `TreeSet` |
| Exception handling | `SQLException`, `IOException`, `IllegalArgumentException` |
| File handling | PDF pora, CSV export |
| JDBC | `Db`, `PaperDao` |
| Multithreading | `ImportPanel` `SwingWorker` |
| Unit test | `TextAnalyzerTest`, `SimilarityTest`, `UiTest`, `PdfReaderTest` |

## Demo (3 minute)
1. My Papers: ekta paper Add koro, search koro, Edit/Delete dekhao.
2. Same title+year abar add korle "already exists" error dekhao (duplicate prevention).
3. Import PDF: ekta PDF choose koro, Save koro.
4. Keywords: paper select kore Analyze.
5. Compare: duita paper select kore Jaccard ar Cosine duitai dekhao.
6. My Papers theke Export CSV, Excel-e khulo.

## Chhoto viva Q&A
- **Jaccard ki?** Common shobdo / mot alada shobdo.
- **Cosine ki?** Shobdo koybar ashe seta dhore duita vector-er angle.
- **Stop word keno bad?** `the`, `and` er moto shobdo topic bujhay na.
- **PreparedStatement keno?** SQL injection thekay.
- **SwingWorker keno?** Boro PDF porar somoy window jeno hang na kore.
- **Limitation?** Eta shudhu shobdo gune, artho bujhe na; scanned PDF-e text pay na.

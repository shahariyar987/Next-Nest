# NextNest — House Rent & Sale Desktop App

A JavaFX desktop application for finding, posting and discussing houses for rent or sale in Dhaka. Users sign up with their mobile number, publish property posts with photos, and interact with other users' listings through likes, dislikes and comments.

## Features

- **Sign up / log in** with mobile number, NID, gender and address
- **Create posts** for rent or sale: title, description, price, house number, location, contact details, negotiable or fixed, and a photo
- **Home feed** of all listings with search by area
- **Like / dislike** reactions (one per user per post) and a **comment** thread on every post
- **Post details** page with full property information
- **Profile page** with the user's own posts, profile picture upload and post deletion
- **Review & complaint** section, settings, help and about pages

## Tech stack

Java 22 · JavaFX (FXML + CSS) · MySQL (JDBC) · Maven · IntelliJ IDEA

## Run it locally

1. Install **JDK 22**, **MySQL**, and **IntelliJ IDEA**.
2. Create the database: open MySQL Workbench and run `database/schema.sql`.
3. Copy `db.properties.example` to `db.properties` (in the project root) and put in your MySQL username and password.
4. Open the folder in IntelliJ as a Maven project.
5. Add the MySQL driver: **File → Project Structure → Libraries → + → Java**, and select `lib/mysql-connector-j-9.1.0.jar`.
6. Run `com.example.nextnest.HelloApplication`.

## Project structure

```
nextnest/
├── src/main/java/com/example/nextnest/
│   ├── HelloApplication.java      # App entry point
│   ├── LogSignUtils.java          # Login / sign-up logic
│   ├── HomeController.java        # Feed, search, reactions
│   ├── newPostController.java     # Create a post
│   ├── PostDetailsController.java
│   ├── PostCommentController.java
│   ├── ProfileController.java
│   ├── ReviewComplainController.java
│   └── DatabaseUtil.java          # MySQL connection (reads db.properties)
├── src/main/resources/            # FXML screens, CSS, images
├── database/schema.sql
├── lib/mysql-connector-j-9.1.0.jar
└── pom.xml
```

## Future improvements

- Hash passwords (currently stored as plain text) with BCrypt
- Use one consistent table-name casing so the app runs on Linux/macOS MySQL without extra settings

## Team

- Md. Injabin Alam
- Iffat Ibne Nashir Sifat
- Md. Al Shahariyar

## License

MIT — see [LICENSE](LICENSE).

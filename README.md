# Project VARS Task Organizer (TEAM 117)

![OrgaLEARNizer](./OrgaLEARNizer.PNG)

# Goal:

The goal of this project is to develop a user-friendly desktop application that simplifies university students' academic lives. The platform aids in effortlessly managing deadlines and tracking grades, with a key focus on automating the data entry processes.

##### Significant Features Include:

1. _Task View:_
   - View of tasks by course and sorted by due date
   - Clickable cards to edit individual tasks
2. _Grades View:_
   - View of tasks by chosen course along with weights
   - Allows users to input scores for tasks and calculates their running total
3. _Data Import Features:_

   - AI feature to prepare tasks from a course outline. Users can upload their respective course outline PDFs, from which we use GPT-3.5 to extract course tasks.
     - The app prompts the user to enter an API key before using this AI feature and stores it locally on the user's system for future reference
   - Web Scraping feature to extract tasks from external course sites. Users can provide a URL of the course along with indices for the date and task field from the table to be parsed out. \
     Sample URLs: \
     CS346 - https://student.cs.uwaterloo.ca/~cs346/1239/schedule/index.html \
     CS135 - https://student.cs.uwaterloo.ca/~cs135/assign/index.html

   - It also supports manual individual task addition.

4. _Export Feature:_
   - Exports Tasks as an ICS file to the Downloads folder that can be imported into any calendar software.
5. _FAQ Help Page (Info icon in the app)_: https://chestnut-spring-739.notion.site/FAQ-OrgaLEARNizer-e7267156665143299f3e04db810c1e72?pvs=4
6. _Preferences:_
   - Preferences tracking feature which stores window position, size, the current view, and lastly, a users OpenAI API key in a local `app_preferences.json` file

## Installation & Running Instructions

1. Run the server

You can run the server locally using Docker.

- Pull the latest image from Docker Hub
  - Run `docker pull r8ravind/project-vars:latest`
  - Run `docker run -p 3000:3000 r8ravind/project-vars:latest`
- Now that the server is running install the application in the next step

2. Install the client-side app

These are the currently released versions of our client-side app, including release notes and platform-specific installers. We recommend installing the latest version for testing.

- version 1.0 ([link](https://git.uwaterloo.ca/a33srini/project-vars/-/wikis/Release-Notes#vars-v10))
- version 1.1 ([link](https://git.uwaterloo.ca/a33srini/project-vars/-/wikis/Release-Notes#vars-v11))
- version 1.2 ([link](https://git.uwaterloo.ca/a33srini/project-vars/-/wikis/Release-Notes#vars-v12))
- version 1.3 (final) ([link](https://git.uwaterloo.ca/a33srini/project-vars/-/wikis/Release-Notes#vars-v13))

After downloading the installer for your respective operating system, run it and install the application.

Note: For testing the AI features, you can use the API Key: `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`

# Team Members:

1. Aditya Srinivasan Mani - a33srini@uwaterloo.ca
2. Srishti Prayag - sprayag@uwaterloo.ca
3. Vedant Gupta - v57gupta@uwaterloo.ca
4. Rohan Ravindran - r8ravind@uwaterloo.ca

# Software Release

1. Software Releases Wiki: https://git.uwaterloo.ca/a33srini/project-vars/-/wikis/Release-Notes

# Project Documents (Links to Wiki Pages):

1. Project Proposal: https://git.uwaterloo.ca/a33srini/project-vars/-/wikis/Project-Proposal
2. Meeting Minutes: https://git.uwaterloo.ca/a33srini/project-vars/-/wikis/Team-117-Meeting-Minutes

# Development Practices Used

In the development of this app, our team used a mix of collaborative and review-based development practices, including:

- _Pair Programming:_ We often worked in groups for complex debugging sessions. This helped us resolve issues quicker by facilitating knowledge sharing and also helped us maintain a high standard of code quality.
- _Code Reviews:_ Regular code reviews for all our Pull Requests were an important part of our workflow

Both these development best-practices were beneficial to our team.

# Architecture & Design Decisions

Design Patterns Used:

- _Client-Server Model:_ Our application follows a standard client-server architecture, with a Kotlin Compose Multiplatform client and a KTor Kotlin backend server. This separation of concerns allows for more manageable development and maintenance.
- _SQLite Database with Singleton Pattern:_ We opted for SQLite and implemented it using the singleton pattern for our database management. Creating multiple database connections can be costly, so by instantiating a single database connection and reusing it throughout the app, we improved performance and simplicity from a code perspective.

Overall Architecture Diagram: ([link](https://git.uwaterloo.ca/a33srini/project-vars/-/wikis/uploads/7dd71cf53d081ae96415cf8770b4b784/IMG_6490.PNG))

Data Model Diagram: ([link](https://git.uwaterloo.ca/a33srini/project-vars/-/wikis/uploads/e23f889fb6553cf159930c92cc4cc531/Screenshot_2023-12-06_at_2.00.27_AM.png))

Beneficial technical decisions:

- Client-Server Architecture: This architecture facilitated a clear distinction between UI and data processing, leading to a modular and scalable application design.
- Use of Kotlin Multiplatform: This allowed us to maintain a single code base targeting multiple platforms (Windows, Linux, macOS), enhancing code reuse and reducing the overall development time.

Un-beneficial technical decisions:

- SQLite Database: While simple, SQLite may not be as powerful as other database solutions, especially in handling high volumes of data or complex transactions. A more robust database system could enhance our app's performance at scale.
- Single-User Database Schema: Our current database schema design is focused on single-user scenarios. This limitation means that scaling the application to a cloud-based, multi-user platform would require significant changes to the database schema.

# Users

Our project primarily targets undergraduate students, addressing their common challenges in managing course deadlines and grades.

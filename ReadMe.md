

# **Mobile Quiz Application Backend Architecture Document**

This document outlines the architecture of the backend system for the Mobile Quiz Application. The system is designed to serve quiz questions to mobile applications, manage user interactions, game sessions, lifelines, and statistics. The architecture ensures security, scalability, and reliability while providing a seamless user experience.

## **System Overview**

The Mobile Quiz Application backend is built using a microservices architecture. It consists of multiple APIs, each responsible for specific functionalities. The APIs interact with a relational database for data storage, AWS S3 for images

### **Components**

#### 1. Admin Service - Provides administrative functions, including question and answer management, user account management, and access control.

#### 2. Game Service - Orchestrates game sessions, tracks user progress, and manages lifeline usage.

#### Caching Strategy - A caching strategy has been implemented, particularly focusing on optimizing user-specific sessions. The caching mechanism serves to reduce database queries for questions and answers, resulting in improved user experience during gameplay.

### API Endpoints and functionalities

Start Game Session API:
GET /api/quiz/startGameSession
Starts a new game session for a user.
Validates user authentication.
Initiates a new game session, possibly assigning questions and lifelines.
Returns a response containing session-related information.

##### GET /api/quiz/questions/next

Retrieves the next question for a given game session.
Validates the game session and checks if it's completed.
Returns a response containing the next question and answer options.

##### GET /api/quiz/questions/answers

Submits a user's selected answer for a question in the current game session.
Validates the game session and checks if it's completed.
Returns a response indicating whether the answer is correct and provides feedback.

##### POST /api/quiz/questions/{questionId}/lifelines/add-time

Allows the user to use the "+10s" lifeline to extend the time for a question.
Validates the game session, checks lifeline availability, and updates the session's end time.
Returns a response indicating the new remaining time after using the lifeline.

##### POST /api/quiz/questions/{questionId}/lifelines/fifty-fifty

Allows the user to use the "50/50" lifeline to remove two incorrect answer options.
Validates the game session and retrieves modified answer options after applying the lifeline.
Returns a response containing the modified answer options.

##### POST /api/quiz/{gameId}/end

Ends the current game session and calculates game statistics.
Returns a response containing game statistics.

##### GET /api/quiz/statistics/{userId}

Retrieves game statistics for a specific user.
Returns a response containing user-specific game statistics.
Session Validation and Error Handling:

The validateSession method is used to validate the current game session against the end time and marks it as completed if necessary.
The controller also handles proper error responses for cases such as invalid game sessions, unauthorized users, and lifeline usage.

 
#### Question API:

##### POST /admin/add-question
Adds a new question to the system.
Accepts a question text and an optional image file.
Returns a response indicating the success of the operation.

##### PUT /admin/edit-question/{id}
Edits an existing question based on the provided question details.
Accepts a QuestionDTO containing the updated question text and options.
Returns a response indicating the success of the operation.

##### DELETE /admin/delete-question/{questionId}
Deletes a question from the system.
Accepts the question ID to identify the question to be deleted.
Returns a response indicating the success of the operation.

##### POST /admin/add-answers/{questionId}
Adds new answers to an existing question.
Accepts the question ID and a list of AnswerDTO objects.
Returns a list of added Answer entities.

##### PUT /admin/edit-answer/{answerId}
Edits an existing answer based on the provided updated answer details.
Accepts an AnswerDTO containing the updated answer text and correctness.
Returns a response indicating the success of the operation.



#### User API:

##### POST /admin/users

Adds a new user to the system.
Accepts a UserDTO object containing user details.
Saves the user to the database and returns a response with the saved user.

##### GET /admin/users/{userId}

Retrieves a user by their ID.
Accepts the user's ID and fetches the user from the database.
Returns the retrieved user if found, or a "not found" response if the user does not exist.

##### PUT /admin/users/{userId}

Edits an existing user's details.
Accepts the user's ID and an UserDTO containing updated user details.
Updates the user's username and email in the database and returns the updated user.

##### DELETE /admin/users/{userId}

Deletes a user from the system.
Accepts the user's ID and deletes the user from the database.
Returns a response indicating the success of the operation.

## Unfinished modules

#### User Authentication and Authorization API (Unfinished)
##### Register a New User
##### Authenticate User (Login)
##### Authorization with OAuth2(AWS cognito provider)
##### User Logout API

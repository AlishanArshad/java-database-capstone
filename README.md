# Schema Architecture

## Section 1: Architecture Summary

The application follows a layered client-server architecture where the frontend communicates with the backend through HTTP requests. The backend receives requests, validates the input, applies business logic, and interacts with the database using a data access layer or ORM. The database stores and retrieves persistent data, and the backend formats the results into JSON responses before sending them back to the frontend. This separation of responsibilities improves maintainability, scalability, and security.

---

## Section 2: Numbered Flow

1. The user performs an action in the frontend (e.g., submits a form or clicks a button).
2. The frontend sends an HTTP request (GET, POST, PUT, or DELETE) to the appropriate backend API endpoint.
3. The backend router receives the request and forwards it to the corresponding controller.
4. The controller validates the request data and performs any necessary authentication or authorization.
5. The controller calls the service layer to execute the required business logic.
6. The service layer processes the request and interacts with the repository/data access layer.
7. The repository or ORM sends SQL queries to the database.
8. The database executes the query and returns the requested data or confirms the update.
9. The repository converts the database result into application objects and returns them to the service layer.
10. The service layer processes the returned data and sends it back to the controller.
11. The controller creates an appropriate HTTP response (usually JSON with a status code).
12. The backend sends the response to the frontend.
13. The frontend receives the response and updates the user interface to display the requested information or confirmation message.

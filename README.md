
# Loan Approval Hub Setup Guide

## Unified Script Usage

All setup and run commands are now handled by a single script:

```
./project.sh [setup|backend|frontend|help]
```

### Commands
- `setup`: Installs dependencies for frontend and backend
- `backend`: Starts the Spring Boot backend server
- `frontend`: Starts the Vite React frontend server
- `help`: Shows usage instructions

## Example Usage

1. Install dependencies:
	```
	./project.sh setup
	```
2. Start backend:
	```
	./project.sh backend
	```
3. Start frontend:
	```
	./project.sh frontend
	```

## Requirements
- Node.js, npm, and Java (JDK 17+) must be installed
- If `./mvnw` is not available, Maven must be installed and accessible via `mvn`

## Project Structure
- `backend/`: Spring Boot backend
- `loan-approval-hub/`: React frontend

---
For more details, see the individual README files in each project folder.

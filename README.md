# TypeSprint

TypeSprint is a real-time multiplayer typing game where players compete to type texts accurately and quickly. This project uses Spring Boot for the backend and WebSockets for real-time communication. 

## Table of Contents

1. [Demo](#demo)
2. [Features](#features)
3. [Technologies Used](#technologies-used)
4. [Getting Started](#getting-started)
    - [Prerequisites](#prerequisites)
    - [Installation](#installation)
5. [Usage](#usage)
    - [Running the Application](#running-the-application)
    - [Accessing the Demo](#accessing-the-demo)

## Demo

Check out the live demo: [TypeSprint Demo](https://typestomp.onrender.com) (hosted on Render.com)  
<sub>Note: The demo might take up to a minute to load due to hosting conditions.</sub>

## Features

- **Real-time Multiplayer Typing Competition**: Players can join rooms and compete against each other to type texts accurately and quickly.
- **Room Creation**: Rooms are created through REST endpoints, and each room is assigned a unique ID. The uniqe room ID is used for all communication within that room, like leaving/joining a room, position handling etc.

- **Real-time Position Tracking**: Players positions in the text are tracked in real-time using the STOMP protocol over WebSockets, allowing all players to see each other's progress.


## Technologies Used

- Java
- Spring Boot
- WebSockets (STOMP)
- Gradle
- HTML/CSS/JavaScript

## Getting Started

### Prerequisites

- Java 11 or higher
- Gradle
- A web browser

### Installation

1. **Clone the repository**:
    ```bash
    git clone https://github.com/willbehn/fullstack-typing-game.git
    cd typingStomp
    ```

2. **Build the project**:
    ```bash
    ./gradlew build
    ```

3. **Run the application**:
    ```bash
    ./gradlew bootRun
    ```

## Usage

### Running the Application

Once the application is running, you can access it at `http://localhost:8080` in your web browser.

### Accessing the Demo

Visit the [live demo](https://typestomp.onrender.com)  
<sub>Note: The demo might take up to a minute to load due to hosting conditions.</sub>

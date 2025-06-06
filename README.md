# Epitweet - Distributed Micro-Blogging Platform

Epitweet is a **proof-of-concept distributed micro-blogging application** developed as part of the EPITA TinyX SCALE project. Inspired by Twitter, this platform allows users to post short messages, interact through likes and follows, and perform full-text search on posts — all using a modern **microservices-based architecture**.

---

## 🌐 Accessing the Application

Skip the setup and explore the live application directly at:  
🔗 [http://tinyx.sortifal.fr](http://tinyx.sortifal.fr)


---

## 🔧 Technical leverages

This project leverages:
- **Java with Quarkus** for backend services
- **Angular** for the frontend
- **MongoDB, ElasticSearch, Neo4j** as NoSQL databases
- **Redis** for asynchronous communication
- **Docker** for local development
- **Kubernetes** for deployment

---

## 🔧 Architecture Overview

```
Frontend
   |
   v
Angular App --> REST APIs --> Microservices (Quarkus)
                          ├── user-service (auth)
                          ├── repo-post (posting)
                          ├── search-service (indexing)
                          ├── social-service (likes/follows/blocks)
                          ├── user-timeline-service
                          └── home-timeline-service
Databases
  ├── MongoDB (posts)
  ├── ElasticSearch (search index)
  └── Neo4j (social graph)

Communication: REST + Redis Messaging Queue
```

Each microservice is independently deployable and communicates primarily through REST and Redis queues. The backend follows a clean architecture with domain isolation, DTOs, and clearly separated layers.

---

## 📁 Repository Structure

```
Epitweet/
├── backend/                     # Microservices (Quarkus)
│   ├── common/                  # Shared code (DTOs, utils)
│   ├── repo-post/               # Post management
│   ├── user-service/            # User authentication
│   ├── social-service/          # Social graph (likes, follows)
│   ├── search-service/          # Search and indexing
│   ├── home-timeline-service/   # Timeline based on follows
│   ├── user-timeline-service/   # Personal timeline
│   ├── docker/                  # Compose setup (Mongo, Elastic, etc.)
│   ├── pom.xml                  # Backend project configuration
│   └── integrationTests.http    # HTTP-based tests
│
├── frontend/                    # Angular 19 frontend
│   ├── src/
│   ├── angular.json
│   └── README.md
│
├── k3s/                        # Kustomize deployment configs
│   ├── *
│
├── README.md                   # This file
```

---

## 🧠 Features Implemented

- User registration and management
- Post creation, reply, repost, deletion
- Like/unlike posts, follow/unfollow/block users
- User and home timelines (with Redis event handling)
- Full-text and hashtag-based search
- Integration with MongoDB, Neo4j, ElasticSearch
- Redis-based asynchronous messaging
- RESTful APIs following clean architecture
- Docker and K3S deployment support

---

## 📚 Project Context

This project was built in the context of the **TinyX SCALE Project at EPITA**. It aims to:
- Transition students from monolithic to microservice architectures
- Train them in NoSQL, message queues, and container orchestration
- Encourage real-world engineering practices: modularity, testing, documentation

---

## 🚀 Build & Run Instructions

You can run **Epitweet** either via full **Kubernetes deployment** (frontend + backend) or locally run **only the backend**. Choose the option that suits your needs:

---

### 🔁 Option 1: Full Deployment with Kubernetes (Frontend + Backend)


1. Navigate to the Kubernetes config directory:
    ```bash
    cd k3s/
    ```
2. Apply all deployments and services using Kustomize:
    ```bash
    kubectl apply -k .
    ```
3. Follow any additional setup or service exposure instructions detailed in the k3s/README.md to connect to our tinyTwitter.

### 🛠️ Option 2: Local Backend Development (Backend Only)

1. Navigate to the backend directory:
    ```
    cd backend/
    ```
2. Refer to the backend README for more detailed instructions and setup to run backend services.

---

## 🧑‍💻 Contributors

Developed by a team of 13 EPITA students across various majors and campuses.

- abel.aubron
- alexandre.privat
- alexis.bruneteau
- axelle.destombes
- emmanuel.le-dreau
- erwann.lesech
- pennec.evan
- florian.ruiz
- kemil.bina
- khatir.youyou
- mikhael.darsot
- nathan.claude
- victor.mandelaire

# Booking Homestay Project

## Overview

The Booking Homestay project is a web application designed to facilitate the booking of homestays. It provides users
with a platform to search, view, and book homestays with various amenities and types. The application is built using
modern web technologies and follows best practices for security and performance.

## Technologies Used

### Backend

- **Spring Boot**: A framework for building Java-based enterprise applications.
- **Spring Security**: Provides authentication and authorization capabilities.
- **Spring Data JPA**: Simplifies database interactions using JPA (Java Persistence API).
- **Hibernate**: An ORM (Object-Relational Mapping) tool for managing database operations.
- **PostgreSQL**: A powerful, open-source relational database system.

### Frontend

- **ReactJS**: A JavaScript library for building user interfaces.
- **Supporting Libraries**: Various libraries to enhance the functionality of ReactJS applications.

### Security

- **JWT (JSON Web Tokens)**: Used for securing API endpoints and managing user sessions.
- **OAuth2**: Integration with Google for user authentication.

### Other Technologies

- **Cloudinary**: For managing and storing images.
- **Pusher**: For real-time notifications.
- **Gmail SMTP**: For sending emails.

## Features

### User Management

- **Registration and Login**: Users can register and log in using email and password or via Google OAuth2.
- **Profile Management**: Users can update their profile information.

### Homestay Management

- **Search and Filter**: Users can search for homestays based on location, type, and amenities.
- **Booking**: Users can book homestays for specific dates.
- **Reviews and Ratings**: Users can leave reviews and ratings for homestays they have stayed in.

### Admin Panel

- **User Management**: Admins can manage user accounts.
- **Homestay Management**: Admins can add, update, or delete homestays.
- **Booking Management**: Admins can view and manage all bookings.

### Notifications

- **Email Notifications**: Users receive email notifications for booking confirmations and updates.
- **Real-time Notifications**: Users receive real-time notifications for important updates.

## Configuration

The configuration details for the project are specified in the `application.properties` file. The project relies on
environment variables that are stored on the server to function correctly. If you clone the project repository, you will
need to manually add these environment variables to your server configuration to ensure the application runs properly.

### Required Environment Variables

- `DATABASE_URL`
- `DATABASE_USERNAME`
- `DATABASE_PASSWORD`
- `JWT_SECRET_KEY`
- `JWT_EXPIRATION`
- `JWT_REFRESH_EXPIRATION`
- `MAIL_USERNAME`
- `MAIL_PASSWORD`
- `GOOGLE_CLIENT_ID`
- `GOOGLE_CLIENT_SECRET`
- `CLOUDINARY_CLOUD_NAME`
- `CLOUDINARY_API_KEY`
- `CLOUDINARY_API_SECRET`
- `PUSHER_APP_ID`
- `PUSHER_KEY`
- `PUSHER_SECRET`
- `PUSHER_CLUSTER`

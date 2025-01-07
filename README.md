# HomeSharing App

## Overview

HomeSharing is a feature-rich Android application developed using Kotlin and Jetpack Compose for an intuitive and responsive user interface. The app leverages Firebase for backend services, including authentication, database, and real-time messaging. It is designed to facilitate users in finding shared accommodations and compatible roommates, managing living arrangements, and enhancing the shared living experience.

## Features

### 1. User Authentication

- **Sign Up, Login, Logout:** Users can register a new account, log in, and log out securely.
- **Phone Number Verification (OTP):** Users must verify their phone numbers via OTP for enhanced security.

### 2. User Profile

- **Profile Management:** Users can create and update their personal profiles, including preferences and lifestyle information.

### 3. Accommodation Search

- **Search for Shared Housing:** Users can search for accommodations based on:
  - **Location**
  - **Price Range**
  - **Amenities**
  - **Preferences and Interests**

### 4. Roommate Search

- **Roommate Matching:** The app uses a filtering algorithm to suggest potential roommates who share similar interests and lifestyles.

### 5. Post Listings

- **Post Advertisements:** Users can post advertisements to find roommates or available accommodations.
- **Interest Tracking:** Users can express interest in posts. When enough members show interest, a group chat is automatically created.

### 6. Detailed Information Display

- **Post Details:** Display detailed information about the user who created the post, including their profile and preferences.

### 7. Messaging System

- **Real-Time Chat:** Users can communicate in real-time through the built-in messaging system.

### 8. Notifications

- **Alerts and Reminders:** The app provides notifications for new messages, post updates, and upcoming events.

### 9. Appointment Scheduling

- **House Viewing Schedule:** Users can schedule house visits and receive reminders.

### 10. Expense Management

- **Living Expense Tracking:** Users can manage and track shared living expenses.

### 11. Contract Templates

- **Predefined Contract Templates:** The app provides contract templates that users can fill out and save.

### 12. Policies and Rules

- **Guidelines:** Users can access predefined policies and guidelines for shared living.

### 13. Roommate Suggestions

- **Intelligent Recommendations:** The app suggests potential roommates based on compatibility.

### 14. Contract Storage

- **Document Storage:** Users can securely store and access shared living contracts.

### 15. Resident Group Creation

- **Community Groups:** Users can create and manage resident groups for communication and coordination.
## Images 
![image](https://github.com/user-attachments/assets/1562f7f6-40aa-4476-9660-db8ff4172054)  ![image](https://github.com/user-attachments/assets/9921e78e-d710-4461-8791-447c3a67fd36)

![image](https://github.com/user-attachments/assets/9fd9852e-2e2f-48c4-9aa1-b42c6bfb939e)  ![image](https://github.com/user-attachments/assets/2efde0f4-7ef3-4bcf-90ec-687ad10888de)

![image](https://github.com/user-attachments/assets/75ff5840-1043-4618-85c3-eefa1ef795af)  ![image](https://github.com/user-attachments/assets/ccf52ad7-e5e1-4bef-8d03-3d1d29e3d4f8)

![image](https://github.com/user-attachments/assets/cfe43e27-019e-4c86-af10-d6764ce1263d)  ![image](https://github.com/user-attachments/assets/c73a916f-b234-4883-847e-d4d40a4ebbef)

![image](https://github.com/user-attachments/assets/33b244cb-c34d-4d22-8cdd-ef66244ed965)  ![image](https://github.com/user-attachments/assets/195a80e5-ee40-48c7-8a2e-4e3a18908c66)

![image](https://github.com/user-attachments/assets/8dfdc4b9-15c5-4a27-80f9-463744ca57ae) ![image](https://github.com/user-attachments/assets/3b88a706-be7a-420d-b853-21b564f44102)

![image](https://github.com/user-attachments/assets/a9059d69-c977-4514-ac5f-dd96dc013d55)

## Tech Stack

### Frontend

- **Kotlin**
- **Jetpack Compose**

### Backend

- **Firebase Authentication:** For secure user authentication
- **Firebase Realtime Database / Firestore:** For storing user data and posts
- **Firebase Cloud Messaging:** For real-time notifications and chat
- **Firebase Storage:** For storing images and documents

### Other Tools and Libraries

- **Coroutines:** For asynchronous operations
- **Hilt:** For dependency injection
- **Retrofit:** For potential external API calls
- **Glide/Coil:** For image loading
- **Material Design Components:** For UI consistency

## Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/PowChann/Final_Android.git
   ```
2. Open the project in Android Studio.
3. Sync the Gradle files.
4. Set up Firebase:
   - Add your `google-services.json` file to the `app/` directory.
   - Enable necessary Firebase services (Authentication, Realtime Database/Firestore, Cloud Messaging, Storage).
5. Build and run the project on an emulator or physical device.

## Usage

1. **Sign up** with a phone number and verify it using OTP.
2. **Create or update** your profile.
3. **Search** for shared accommodations or potential roommates using filters.
4. **Post advertisements** for available accommodations or roommate searches.
5. **Express interest** in posts and join group chats when enough members are interested.
6. **Schedule house viewings** and manage shared expenses.
7. **Access contract templates**, fill them out, and store them securely.


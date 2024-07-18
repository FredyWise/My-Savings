# My Savings
<img src="https://github.com/user-attachments/assets/80bcd632-3803-4e0f-9002-543c5111068f" 
     alt="Image description" 
     style="margin: 0 auto; width: auto;">

## Description

**My Savings** is a cutting-edge money management system that leverages machine learning to simplify expense tracking. By converting pictures of receipts into text and objects, this Android native application, built with Kotlin and Jetpack Compose, makes it easier to record expenses. The application also supports multiple currencies, providing real-time data updates through an API, and offers advanced features to personalize and secure your financial data.

## Features

- **Receipt Scanning**: Capture and convert receipts into text and object representations for easy expense recording.
- **Currency Converter**: Record expenses and income in multiple currencies with real-time updates.
- **Multi-Wallet Support**: Record and manage multiple bank accounts or savings locations.
- **Multi-Category Support**: Define custom categories for income and expenses, such as food, drinks, taxes, and salary.
- **Multi-Books Support**: Separate personal records from business or other types of records using different books.
- **Advanced Filtering**: Filter records by time range and currency.
- **Preferences Settings**: Add biometric security, customize the application theme, and set notification reminders.
- **Flexible Authentication**: Choose from email-password, phone number, or Google authentication, powered by Firebase.

## Installation

### For Normal Users

#### Prerequisites

- Android mobile phone with minimum API level 29

#### Steps

1. **Download the APK**: Get the latest release from the [Releases](https://github.com/FredyWise/My-Savings/tree/main/release) section.
2. **Install the APK**: Transfer the APK to your Android device and install it.
3. **Run the Application**: Open the application and start managing your finances!

### For Developers

#### Prerequisites

- Android Studio installed
- Basic knowledge of Kotlin and Jetpack Compose

#### Steps

1. **Clone the Repository**:
    ```sh
    git clone https://github.com/FredyWise/My-Savings.git
    ```
2. **Open the Project**: Open the project in Android Studio.
3. **Sync Gradle**: Sync the project with Gradle to download dependencies.
4. **Build and Run**: Build and run the application on an emulator or a physical device.

## Usage

### Getting Started

1. **Open the Application**: Launch the My Savings app on your device.
2. **Sign In**: Choose a sign-in method:
   - **Email-Password**: Register with the sign-up button if you don't have an account (the email doesn't need to be real).
   - **Phone Number**: Use your phone number for authentication.
   - **Google Auth**: Sign in with your Google account.
3. **Successful Authentication**: After signing in, the Record screen will appear.

### Main Features

- **Record Screen**: Start recording your expenses or income. You can capture receipts and convert them into text and objects for easier management.
- **Analysis Screen**: Access through the bottom bar to view your records in various graphs and visual formats.
- **Wallet Screen**: Perform CRUD (Create, Read, Update, Delete) operations on your wallets.
- **Category Screen**: Perform CRUD operations on your custom categories.

### Additional Settings

1. **Side Drawer**: Open the side drawer by clicking the burger button on the top left of the screen. Here you will find:
   - **Preferences Settings**: Customize the application with biometric security, themes, and daily notification reminders.
   - **Import/Export Function**: Import and export your data to/from CSV files for easy data access and portability.
   - **Currency Settings**: Choose your base currency and adjust currency values.
   - **Log Out**: Sign out from the application.

## Contributing

We welcome contributions to enhance My Savings! To contribute:

1. Fork the repository.
2. Create a new branch for your feature or bug fix:
    ```sh
    git checkout -b feature-name
    ```
3. Make your changes and commit them:
    ```sh
    git commit -m "Add new feature"
    ```
4. Push to your branch:
    ```sh
    git push origin feature-name
    ```
5. Create a pull request detailing your changes.

## Additional Information

### Build Configuration

- **Namespace**: `com.fredy.mysavings`
- **Compile SDK**: 34
- **Min SDK**: 29
- **Target SDK**: 33
- **Version Code**: 1
- **Version Name**: "1.0"

### Dependencies

- **Core Libraries**:
  - `androidx.core:core-ktx:1.12.0`
  - `androidx.lifecycle:lifecycle-runtime-ktx:2.7.0`
  - `androidx.activity:activity-compose:1.8.2`
- **UI Libraries**:
  - `androidx.compose.ui:ui`
  - `androidx.compose.ui:ui-graphics`
  - `androidx.compose.ui:ui-tooling-preview`
  - `androidx.compose.material3:material3`
- **Firebase**:
  - `com.google.firebase:firebase-ml-vision:24.1.0`
  - `com.google.firebase:firebase-firestore-ktx`
  - `com.google.firebase:firebase-storage-ktx`
  - `com.google.firebase:firebase-auth-ktx`
  - `com.google.firebase:firebase-messaging-ktx`
- **Others**:
  - `com.google.dagger:hilt-android:2.50`
  - `androidx.room:room-runtime:2.6.1`
  - `com.squareup.retrofit2:retrofit:2.9.0`
  - `io.coil-kt:coil-compose:2.4.0`

---

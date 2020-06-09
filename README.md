# Busy Mama

## Project Summary

Busy Mama uses geolocation to recognize shop locations and remind the user to update their budget with the latest expense. Busy Mama also uses Firebase to authenticate, to store shoping location and spending data.

## Pre-requisites

- Android SDK v28
- Android min SDK v23

## Tools Used

- [**Firebase**](https://firebase.google.com/) 17.0.1: For user authentication, and storing data. It will keep these features centralized, on the cloud, and easily available across other mobile devices that the user has.

- [**Google Play Services**](https://firebase.google.com/) 17.0.0: For geolocation and geofencing

## Instructions

Download or clone this repo on your machine, open the project using Android Studio. Go to [Firebase](https://firebase.google.com/), create an account, setup a project for Android, setup Firebase authentication, and Places database . Follow the instructions on Firebase console, and download the appropriate json file. Once Gradle builds the project, click "run" and choose an emulator. A Google Services API key is also required.

## User Experience

- Users can sign up or login using their email
- Users can add their favorite shopping places
- Users can track their expenses

## License
MIT

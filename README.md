# Suraksha: Stay One Step Ahead of Danger

![Suraksha Logo](https://github.com/satyamsharma17/Suraksha/blob/master/app/src/main/res/drawable/Suraksha%20Banner.png)

**Suraksha** is an open-source personal safety app designed to enhance your security with intuitive and easy-to-use features. From SOS alerts to fake siren sounds, Suraksha is your go-to application for personal safety.

## Table of Contents
- [Features](#features)
- [Installation and Setup](#installation-and-setup)
- [Usage](#usage)
- [Project Structure](#project-structure)
- [Contributing](#contributing)
- [License](#license)
- [Contact](#contact)

## Features

1. **Emergency Contacts Configuration**: Easily add up to 5 trusted contacts for emergency notifications.
2. **SOS Activation**: Shake your device to quickly alert your emergency contacts.
3. **Fake Siren Alert**: Activate a loud siren to deter potential threats.
4. **Emergency Helplines**: Provides easy access to essential emergency helplines.
5. **Continuous Safety Feature**: Suraksha continues to operate in the background, ensuring ongoing protection.

## Installation and Setup

### Clone the Repository

```bash
git clone https://github.com/satyamsharma17/Suraksha.git
```

Navigate to the cloned directory and follow platform-specific setup guidelines to get the app running on your device.

## Usage

- **Configuring Emergency Contacts**: In the 'Emergency Contacts' section, add trusted contacts and inform them about their inclusion.
- **SOS Activation**: Tap the SOS button on the main interface to enable the shake-to-alert feature. Shake your device more than five times to send an emergency alert.
- **Fake Siren Alert**: Access this from the main interface to start a loud siren; tap again to stop it.
- **Emergency Helplines**: Quickly access and call emergency services through a dedicated section.

## Project Structure

```plaintext
Suraksha/
│
├── .idea/                                  # IDE configuration files
│   ├── .name
│   ├── compiler.xml
│   ├── deploymentTargetDropDown.xml
│   ├── gradle.xml
│   ├── kotlinc.xml
│   ├── misc.xml
│   └── vcs.xml
│
├── .gitignore                              # Root-level .gitignore file
├── LICENSE                                 # License file for the project
├── README.md                               # Project documentation
├── build.gradle                            # Root-level Gradle build script
├── gradle.properties                       # Gradle configuration properties
├── gradlew                                 # Gradle wrapper script for Linux/Mac
├── gradlew.bat                             # Gradle wrapper script for Windows
├── settings.gradle                         # Root Gradle settings file
│
├── app/
│   ├── src/
│   │   ├── androidTest/
│   │   │   └── java/com/satverse/suraksha/
│   │   │       └── ExampleInstrumentedTest.kt     # Android instrumented test example
│   │   │
│   │   ├── main/                                  
│   │   │   ├── java/com/satverse/suraksha/        # Main Kotlin codebase
│   │   │   │   ├── dropdown/                      # Dropdown functionality
│   │   │   │   │   ├── EditProfileActivity.kt
│   │   │   │   │   ├── HowToUseActivity.kt
│   │   │   │   │   └── PrivacyPolicyActivity.kt
│   │   │   │   │
│   │   │   │   ├── onboarding/                    # Onboarding screens
│   │   │   │   │   ├── screens/
│   │   │   │   │   │   ├── FirstScreen.kt
│   │   │   │   │   │   ├── SecondScreen.kt
│   │   │   │   │   │   └── ThirdScreen.kt
│   │   │   │   │   ├── ViewPagerAdapter.kt
│   │   │   │   │   └── ViewPagerFragment.kt
│   │   │   │   │
│   │   │   │   ├── sos/                           # SOS functionality and related services
│   │   │   │   │   ├── contacts/                  # Contact management within SOS
│   │   │   │   │   │   ├── ContactModel.kt
│   │   │   │   │   │   ├── CustomAdapter.kt
│   │   │   │   │   │   └── DbHelper.kt
│   │   │   │   │   ├── shake/                     # Shake detection services
│   │   │   │   │   │   ├── SensorService.kt
│   │   │   │   │   │   └── ShakeDetector.kt
│   │   │   │   │   └── EmergencyContactsActivity.kt
│   │   │   │   │
│   │   │   │   ├── userlogin/                     # User login and authentication
│   │   │   │   │   ├── EmailVerificationActivity.kt
│   │   │   │   │   ├── ForgotPasswordActivity.kt
│   │   │   │   │   ├── LoginActivity.kt
│   │   │   │   │   ├── ResetPasswordActivity.kt
│   │   │   │   │   ├── SignUpActivity.kt
│   │   │   │   │   ├── TermsAndConditionsActivity.kt
│   │   │   │   │   └── VerifyEmailActivity.kt
│   │   │   │   │
│   │   │   │   ├── AboutUsActivity.kt
│   │   │   │   ├── EmergencyHelplineActivity.kt
│   │   │   │   ├── LandingPageActivity.kt
│   │   │   │   ├── SplashFragment.kt
│   │   │   │   ├── SplashScreen.kt
│   │   │   │   ├── AndroidManifest.xml            # Application configuration file
│   │   │   │   └── ic_launcher-playstore.png      # App icon for Play Store
│   │   │   │  
│   │   │   └── permissions/dispatcher/            # Runtime permissions handling
│   │   │       ├── GrantableRequest.kt
│   │   │       ├── NeedsPermission.kt
│   │   │       ├── OnNeverAskAgain.kt
│   │   │       ├── OnPermissionDenied.kt
│   │   │       ├── OnShowRationale.kt
│   │   │       └── PermissionRequest.kt
│   │   │
│   │   └── test/                                   # Unit tests directory
│   │       └── java/com/satverse/suraksha/
│   │           └── ExampleUnitTest.kt             # Example unit test
│   ├── .gitignore    
│   ├── build.gradle 
│   └── proguard-rules.pro                         # ProGuard configuration for code obfuscation
│
└── gradle/                                        # Gradle wrapper files
    └── wrapper/
        ├── gradle-wrapper.jar
        └── gradle-wrapper.properties
```

## Contributing

1. Fork the repo by clicking the 'Fork' button at the top of this page.
2. Clone your fork:

    ```bash
    git clone https://github.com/your-username/Suraksha.git
    ```

3. Create a new branch:

    ```bash
    git checkout -b BRANCH_NAME
    ```

4. Commit your changes:

    ```bash
    git commit -m "Your commit message"
    ```

5. Push to your fork:

    ```bash
    git push origin BRANCH_NAME
    ```

6. Create a Pull Request on GitHub. Make sure your code follows the project coding conventions.

## License

Suraksha is licensed under the [MIT License](https://github.com/satyamsharma17/Suraksha/blob/master/LICENSE).

## Contact

For any queries or feedback, reach out to:
- [Satyam Sharma](mailto:satyamsharma1725@email.com)

Stay safe with Suraksha!

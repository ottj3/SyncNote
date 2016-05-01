# SyncNote
Final project for CSC 470-01: HCI, Spring 2016. By Connor Davis, Jan-Lucas Ott, Nate Harris, and Randell Carrido.

### Table of Contents
* [About](#about)
* [Usage](#usage)
* [Compiling](#compiling)
  * [Java](#java)
  * [Android](#android)
  * [iOS](#ios)
* [Server](#server)

## About
SyncNote is a cross-platform application meant to ease note-taking tasks for users across multiple devices. It allows the user to take notes on any compatible device, and have them sync automatically to their other devices.
It currently works on any Java platform and has Android and iOS apps partially feature-complete.

## Usage
Each platform may deviate slightly to accommodate different platforms and stages of development, but in general:
Upon opening the jar/app for the first time, you will want to log in to or register an account. You will be prompted automatically on some platforms, but the option is in the settings menu if you wish to login or logout at any time. A developer test account is available by using the username "test" and the password "12345".
Note that as of now, password are sent unencrypted and stored in plain text, so don't actually create an account with info you care about.

Notes have two parts: a title, and the note text itself. Changing a note title and then syncing will create a new note with that tile. Changing just the text of a note without changing the title will change that note. That is, notes are keyed by their titles, and each note must have a unique title.

Notes will be saved automatically, but at the moment the user must manually press the upload and download buttons to sync from and to the server. The "left" and "right" buttons will show different notes, if the user has multiple notes. The "new" button will open a new note. That note window can also be used to show an existing note via the left and right buttons.

## Compiling

### Java
The Java portion of the project can be compiled by running the gradle build script. If you do not have a local installation of gradle, a wrapper script is included. To compile the entire thing (see Android notes below), run ```./gradlew build``` (or ```./gradlew.bat``` for Windows systems). To compile without the Android portion, run ```./gradlew :xplat:build```. The output files will be in the ```xplat/build``` folder. The jar file there should be runnable out of the box.
Additionally, the project can be imported into any IDE that supports gradle by importing the build.gradle file in the root directory. This should also import and configure the subprojects for each portion.

#### Android
Compiling the Android portion of SyncNote will additionally require the Android SDK to be installed. We recommend [installing Android Studio](http://developer.android.com/sdk/installing/index.html) and importing the project as above.
If you wish to compile on a standalone installation of the SDK, this can be accomplished by creating a ```local.properties``` file in the root directory containing the line ```sdk.dir=<path-to-sdk>```, for example ```sdk.dir=C\:\\Program Files (x86)\\Android\\android-studio\\sdk```.
The apk file should be in the ```app/build``` folder, but will only run on devices (or an emulator) in developer mode, as it will not be signed.

### iOS
Import the ```SyncNote.xcodeproj``` file from the ```iOS``` file into XCode.

## Server
The server-side which handles users and stores notes is written completely in a few php scripts. If you wish to test or develop these yourself, you will need any HTTP server with PHP installed and configured, or run the php interpreter in server mode (```php -S localhost:80```, for example).
The "official" production server is currently hosted in our TCNJ webspace, and the application should connect there automatically. To connect to a test server instead, you must use the Java application and run with ```-Dsyncnote.server=<url>```.
One php script, namely ```debug.php```, is just for testing purposes and not required by SyncNote at all.
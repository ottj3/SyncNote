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
* [Development](#development)

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
The Java portion of the project can be compiled by running the gradle build script. If you do not have a local installation of gradle, a wrapper script is included. To compile the entire thing (see Android notes below), run ```./gradlew build``` (or ```./gradlew.bat``` for Windows systems). *To compile without the Android portion, run ```./gradlew :xplat:shadowJar```. The output files will be in the ```xplat/build/libs``` folder.* The jar file there should be runnable out of the box.
Additionally, the project can be imported into any IDE that supports gradle by importing the build.gradle file in the root directory. This should also import and configure the subprojects for each portion.

#### Android
Compiling the Android portion of SyncNote will additionally require the Android SDK to be installed. We recommend [installing Android Studio](http://developer.android.com/sdk/installing/index.html) and importing the project as above.
If you wish to compile on a standalone installation of the SDK, this can be accomplished by creating a ```local.properties``` file in the root directory containing the line ```sdk.dir=<path-to-sdk>```, for example ```sdk.dir=C\:\\Program Files (x86)\\Android\\android-studio\\sdk```.
The apk file should be in the ```app/build``` folder, but will only run on devices (or an emulator) in developer mode, as it will not be signed.

### iOS
Import the ```SyncNote.xcodeproj``` file from the ```iOS``` folder into XCode.

## Server
The server-side which handles users and stores notes is written completely in a few php scripts. If you wish to test or develop these yourself, you will need any HTTP server with PHP installed and configured, or run the php interpreter in server mode (```php -S localhost:80```, for example).
The "official" production server is currently hosted in our TCNJ webspace, and the application should connect there automatically. To connect to a test server instead, you must use the Java application and run with ```-Dsyncnote.server=<url>```.
One php script, namely ```debug.php```, is just for testing purposes and not required by SyncNote at all.

## Development
This section is meant to provide a brief overview of the (Java) codebase and its inner workings.

The heart of SyncNote is the ```SyncNoteCore``` class, which creates a Singleton instance when loaded on any platform.
This instance provides the platform-specific code bases access to three main components:
* Manager: The manager stores all the loaded notes in a Map, keyed by the notes' id (title). It provides methods to get, set, and remove notes individually, as well as methods to get a list of all notes and to clear the database.
* NoteParser: This is a small utility class that uses Google's Gson library to convert between the JSON format of notes stored by the server and the Manager class. It can encode the contents of the Manager to JSON, and decode JSON automatically filling the Manager.
* CoreConfig: The config holds various session variables for ease of access, such as the authentication token. It is meant to be extensible, and can be extended by each implementation of SyncNote as needed (e.g. the Android app could create a AndroidCoreConfig class) and then override the default via the SyncNoteCore#setConfig method.

Additionally, there are a few more classes of *note*:
* Note: Provides a basic data structure for notes. In the current spec, this is just a String used as the title, and a String for the text body. This class is primarily for future usability, in case formatting, metadata, revisions, etc are to be added, that can be done without breaking existing code.
* HTTPTasks: Provides static methods for interfacing with the server. While it is never called from within the core SyncNoteCode, it exists to standardize the network protocol between implementations of SyncNote.
  * Constants: This class provides the server url as a constant, and allows overriding it for [testing](#server). It may hold other constant variables in the future.
* Exceptions: The classes in the exceptions package serve to create more passable errors between the HTTPTasks and NoteParser classes and whatever implementation may be calling them. In general, InvalidRequest maps to a HTTP 400 response, Forbidden to a 403, and InvalidNotes is from a JSON exception.

#### Swing Implementation

The implementation of SyncNote designed for PC use (Windows, Mac, Linux) uses the Java Swing UI toolkit.

The main (entry) class is SyncNoteApplication. It has two main purposes: loading/saving configuration, and managing windows.

Configuration is done by creating a ```settings.json``` file and (de)serializing the CoreConfig class mentioned earlier with Gson.

To manage windows, the Application creates a monitor thread that will wait for all Note windows to be closed, at which point it will save the configuration to disk and exit.
It also can ask a user to login, or log them in automatically, using a dialog (```LoginDialog```), depending on if a settings file exists already. It then opens either a blank window, or opens the user's last open note, and starts the monitor thread and waits.

The interface is composed of ```NoteWindows```s, which provide the basic note interface and functionality. Each window is initialized with various buttons, whose functionality is, in line with Swing design principles, composed of a lengthy series of closures and anonymous classes.

Two classes were separated from these: DragPanel, which allows the user to drag the note windows around since NoteWindows are undecorated by the OS, was extracted for reusability, and ComponentResizer, which allows resizing windows by listening to mouse clicks on Frame borders,
which was kept separate as it is a class originally written by Rob Camick for his blog. See the file for details.

The heart of each NoteWindow is the ```noteIdPane``` and ```textEditorPane```, which the user actually uses to edit notes. Each NoteWindow periodically checks for changes and saves them to SyncNoteCore's Manager class for tracking (including adding and deleting notes). It also exposes these two
via the methods ```#showNote```, ```#getCurrentNoteId``` and ```#setTextBox``` to allow other windows to change the contents of a window (for example, when logging out).

Additionally, the NoteWindow has a button that can open the SettingsWindow, which provides (very basic) functionality for changing SyncNote settings.
At the moment, this is limited to
* logging out (if the user is logged in) or logging in/registering (if logged out) (using LoginDialog again)
* listing all notes the user has, even those not open in windows, and allowing the user to open notes or delete notes en masse

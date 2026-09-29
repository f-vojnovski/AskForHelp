# AskForHelp

An Android app for solving household problems with help from the people around you. Post
what is broken, attach photos of it, and collect answers from other users. The app also
lists local handymen with the distance to each one, and vendors you can open straight in
Google Maps.

Written in Java, using Firebase for authentication, data and file storage, and Algolia as
the search index. Built in 2022 as a university project for the Мобилни Апликации (Mobile
Applications) course. It is not maintained.

![AskForHelp on a device: the help topics feed on the left, a single topic with its attached image and posted solutions on the right](https://user-images.githubusercontent.com/48998036/152127481-e21061e8-6987-4ef7-85ed-ee0849076db5.png)

On the left is the topics feed, each row showing a title, its author and a like count, with
the five bottom-navigation destinations underneath. On the right is one topic opened up:
description, the images attached to it, the box for adding a solution, and the solutions
other users have already posted.

## Features

- Registration and login with email and password, with a display name attached to the account
- Adding a new post describing a problem, with a title and a description
- Attaching an array of images to a post, either taken with the camera or picked from the gallery
- Viewing existing posts and answering them with solutions
- Looking up existing posts by keyword through a search index
- Location-aware screens: handymen shown with their distance from you, vendors opened in Google Maps
- A mock notification listening system built on Firebase Cloud Messaging
- A profile screen listing everything you have posted and answered

## How it works

### Authentication

`FirebaseAuth` handles email and password sign-up and sign-in.
[RegisterFragment.java](app/src/main/java/com/example/askforhelp/RegisterFragment.java)
creates the account, then writes the chosen display name onto it with a
`UserProfileChangeRequest`, which is what lets posts and solutions be attributed by name
instead of by uid.
[MainActivity.java](app/src/main/java/com/example/askforhelp/MainActivity.java) checks
`getCurrentUser()` before it even sets its content view and forwards an already signed-in
user to `HomeActivity`, so the login screen only appears when there is no session. Logging
out from the toolbar menu signs out and clears the activity stack back to the login screen.

### Data

Storage is the Firebase Realtime Database, read through `ValueEventListener`s so every list
in the app updates live as the data changes. Four top-level nodes hold everything:

```
help-topics/{topicUid}          title, description, authorUid, authorName, status, likes,
                                dateTime, imageURLs[], solutions/{solutionUid}
users-activity-logs/{userUid}   activityType, topicUid, topicTitle, dateTime
handymen/{uid}                  name, biography, latitude, longitude
vendors/{uid}                   name, description, location
```

The classes in
[firebase/model/](app/src/main/java/com/example/askforhelp/firebase/model/) map onto those
nodes: `HelpTopic`, `HelpTopicSolution` and `UserActivityLog`. Each one marks its `uid`
field `@Exclude`, since the uid is the key the record is written under and does not need to
be duplicated inside it.

Timestamps are stored as `System.currentTimeMillis() * -1`. The Realtime Database only sorts
ascending, so negating the millisecond value makes `orderByChild("dateTime")` return the
newest topics and solutions first.

Posting a topic or a solution also appends a `UserActivityLog` under the author's uid, which
is what [ViewProfileActivity.java](app/src/main/java/com/example/askforhelp/ViewProfileActivity.java)
reads back to build the profile feed.

### Images attached to posts

Photos live in Firebase Storage, with the download URLs kept on the topic record.
[NewHelpTopicFragment.java](app/src/main/java/com/example/askforhelp/NewHelpTopicFragment.java)
holds each attached photo as a `Bitmap` in the list backing a horizontal `RecyclerView`, so
the user can see what they have attached before posting. On post, every bitmap goes through
`BitmapToUriConverter` to get a content `Uri`, `FileExtensionFinder` resolves the extension
from the content resolver's MIME type, and the file uploads to
`images/{topicUid}/{index}.{ext}`.

Each upload chains into `getDownloadUrl()`, and the topic is written to the database only once
every upload has finished and its URL is in the `imageURLs` array. If an upload fails, the
uploaded images are deleted and the topic is not posted. A post with no photos skips straight
to that write.
[ViewHelpTopicActivity.java](app/src/main/java/com/example/askforhelp/ViewHelpTopicActivity.java)
reads the array back and Picasso loads each URL into the image strip.

### Search

Search runs against an Algolia index called `help-topics`, kept in step with the database at
write time. When a topic is saved, `NewHelpTopicFragment` pushes a compact record of it
(uid, title, likes, author uid and author name) to the index using the topic's own uid as the
objectID.

[SearchHelpTopicsFragment.java](app/src/main/java/com/example/askforhelp/SearchHelpTopicsFragment.java)
calls `searchAsync` and maps the returned hits JSON back into the same `HelpTopicItem` rows
the feed uses, which means search results render through the same adapter and open the same
detail activity as any other topic. Only the fields pushed into the index are searchable;
post descriptions are not indexed.

### Location

Two screens use location, for different things.
[ServicesFragment.java](app/src/main/java/com/example/askforhelp/ServicesFragment.java) asks
for `ACCESS_FINE_LOCATION` at runtime and then subscribes to `FusedLocationProviderClient`
updates at high accuracy, on a 60 second interval with a 5 second fastest interval. The first
fix is what triggers the read of the `handymen` node, so distances are available as soon as
the rows appear; `LocationDistanceCalculator` wraps `Location.distanceTo` to turn each
handyman's stored coordinates into a distance in kilometres.

[VendorsFragment.java](app/src/main/java/com/example/askforhelp/VendorsFragment.java) works
off the location string stored on each vendor, firing an `ACTION_VIEW` intent targeted at
`com.google.android.apps.maps` so a tap hands the vendor's position to Google Maps.

### Notifications

[MyFirebaseMessagingService.java](app/src/main/java/com/example/askforhelp/services/MyFirebaseMessagingService.java)
extends `FirebaseMessagingService` and is registered in the manifest for
`com.google.firebase.MESSAGING_EVENT`. On a received message it creates the
`HEADS_UP_NOTIFICATION` channel and posts a notification carrying the payload's title and
body.

The listening half is what is built here. No backend sends a push when someone answers your
topic, so the service is exercised by sending test messages from the Firebase console, and a
sender is the piece that would make it live.

## Project structure

Each screen is a fragment or an activity in the top-level package, with lists, entities and
helpers separated into their own packages.

```
app/src/main/java/com/example/askforhelp/
├── MainActivity.java              launcher; login, or straight to Home if a session exists
├── HomeActivity.java              toolbar and bottom navigation, swaps the five fragments
├── ViewHelpTopicActivity.java     one topic: description, images, solutions
├── ViewProfileActivity.java       display name and the user's activity log
├── LoginFragment.java             email and password sign-in
├── RegisterFragment.java          sign-up, then sets the display name
├── HelpTopicsFragment.java        the feed of existing topics
├── SearchHelpTopicsFragment.java  keyword search through Algolia
├── NewHelpTopicFragment.java      compose a topic and upload its images
├── ServicesFragment.java          handymen, with distance from the current location
├── VendorsFragment.java           vendors, opened in Google Maps
├── adapters/                      an Item class per row type, paired with its Adapter
├── firebase/model/                HelpTopic, HelpTopicSolution, UserActivityLog
├── datainterface/                 the UserActivityType enum
├── services/                      MyFirebaseMessagingService
└── util/                          bitmap to Uri, file extension, distance helpers
```

`HomeActivity` owns the five bottom-navigation destinations (home, search posts, new post,
find services, vendors) and replaces the fragment in its container on each selection.
Opening a topic or a profile leaves that container and starts a separate activity.

Every list follows the same pairing in
[adapters/](app/src/main/java/com/example/askforhelp/adapters/). The `Item` class is a plain
holder for the fields one row shows, and the matching `Adapter` inflates the row layout and
binds those fields, exposing a click listener interface where the screen needs to react to a
tap:

| Row data | Adapter | Used by |
| --- | --- | --- |
| `HelpTopicItem` | `HelpTopicsAdapter` | the feed and search results |
| `TopicSolutionItem` | `HelpTopicSolutionsAdapter` | solutions on a topic |
| `AddNewTopicImageItem` | `AddNewTopicImagesAdapter` | local bitmaps queued for upload |
| `ViewHelpTopicImageItem` | `ViewHelpTopicImagesAdapter` | remote image URLs, loaded by Picasso |
| `HandymanItem` | `HandymenAdapter` | the handymen list |
| `VendorItem` | `VendorsAdapter` | the vendors list |
| `UserActivityLogItem` | `UserActivityAdapter` | the profile activity feed |

Sharing `HelpTopicsAdapter` between the feed and the search screen is the reason a search hit
has to be mapped into a `HelpTopicItem` rather than used as raw JSON.

## Built with

- Java 8, AndroidX and Material Components
- Firebase Authentication, Realtime Database, Storage and Cloud Messaging (BoM 28.3.1)
- Algolia `algoliasearch-android` 3.27.0
- Google Play services location (`FusedLocationProviderClient`)
- Picasso 2.71828 for loading remote images
- RecyclerView for every list in the app

## Building and running

Open the project in Android Studio and let it sync, or build from the command line with the
included Gradle wrapper:

```bash
git clone https://github.com/f-vojnovski/AskForHelp.git
cd AskForHelp
./gradlew assembleDebug     # or ./gradlew installDebug with a device attached
```

The build targets `compileSdkVersion 30`, `minSdkVersion 21` and `targetSdkVersion 30`, with
source and target compatibility set to Java 8. It is pinned to the toolchain it was written
against: Android Gradle Plugin 4.1.3 on Gradle 6.5, resolving dependencies from `google()`
and `jcenter()`. Building it today means using a JDK that AGP 4.1.3 accepts (8 or 11), and
`jcenter()` has been read-only since it was sunset, so a newer AGP and `mavenCentral()` are
the first things to change if you plan to work on it.

### Supply your own service configuration

The app talks to two external services, and a fresh clone will not reach either one without
your own credentials.

For Firebase, create a project and add an Android app to it using the applicationId
`com.example.askforhelp` (or change `applicationId` in [app/build.gradle](app/build.gradle)
to match your own). Enable the Email/Password sign-in provider, the Realtime Database,
Storage and Cloud Messaging, then download the generated `google-services.json` and put it in
[app/](app/).

For Algolia, create an application and an index named `help-topics`, then copy
[local.properties.example](local.properties.example) to `local.properties` and fill in the
app ID, a key permitted to add objects, and a search-only key.

Nothing in the app writes to the `handymen` or `vendors` nodes, so seed those by hand in the
Firebase console if you want the Find services and Vendors screens to show anything. A
vendor's `location` is the URI handed to Google Maps, and a handyman needs `latitude` and
`longitude` for the distance calculation.

## Testing

The app has been tested on multiple devices and is working as expected. That testing was
done on real hardware, several physical phones including friends' devices, rather than on an
emulator alone.

Demonstration
=============

Pre-requisites
--------------
- Visit the page http://developers.bistri.com/webrtc-sdk/#iOS-sdk and get the latest iOS sdk or download it directly [here](http://bit.ly/bistri_ios_1_0)

- Into *bistri-ios-VERSION.tar.gz*, you have 3 folders: **doc**, **demo** and **sdk** wich contains all necessary files.

Xcode Part
----------
For demo, simply:
- Open BistriAPIDemo.xcodeproj from your archive with xcode

- Run it on a real device !

Or, if you want to do your own application:
- Open or create a new project with xcode

- In project navigator, select your target
In **linked Frameworks and libraries**, click on **"+"** and add *libbistriapi.a* from the **sdk/bistri** folder of the archive by selected **Add Other**

- In the same part, use the search field to add *libstdc++.dylib* and *libsqlite3.dylib*

- Now go on **build settings** and in **Search Paths** categories, add the project full path *$PATH/bistri-ios-sdk-VERSION*

- Run it on a real device !

Browser Part
------------
- Open your browser on your desktop to this URL: https://api.bistri.com/demo/#iosroom.
   You can replace **iosroom** by an other room name if you don't want to see an other developer.

- Open the demo app on your device and set the same conference room name
   You should see the iOS video in your browser and reciprocally.

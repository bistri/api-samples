Demonstration
=============

Pre-requisites
--------------
- Visit the page http://developers.bistri.com/webrtc-sdk/#iOS-sdk and get the latest iOS sdk or download it directly [here](http://bit.ly/bistri_ios_1_0-beta-1)

- Into *bistri-ios-VERSION.tar.gz*, you have 3 folders: **doc**, **demo** and **sdk** wich contains all necessary files.

Xcode Part
----------

- Open BistriAPIDemo.xcodeproj from your archive with xcode

- In project navigator, select targets **BistriAPIDemo**
In **linked Frameworks and libraries**, click on **"+"** and add *libbistriapi.a* from the **sdk/bistri** folder of the archive by selected **Add Other**

- In the same part, use the search field to add *libstdc++.dylib* and *libsqlite3.dylib*

- Now go on **build settings** and in **Search Paths** categories, add the project full path *$PATH/bistri-ios-sdk-VERSION*

- In project navigator, add a new group and modify the path of this folder to match the **sdk/bistri** folder by clicking on the folder icon into **Utilities** panel.

- Go back into **BistriAPIDemo** target, General section, and add all the .h files from **sdk/bistri** folder into **Embedded Binaries**
Uncheck **Copy items if needed** and press **Finish**

- In project navigator, select the 5 .h files imported in point 6 and move them into the folder created in point 5

- Run it on a real device !

Browser Part
------------
- Open your browser on your desktop to this URL: https://api.bistri.com/demo/#iosroom.
   You can replace **iosroom** by an other room name if you don't want to see an other developer.

- Open the demo app on your device and set the same conference room name
   You should see the iOS video in your browser and reciprocally.

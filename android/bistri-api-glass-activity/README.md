Pre-requisites
--------------

- Install [Apache Maven](http://maven.apache.org)
- Install and run the [Maven Android SDK Deployer](https://github.com/mosabua/maven-android-sdk-deployer)

Build
-----

To build this demo, you should run the following command:

    mvn clean install

The apk file is generated under the target directory. You can install it on your device with 'adb' or with the following command:

    mvn android:deploy

Run
---

- Open your browser on your desktop to this [URL](http://jsfiddle.net/bistri/GJ643/) and set the conference room name into the field
- Open the demo app on your Glass and set the same conference room name

You should see the video sent from the Glass in your browser.


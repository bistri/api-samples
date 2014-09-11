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

- Open your browser on your desktop to this URL: https://api.bistri.com/demo/#androidroom. You can replace 'androidroom' by an other room name if you don't want to see an other developer.
- Open the demo app on your device and set the same conference room name

You should see the Android video in your browser and reciprocally.

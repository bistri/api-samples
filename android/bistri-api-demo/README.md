Pre-requisites
--------------

- Install a JDK and set your JAVA_HOME environement var
- Install [Apache Maven](http://maven.apache.org) to build the project
- Install the [Android SDK](https://developer.android.com/sdk) and define ANDROID_HOME and ANDROID_SDK_ROOT environment var to your Android home directory
- Install the [Maven Android SDK Deployer](https://github.com/mosabua/maven-android-sdk-deployer) and run the following command to install Android libraries to your Maven repository:

    mvn install -P 4.4

Currently, we defined the version of Android libraries to 4.4.2_r4 in pom.xml but the latest release of the 4.4.2 can be greater. In this case, you'll need to update the version in your pom.xml

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

NEXT STEPS
----------

Before to start to develop your own application, you need to set up your application into your developer's Dashboard:

1. Go to the developers website ( http://developers.bistri.com/webrtc-sdk/#manage-sdk )
2. Choose "Manage your applications" menu
3. Click on the "View" button of your application and modify the referrer filters

For android and glass applications, the referrer is the package name of your application (defined in your AndroidManifest.xml)
For javascript application, it is a pattern of your URLs, for example : *yourdomain.com


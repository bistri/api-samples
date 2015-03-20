
ECLIPSE ADT HOWTO
-----------------

1 - Generate an empty eclipse project called BistriDemo in ~/developpement/workspace_adt

2 - Download bistri-api tar.gz from bistri developers website : https://developers.bistri.com/webrtc-sdk/

3 - Uncompress bistri-api tar.gz and go into extracted folder like this

mkdir bistri-api
tar -zxf bistri-api-archive-1.2.tar.gz -C bistri-api
cd bistri-api

4 - Eclipse needs the content of the aar file, so uncompress it.

unzip sdk/bistri-api-1.2.aar -d aar

5 - Copy bistri libraries (java and native)

cp aar/classes.jar ~/developpement/workspace_adt/BistriDemo/libs/bistri_sdk.jar
cp -R aar/libs/* ~/developpement/workspace_adt/BistriDemo/libs/

6 - At this step, bistri API is integrated to your project
    To build demo application, copy resources

cp -R bistri-api-demo/res/* ~/developpement/workspace_adt/BistriDemo/res

7 - Copy demo sources

cp -R bistri-api-demo/src/main/java/com ~/developpement/workspace_adt/BistriDemo/src/

8 - Copy demo android manifest

cp bistri-api-demo/AndroidManifest.xml ~/developpement/workspace_adt/BistriDemo/

9 - Finally don't forget to refresh your project.

    That's it !

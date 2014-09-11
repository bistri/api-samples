#!/bin/sh
cd ..
HOME=$(pwd) 
cd -

FOLDERS=( "jni" "obj" "libs" "src/main/java/com/bistri/api" "src/main/java/org" )

function link {
	echo "Link bistri-api folders"
	for folder in ${FOLDERS[@]}; do
		if [ ! -e ./$folder ]; then
			echo "link $HOME/bistri-api/$folder $folder"
			ln -s $HOME/bistri-api/$folder $folder
		fi
	done	
}

function unlink {
	echo "Unlink"
	for folder in ${FOLDERS[@]}; do
		rm $folder 2> /dev/null
	done
}

if [ "$1" = "unlink" ]
then
   unlink
else
   link
fi

#!/bin/bash

apk_name=$1
url="$2"
apk_package_name=$3
icon_path="$4"
target=$5
apk_name_chs=$6
apk_path="$7"
sh_path="/var/apk/"

/usr/java/Mobile_Android/android-sdk-linux/tools/android create project -n $apk_name -t  $target -k com.nmi.$apk_package_name -a "$apk_name"Activity -p "$sh_path"tmp/$apk_name

sed "s/__BContapk__/$apk_package_name/;s#__BContURL__#$url#;s/__BContActivity__/$apk_name/" "$sh_path"sh/Activity.java > "$sh_path"tmp/$apk_name/src/com/nmi/$apk_package_name/"$apk_name"Activity.java

sed "s/__BContapk__/$apk_package_name/;s/__BContActivity__/$apk_name/" "$sh_path"sh/PopupMenu.java > "$sh_path"tmp/$apk_name/src/com/nmi/$apk_package_name/PopupMenu.java

sed "s/__BContapk__/$apk_package_name/;s/__BContActivity__/$apk_name/" "$sh_path"sh/LocalFileContentProvider.java > "$sh_path"tmp/$apk_name/src/com/nmi/$apk_package_name/LocalFileContentProvider.java

sed "s/__BContapk__/$apk_package_name/;s/__BContActivity__/$apk_name/" "$sh_path"sh/AndroidManifest.xml > "$sh_path"tmp/$apk_name/AndroidManifest.xml

mkdir "$sh_path"tmp/$apk_name/assets/
cp -rf "$sh_path"sh/assets "$sh_path"tmp/$apk_name/

sed -i "s/"$apk_name"Activity/$apk_name_chs/" "$sh_path"tmp/$apk_name/res/values/strings.xml
cp -rf "$sh_path"sh/res "$sh_path"tmp/$apk_name/

mkdir "$sh_path"tmp/$apk_name/res/drawable-hdpi/
mkdir "$sh_path"tmp/$apk_name/res/drawable-mdpi/
mkdir "$sh_path"tmp/$apk_name/res/drawable-ldpi/

cp $icon_path/256 "$sh_path"tmp/$apk_name/assets/logo.png
cp $icon_path/72 "$sh_path"tmp/$apk_name/res/drawable-hdpi/ic_launcher.png
cp $icon_path/48 "$sh_path"tmp/$apk_name/res/drawable-mdpi/ic_launcher.png
cp $icon_path/36 "$sh_path"tmp/$apk_name/res/drawable-ldpi/ic_launcher.png

cd "$sh_path"tmp/$apk_name/ && ant release

cd "$sh_path"tmp/$apk_name/Apk/ && jarsigner -verbose -storepass 123456 -keystore "$sh_path"keystore/bcont.keystore -signedjar "$apk_name"-signed.apk "$apk_name"-release-unsigned.apk bcont.keystore

cp "$sh_path"tmp/$apk_name/Apk/"$apk_name"-signed.apk $apk_path

rm -rf "$sh_path"tmp/$apk_name/

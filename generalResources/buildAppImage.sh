#!/bin/bash

cd app/build/tmp || return
mkdir -p SplashComicReader.AppDir/usr/bin
cp -a SplashComicReader/. SplashComicReader.AppDir/usr/bin/
cp ../../../generalResources/AppRun SplashComicReader.AppDir/
cp ../../../generalResources/SplashComicReader.desktop SplashComicReader.AppDir/
cp ../../../generalResources/icon.png SplashComicReader.AppDir/
cp ../../../generalResources/SplashComicReader SplashComicReader.AppDir/usr/bin/
chmod 755 SplashComicReader.AppDir/AppRun
chmod 755 SplashComicReader.AppDir/SplashComicReader.desktop
chmod 755 SplashComicReader.AppDir/usr/bin/SplashComicReader
wget -O appimagetool.AppImage https://github.com/AppImage/AppImageKit/releases/download/13/appimagetool-x86_64.AppImage
chmod 755 appimagetool.AppImage
./appimagetool.AppImage SplashComicReader.AppDir
rm -R SplashComicReader
rm -R SplashComicReader.AppDir
rm appimagetool.AppImage


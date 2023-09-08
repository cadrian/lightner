#/usr/bin/env bash

function prepareModule() {
    local module=$1
    sudo dpkg -P lib$1-java

    (
        echo
        echo Preparing $module
        cp -al ../$module build/$module
    )
}

function buildModule() {
    local module=$1

    (
        echo
        echo Building $module

        cd build/$module

        mvn install > mvn-install.log
        case $module in
            lightner-main)
                # Special treatment for main: need to fill the right classpath in the main script
                (
                    mvn dependency:build-classpath -Dmdep.outputFile=src/scripts/cp.txt > getdep.log
                    cd src/scripts
                    sed "s!%CLASSPATH%!$(sed "s!$HOME/.m2/repository!/usr/share/maven-repo!g;s!/[^/]*/[^/]*jar!/debian/*!g" cp.txt)!" lightner.template > lightner
                )
                ;;
            *)
                :
                ;;
        esac
        debuild -b -us -uc > build.log || {
            echo "less -R pkg/$module/build.log"
            exit 1
        }
        cd ..
        sudo dpkg -i *.deb || exit 1
    ) || exit 1

    mv build/*.deb debs/
}

buildmsi() {
    # https://wiki.gnome.org/msitools/HowTo/CreateMSI

    echo
    echo Build Windows MSI

    mkdir -p win/src/lib win/src/jdk win/out

    if [ -r $HOME/.cache/openjdk.zip ]; then
        cp $HOME/.cache/openjdk.zip win/
    else
        curl https://download.java.net/java/GA/jdk17.0.1/2a2082e5a09d4267845be086888add4f/12/GPL/openjdk-17.0.1_windows-x64_bin.zip --output win/openjdk.zip
        if [ -d $HOME/.cache ]; then
            cp win/openjdk.zip $HOME/.cache
        fi
    fi
    unzip -b -d win/src/jdk -o -q win/openjdk.zip

    sed 's/$/\r/g' ../lightner-main/src/scripts/lightner.ps1.template > win/src/lightner.ps1
    cp build/lightner-main/target/main-0.0.1-SNAPSHOT.jar win/src/lib/main.jar
    cp build/lightner-model/target/model-0.0.1-SNAPSHOT.jar win/src/lib/model.jar
    cp build/lightner-swing/target/swing-0.0.1-SNAPSHOT.jar win/src/lib/swing.jar

    find win/src -name jdk -prune | wixl-heat -p win/src/ --component-group lightner_main --var var.DESTDIR --directory-ref=INSTALLDIR > win/lightner_main.wxs
    jdk=$(ls -d win/src/jdk/*)
    echo jdk=$jdk
    mv $jdk/* win/src/jdk/
    rmdir $jdk
    find win/src/jdk | wixl-heat -p win/src/ --component-group lightner_jdk --var var.DESTDIR --directory-ref=INSTALLDIR > win/lightner_jdk.wxs

    MANUFACTURER='Cyril Adrian' wixl -D SourceDir=win/src -D DESTDIR=win/src -v \
                -o win/out/lightner.msi \
                ../lightner-main/src/scripts/lightner.wxs \
                win/lightner_main.wxs \
                win/lightner_jdk.wxs
}

if [ "$1" == msi ]; then
    cd pkg
    rm -rf win
    buildmsi
else
    rm -rf pkg $HOME/.m2/repository/net/cadrian/lightner
    mkdir -p pkg/build pkg/debs
    cd pkg

    for module in main swing model root; do
        prepareModule lightner-$module
    done

    for module in root model swing main; do
        buildModule lightner-$module
    done

    buildmsi
fi

echo
echo Done.

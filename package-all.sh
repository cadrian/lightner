#/usr/bin/env bash

function prepareModule() {
    local module=$1

    echo
    echo Preparing $module

    case $module in
        lightner-main)
            sudo dpkg -P $1-java
            ;;
        *)
            sudo dpkg -P lib$1-java
            ;;
    esac

    (
        cp -al ../$module build/$module
    )
}

function buildModule() {
    local module=$1

    echo
    echo Building $module

    (
        cd build/$module
        mvn install -Dmaven.test.skip=true > mvn-install.log || exit 1

        if [ -d debian ]; then
            case $module in
                lightner-main)
                    # Special treatment for main: need to fill the right classpath in the main script
                    (
                        mvn dependency:build-classpath -DincludeScope=runtime -Dmdep.outputFile=src/scripts/cp.txt > getdep.log
                        cd src/scripts
                        sed "s!%CLASSPATH%!$(sed "s!$HOME/.m2/repository!/usr/share/maven-repo!g;s!/[^/]*/[^/]*jar!/debian/*!g" cp.txt)!" lightner.template > lightner
                    )
                    ;;
                *)
                    :
                    ;;
            esac
            debuild -b -us -uc > build.log || {
                echo "less -R pkg/build/$module/build.log"
                exit 1
            }
            cd ..
            sudo dpkg -i *.deb || exit 1
            mv *.deb ../debs/
        fi
    ) || exit 1
}

buildmsi() {
    # https://wiki.gnome.org/msitools/HowTo/CreateMSI

    echo
    echo Building Windows MSI

    mkdir -p win/src/lib win/src/jdk win/out
    out=$(pwd)/win/out

    (
        cd ../lightner-main
        mvn dependency:build-classpath -DincludeScope=runtime -Dmdep.outputFile=$out/cp.txt
        # also adding main
        {
            echo -n ":"
            ls $(pwd)/target/*.jar
        } >> $out/cp.txt
    )

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
    cat $out/cp.txt | tr ':' '\n' | while read f; do
        echo ">>> $f"
        t=win/src/lib/$(echo "${f##*/}" | sed 's/-[0-9].*\.jar$/.jar/')
        echo "  > $t"
        cp "$f" "$t"
    done

    find win/src -name jdk -prune -o -print | sort | wixl-heat -p win/src/ --component-group lightner_main --var var.DESTDIR --directory-ref=INSTALLDIR > win/lightner_main.wxi
    jdk=$(ls -d win/src/jdk/*)
    echo jdk=$jdk
    mv $jdk/* win/src/jdk/
    rmdir $jdk
    find win/src/jdk -print | sort | wixl-heat -p win/src/ --component-group lightner_jdk --var var.DESTDIR --directory-ref=INSTALLDIR > win/lightner_jdk.wxi

    cp ../lightner-main/src/scripts/lightner.wxs win/
    MANUFACTURER='Cyril Adrian' wixl -D SourceDir=win/src -D DESTDIR=win/src -v \
                -o win/out/lightner.msi \
                win/lightner.wxs \
                win/lightner_main.wxi \
                win/lightner_jdk.wxi
}

echo
echo "Testing & Packaging"

mvn package || exit 1

if [ "$1" == msi ]; then
    cd pkg
    rm -rf win
    buildmsi
else
    echo
    echo Cleaning

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

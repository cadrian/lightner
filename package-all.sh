#/usr/bin/env bash

rm -rf pkg $HOME/.m2/repository/net/cadrian/lightner
mkdir -p pkg/build pkg/debs
cd pkg

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

if [ $# -gt 0 ]; then
    for module in "$@"; do
        prepareModule lightner-${module#lightner-}
    done
    for module in "$@"; do
        buildModule lightner-${module#lightner-}
    done
else
    for module in main swing model root; do
        prepareModule lightner-$module
    done
    for module in root model swing main; do
        buildModule lightner-$module
    done
fi

echo
echo Done.

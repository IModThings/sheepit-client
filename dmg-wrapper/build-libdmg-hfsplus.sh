#!/bin/bash

sudo apt-get install git cmake build-essential zlib1g-dev

git clone https://github.com/fanquake/libdmg-hfsplus.git
cd libdmg-hfsplus
cmake . -B build
make -C build/dmg -j8
mv build/dmg/dmg ..
cd ..
rm -rf libdmg-hfsplus/

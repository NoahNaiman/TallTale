#!/bin/bash

git submodule init; git submodule update
cd tacotron2
git submodule init; git submodule update
wget https://data.keithito.com/data/speech/LJSpeech-1.1.tar.bz2

tar -xjf  LJSpeech-1.1.tar.bz2

mv LJSpeech-1.1 ljs_dataset_folder

sed -i -- 's,DUMMY,ljs_dataset_folder/wavs,g' filelists/*.txt
pip install -r requirements.txt

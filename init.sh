#!/bin/bash

git submodule init; git submodule update
cd tacotron2
git submodule init; git submodule update
wget https://data.keithito.com/data/speech/LJSpeech-1.1.tar.bz2

tar -xjf  LJSpeech-1.1.tar.bz2

mv LJSpeech-1.1 ljs_dataset_folder
cp -r ljs_dataset_folder ../../


sed -i -- 's,DUMMY,ljs_dataset_folder/wavs,g' filelists/*.txt
pip install -r requirements.txt


cd ../../


git clone https://github.com/NVIDIA/apex.git
cd apex
pip install -v --no-cache-dir --global-option="--cpp_ext" --global-option="--cuda_ext" .

cd ../ramble/tacotron2

pip install --upgrade numpy
#python train.py --output_directory=outdir --log_directory=logdir -c tacotron2_statedict.pt --warm_start

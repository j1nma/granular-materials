import os
from oct2py import octave
octave.addpath('./scripts/')

d = [ 0.15, 0.19, 0.23, 0.27 ];

dirName = './output';
flowsDirName = dirName + '/4Flows';

if not os.path.exists(dirName):
        os.mkdir(dirName)
        print("Directory ", dirName, " created.")

if not os.path.exists(flowsDirName):
        os.mkdir(flowsDirName)
        print("Directory ", flowsDirName, " created.")

open('./scripts/means.txt', 'w').close()
open('./scripts/stds.txt', 'w').close()

for diameter in d:
	func = 'flowWithD(' + str(diameter) + ', \"' + str(flowsDirName) + '\")';
	octave.eval(func);
import os
from oct2py import octave
octave.addpath('./scripts/')

d = [ 0.15, 0.19, 0.23, 0.27 ];

dirName = './output';
kineticEnergiesDirName = dirName + '/4KineticEnergies';

if not os.path.exists(dirName):
        os.mkdir(dirName)
        print("Directory ", dirName, " created.")

if not os.path.exists(kineticEnergiesDirName):
        os.mkdir(kineticEnergiesDirName)
        print("Directory ", kineticEnergiesDirName, " created.")

for diameter in d:
	func = 'kineticEnergyWithD(' + str(diameter) + ', \"' + str(kineticEnergiesDirName) + '\")';
	octave.eval(func);
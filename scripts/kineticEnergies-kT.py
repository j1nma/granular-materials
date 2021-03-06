import os
from oct2py import octave
octave.addpath('./scripts/')

kT = [10000, 50000, 100000, 200000];

dirName = './output';
kineticEnergieskTDirName = dirName + '/kineticEnergies-kT';

if not os.path.exists(dirName):
        os.mkdir(dirName)
        print("Directory ", dirName, " created.")

if not os.path.exists(kineticEnergieskTDirName):
        os.mkdir(kineticEnergieskTDirName)
        print("Directory ", kineticEnergieskTDirName, " created.")

for k in kT:
	func = 'kineticEnergyWithkT(' + str(k) + ', \"' + str(kineticEnergieskTDirName) + '\")';
	octave.eval(func);
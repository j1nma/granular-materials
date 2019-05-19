import os
import subprocess
import numpy
import matplotlib.pyplot as plt
from oct2py import octave
octave.addpath('./scripts/')

W = 0.3;
L = 1.0;
diameter = 0.0;

limitTime = 15.0
printDeltaT = 0.01

subprocess.call(['java', '-jar', './target/granular-materials-1.0-SNAPSHOT.jar',
		'--limitTime={lT}'.format(lT = limitTime),
		'--printDeltaT={pT}'.format(pT = printDeltaT),
		'--length={l}'.format(l = L),
		'--diameter={d}'.format(d = diameter)])

dirName = './output';
kineticEnergyDZeroDirName = dirName + '/KineticEnergyD=0';

if not os.path.exists(dirName):
        os.mkdir(dirName)
        print("Directory ", dirName, " created.")

if not os.path.exists(kineticEnergyDZeroDirName):
        os.mkdir(kineticEnergyDZeroDirName)
        print("Directory ", kineticEnergyDZeroDirName, " created.")

func = 'kineticEnergyWithD(' + str(diameter) + ', \"' + str(kineticEnergyDZeroDirName) + '\")';
octave.eval(func);
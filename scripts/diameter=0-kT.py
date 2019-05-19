import os
import subprocess
import numpy
import matplotlib.pyplot as plt
from oct2py import octave
octave.addpath('./scripts/')

W = 0.3;
L = 1.0;
diameter = 0.0;
kT = [300000, 400000];

limitTime = 5.0
printDeltaT = 0.01

for k in kT:
	subprocess.call(['java', '-jar', './target/granular-materials-1.0-SNAPSHOT.jar', 
		'--limitTime={lT}'.format(lT = limitTime),
		'--printDeltaT={pT}'.format(pT = printDeltaT),
		'--length={l}'.format(l = L),
		'--diameter={d}'.format(d = diameter),
		'--tangentK={k}'.format(k = k)])

os.system("python3 ./scripts/kineticEnergies-Kt.py")
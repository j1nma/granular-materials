import os
import subprocess
import numpy
import matplotlib.pyplot as plt
from oct2py import octave
octave.addpath('./scripts/')

W = 0.3;
L = 1.0;
d = [ 0.15, 0.19, 0.23, 0.27 ];

limitTime = 5.0
printDeltaT = 0.01

for diameter in d:
	subprocess.call(['java', '-jar', './target/granular-materials-1.0-SNAPSHOT.jar', 
		'--limitTime={lT}'.format(lT = limitTime),
		'--printDeltaT={pT}'.format(pT = printDeltaT),
		'--length={l}'.format(l = L),
		'--diameter={d}'.format(d = diameter)])

os.system("python3 ./scripts/4KineticEnergies.py")
os.system("python3 ./scripts/4Flows.py")
import os
import subprocess
import numpy
import matplotlib.pyplot as plt
from oct2py import octave
octave.addpath('./scripts/')

g = 10;
R_min = 0.01; 
R_max = 0.015;
m = 0.01;
N = 300;
W = 0.3;
L = 1.0;
d = [ 0.15, 0.19, 0.23, 0.27 ];

limitTime = 3.5
printDeltaT = 0.001
length = 1.0

for diameter in d:
	command = 'java -jar ./target/granular-materials-1.0-SNAPSHOT.jar --printDeltaT=0.001 --length=1.5 --diameter={diameter}'.format(
						diameter = diameter,
						limitTime = limitTime,
						)
	print(command)
	# subprocess.call(['java', '-jar', './target/granular-materials-1.0-SNAPSHOT.jar', 
	# 	'--limitTime={lT}'.format(lT = limitTime),
	# 	'--printDeltaT={pT}'.format(pT = printDeltaT),
	# 	'--length={l}'.format(l = length),
	# 	'--diameter={d}'.format(d = diameter)])

#os.system("python3 ./scripts/4KineticEnergies.py")
os.system("python3 ./scripts/4Flows.py")
import os
import subprocess
import numpy
from numpy import zeros, log
import matplotlib.pyplot as plt

dirName = './output';

if not os.path.exists(dirName):
        os.mkdir(dirName)
        print("Directory ", dirName, " created.")

printDeltaT = 0.001

maxD = 0.25
minD = 0.15
step = numpy.round((maxD - minD) / 4, 2)


dRange = numpy.arange(minD, maxD, step)

energy_size = numpy.size(dRange)

# Generate a file with set of parameters
for d in dRange:
	command = 'java -jar ./target/granular-materials-1.0-SNAPSHOT.jar --diameter={diameter} --printDeltaT={printDeltaT}'.format(
						diameter = d,
						printDeltaT = printDeltaT,
						)
	print(command)
	p = subprocess.Popen(command, shell=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT, bufsize=0)

# Prepare MSE plot
f, ax = plt.subplots(1)

# Plot data
#plt.plot(xRange, numpy.log10(beeman_mse), linestyle='None', marker='o')
#plt.plot(xRange, numpy.log10(verlet_mse), linestyle='None', marker='o')
#plt.plot(xRange, numpy.log10(gear_mse), linestyle='None', marker='o')
#plt.legend(['Beeman', 'Velocity Verlet', 'Order 5 Gear PC'], loc=2)
#ax.grid()
#plt.xlabel("log$_{10}$(Δt) [s]")
#plt.ylabel("log$_{10}$(MSE) [$m^2$]")
#plt.xticks(xRange)
#plt.ylim([-30, 30])
#plt.yticks(numpy.arange(-30, 30, 5))

# Save plot
#plt.savefig('./output/ex1/logarithmicMSEs.svg')
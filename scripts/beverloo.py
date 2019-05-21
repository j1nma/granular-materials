import os
import subprocess
import csv
import numpy
import matplotlib.pyplot as plt
import math
from numpy import vstack, zeros, array, ones, linalg, transpose, delete, mean, power
from pylab import plot, show
from oct2py import octave
from oct2py.io import read_file
octave.addpath('./scripts/')

def squared_error(y, modelY):
    return sum((modelY - y) * (modelY - y))

dirName = './output';
beverlooDirName = dirName + '/beverloo';

if not os.path.exists(dirName):
        os.mkdir(dirName)
        print("Directory ", dirName, " created.")

if not os.path.exists(beverlooDirName):
        os.mkdir(beverlooDirName)
        print("Directory ", beverlooDirName, " created.")

g = 10;
R_min = 0.01; 
R_max = 0.015;
m = 0.01;
N = 300;
W = 0.3;
L = 1.0;
d = [ 0.15, 0.16, 0.18, 0.19 ];

means_file = open("./scripts/means.txt", "r")
mean_Qs = means_file.read().split();
mean_Qs = [float(i) for i in mean_Qs]
means_file.close()

stds_file = open("./scripts/stds.txt", "r")
std_Qs = stds_file.read().split();
std_Qs = [float(i) for i in std_Qs]
stds_file.close()

cLimit = 10
c = numpy.arange(start=0, stop=cLimit, step=0.1);

# Beverloo flow
Qb = [[0 for col in range(len(d))] for row in range(len(c))];
for i in range(0, len(c)):
	for j in range(0, len(d)):
		Qb[i][j] = 1773.33 * math.sqrt(g) * power(d[j]-c[i]*((R_min + R_max)/2), 3/2);

# MSE
mse = [[0 for col in range(1)] for row in range(len(c))];
for i in range(0, len(c)):
	mse[i] = ((array(mean_Qs) - array(Qb[i]))**2).mean(axis=0);

# Prepare MSD plot
f, ax = plt.subplots(1)

# Get best fit slope
bestKIndex = mse.index(min(mse))
bestK = round(c[bestKIndex], 2)

# Calculate best fit curve for values of x
bestY = Qb[bestKIndex]

# Plot data and best fit curve
ax.errorbar(d, mean_Qs, std_Qs, linestyle='None', marker='^')
ax.grid()
ax.set_ylim(bottom=0)
plt.xlabel("Diámetero [m]")
plt.ylabel("Caudal [part./s]")
plt.xticks(d)
plt.plot(d, bestY)
plt.legend(['Ajuste Ley de Beverloo c = {c}'.format(c = bestK), 'Datos (promedios)'], loc=2)

# Save plot
plt.savefig('./output/beverloo/beverloo.png')

# Plot sum of squared fit errors
g, ay = plt.subplots(1)
ay.grid()
ay.set_ylim(bottom=0, top=max(mse))
ay.set_xlim(0, cLimit)
plt.xlabel("Parámetro libre de Beverloo (c)")
plt.ylabel("Error del ajuste")
ay.set_xticks([bestK])
plt.plot(c, mse, linestyle='None', marker='*')
g.savefig('./output/beverloo/squaredError')

# Calculate and print diffusion coefficient and R squared
print("Parámetro libre de Beverloo = %f" % (bestK))
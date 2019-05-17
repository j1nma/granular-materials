import os
import subprocess
import csv
import numpy
import matplotlib.pyplot as plt
import math
from numpy import vstack, zeros, array, ones, linalg, transpose, delete, mean, power
from pylab import plot, show
from oct2py import octave
octave.addpath('./scripts/')

def squared_error(y, modelY):
    return sum((modelY - y) * (modelY - y))

def coefficient_of_determination(y, modelY):
    yMeans = [mean(y) for x in y]
    squaredErrorRegression = squared_error(y, modelY)
    squaredErrorYMean = squared_error(y, yMeans)
    return 1 - (squaredErrorRegression/squaredErrorYMean)

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
d = [ 0.15, 0.19, 0.23, 0.27 ];

# Results TODO: change to real values
mean_Qs = [121.0294118, 166.0784, 196.2745098, 304.9673203];
std_Qs = [10.0, 10.0, 10.0, 10.0];

c = numpy.arange(start=0, stop=10, step=0.1);

# Beverloo flow
Qb = [[0 for col in range(4)] for row in range(len(c))];
for i in range(0, len(c)):
	for j in range(0, 4):
		Qb[i][j] = (N/(W*L)) * math.sqrt(g) * power(d[j]-c[i]*((R_min+R_max)/2), 3/2);

# MSE
mse = [[0 for col in range(1)] for row in range(len(c))];
for i in range(0, len(c)):
	mse[i] = ((array(mean_Qs) - array(Qb[i]))**2).mean(axis=0);

# Plot
# plot(d, mean_Qs, linestyle='None', marker='o')
# plot(d, Qb[mse.index(min(mse))], linestyle='None', marker='*')
# plt.savefig('./output/beverloo/beverloo.png')

# Prepare MSD plot
f, ax = plt.subplots(1)

# Calculate diameter range and step
step = 0.04
limitD = 0.27
xRange = numpy.arange(start=0.15, stop=limitD + step, step=step)

# Get best fit slope
bestKIndex = mse.index(min(mse))
bestK = round(c[bestKIndex], 2)

# Calculate best fit curve for values of x
bestY = Qb[bestKIndex]

# Plot data and best fit curve
ax.errorbar(xRange, mean_Qs, std_Qs, linestyle='None', marker='^')
ax.grid()
ax.set_ylim(bottom=0)
plt.xlabel("Diámetero [m]")
plt.ylabel("Caudal [part./s]")
plt.xticks(xRange)
plt.plot(xRange, bestY)
plt.legend(['Ajuste Ley de Beverloo c = {c}'.format(c = bestK), 'Datos (promedios)'], loc=2)

# Save plot
plt.savefig('./output/beverloo/beverloo.png')

# Plot sum of squared fit errors
g, ay = plt.subplots(1)
ay.grid()
ay.set_ylim(bottom=0, top=max(mse))
ay.set_xlim(0, 10)
plt.xlabel("Parámetro libre de Beverloo (c)")
plt.ylabel("Error del ajuste [m$^4$]")
ay.set_xticks([bestK])
plt.plot(c, mse, linestyle='None', marker='*')
g.savefig('./output/beverloo/SquaredError')

# Calculate and print diffusion coefficient and R squared
print("Parámetro libre de Beverloo = %f" % (bestK))
print("R² = %f" % (coefficient_of_determination(array(mean_Qs), array(bestY))))
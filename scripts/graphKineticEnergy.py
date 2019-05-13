import os
import subprocess
import csv
import numpy
import matplotlib.pyplot as plt
import math
from numpy import vstack
from numpy import zeros
from oct2py import octave
octave.addpath('./scripts/')

octave.eval("kineticEnergy");
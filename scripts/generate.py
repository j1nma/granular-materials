from functions import is_int_string, generate_granular_materials_file
import sys

if len(sys.argv) != 5:
    sys.exit("Arguments missing. Exit.")

number_of_particles = sys.argv[1]
area_length = sys.argv[2]
lower_diameter = sys.argv[3]
upper_diameter = sys.argv[4]

if not is_int_string(number_of_particles):
    sys.exit("Must be integer. Exit.")

generate_granular_materials_file(int(number_of_particles),
float(area_length),
float(0),
float(lower_diameter),
float(upper_diameter),
float(0.01))
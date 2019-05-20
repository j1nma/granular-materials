import ovito
from PyQt5.QtCore import *
from PyQt5.QtGui import *
import numpy as np
def project_point(xyz, painter, args):
	view_tm = args['view_tm'] # 3x4 matrix
	proj_tm = args['proj_tm'] # 4x4 matrix
	world_pos = np.append(xyz, 1) # Convert to 4-vector.
	view_pos = np.dot(view_tm, world_pos) # Transform to view space.	
	# Check if point is behind the viewer. If yes, stop here.
	if args['is_perspective'] and view_pos[2] >= 0.0: return None
	# Project to screen space:
	screen_pos = np.dot(proj_tm, np.append(view_pos, 1)) 
	screen_pos[0:3] /= screen_pos[3]
	win_rect = painter.window()
	x = win_rect.left() + win_rect.width() * (screen_pos[0] + 1) / 2
	y = win_rect.bottom() - win_rect.height() * (screen_pos[1] + 1) / 2 + 1
	return (x,y)	

# This user-defined function is called by OVITO to let it draw arbitrary graphics on top of the viewport.
# It is passed a QPainter (see http://qt-project.org/doc/qt-5/qpainter.html).
def render(painter, **args):

	node = ovito.dataset.selected_node
	positions = node.compute().particle_properties.position.array

	# lower right
	xy1 = project_point([0.3, 0.1, 0], painter, args)
	# upper right
	xy2 = project_point([0.3, 1.1, 0], painter, args)
	# lower left
	xy3 = project_point([0.0, 0.1, 0], painter, args)
	# upper right
	xy4 = project_point([0.0, 1.1, 0], painter, args)
	
	gapSize = 0.15
	gapSides = (0.3-gapSize)/2
	# gap left
	xy5 = project_point([gapSides, 0.1, 0], painter, args)
	# gap right
	xy6 = project_point([gapSides+gapSize, 0.1, 0], painter, args)
	
	# This demo code prints the current animation frame into the upper left corner of the viewport.
	
	pen = QPen(Qt.SolidLine)
	pen.setWidth(1)
	pen.setColor(QColor(255,255,255))
	painter.setPen(pen)

	painter.drawLine(xy1[0],xy1[1],xy2[0],xy2[1])
	painter.drawLine(xy2[0],xy2[1],xy4[0],xy4[1])
	painter.drawLine(xy3[0],xy3[1],xy4[0],xy4[1])
	
	# handle gap left
	painter.drawLine(xy3[0],xy3[1],xy5[0],xy5[1])
	# handle gap right
	painter.drawLine(xy6[0],xy6[1],xy1[0],xy1[1])
	
	# Also print the current number of particles into the lower left corner of the viewport.



	


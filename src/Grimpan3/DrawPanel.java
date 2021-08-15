package Grimpan3;

import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.Rectangle2D.Double;
import java.util.ArrayList;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputListener;

public class DrawPanel extends JPanel implements MouseInputListener {


	private static final long serialVersionUID = 1L;

	static final int MINPOLYDIST = 6;
		
	private GrimPanModel model = null;

	public DrawPanel(GrimPanModel model){
		this.model = model;
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		for (GrimShape shape:model.shapeList){
			shape.draw(g2);
		}
		if (model.curDrawShape != null){
			GrimShape curGrimShape = new GrimShape(model.curDrawShape, 
					model.getStrokeWidth(), 
					model.getStrokeColor(),
					model.getFillColor(), model.isShapeFill());
			curGrimShape.draw(g2);
		}
		
	}
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub

	}
	public void mousePressed(MouseEvent ev) {
		Point p1 = ev.getPoint();
		model.setMousePosition(p1);
		//System.out.println("Pressed Mouse Point=("+p1.x+","+p1.y+")");

		if (SwingUtilities.isLeftMouseButton(ev)){
			model.setClickedMousePosition(p1);
			switch (model.getEditState()){
			case GrimPanModel.SHAPE_LINE:
				genLineShape();
				break;
			case GrimPanModel.SHAPE_PENCIL:
				model.setLastMousePosition(model.getMousePosition());				
				model.curDrawShape = new Path2D.Double();
				((Path2D)model.curDrawShape).moveTo((double)p1.x, (double)p1.y);
				break;
			case GrimPanModel.SHAPE_POLYGON:
				break;
			case GrimPanModel.SHAPE_REGULAR:
				genCenteredPolygon();
				break;
			case GrimPanModel.SHAPE_OVAL:
				genEllipse2D();
				break;
			case GrimPanModel.SHAPE_Rectangle:
				Rectangle();
				break;
			case GrimPanModel.SHAPE_CURVE:
				Curve();
				break;
			case GrimPanModel.EDIT_MOVE:
				getSelectedShape();
				break;
			case GrimPanModel.EDIT_REMOVE:
				getSelectedShape();
				break;
			case GrimPanModel.EDIT_ROTATE:
				getSelectedShape();
				break;
			}

		}
		repaint();
	}

	public void mouseReleased(MouseEvent ev) {
		Point p1 = ev.getPoint();
		model.setMousePosition(p1);
		//System.out.println("Released Mouse Point=("+p1.x+","+p1.y+")");

		switch (model.getEditState()){
		case GrimPanModel.SHAPE_LINE:
			genLineShape();
			if (model.curDrawShape != null){
				model.shapeList.add(new GrimShape(model.curDrawShape, 
						model.getStrokeWidth(), 
						model.getStrokeColor(),
						model.getFillColor(), model.isShapeFill()));
				model.curDrawShape = null;
			}
			break;
		case GrimPanModel.SHAPE_PENCIL:
			((Path2D)model.curDrawShape).lineTo((double)p1.x, (double)p1.y);
			if (model.curDrawShape != null){
				model.shapeList.add(new GrimShape(model.curDrawShape, 
						model.getStrokeWidth(), 
						model.getStrokeColor(),
						model.getFillColor(), model.isShapeFill()));
				model.curDrawShape = null;
			}
			break;
		case GrimPanModel.SHAPE_POLYGON:
			model.polygonPoints.add(p1);
			model.curDrawShape = buildPath2DFromPoints(model.polygonPoints);
			if (ev.isShiftDown()) {
				((Path2D)model.curDrawShape).closePath();
				model.polygonPoints.clear();
				model.shapeList.add(new GrimShape(model.curDrawShape, 
						model.getStrokeWidth(), 
						model.getStrokeColor(),
						model.getFillColor(), model.isShapeFill()));
				model.curDrawShape = null;
			}
			break;
		case GrimPanModel.SHAPE_REGULAR:
			genCenteredPolygon();
			if (model.curDrawShape != null){
				model.shapeList.add(new GrimShape(model.curDrawShape, 
						model.getStrokeWidth(), model.getStrokeColor(),
						model.getFillColor(), model.isShapeFill()));
				model.curDrawShape = null;
			}
			break;
		case GrimPanModel.SHAPE_OVAL:
			genEllipse2D();
			if (model.curDrawShape != null){
				model.shapeList.add(new GrimShape(model.curDrawShape, 
						model.getStrokeWidth(), model.getStrokeColor(),
						model.getFillColor(), model.isShapeFill()));
				model.curDrawShape = null;
			}
			break;
		case GrimPanModel.SHAPE_Rectangle:
			Rectangle();
			if (model.curDrawShape != null){
				model.shapeList.add(new GrimShape(model.curDrawShape, 
						model.getStrokeWidth(), model.getStrokeColor(),
						model.getFillColor(), model.isShapeFill()));
				model.curDrawShape = null;
			}
			break;
		case GrimPanModel.SHAPE_CURVE:
			Curve();
			if (model.curDrawShape != null){
				model.shapeList.add(new GrimShape(model.curDrawShape, 
						model.getStrokeWidth(), model.getStrokeColor(),
						model.getFillColor(), model.isShapeFill()));
				model.curDrawShape = null;
			}
			break;
		case GrimPanModel.EDIT_MOVE:
			endShapeMove();
			break;
		case GrimPanModel.EDIT_REMOVE:
			Shaperemove();
			break;
		case GrimPanModel.EDIT_ROTATE:
			rotateShapeByMouse();
			break;

		}

		repaint();

	}

	private void Shaperemove() {
		// TODO Auto-generated method stub
		int selIndex = model.getSelectedShape();
		GrimShape shape = null;
		shape = model.shapeList.get(selIndex);
		model.shapeList.remove(shape);
	}

	public void mouseDragged(MouseEvent ev) {
		Point p1 = ev.getPoint();
		model.setLastMousePosition(model.getMousePosition());
		model.setMousePosition(p1);
		//System.out.println("Dragged Mouse Point=("+p1.x+","+p1.y+")");

		switch (model.getEditState()){
		case GrimPanModel.SHAPE_LINE:
			genLineShape();
			break;
		case GrimPanModel.SHAPE_PENCIL:
			((Path2D)model.curDrawShape).lineTo((double)p1.x, (double)p1.y);
			break;
		case GrimPanModel.SHAPE_POLYGON:
			break;
		case GrimPanModel.SHAPE_REGULAR:
			genCenteredPolygon();
			break;
		case GrimPanModel.SHAPE_OVAL:
			genEllipse2D();
			break;
		case GrimPanModel.SHAPE_Rectangle:
			Rectangle();
			break;
		case GrimPanModel.SHAPE_CURVE:
			Curve();
			break;
		case GrimPanModel.EDIT_MOVE:
			moveShapeByMouse();
			break;
		/*case GrimPanModel.EDIT_ROTATE:
			rotateShapeByMouse();
			break;*/
			

		}
		repaint();

	}
	

	private void rotateShapeByMouse() {
		
		float x, y;
		
		x = model.getMousePosition().x;
		y = model.getMousePosition().y;

		
		float cosA = (float) Math.cos(Math.toRadians(90));
		float sinA = (float) Math.sin(Math.toRadians(90));

		
		int selIndex = model.getSelectedShape();
		GrimShape shape = null;
		if (selIndex != -1){
			shape = model.shapeList.get(selIndex);
			
			shape.translate(
					(float)((x * cosA + y * sinA)/50), 
					(float)((-x * sinA + y * cosA))/100);
		}
	}



	
	private void genLineShape() {
		Point p1 = model.getClickedMousePosition();
		Point p2 = model.getMousePosition();
		model.curDrawShape = new Line2D.Double(p1, p2);

	}
	public static Path2D buildPath2DFromPoints(ArrayList<Point> points){
		Path2D result = new Path2D.Double();
		if (points != null && points.size() > 0) {
			Point p0 = points.get(0);
			result.moveTo((double)(p0.x), (double)(p0.y));
			for (int i=1; i<points.size(); ++i){
				p0 = points.get(i);
				result.lineTo((double)(p0.x), (double)(p0.y));
			}
		}

		return result;
	}
	private void genCenteredPolygon(){
		Point pi = model.getMousePosition();
		Point center = model.getClickedMousePosition();
		if (pi.distance(new Point2D.Double(center.x, center.y)) <= MINPOLYDIST)
			return;

		getPolygonWithCenterNVertex(center, new Point2D.Double(pi.x, pi.y));
	}
	private void getPolygonWithCenterNVertex(Point center, Point2D pi){
		AffineTransform rotNTimes = new AffineTransform();
		rotNTimes.setToRotation(2*Math.PI/model.getNPolygon()); //360/n degree

		Point2D[] polyPoints = new Point2D[model.getNPolygon()];
		polyPoints[0] = new Point2D.Double(pi.getX()-center.x, pi.getY()-center.y); 
		for (int i=1; i<polyPoints.length; ++i){
			polyPoints[i] = (Point2D)polyPoints[i-1].clone();
			rotNTimes.transform(polyPoints[i], polyPoints[i]);
		}

		polyPoints[0] = new Point2D.Double(pi.getX(), pi.getY()); 
		for (int i=1; i<polyPoints.length; ++i){
			polyPoints[i].setLocation(
					polyPoints[i].getX()+center.x, 
					polyPoints[i].getY()+center.y);
		}
		Path2D polygonPath = new Path2D.Double();
		polygonPath.moveTo(polyPoints[0].getX(), polyPoints[0].getY());
		//System.out.println("x= "+polyPoints[0].getX()+" y= "+polyPoints[0].getY());
		for (int i=1; i<polyPoints.length; ++i){
			polygonPath.lineTo(polyPoints[i].getX(), polyPoints[i].getY());
			//System.out.println("x= "+polyPoints[i].getX()+" y= "+polyPoints[i].getY());
		}
		polygonPath.closePath();

		model.curDrawShape = polygonPath;

	}
	private void genEllipse2D(){
		Point pi = model.getMousePosition();
		Point topleft = model.getClickedMousePosition();
		if (pi.distance(new Point2D.Double(topleft.x, topleft.y)) <= MINPOLYDIST)
			return;
		Ellipse2D oval = new Ellipse2D.Double(
				topleft.x, topleft.y,
				pi.x-topleft.x, pi.y-topleft.y);
		model.curDrawShape = oval;
	}
	
	private void Rectangle(){
		Point pi = model.getMousePosition();
		Point topleft = model.getClickedMousePosition();
		if (pi.distance(new Point2D.Double(topleft.x, topleft.y)) <= MINPOLYDIST)
			return;
		Double Rectangle1 = new Rectangle.Double(
				topleft.x, topleft.y,
				pi.x-topleft.x, pi.y-topleft.y);
		model.curDrawShape = Rectangle1;
	}
	
	private void Curve(){
		Point pi = model.getMousePosition();
		Point topleft = model.getClickedMousePosition();
		if (pi.distance(new Point2D.Double(topleft.x, topleft.y)) <= MINPOLYDIST)
			return;
		
		QuadCurve2D q = new QuadCurve2D.Float(
				topleft.x, topleft.x, pi.x-topleft.x, pi.y-topleft.y, pi.x, pi.y);
		model.curDrawShape = q;
		
    }


				
		
	private void getSelectedShape(){
		int selIndex = -1;
		GrimShape shape = null;
		for (int i=model.shapeList.size()-1; i >= 0; --i){
			shape = model.shapeList.get(i);
			if (shape.contains(model.getMousePosition().getX(), model.getMousePosition().getY())){
				selIndex = i;
				break;
			}
		}
		if (selIndex != -1){
			model.setLastMousePosition(model.getClickedMousePosition());
			Color scolor = shape.getGrimStrokeColor();
			Color fcolor = shape.getGrimFillColor();
			if (scolor!=null){
				shape.setGrimStrokeColor(new Color (scolor.getRed(), scolor.getGreen(), scolor.getBlue(), 127));
			}
			if (fcolor!=null){
				shape.setGrimFillColor(new Color (fcolor.getRed(), fcolor.getGreen(), fcolor.getBlue(), 127));
			}
		}
		model.setSelectedShape(selIndex);
	}
	private void moveShapeByMouse(){
		int selIndex = model.getSelectedShape();
		GrimShape shape = null;
		if (selIndex != -1){
			shape = model.shapeList.get(selIndex);
			shape.translate(
					(float)(model.getMousePosition().x-model.getLastMousePosition().x), 
					(float)(model.getMousePosition().y-model.getLastMousePosition().y));
		}
	}
	private void endShapeMove(){
		int selIndex = model.getSelectedShape();
		GrimShape shape = null;
		if (selIndex != -1){
			shape = model.shapeList.get(selIndex);
			Color scolor = shape.getGrimStrokeColor();
			Color fcolor = shape.getGrimFillColor();
			if (scolor!=null){
				shape.setGrimStrokeColor(new Color (scolor.getRed(), scolor.getGreen(), scolor.getBlue()));
			}
			if (fcolor!=null){
				shape.setGrimFillColor(new Color (fcolor.getRed(), fcolor.getGreen(), fcolor.getBlue()));
			}
		}
	}

}

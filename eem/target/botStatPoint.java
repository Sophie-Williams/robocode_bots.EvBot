// -*- java -*-

package eem.target;

import eem.EvBot;
import robocode.*;
import robocode.Rules.*;
import java.awt.geom.Point2D;

public class botStatPoint {
	private Point2D.Double pos;
	private long tStamp;
	private double velocity;
	private double headingDegrees;
	private double energy;

	public botStatPoint(AdvancedRobot bot, ScannedRobotEvent e ) {
		Point2D.Double myCoord = new Point2D.Double();
		myCoord.x = bot.getX();
	       	myCoord.y = bot.getY();
		double angle = (bot.getHeading()+ e.getBearing())/360.*2.*Math.PI;
		double distance = e.getDistance();
		pos = new Point2D.Double( (myCoord.x + Math.sin(angle) * distance),
				(myCoord.y + Math.cos(angle) * distance) );
		tStamp = bot.getTime();
		velocity = e.getVelocity();
		headingDegrees = e.getHeading();
		energy = e.getEnergy();
	}

	public botStatPoint(Point2D.Double p, long t ) {
		tStamp = t;
		pos = p;
	}

	public Double getDistance(Point2D.Double p) {
		return p.distance(pos);
	}

	public String format() {
		String str = "";
		str = str + "energy = " + energy + ", velocity = " + velocity + ", heading = " + headingDegrees;
		str = str + "\n";
		str = str + "position = " + pos + ", tStamp = " + tStamp;
		return str;
	}

	public double getX() {
		return pos.x;
	}

	public double getY() {
		return pos.y;
	}
}

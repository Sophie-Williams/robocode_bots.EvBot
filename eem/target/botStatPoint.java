// -*- java -*-

package eem.target;

import eem.EvBot;
import robocode.*;
import robocode.Rules.*;
import java.awt.geom.Point2D;

public class botStatPoint {
	private Point2D.Double pos;
	private long tStamp;
	private double distance;

	public botStatPoint(AdvancedRobot bot, ScannedRobotEvent e ) {
		Point2D.Double myCoord = new Point2D.Double();
		myCoord.x = bot.getX();
	       	myCoord.y = bot.getY();
		double angle = (bot.getHeading()+ e.getBearing())/360.*2.*Math.PI;
		pos = new Point2D.Double( (myCoord.x + Math.sin(angle) * e.getDistance()),
				(myCoord.y + Math.cos(angle) * e.getDistance()) );
		tStamp = bot.getTime();
		distance = e.getDistance();
	}

	public botStatPoint(Point2D.Double p, long t ) {
		tStamp = t;
		pos = p;
	}

	public Double getDistance() {
		return distance;
	}

	public String format() {
		String str;
		str = "position = " + pos + ", tStamp = " + tStamp;
		return str;
	}
}
// -*- java -*-

package eem.target;

import eem.target.botStatPoint;
import eem.misc.*;
import java.awt.geom.Point2D;
import java.awt.Color;
import java.awt.Graphics2D;

public class target {
	private int nonexisting_coord = -10000;
	private long far_ago  = -10000;
	private botStatPoint statLast;
	private botStatPoint statPrev;
	private String name = "";
	public boolean haveTarget=false;
	public boolean targetUnlocked = false; 

	private static int bulletHitCount = 0;
	private static int bulletFiredCount = 0;

	public target() {
		statPrev = new botStatPoint( new Point2D.Double( nonexisting_coord, nonexisting_coord), far_ago);
		statLast = new botStatPoint( new Point2D.Double( nonexisting_coord, nonexisting_coord), far_ago);
		haveTarget=false;
	}
	
	public int getBulletFiredCount() {
		return this.bulletFiredCount;
	}

	public int getBulletHitCount() {
		return this.bulletHitCount;
	}

	protected void incBulletFiredCount() {
		this.bulletFiredCount++;
	}

	public void incBulletHitCount() {
		this.bulletHitCount++;
	}

	public double getGunHitRate() {
		return (this.getBulletHitCount() ) / (this.getBulletFiredCount() + 1.0);
	}

	public void printGunsStats() {
		logger.routine("Enemy gun hit rate = " + this.getGunHitRate() );
	}

	public void initTic(long ticTime) {
		// updating UnLocked status
		if ( ( ticTime - this.getLastSeenTime() ) > 2) 
			this.setUnLockedStatus(true);
		else
			this.setUnLockedStatus(false);

		if ( didItFireABullet() ) {
			this.incBulletFiredCount();
		}
	}

	public void setUnLockedStatus(boolean val) {
		targetUnlocked = val;
	}

	public target update(Point2D.Double pos, long tStamp) {
		statPrev = statLast;
		statLast = new botStatPoint(pos, tStamp);
		haveTarget = true;
		return this;
	}

	public target update(botStatPoint statPnt) {
		statPrev = statLast;
		statLast = statPnt;
		haveTarget = true;
		return this;
	}

	public double getEnergy() {
		return statLast.getEnergy();
	}

	public Point2D.Double getVelocity() {
		return statLast.getVelocity();
	}

	public void setName(String n) {
		name = n;
	}

	public double getLastDistance(Point2D.Double p) {
		return  statLast.getDistance(p);
	}

	public double getX() {
		return  statLast.getX();
	}

	public double getY() {
		return  statLast.getY();
	}

	public Point2D.Double getPosition() {
		return  statLast.getPosition();
	}

	public long getLastSeenTime() {
		return  statLast.getTimeStamp();
	}


	public double energyDrop() {
		return  statPrev.getEnergy() - statLast.getEnergy();
	}

	public boolean didItFireABullet() {
		boolean stat = true;
		double eDrop = energyDrop();
		if ( (eDrop < .1) || (3 < eDrop) ) {
			stat=false;
			logger.noise("enemy did not fired a bullet");
		}
		return stat;
	}


	public String getName() {
		return name;
	}

	public String format() {
		String str;
		str = "Target bot name: " + getName() + "\n";
		str = str + "Last: " + statLast.format() + "\n" + "Prev: " + statPrev.format();
		return str;
	}

	public void onPaint(Graphics2D g) {
			g.setColor(new Color(0xff, 0x00, 0x00, 0x80));

			// Draw a filled square on top of the scanned robot that covers it
			g.fillRect((int)statLast.getX() - 20, (int)statLast.getY() - 20, 40, 40);
	}

}


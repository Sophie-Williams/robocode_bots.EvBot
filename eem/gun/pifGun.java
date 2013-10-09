// -*- java -*-

package eem.gun;

import eem.EvBot;
import eem.target.*;
import eem.misc.*;
import eem.gun.misc;
import java.awt.Color;
import java.awt.geom.Point2D;
import robocode.util.*;
import java.util.*;
import java.awt.Graphics2D;

// play it forward (PIF) gun
public class pifGun extends baseGun {
	private static int bulletHitCount = 0;
	private static int bulletMissedCount = 0;
	private static int bulletFiredCount = 0;

	long refLength  = 5;

	public int getBulletFiredCount() {
		return this.bulletFiredCount;
	}

	public int getBulletHitCount() {
		return this.bulletHitCount;
	}

	public int getBulletMissedCount() {
		return this.bulletFiredCount - this.bulletHitCount;
	}

	protected void incBulletFiredCount() {
		this.bulletFiredCount++;
	}

	public void incBulletHitCount() {
		this.bulletHitCount++;
	}

	public pifGun() {
		gunName = "pif";
		gunColor = new Color(0xff, 0x00, 0xff, 0x80);
	}

	public pifGun(EvBot bot) {
		this();
		myBot = bot;
		calcGunSettings();
	}

	public Point2D.Double calcTargetFuturePosition( Point2D.Double firingPosition, double firePower, InfoBot tgt) {
		Point2D.Double p = new Point2D.Double(0,0);

		double bSpeed = bulletSpeed ( calcFirePower() );
		p = tgt.getPosition();

		double dist = p.distance(myBot.myCoord);
		int afterTime = (int) (dist/bSpeed);
		int oldAfterTime;
		int iterCnt = 1;
		do {
			oldAfterTime = afterTime;
			//logger.dbg("iteration = " + iterCnt );
			//logger.dbg("after time = " + afterTime );

			LinkedList<Point2D.Double> posList = tgt.possiblePositionsAfterTime(afterTime, refLength);
			//logger.dbg("Match list size = " + posList.size() );
			if ( posList.size() < 1 ) {
				p = tgt.getPosition();
			} else {
				p = posList.getFirst();
			}
			dist = p.distance(myBot.myCoord);
			afterTime = (int) (dist/bSpeed);
			iterCnt++;
		} while ( ( Math.abs( oldAfterTime -afterTime ) > 1 ) && (iterCnt < 5) ) ;
		//logger.dbg("point to aim = " + p );
		return p;
	}

	public void drawPossiblePlayForwardTracks(Graphics2D g) {
		target tgt = myBot._trgt;
		Point2D.Double p = tgt.getPosition();
		double bSpeed = bulletSpeed ( calcFirePower() );
		double dist = p.distance(myBot.myCoord);
		int playTime = (int) (dist/bSpeed);

		int nRequiredMatches = 1000;

		LinkedList<Integer> templateEnds = tgt.endsOfMatchedSegments( refLength, nRequiredMatches);
		for ( int i=0; i < templateEnds.size(); i++ ) {
			LinkedList<Point2D.Double> trace = tgt.playForwardTrace( (int)( templateEnds.get(i) ), (long) playTime );
			for ( Point2D.Double pT : trace ) {
				double disp = 5;
				double rx = disp*Math.random();
				double ry = disp*Math.random();
				graphics.drawCircle( g, new Point2D.Double(pT.x+rx, pT.y +ry), 1);

			}
		}

	}

	public void onPaint(Graphics2D g) {
		super.onPaint( g );
		drawPossiblePlayForwardTracks( g );
	}

}	

// -*- java -*-
// (C) 2013 by Eugeniy Mikhailov, <evgmik@gmail.com>

package eem.radar;

import eem.EvBot;
import eem.misc.*;
import eem.target.*;
import java.util.Random;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.LinkedList;
import robocode.*;

public class radar {
	protected EvBot myBot;

	protected double angle2rotate = 0;
	protected int countFullSweepDelay=0;
	protected int radarSpinDirection =1;
	protected int radarMotionMultiplier = 1;
	protected static int fullSweepDelay = 200;
	protected static double radarMaxRotationAngle;
	protected static double radarSmallestRockingMotion;
	protected static int numberOfSmallRadarSweeps;
	protected int countForNumberOfSmallRadarSweeps;
	protected boolean searchForClosestTarget = true;
	protected boolean movingRadarToLastKnownTargetLocation = false;
	protected double radarBearingToEnemy=0;
	protected LinkedList<String> scannedBots = new LinkedList<String>();
	protected String botToSearchFor = "";

	public radar(EvBot bot) {
		myBot = bot;
		radarMaxRotationAngle = myBot.game_rules.RADAR_TURN_RATE ;
		radarSmallestRockingMotion = myBot.game_rules.RADAR_TURN_RATE/2;
		numberOfSmallRadarSweeps =(int) Math.ceil(360 / radarMaxRotationAngle);
		countForNumberOfSmallRadarSweeps=numberOfSmallRadarSweeps;

	}

	public void initTic() {
		myBot.setAdjustRadarForGunTurn(true); // decouple gun and radar
	}

	public void manage() {
		double angle = 0;
		if ( scannedBots.size() < myBot.getOthers() ) {
			// we have not seen all bots thus we need to do/keep sweeping
			// performing initial sweep
			angle = radarSpinDirection*radarMaxRotationAngle;
			setTurnRadarRight(angle);
			return;
		}

		boolean NeedToRefreshBotsPositions = true;
		if ( NeedToRefreshBotsPositions ) {
			refreshBotsPositions();
		}

		boolean NeedToTrackTarget = true;
		if ( NeedToTrackTarget ) {
			if ( myBot._trgt.haveTarget ) {
				String bName = myBot._trgt.getName();
				moveRadarToBot( bName );
			} else {
				refreshBotsPositions();
			}
		}
			//this.performRockingSweepIfNeded();
			//this.moveToOrOverOldTargetPositionIfNeeded();
			//this.decreaseFullSweepDelay();
			//this.performFullSweepIfNeded();
	}

	public void refreshBotsPositions() {
		String bName = scannedBots.getFirst();
		moveRadarToBot( bName );
	}

	public void moveRadarToBot( String bName ) {
		double angle = 0;
		long lastSeenDelay = myBot.ticTime -  myBot._botsmanager.getBotByName( bName ).getLastSeenTime();
		if ( botToSearchFor.equals( bName ) && (lastSeenDelay > 1) ) {
			// we already set radar motion parameters
			angle = radarSpinDirection*radarMaxRotationAngle;
		} else {
			// new bot to search or we just saw this bot so its position
			// can be used for radar spin calculations
			botToSearchFor = bName;
			double radar_heading = myBot.getRadarHeading();
			double angleToLastBotPosition = math.angle2pt(myBot.myCoord, myBot._botsmanager.getBotByName( bName ).getPosition() );
			angle= angleToLastBotPosition - radar_heading;
			angle = math.shortest_arc(angle);
			radarSpinDirection = math.signNoZero(angle);
			angle = Math.abs( angle );
				angle+=radarMaxRotationAngle/2; // we want to overshoot
			angle = radarSpinDirection*angle;
		}
		setTurnRadarRight(angle);
	}

	public void setMovingRadarToLastKnownTargetLocation(boolean val) {
		movingRadarToLastKnownTargetLocation = val;
	}

	public void setFullSweepAllowed() {
		countFullSweepDelay = -1; // we can sweep do full radar sweep
	}

	public void decreaseFullSweepDelay() {
		countFullSweepDelay--; // we can sweep do full radar sweep
	}


	public void moveToOrOverOldTargetPositionIfNeeded() {
		double angle;
		// moving radar to or over old target position
		if ( !searchForClosestTarget && myBot._trgt.targetUnlocked && movingRadarToLastKnownTargetLocation) {
			logger.noise("Spinning radar to the old target location");
			spinInTheSameDirection(radarSpinDirection);
		}
	}

	public void spinInTheSameDirection(int direction) {
		double angle;
		radarSpinDirection = direction;
		angle = radarSpinDirection*radarMaxRotationAngle;
		setTurnRadarRight(angle);
	}

	public void performFullSweepIfNeded() {
		double angle=0;

		logger.noise("countFullSweepDelay = " + countFullSweepDelay);
		logger.noise("searchForClosestTarget = " + searchForClosestTarget);
		countForNumberOfSmallRadarSweeps--;
		// full sweep for the closest enemy
		if ( (countFullSweepDelay<0) && !searchForClosestTarget && (myBot.getOthers() > 1) || !myBot._trgt.haveTarget) {
			logger.noise("Begin new cycle for closest enemy search");
			logger.noise("We have target = " + myBot._trgt.haveTarget);
			searchForClosestTarget = true;
			countForNumberOfSmallRadarSweeps = numberOfSmallRadarSweeps;
		}

		if ( searchForClosestTarget ) {
			angle = radarMaxRotationAngle;
			logger.noise("Search sweep");
			setTurnRadarRight(angle);
			myBot._trgt.setUnLockedStatus(true);
		}

		logger.noise("countForNumberOfSmallRadarSweeps = " + countForNumberOfSmallRadarSweeps);
		if ( countForNumberOfSmallRadarSweeps <= 0 && searchForClosestTarget ) {
			searchForClosestTarget = false;
			countFullSweepDelay = fullSweepDelay;
			logger.noise("Full sweep for closest enemy is completed");
			movingRadarToLastKnownTargetLocation = true;

			double radar_angle = myBot.getRadarHeading();
			angle=(myBot.angle2target()-radar_angle);
			angle = math.shortest_arc(angle);
			if (math.sign(angle) >= 0 ) {
				radarSpinDirection=1;
				angle = myBot.game_rules.RADAR_TURN_RATE;
			} else {
				radarSpinDirection=-1;
				angle = -myBot.game_rules.RADAR_TURN_RATE;
			}
			logger.noise("Full sweep radar motion");
			setTurnRadarRight(angle);
		}
	}

	public void performRockingSweepIfNeded() {
		double angle;
		// radar rocking motion to relock target
		if (myBot._trgt.haveTarget && !searchForClosestTarget && !movingRadarToLastKnownTargetLocation) {
			logger.noise("Doing radar rocking motion");
			if (!myBot._trgt.targetUnlocked) {
				logger.noise("Target scanned last time");
				radarBearingToEnemy = math.shortest_arc( myBot.angle2target()-myBot.getRadarHeading() );
				logger.noise("Bearing to the target = " + radarBearingToEnemy);
				radarSpinDirection = math.signNoZero(radarBearingToEnemy);
				angle = radarBearingToEnemy + radarSpinDirection*radarMaxRotationAngle/2;
				// we will need to know opposite to made direction if on scan we missed target
				// then unlocked condition will kick in with opposite rotation
				radarSpinDirection*=-1; 
			} else {
				logger.noise("Target unlocked, long sweep to catch it");
				angle = radarSpinDirection*radarMaxRotationAngle;
			}
			logger.noise("Trying to relock on target with radar motion");
			setTurnRadarRight(angle);
		}
	}

	protected void setTurnRadarRight(double angle) {
		angle2rotate = math.putWithinRange(angle, -radarMaxRotationAngle, radarMaxRotationAngle);
		logger.noise("Radar rotation angle = " + angle2rotate);
		myBot.setTurnRadarRight(angle2rotate);
	}

	public void onRobotDeath(RobotDeathEvent e) {
		String dBotName = e.getName();
		scannedBots.remove( dBotName );
	}

	public void onScannedRobot(ScannedRobotEvent e) {
		String scannedBotName = e.getName();
		for ( String bName : scannedBots ) {
			if ( bName.equals( scannedBotName ) ) {
				scannedBots.remove( bName );
				break;
			}
		}
		scannedBots.addLast( scannedBotName );
	}
}


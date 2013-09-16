// -*- java -*-

package eem.gun;

import eem.EvBot;
import eem.target.*;
import eem.misc.*;
import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.Random;
import robocode.util.*;

public class randomGun extends baseGun {
	public randomGun(EvBot bot) {
		myBot = bot;
		gunName = "random";
		gunColor = new Color(0xff, 0xff, 0xff, 0x80);
	}

	public void setTargetFuturePosition(target tgt) {
		if ( !gunHasTargetPoint ) {
			targetFuturePosition = math.putWithinBorders(
				findTargetHitPositionWithRandomPredictor( firePower, tgt),
				myBot.BattleField
				);
			gunHasTargetPoint = true;
		}
	}

	public Point2D.Double  findTargetHitPositionWithRandomPredictor(double firePower, target tgt) {
		Point2D.Double vTvec;
		Point2D.Double tF;
		double Tx, Ty, vT,  dx, dy, dist;
		double tFX, tFY; // target future position
		double sin_vT, cos_vT;
		double timeToHit;
		double a, b, c;
		double k, rnd;
		double bSpeed=bulletSpeed( firePower );

		myBot.dbg(myBot.dbg_noise, "Bullet speed " + bSpeed );

		vTvec = tgt.getVelocity();
		myBot.dbg(myBot.dbg_noise, "Target velocity " + vTvec.x +", " + vTvec.y);

		vT = Math.sqrt(vTvec.x*vTvec.x + vTvec.y*vTvec.y);
		if ( !Utils.isNear(vT, 0) ) {
			cos_vT=vTvec.x/vT;
			sin_vT=vTvec.y/vT;
		} else {
			// target is stationary
			// assign small speed in random direction
			vT=0.00001;
			double rand_angle=2*Math.PI*Math.random();
			cos_vT=Math.cos(rand_angle);
			sin_vT=Math.sin(rand_angle);
		}

		// estimated current target position
		Tx = tgt.getX() + vTvec.x*(myBot.getTime()-tgt.getLastSeenTime());
		Ty = tgt.getY() + vTvec.y*(myBot.getTime()-tgt.getLastSeenTime());
		myBot.dbg(myBot.dbg_noise, "Target estimated current position Tx = " + Tx + " Ty = " + Ty);

		rnd=Math.random();
		// assume that target will change speed
		if ( rnd > .99) {
			// k in -1..1
			k = 2*(Math.random()-0.5); 
			vT=(1+k*8)*vT; // we change speed +/- to old values
		} else {
			// often smart bots have only small displacements
			k = 2*(gun_rand.nextGaussian()); 
			vT=k; // no memory of previous motion
		}
		myBot.dbg(myBot.dbg_debuging, "guessed speed coefficient = " + k);
		// we keep the same target heading
		if ( vT == 0 ) {
			// to avoid division by zero
			vT=0.1; 
		}
		// check that we are with in game limits
		// 8 is maximum speed
		vT = Math.max(-8,vT);
		vT = Math.min( 8,vT);

		// use randomized target velocity vector
		vTvec.x = cos_vT*vT; 
		vTvec.y = sin_vT*vT;
		
		tF=misc.linear_predictor( bSpeed, new Point2D.Double(Tx, Ty), 
				vTvec,  myBot.myCoord);

		myBot.dbg(myBot.dbg_noise, "Predicted and boxed target position " + tF.x +", " + tF.y);
		
		return tF;
	}
}	


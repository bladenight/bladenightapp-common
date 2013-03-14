package de.greencity.bladenightapp.network.messages;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class PointOnRoute {
	public double pos;
	public double spd;
	
	public PointOnRoute() {
	}

	public PointOnRoute(double linearPosition, double linearSpeed) {
		setPosition(linearPosition);
		setSpeed(linearSpeed);
	}

	public double getPosition() {
		return pos;
	}

	public void setPosition(double pos) {
		this.pos = pos;
	}

	public double getSpeed() {
		return spd;
	}
	
	public void setSpeed(double spd) {
		this.spd = spd;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}

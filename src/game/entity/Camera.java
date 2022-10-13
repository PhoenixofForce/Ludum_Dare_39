package game.entity;

import java.util.LinkedList;
import java.util.List;

public class Camera{
	private static final int TIME_FRAC = 25;
	private static final float MIN_AMP = 0.0001f;
	private static final float DECAY = 0.8f;

	public float x, y;

	private List<Screenshake> screenshakeList;

	private class Screenshake {
		private Screenshake(long startTime, float decay, float amp_x, float amp_y, float phase_x, float phase_y, float freq_x, float freq_y) {
			this.startTime = startTime;
			this.decay = decay;
			this.amp_x = amp_x;
			this.amp_y = amp_y;
			this.phase_x = phase_x;
			this.phase_y = phase_y;
			this.freq_x = freq_x;
			this.freq_y = freq_y;
		}

		long startTime;
		float decay;
		float amp_x, amp_y, phase_x, phase_y, freq_x, freq_y;
}

	private float tx, ty;
	private boolean z2 = false;
	private float targetX, targetY;
	private long beginTime2 = 0, targetTime2 = 0;
	private float a2, b2, c2, d2;
	private float a3, b3, c3, d3;

	public Camera() {
		x = 0;
		y = 0;
		tx = x;
		ty = y;

		screenshakeList = new LinkedList<>();
	}

	/**
	 * Takes t-Values and put it to the inUse values
	 */
	public boolean update() {
		boolean b5 = (x != tx) || (y != ty) || z2 || !screenshakeList.isEmpty();
		long time = System.currentTimeMillis() % 10000000;


		if (z2) {
			if (time > targetTime2) {
				tx = targetX;
				ty = targetY;
				z2 = false;
			} else {
				tx = calculateFunction((time * 1.0f - beginTime2) / (targetTime2 - beginTime2), a2, b2, c2, d2);
				ty = calculateFunction((time * 1.0f - beginTime2) / (targetTime2 - beginTime2), a3, b3, c3, d3);
			}
		}

		float sx = 0, sy = 0;
		for (int i = 0; i < screenshakeList.size(); i++) {
			Screenshake s = screenshakeList.get(i);
			double d = Math.pow(s.decay, (time - s.startTime)/TIME_FRAC);
			if (d * s.amp_x < MIN_AMP && d * s.amp_y < MIN_AMP) {
				screenshakeList.remove(s);
			} else {
				sx += d * s.amp_x * Math.cos(s.freq_x * (time-s.startTime) / TIME_FRAC + s.phase_x);
				sy += d * s.amp_y * Math.cos(s.freq_y * (time-s.startTime) / TIME_FRAC + s.phase_y);
			}
		}

		x = tx + sx;
		y = ty + sy;
		return b5;
	}

	public void addScreenshake(float strength) {
		screenshakeList.add(new Screenshake(System.currentTimeMillis() % 10000000, DECAY, strength, strength, (float) (Math.random() * 2 * Math.PI), (float) (Math.random() * 2 * Math.PI), 1, 1));
	}

	public void setPosition(float x, float y) {
		z2 = false;
		this.tx = x;
		this.ty = y;
	}

	public void setPositionSmooth(float x, float y, long time) {
		float v2 = 0, v3 = 0;
		float t2 = x, t3 = y;
		if (z2) {
			v2 = calculateDerivative(((System.currentTimeMillis() % 10000000) * 1.0f - beginTime2) / (targetTime2 - beginTime2), a2, b2, c2, d2);
			v3 = calculateDerivative(((System.currentTimeMillis() % 10000000) * 1.0f - beginTime2) / (targetTime2 - beginTime2), a3, b3, c3, d3);
		}
		float currentX = tx, currentY = ty;

		d2 = currentX;
		c2 = v2;
		b2 = 3 * t2 - 2 * v2 - 3 * currentX;
		a2 = v2 + 2 * currentX - 2 * t2;

		d3 = currentY;
		c3 = v3;
		b3 = 3 * t3 - 2 * v3 - 3 * currentY;
		a3 = v3 + 2 * currentY - 2 * t3;

		beginTime2 = System.currentTimeMillis() % 10000000;
		targetTime2 = System.currentTimeMillis() % 10000000 + time;
		targetX = x;
		targetY = y;

		z2 = true;
	}

	public float getX() {
		return tx;
	}

	public float getY() {
		return ty;
	}

	private float calculateFunction(float x, float a, float b, float c, float d) {
		return a * x * x * x + b * x * x + c * x + d;
	}

	private float calculateDerivative(float x, float a, float b, float c, float d) {
		return 3 * a * x * x + 2 * b * x + c;
	}
}
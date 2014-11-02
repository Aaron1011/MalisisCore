/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Ordinastie
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package net.malisis.core.renderer.animation.transformation;

import java.util.List;

public abstract class Transformation<T extends Transformation, S extends ITransformable>
{
	public static final int LINEAR = 0, SINUSOIDAL = 1;

	protected int movement = LINEAR;
	protected int duration, delay = 0;
	protected int loops = 1, loopStartDelay = 0, loopResetDelay = 0;
	protected float elapsedTimeCurrentLoop;
	protected boolean reversed = false;

	public T movement(int movement)
	{
		this.movement = movement;
		return (T) this;
	}

	public T delay(int delay)
	{
		this.delay = delay;
		return (T) this;
	}

	public T forTicks(int duration)
	{
		return forTicks(duration, delay);
	}

	public T forTicks(int duration, int delay)
	{
		if (this.duration == 0)
		{
			this.duration = duration;
			this.delay = delay;
		}
		return (T) this;
	}

	public int getDuration()
	{
		return duration;
	}

	public int getDelay()
	{
		return delay;
	}

	public int getLoops()
	{
		return loops;
	}

	public int totalDuration()
	{
		if (loops == -1)
			return Integer.MAX_VALUE;

		return delay + loops * getLoopDuration();
	}

	public int getLoopDuration()
	{
		return duration + loopStartDelay + loopResetDelay;
	}

	public T loop(int loops)
	{
		return loop(loops, 0, 0);
	}

	public T loop(int loops, int startDelay, int resetDelay)
	{
		if (loops == 0)
			return (T) this;

		this.loops = loops;
		this.loopStartDelay = startDelay;
		this.loopResetDelay = resetDelay;
		return (T) this;
	}

	public void transform(List<S> transformables, float elapsedTime)
	{
		for (S transformable : transformables)
			transform(transformable, elapsedTime);
	}

	public void transform(S transformable, float elapsedTime)
	{
		doTransform(transformable, completion(Math.max(0, elapsedTime)));
	}

	protected float completion(float elapsedTime)
	{
		if (duration == 0)
			return 0;

		float comp = 0;
		int loopDuration = getLoopDuration();
		elapsedTimeCurrentLoop = elapsedTime - delay;

		if (loops != -1 && elapsedTimeCurrentLoop > loops * loopDuration)
			return 1;

		if (loops != 1)
		{
			elapsedTimeCurrentLoop %= loopDuration;
			if (elapsedTimeCurrentLoop < loopStartDelay)
				return 0;
			if (elapsedTimeCurrentLoop - loopResetDelay > loopDuration)
				return 1;
			elapsedTimeCurrentLoop -= loopStartDelay;
		}

		comp = Math.min(elapsedTimeCurrentLoop / duration, 1);
		comp = Math.max(0, Math.min(1, comp));
		if (movement == SINUSOIDAL)
		{
			comp = (float) (1 - Math.cos(comp * Math.PI)) / 2;
		}

		return comp;
	}

	public T reversed(boolean reversed)
	{
		this.reversed = reversed;
		return (T) this;
	}

	protected abstract void doTransform(S transformable, float comp);

}

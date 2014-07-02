package net.malisis.core.renderer.animation.transformation;

import net.malisis.core.renderer.element.Shape;

public class Scale extends Transformation<Scale>
{
	protected float fromX = 1, fromY = 1, fromZ = 1;
	protected float toX = 1, toY = 1, toZ = 1;

	public Scale()
	{}

	public Scale(float x, float y, float z)
	{
		to(x, y, z);
	}

	public Scale(float fromX, float fromY, float fromZ, float toX, float toY, float toZ)
	{
		from(fromX, fromY, fromZ);
		to(toX, toY, toZ);
	}

	protected Scale from(float x, float y, float z)
	{
		fromX = x;
		fromY = y;
		fromZ = z;
		return this;
	}

	protected Scale to(float x, float y, float z)
	{
		toX = x;
		toY = y;
		toZ = z;
		return this;
	}

	@Override
	protected void doTransform(Shape s, float comp)
	{
		comp = Math.max(comp, 0);
		s.scale(fromX + (toX - fromX) * comp, fromY + (toY - fromY) * comp, fromZ + (toZ - fromZ) * comp);
	}

	@Override
	public Scale reversed(boolean reversed)
	{
		if (!reversed)
			return this;

		float tmpX = fromX;
		float tmpY = fromY;
		float tmpZ = fromZ;
		fromX = toX;
		fromY = toY;
		fromZ = toZ;
		toX = tmpX;
		toY = tmpY;
		toZ = tmpZ;
		return this;
	}

}

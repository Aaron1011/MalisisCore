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

package net.malisis.core.util;

import java.util.HashSet;
import java.util.Set;

import net.malisis.core.block.BoundingBoxType;
import net.malisis.core.util.chunkcollision.IChunkCollidable;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;

/**
 * @author Ordinastie
 *
 */
public class AABBUtils
{

	public static AxisAlignedBB[] rotate(AxisAlignedBB[] aabbs, int angle)
	{
		for (AxisAlignedBB aabb : aabbs)
			rotate(aabb, angle);
		return aabbs;
	}

	public static AxisAlignedBB rotate(AxisAlignedBB aabb, int angle)
	{
		int[] cos = { 1, 0, -1, 0 };
		int[] sin = { 0, 1, 0, -1 };

		int a = angle % 4;
		if (a < 0)
			a += 4;

		AxisAlignedBB copy = AxisAlignedBB.getBoundingBox(1, aabb.minY, 1, 1, aabb.maxY, 1);
		aabb.offset(-0.5F, 0, -0.5F);

		copy.minX = (aabb.minX * cos[a]) - (aabb.minZ * sin[a]);
		copy.minZ = (aabb.minX * sin[a]) + (aabb.minZ * cos[a]);

		copy.maxX = (aabb.maxX * cos[a]) - (aabb.maxZ * sin[a]);
		copy.maxZ = (aabb.maxX * sin[a]) + (aabb.maxZ * cos[a]);

		aabb.setBB(fix(copy));
		aabb.offset(0.5F, 0, 0.5F);

		return aabb;
	}

	public static AxisAlignedBB fix(AxisAlignedBB aabb)
	{
		double tmp;
		if (aabb.minX > aabb.maxX)
		{
			tmp = aabb.minX;
			aabb.minX = aabb.maxX;
			aabb.maxX = tmp;
		}

		if (aabb.minZ > aabb.maxZ)
		{
			tmp = aabb.minZ;
			aabb.minZ = aabb.maxZ;
			aabb.maxZ = tmp;
		}

		return aabb;
	}

	public static AxisAlignedBB readFromNBT(NBTTagCompound tag, AxisAlignedBB aabb)
	{
		return aabb.setBounds(tag.getDouble("minX"), tag.getDouble("minY"), tag.getDouble("minZ"), tag.getDouble("maxX"),
				tag.getDouble("maxY"), tag.getDouble("maxZ"));
	}

	public static void writeToNBT(NBTTagCompound tag, AxisAlignedBB aabb)
	{
		if (aabb == null)
			return;
		tag.setDouble("minX", aabb.minX);
		tag.setDouble("minY", aabb.minY);
		tag.setDouble("minZ", aabb.minZ);
		tag.setDouble("maxX", aabb.maxX);
		tag.setDouble("maxY", aabb.maxY);
		tag.setDouble("maxZ", aabb.maxZ);
	}

	/**
	 * Gets a {@link AxisAlignedBB} that englobes the passed {@code AxisAlignedBB}.
	 *
	 * @param aabbs the aabbs
	 * @return the axis aligned bb
	 */
	public static AxisAlignedBB combine(AxisAlignedBB[] aabbs)
	{
		AxisAlignedBB ret = AxisAlignedBB.getBoundingBox(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE, Double.MIN_VALUE,
				Double.MAX_VALUE, Double.MAX_VALUE);

		for (AxisAlignedBB aabb : aabbs)
		{
			ret.minX = Math.min(aabb.minX, ret.minX);
			ret.maxX = Math.max(aabb.maxX, ret.maxX);
			ret.minY = Math.min(aabb.minY, ret.minY);
			ret.maxY = Math.max(aabb.maxY, ret.maxY);
			ret.minZ = Math.min(aabb.minZ, ret.minZ);
			ret.maxZ = Math.max(aabb.maxZ, ret.maxZ);
		}

		return ret;
	}

	/**
	 * Offsets the passed {@link AxisAlignedBB}s by the specified coordinates.
	 *
	 * @param x the x
	 * @param y the y
	 * @param z the z
	 * @param aabbs the aabbs
	 */
	public static AxisAlignedBB[] offset(double x, double y, double z, AxisAlignedBB... aabbs)
	{
		for (AxisAlignedBB aabb : aabbs)
			if (aabb != null)
				aabb.offset(x, y, z);
		return aabbs;
	}

	/**
	 * Gets the {@link ChunkPosition} overlapping {@link AxisAlignedBB}s.
	 *
	 * @param aabbs the aabbs
	 * @return the overlapping blocks
	 */
	public static Set<ChunkPosition> getCollidingPositions(AxisAlignedBB... aabbs)
	{
		Set<ChunkPosition> blocks = new HashSet<>();
		for (AxisAlignedBB aabb : aabbs)
		{
			if (aabb == null)
				continue;

			int minX = (int) Math.floor(aabb.minX);
			int maxX = (int) Math.ceil(aabb.maxX);
			int minY = (int) Math.floor(aabb.minY);
			int maxY = (int) Math.ceil(aabb.maxY);
			int minZ = (int) Math.floor(aabb.minZ);
			int maxZ = (int) Math.ceil(aabb.maxZ);

			for (int x = minX; x < maxX; x++)
				for (int y = minY; y < maxY; y++)
					for (int z = minZ; z < maxZ; z++)
						blocks.add(new ChunkPosition(x, y, z));
		}

		return blocks;
	}

	/**
	 * Checks if a group of {@link AxisAlignedBB} is colliding with another one.
	 *
	 * @param aabbs1 the aabbs1
	 * @param aabbs2 the aabbs2
	 * @return true, if is colliding
	 */
	public static boolean isColliding(AxisAlignedBB[] aabbs1, AxisAlignedBB[] aabbs2)
	{
		for (AxisAlignedBB aabb1 : aabbs1)
		{
			if (aabb1 != null)
			{
				for (AxisAlignedBB aabb2 : aabbs2)
					if (aabb2 != null && aabb1.intersectsWith(aabb2))
						return true;
			}
		}

		return false;
	}

	public static AxisAlignedBB[] getCollisionBoundingBoxes(Block block, World world, int x, int y, int z)
	{
		if (block instanceof IChunkCollidable)
			return ((IChunkCollidable) block).getBoundingBox(world, x, y, z, BoundingBoxType.CHUNKCOLLISION);

		AxisAlignedBB aabb = block.getCollisionBoundingBoxFromPool(world, x, y, z);
		if (aabb == null)
			return new AxisAlignedBB[0];
		aabb.offset(-x, -y, -z);
		return new AxisAlignedBB[] { aabb };
	}

}

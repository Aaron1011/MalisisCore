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

package net.malisis.core.renderer;

import java.util.Map;
import java.util.Optional;

import net.malisis.core.block.IComponent;
import net.malisis.core.registry.MalisisRegistry;
import net.malisis.core.renderer.component.AnimatedModelComponent;
import net.malisis.core.util.BlockPosUtils;
import net.malisis.core.util.EntityUtils;
import net.malisis.core.util.Point;
import net.malisis.core.util.callback.CallbackResult;
import net.malisis.core.util.callback.ICallback.CallbackOption;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.google.common.collect.Maps;

/**
 * @author Ordinastie
 *
 */
public class AnimatedRenderer extends MalisisRenderer<TileEntity>
{
	/** Map of {@link ISortedRenderable} per {@link Chunk Chunks}. */
	private static Map<BlockPos, IAnimatedRenderable> animatedRenderables = Maps.newHashMap();
	static
	{
		MalisisRegistry.onPostSetBlock(AnimatedRenderer::removeRenderable, CallbackOption.of());
	}

	ICamera camera;
	IAnimatedRenderable renderable;

	public AnimatedRenderer()
	{
		setBatched();
		registerForRenderWorldLast();
		MinecraftForge.EVENT_BUS.register(this);
	}

	/**
	 * Gets the currently drawn {@link IAnimatedRenderable}.
	 *
	 * @return the renderable
	 */
	public IAnimatedRenderable getRenderable()
	{
		return renderable;
	}

	@Override
	public boolean shouldRender(RenderWorldLastEvent event, IBlockAccess world)
	{
		return animatedRenderables.size() > 0;
	}

	@Override
	public void render()
	{
		enableBlending();
		Point viewOffset = EntityUtils.getRenderViewOffset(partialTick);
		ICamera camera = new Frustum();
		//camera.setPosition(viewOffset.x, viewOffset.y, viewOffset.z);

		renderType = RenderType.ANIMATED;
		animatedRenderables.values()
							.stream()
							.filter(r -> r.inFrustrum(camera))
							.sorted((r1, r2) -> -BlockPosUtils.compare(viewOffset, r1.getPos(), r2.getPos()))
							.forEach(this::renderRenderable);
		renderType = RenderType.WORLD_LAST;
	}

	private void renderRenderable(IAnimatedRenderable renderable)
	{
		this.renderable = renderable;
		set(renderable.getWorld(), renderable.getPos());
		posOffset = new Vec3d(pos.getX(), pos.getY(), pos.getZ());

		renderable.renderAnimated(block, this);
	}

	@SubscribeEvent
	public void onChunkUnload(ChunkEvent.Unload event)
	{
		animatedRenderables.keySet().removeIf(p -> (p.getX() >> 4) == event.getChunk().xPosition
				&& (p.getZ() >> 4) == event.getChunk().zPosition);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Removes the stored {@link ISortedRenderable} in the chunk for the position, if necessary.<br>
	 * Called every time a block is set in the world.
	 *
	 * @param chunk the chunk
	 * @param pos the pos
	 * @param oldState the old state
	 * @param newState the new state
	 * @return the callback result
	 */
	public static CallbackResult<Void> removeRenderable(Chunk chunk, BlockPos pos, IBlockState oldState, IBlockState newState)
	{
		if (oldState.getBlock() == newState.getBlock()) //same block, so same components
			return CallbackResult.noResult();

		//TODO: make IRenderableProvider ?
		AnimatedModelComponent comp = IComponent.getComponent(AnimatedModelComponent.class, oldState.getBlock());
		if (comp != null)
			animatedRenderables.remove(pos);

		return CallbackResult.noResult();
	}

	/**
	 * Gets the {@link ISortedRenderable} for the specified {@link BlockPos}, if the chunk is loaded.
	 *
	 * @param <T> the generic type
	 * @param pos the pos
	 * @return the renderable
	 */
	public static Optional<IAnimatedRenderable> getRenderable(BlockPos pos)
	{
		return Optional.ofNullable(animatedRenderables.get(pos));
	}

	/**
	 * Registers the {@link ISortedRenderable} at the specified position if there isn't already one.
	 *
	 * @param pos the pos
	 * @param amc the amc
	 */
	public static void registerRenderable(IBlockAccess world, BlockPos pos, AnimatedModelComponent amc)
	{
		IAnimatedRenderable r = animatedRenderables.get(pos);
		if (r == null)
			animatedRenderables.put(pos, amc.createRenderable(world, pos));
	}
}
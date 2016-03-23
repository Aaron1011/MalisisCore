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

import java.util.List;

import javax.vecmath.Matrix4f;

import net.malisis.core.MalisisRegistry;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.IPerspectiveAwareModel;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.ImmutableList;

/**
 * @author Ordinastie
 *
 */
public interface IItemRenderer
{
	public boolean renderItem(ItemStack itemStack, float partialTick);

	public void setTransformType(TransformType transformType);

	public Matrix4f getTransform(TransformType tranformType);

	public boolean isGui3d();

	public static class DummyModel implements IPerspectiveAwareModel
	{
		private Item item;
		private ModelResourceLocation rl;

		public DummyModel(Item item, String name)
		{
			this.item = item;
			this.rl = new ModelResourceLocation(name, "inventory");
		}

		public ModelResourceLocation getResourceLocation()
		{
			return rl;
		}

		@Override
		public Pair<? extends IBakedModel, Matrix4f> handlePerspective(TransformType transformType)
		{
			IItemRenderer itemRenderer = MalisisRegistry.getItemRenderer(item);
			if (itemRenderer == null)
				return Pair.of(this, null);

			itemRenderer.setTransformType(transformType);
			return Pair.of(this, itemRenderer.getTransform(transformType));
		}

		@Override
		public boolean isGui3d()
		{
			IItemRenderer itemRenderer = MalisisRegistry.getItemRenderer(item);
			return itemRenderer != null && itemRenderer.isGui3d();
		}

		//@formatter:off
		@Override public boolean isAmbientOcclusion() 					{ return false; }
		@Override public boolean isBuiltInRenderer() 					{ return false; }
		@Override public TextureAtlasSprite getParticleTexture() 		{ return null; }
		@Override public ItemCameraTransforms getItemCameraTransforms() { return ItemCameraTransforms.DEFAULT; }
		@Override public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand)	{ return ImmutableList.of(); }
		@Override public ItemOverrideList getOverrides() 				{ return ItemOverrideList.NONE; }
		//@formatter:on

		@Override
		public String toString()
		{
			return item.getUnlocalizedName() + "[" + rl + "]";
		}

	};

}

package net.malisis.core.renderer;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.malisis.core.MalisisCore;
import net.malisis.core.renderer.element.Face;
import net.malisis.core.renderer.element.Shape;
import net.malisis.core.renderer.element.Vertex;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.DestroyBlockProgress;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.relauncher.ReflectionHelper;

public class BaseRenderer extends TileEntitySpecialRenderer implements ISimpleBlockRenderingHandler, IItemRenderer
{
	/**
	 * Set rendering for world
	 */
	public static final int TYPE_ISBRH_WORLD = 1;
	/**
	 * Set rendering for inventory with ISBRH
	 */
	public static final int TYPE_ISBRH_INVENTORY = 2;
	/**
	 * Set rendering for inventory with IItemRenderer
	 */
	public static final int TYPE_ITEM_INVENTORY = 3;
	/**
	 * Set rendering for TESR
	 */
	public static final int TYPE_TESR_WORLD = 4;

	//Reference to Minecraft.renderGlobal.damagedBlocks (lazy loaded)
	private static Map damagedBlocks;
	protected static IIcon[] damagedIcons;

	protected int renderId = -1;
	protected Tessellator t = Tessellator.instance;
	protected IBlockAccess world;
	protected RenderBlocks renderBlocks;

	/**
	 * Block to render (ISBRH/TESR)
	 */
	protected Block block;
	/**
	 * Metadata of the block to render (ISBRH/TESR)
	 */
	protected int blockMetadata;
	/**
	 * Position of the block
	 */
	protected int x, y, z;
	/**
	 * ItemStack to render (ITEM)
	 */
	protected ItemStack itemStack;
	/**
	 * Type of item rendering.
	 * 
	 * @see {@link ItemRenderType}
	 */
	protected ItemRenderType itemRenderType;
	/**
	 * Type of rendering : <code>TYPE_ISBRH_WORLD</code>, <code>TYPE_ISBRH_INVENTORY</code>, <code>TYPE_ITEM_INVENTORY</code> or
	 * <code>TYPE_TESR_WORLD</code>
	 */
	protected int renderType;
	/**
	 * Mode of rendering (GL constants)
	 */
	protected int drawMode;

	/**
	 * Are render coordinates already shifted (<code>TYPE_ISBRH_WORLD</code> only)
	 */
	protected boolean isShifted = false;

	/**
	 * The shape to render
	 */
	protected Shape shape;
	/**
	 * Current face being rendered
	 */
	protected Face face;
	/**
	 * Global shape parameters
	 */
	protected RenderParameters shapeParams;
	/**
	 * Current parameters for the face being rendered
	 */
	protected RenderParameters params;
	/**
	 * Base brightness of the block
	 */
	protected int baseBrightness;
	/**
	 * An override texture set by the renderer
	 */
	protected IIcon overrideTexture;
	/**
	 * TileEntity currently drawing (for TESR)
	 */
	protected TileEntity tileEntity;
	/**
	 * Partial tick time (for TESR)
	 */
	protected float partialTick = 0;
	/**
	 * Get the damage for the block (for TESR)
	 */
	protected boolean getBlockDamage = false;
	/**
	 * Current block destroy progression (for TESR)
	 */
	protected DestroyBlockProgress destroyBlockProgress = null;

	/**
	 * Is at least one vertex been drawn
	 */
	protected boolean vertexDrawn = false;

	public BaseRenderer()
	{
		this.t = Tessellator.instance;
	}

	/**
	 * Gets the partialTick for this frame. Used for TESR and ITEMS
	 * 
	 * @return
	 */
	public float getPartialTick()
	{
		return partialTick;
	}

	// #region set()
	/**
	 * Resets data so this <code>BaseRenderer</code> can be reused.
	 */
	public void reset()
	{
		this.renderType = 0;
		this.drawMode = 0;
		this.world = null;
		this.block = null;
		this.blockMetadata = 0;
		this.x = 0;
		this.y = 0;
		this.z = 0;
		this.overrideTexture = null;
		this.destroyBlockProgress = null;
	}

	/**
	 * Sets informations for this <code>BaseRenderer</code>.
	 * 
	 * @param world
	 * @param block
	 * @param x
	 * @param y
	 * @param z
	 * @param metadata
	 */
	public void set(IBlockAccess world, Block block, int x, int y, int z, int metadata)
	{
		this.world = world;
		this.block = block;
		this.blockMetadata = metadata;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * Sets informations for this <code>BaseRenderer</code>.
	 * 
	 * @param block
	 */
	public void set(Block block)
	{
		set(world, block, x, y, z, blockMetadata);
	}

	/**
	 * Sets informations for this <code>BaseRenderer</code>.
	 * 
	 * @param blockMetadata
	 */
	public void set(int blockMetadata)
	{
		set(world, block, x, y, z, blockMetadata);
	}

	/**
	 * Sets informations for this <code>BaseRenderer</code>.
	 * 
	 * @param block
	 * @param blockMetadata
	 */
	public void set(Block block, int blockMetadata)
	{
		set(world, block, x, y, z, blockMetadata);
	}

	/**
	 * Sets informations for this <code>BaseRenderer</code>.
	 * 
	 * @param x
	 * @param y
	 * @param z
	 */
	public void set(int x, int y, int z)
	{
		set(world, block, x, y, z, blockMetadata);
	}

	/**
	 * Sets informations for this <code>BaseRenderer</code>.
	 * 
	 * @param te
	 * @param partialTick
	 */
	public void set(TileEntity te, float partialTick)
	{
		set(te.getWorldObj(), te.getBlockType(), te.xCoord, te.yCoord, te.zCoord, te.getBlockMetadata());
		this.partialTick = partialTick;
		this.tileEntity = te;
	}

	/**
	 * Sets informations for this <code>BaseRenderer</code>.
	 * 
	 * @param type
	 * @param itemStack
	 */
	public void set(ItemRenderType type, ItemStack itemStack)
	{
		if (itemStack.getItem() instanceof ItemBlock)
			set(Block.getBlockFromItem(itemStack.getItem()));
		this.itemStack = itemStack;
		this.itemRenderType = type;
	}

	// #end

	// #region ISBRH
	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer)
	{
		set(block, metadata);
		renderBlocks = renderer;
		prepare(TYPE_ISBRH_INVENTORY);
		render();
		clean();
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer)
	{
		set(world, block, x, y, z, world.getBlockMetadata(x, y, z));
		renderBlocks = renderer;
		vertexDrawn = false;

		prepare(TYPE_ISBRH_WORLD);
		if (renderer.hasOverrideBlockTexture())
			overrideTexture = renderer.overrideBlockTexture;
		render();
		clean();
		return vertexDrawn;
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId)
	{
		return false;
	}

	// #end ISBRH

	// #region IItemRenderer
	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type)
	{
		return true;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper)
	{
		return true;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data)
	{
		set(type, item);
		prepare(TYPE_ITEM_INVENTORY);
		render();
		clean();
	}

	// #end IItemRenderer

	// #region TESR
	@Override
	public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTick)
	{
		set(te, partialTick);
		prepare(TYPE_TESR_WORLD, x, y, z);
		render();
		if (getBlockDamage)
		{
			destroyBlockProgress = getBlockDestroyProgress();
			if (destroyBlockProgress != null)
			{
				next();

				GL11.glEnable(GL11.GL_BLEND);
				OpenGlHelper.glBlendFunc(GL11.GL_DST_COLOR, GL11.GL_SRC_COLOR, GL11.GL_ONE, GL11.GL_ZERO);
				GL11.glAlphaFunc(GL11.GL_GREATER, 0);
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.5F);

				t.disableColor();
				renderDestroyProgress();
				next();
				GL11.glDisable(GL11.GL_BLEND);
			}
		}
		clean();
	}

	// #end TESR

	// #region prepare()
	/**
	 * Prepares the tessellator and the GL states for the <b>renderType</b>. <b>data</b> is only for TESR.
	 * 
	 * @param renderType
	 * @param data
	 */
	public void prepare(int renderType, double... data)
	{
		this.renderType = renderType;
		if (renderType == TYPE_ISBRH_WORLD)
		{
			tessellatorShift();
		}
		else if (renderType == TYPE_ISBRH_INVENTORY)
		{
			GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
			startDrawing();
		}
		else if (renderType == TYPE_ITEM_INVENTORY)
		{
			GL11.glPushAttrib(GL11.GL_LIGHTING_BIT);
			startDrawing();
		}
		else if (renderType == TYPE_TESR_WORLD)
		{
			GL11.glPushAttrib(GL11.GL_LIGHTING_BIT);
			RenderHelper.disableStandardItemLighting();
			GL11.glEnable(GL11.GL_COLOR_MATERIAL);
			GL11.glShadeModel(GL11.GL_SMOOTH);

			GL11.glPushMatrix();
			GL11.glTranslated(data[0], data[1], data[2]);

			bindTexture(TextureMap.locationBlocksTexture);

			startDrawing();
		}
	}

	/**
	 * Tells the tessellator to start drawing GL_QUADS.
	 */
	public void startDrawing()
	{
		startDrawing(GL11.GL_QUADS);
	}

	/**
	 * Tells the tessellator to start drawing <b>drawMode</b>.
	 * 
	 * @param drawMode
	 */
	public void startDrawing(int drawMode)
	{
		t.startDrawing(drawMode);
		this.drawMode = drawMode;
	}

	/**
	 * Triggers a draw and restart drawing with current <i>drawMode</i>.
	 */
	public void next()
	{
		next(drawMode);
	}

	/**
	 * Triggers a draw and restart drawing with <b>drawMode</b>.
	 * 
	 * @param drawMode
	 */
	public void next(int drawMode)
	{
		draw();
		startDrawing(drawMode);
	}

	/**
	 * Triggers a draw.
	 */
	public void draw()
	{
		t.draw();
	}

	/**
	 * Cleans the current renderer state.
	 */
	public void clean()
	{
		if (renderType == TYPE_ISBRH_WORLD)
		{
			tessellatorUnshift();
		}
		else if (renderType == TYPE_ISBRH_INVENTORY)
		{
			draw();
			GL11.glTranslatef(0.5F, 0.5F, 0.5F);
		}
		else if (renderType == TYPE_ITEM_INVENTORY)
		{
			draw();
			GL11.glPopAttrib();
		}
		else if (renderType == TYPE_TESR_WORLD)
		{
			draw();
			GL11.glPopMatrix();
			GL11.glPopAttrib();
		}
		reset();
	}

	/**
	 * Shifts the tessellator for ISBRH rendering.
	 */
	public void tessellatorShift()
	{
		if (isShifted)
			return;

		isShifted = true;
		t.addTranslation(x, y, z);
	}

	/**
	 * Unshifts the tessellator for ISBRH rendering.
	 */
	public void tessellatorUnshift()
	{
		if (!isShifted)
			return;

		isShifted = false;
		t.addTranslation(-x, -y, -z);
	}

	/**
	 * Enables the blending for the rendering. Ineffective for ISBRH.
	 */
	public void enableBlending()
	{
		if (renderType == TYPE_ISBRH_WORLD)
			return;

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_COLOR_MATERIAL);
		GL11.glShadeModel(GL11.GL_SMOOTH);
		OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
		GL11.glAlphaFunc(GL11.GL_GREATER, 0.0F);

	}

	// #end prepare()

	/**
	 * Renders the block using the default Minecraft rendering system
	 * 
	 * @param renderer
	 */
	public void renderStandard()
	{
		renderStandard(renderBlocks);
	}

	/**
	 * Renders the blocks using the defaul Minecraft rendering system with the specified <b>renderer</b>
	 * 
	 * @param renderer
	 */
	public void renderStandard(RenderBlocks renderer)
	{
		if (renderer == null)
			return;

		boolean b = isShifted;
		if (b)
			tessellatorUnshift();
		renderer.setRenderBoundsFromBlock(block);
		renderer.renderStandardBlock(block, x, y, z);
		if (b)
			tessellatorShift();
	}

	/**
	 * Main rendering method.
	 */
	public void render()
	{}

	/**
	 * Renders the destroy progress manually for TESR. Called if BaseRenderer.destroyBlockProgress is not null
	 */
	public void renderDestroyProgress()
	{}

	/**
	 * Draws a shape with its own parameters
	 * 
	 * @param shape
	 */
	public void drawShape(Shape shape)
	{
		drawShape(shape, null);
	}

	/**
	 * Draws a shape with specified parameters
	 * 
	 * @param shape
	 * @param rp
	 */
	public void drawShape(Shape s, RenderParameters rp)
	{
		if (s == null)
			return;

		shape = s;
		shapeParams = rp != null ? rp : new RenderParameters();
		s.applyMatrix();

		if (shapeParams.applyTexture.get())
			applyTexture(s, shapeParams);

		for (Face face : s.getFaces())
			drawFace(face, face.getParameters());
	}

	/**
	 * Draws a face with its own parameters
	 * 
	 * @param face
	 */
	public void drawFace(Face face)
	{
		drawFace(face, face.getParameters());
	}

	/**
	 * Draws a face with specified parameters.
	 * 
	 * @param f
	 * @param rp
	 */
	protected void drawFace(Face f, RenderParameters rp)
	{
		if (f == null)
			return;

		int vertexCount = f.getVertexes().length;
		if (vertexCount != 4 && renderType == TYPE_ISBRH_WORLD)
		{
			MalisisCore.log.error("[BaseRenderer] Attempting to render a face containing {} vertexes in ISBRH. Ignored", vertexCount);
			return;
		}

		face = f;
		params = RenderParameters.merge(shapeParams, rp);

		if (!shouldRenderFace(face))
			return;

		if (renderType == TYPE_ITEM_INVENTORY || renderType == TYPE_ISBRH_INVENTORY || params.useNormals.get())
			t.setNormal(params.direction.get().offsetX, params.direction.get().offsetY, params.direction.get().offsetZ);

		baseBrightness = getBaseBrightness();

		// vertex position
		if (params.vertexPositionRelativeToRenderBounds.get())
			calcVertexesPosition(getRenderBounds());

		drawVertexes(face.getVertexes());

		if (drawMode == GL11.GL_POLYGON)
			next();
	}

	/***
	 * Draws an array of vertexes (usually <i>Face.getVertexes()</i>)
	 * 
	 * @param vertexes
	 */
	protected void drawVertexes(Vertex[] vertexes)
	{
		for (int i = 0; i < vertexes.length; i++)
		{
			drawVertex(vertexes[i], i);
			if (drawMode == GL11.GL_LINE_STRIP)
				drawVertex(vertexes[i == vertexes.length - 1 ? 0 : i + 1], i);
		}
	}

	/**
	 * Draws a single vertex
	 * 
	 * @param vertex
	 */
	protected void drawVertex(Vertex vertex, int count)
	{
		// brightness
		int brightness = calcVertexBrightness(vertex, (int[][]) params.aoMatrix.get(count));
		vertex.setBrightness(brightness);

		// color
		int color = calcVertexColor(vertex, (int[][]) params.aoMatrix.get(count));
		vertex.setColor(color);

		// alpha
		if (!params.usePerVertexAlpha.get())
			vertex.setAlpha(params.alpha.get());

		t.setColorRGBA_I(vertex.getColor(), vertex.getAlpha());
		t.setBrightness(vertex.getBrightness());

		if (drawMode != GL11.GL_LINE && params.useTexture.get())
			t.addVertexWithUV(vertex.getX(), vertex.getY(), vertex.getZ(), vertex.getU(), vertex.getV());
		else
			t.addVertex(vertex.getX(), vertex.getY(), vertex.getZ());

		vertexDrawn = true;
	}

	/**
	 * Gets the IIcon corresponding to <b>params</b>.
	 * 
	 * @param params
	 * @return
	 */
	protected IIcon getIcon(RenderParameters params)
	{
		IIcon icon = params.icon.get();
		if (params.useCustomTexture.get())
			icon = new MalisisIcon(); //use a generic icon where UVs go from 0 to 1
		else if (overrideTexture != null)
			icon = overrideTexture;
		else if (block != null && icon == null)
		{
			int side = 0;
			if (params.textureSide.get() != null)
				side = params.textureSide.get().ordinal();
			icon = block.getIcon(side, blockMetadata);
		}

		return icon;
	}

	/**
	 * Checks if <b>side</b> should be rendered
	 * 
	 * @param side
	 */
	protected boolean shouldRenderFace(Face face)
	{
		if (renderType != TYPE_ISBRH_WORLD || world == null || block == null)
			return true;
		if (shapeParams != null && shapeParams.renderAllFaces.get())
			return true;
		if (renderBlocks != null && renderBlocks.renderAllFaces == true)
			return true;
		RenderParameters p = face.getParameters();
		if (p.direction.get() == null)
			return true;

		boolean b = block.shouldSideBeRendered(world, x + p.direction.get().offsetX, y + p.direction.get().offsetY, z
				+ p.direction.get().offsetZ, p.direction.get().ordinal());
		return b;
	}

	/**
	 * Applies the texture to the <b>shape</b>. Usually necessary before some shape transformations in conjunction with
	 * RenderParameters.applyTexture set to false to prevent reapplying texture when rendering.
	 * 
	 * @param shape
	 */
	public void applyTexture(Shape shape)
	{
		applyTexture(shape, null);
	}

	/**
	 * Applies the texture to the <b>shape</b> with specified <b>parameters. Usually necessary before some shape transformations in
	 * conjunction with RenderParameters.applyTexture set to false to prevent reapplying texture when rendering.
	 * 
	 * @param shape
	 * @param parameters
	 */
	public void applyTexture(Shape shape, RenderParameters parameters)
	{
		//shape.applyMatrix();
		for (Face f : shape.getFaces())
		{
			RenderParameters params = RenderParameters.merge(f.getParameters(), parameters);
			IIcon icon = getIcon(params);
			if (icon != null)
				f.setTexture(icon, params.flipU.get(), params.flipV.get(), params.interpolateUV.get());
		}
	}

	/**
	 * Calculates the ambient occlusion for a vertex and also apply the side dependent shade.<br />
	 * <b>aoMatrix</b> is the list of block coordinates necessary to compute AO. If it's empty, only the global face shade is applied.<br />
	 * Also, <i>params.colorMultiplier</i> is applied as well.
	 * 
	 * @param vertex
	 * @param aoMatrix
	 */
	protected int calcVertexColor(Vertex vertex, int[][] aoMatrix)
	{
		int color = params.usePerVertexColor.get() ? vertex.getColor() : params.colorMultiplier.get();

		if (drawMode == GL11.GL_LINE)
			return color;
		if (renderType != TYPE_ISBRH_WORLD && renderType != TYPE_TESR_WORLD)
			return color;
		if (!params.calculateAOColor.get() || aoMatrix == null)
			return color;

		float factor = getBlockAmbientOcclusion(world, x + params.direction.get().offsetX, y + params.direction.get().offsetY, z
				+ params.direction.get().offsetZ);

		for (int i = 0; i < aoMatrix.length; i++)
			factor += getBlockAmbientOcclusion(world, x + aoMatrix[i][0], y + aoMatrix[i][1], z + aoMatrix[i][2]);

		factor *= params.colorFactor.get();

		int r = (int) ((color >> 16 & 255) * factor / (aoMatrix.length + 1));
		int g = (int) ((color >> 8 & 255) * factor / (aoMatrix.length + 1));
		int b = (int) ((color & 255) * factor / (aoMatrix.length + 1));

		color = r << 16 | g << 8 | b;

		return color;
	}

	/**
	 * Gets the base brightness for the current face.<br />
	 * If <i>params.useBlockBrightness</i> = false, <i>params.brightness</i>. Else, the brightness is determined base on
	 * <i>params.offset</i> and <i>getBlockBounds()</i>
	 */
	protected int getBaseBrightness()
	{
		if ((renderType != TYPE_ISBRH_WORLD && renderType != TYPE_TESR_WORLD) || world == null || !params.useBlockBrightness.get()
				|| params.direction.get() == null)
			return params.brightness.get();

		double[][] bounds = getRenderBounds();
		ForgeDirection dir = params.direction.get();
		int ox = x + dir.offsetX;
		int oy = y + dir.offsetY;
		int oz = z + dir.offsetZ;

		if (dir == ForgeDirection.WEST && bounds[0][0] > 0)
			ox += 1;
		else if (dir == ForgeDirection.EAST && bounds[1][0] < 1)
			ox -= 1;
		else if (dir == ForgeDirection.NORTH && bounds[0][2] > 0)
			oz += 1;
		else if (dir == ForgeDirection.SOUTH && bounds[1][2] < 1)
			oz -= 1;
		else if (dir == ForgeDirection.DOWN && bounds[0][1] > 0)
			oy += 1;
		else if (dir == ForgeDirection.UP && bounds[1][1] < 1)
			oy -= 1;

		return getMixedBrightnessForBlock(world, ox, oy, oz);
	}

	/**
	 * Calculates the ambient occlusion brightness for a vertex. <b>aoMatrix</b> is the list of block coordinates necessary to compute AO.
	 * Only first 3 blocks are used.<br />
	 * 
	 * @param vertex
	 * @param baseBrightness
	 * @param aoMatrix
	 */
	protected int calcVertexBrightness(Vertex vertex, int[][] aoMatrix)
	{
		if (drawMode == GL11.GL_LINE)
			return baseBrightness;
		if (renderType != TYPE_ISBRH_WORLD && renderType != TYPE_TESR_WORLD)
			return baseBrightness;
		if (!params.calculateBrightness.get() || aoMatrix == null)
			return baseBrightness;

		int[] b = new int[Math.max(3, aoMatrix.length)];

		for (int i = 0; i < aoMatrix.length; i++)
			b[i] += getMixedBrightnessForBlock(world, x + aoMatrix[i][0], y + aoMatrix[i][1], z + aoMatrix[i][2]);

		int brightness = getAoBrightness(b[0], b[1], b[2], baseBrightness);

		return brightness;
	}

	/**
	 * Does the actual brightness calculation (copied from net.minecraft.client.renderer.BlocksRenderer.java)
	 */
	protected int getAoBrightness(int b1, int b2, int b3, int base)
	{
		if (b1 == 0)
			b1 = base;

		if (b2 == 0)
			b2 = base;

		if (b3 == 0)
			b3 = base;

		return b1 + b2 + b3 + base >> 2 & 16711935;
	}

	/**
	 * Gets the block ambient occlusion value. Contrary to base Minecraft code, it's the actual block at the <b>x</b>, <b>y</b> and <b>z</b>
	 * coordinates which is used to get the value, and not value of the block drawn. This allows to have different logic behaviors for AO
	 * values for a block.
	 * 
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 */
	protected float getBlockAmbientOcclusion(IBlockAccess world, int x, int y, int z)
	{
		Block block = world.getBlock(x, y, z);
		if (block == null)
			return 1.0F;

		return block.getAmbientOcclusionLightValue();
	}

	/**
	 * Gets the mix brightness for a block (sky + block source) TODO: handle block light value for light emitting block sources (as base
	 * Minecraft does)
	 * 
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 */
	protected int getMixedBrightnessForBlock(IBlockAccess world, int x, int y, int z)
	{
		// return world.getLightBrightnessForSkyBlocks(x, y, z, 0);
		return world.getBlock(x, y, z).getMixedBrightnessForBlock(world, x, y, z);
	}

	/**
	 * Gets the rendering bounds. If <i>params.useBlockBounds</i> = false, <i>params.renderBounds</i> is used instead of the actual block
	 * bounds.
	 * 
	 * @return
	 */
	protected double[][] getRenderBounds()
	{
		if (block == null || !params.useBlockBounds.get())
			return params.renderBounds.get();

		if (world != null)
			block.setBlockBoundsBasedOnState(world, x, y, z);
		return new double[][] { { block.getBlockBoundsMinX(), block.getBlockBoundsMinY(), block.getBlockBoundsMinZ() },
				{ block.getBlockBoundsMaxX(), block.getBlockBoundsMaxY(), block.getBlockBoundsMaxZ() } };
	}

	/**
	 * Modifies the vertexes coordinates relative to the bounds specified.<br />
	 * Eg : if x = 0.5, minX = 1, maxX = 3, x becomes 2
	 * 
	 * @param bounds
	 */
	protected void calcVertexesPosition(double[][] bounds)
	{
		for (Vertex v : face.getVertexes())
			v.interpolateCoord(bounds);
	}

	/**
	 * Gets and hold reference to damagedBlocks from Minecraft.renderGlobal via reflection.
	 * 
	 * @return
	 */
	protected Map getDamagedBlocks()
	{
		if (damagedBlocks != null)
			return damagedBlocks;

		try
		{
			Field modifiers = Field.class.getDeclaredField("modifiers");
			modifiers.setAccessible(true);

			Field f = ReflectionHelper.findField(RenderGlobal.class, MalisisCore.isObfEnv ? "field_94141_F" : "destroyBlockIcons");
			modifiers.setInt(f, f.getModifiers() & ~Modifier.FINAL);
			damagedIcons = (IIcon[]) f.get(Minecraft.getMinecraft().renderGlobal);

			f = ReflectionHelper.findField(RenderGlobal.class, MalisisCore.isObfEnv ? "field_72738_E" : "damagedBlocks");

			modifiers.setInt(f, f.getModifiers());
			damagedBlocks = (HashMap) f.get(Minecraft.getMinecraft().renderGlobal);

			return damagedBlocks;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Gets the destroy block progress for this rendering. Only used for TESR.
	 * 
	 * @return
	 */
	protected DestroyBlockProgress getBlockDestroyProgress()
	{
		if (renderType != TYPE_TESR_WORLD)
			return null;
		Map damagedBlocks = getDamagedBlocks();
		if (damagedBlocks == null || damagedBlocks.isEmpty())
			return null;

		Iterator iterator = damagedBlocks.values().iterator();
		while (iterator.hasNext())
		{
			DestroyBlockProgress dbp = (DestroyBlockProgress) iterator.next();
			if (isCurrentBlockDestroyProgress(dbp))
				return dbp;
		}
		return null;

	}

	/**
	 * Checks whether the DestroyBlockProgress specified should apply for this TESR.
	 * 
	 * @param dbp
	 * @return
	 */
	protected boolean isCurrentBlockDestroyProgress(DestroyBlockProgress dbp)
	{
		return dbp.getPartialBlockX() == x && dbp.getPartialBlockY() == y && dbp.getPartialBlockZ() == z;
	}

	/**
	 * Creates a renderer of type <b>clazz</b> and sets the renderId for the specified <b>blocks</b>.
	 * 
	 * @param clazz
	 * @param blocks
	 * @return
	 */
	public static <T extends BaseRenderer> T create(Class<T> clazz, IBaseRendering... blocks)
	{
		T r = create(clazz);
		for (IBaseRendering b : blocks)
		{
			if (b != null)
				b.setRenderId(r.getRenderId());
		}
		return r;
	}

	/**
	 * Creates a renderer of type <b>clazz</b>. Sets the renderer static field renderId.
	 * 
	 * @param clazz
	 * @return
	 */
	public static <T extends BaseRenderer> T create(Class<T> clazz)
	{
		try
		{
			T r = clazz.newInstance();
			r.setRenderId(RenderingRegistry.getNextAvailableRenderId());
			return r;
		}
		catch (ReflectiveOperationException e)
		{
			throw new RuntimeException(e);
		}
	}

	/**
	 * Sets the renderId for this <code>BaseRenderer</code>
	 * 
	 * @param id
	 */
	public void setRenderId(int id)
	{
		this.renderId = id;
	}

	@Override
	public int getRenderId()
	{
		return renderId;
	}
}

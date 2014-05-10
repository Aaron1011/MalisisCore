package net.malisis.core.test;

import net.malisis.core.renderer.BaseRenderer;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;

public class Test
{
	TestBlock testBlock;
	StargateBlock sgBlock;
	
	public void preInit() 
	{
		(testBlock = new TestBlock()).setBlockName("testBlock");
		GameRegistry.registerBlock(testBlock, testBlock.getUnlocalizedName().substring(5));
		
		(sgBlock = new StargateBlock()).setBlockName("sgBlock");
		GameRegistry.registerBlock(sgBlock, sgBlock.getUnlocalizedName().substring(5));
		
		GameRegistry.registerTileEntity(StargateTileEntity.class, "stargateTileEntity");
				
		if(FMLCommonHandler.instance().getSide() == Side.CLIENT)
		{
			TestRenderer r = BaseRenderer.create(TestRenderer.class);
			RenderingRegistry.registerBlockHandler(r);
			
			ClientRegistry.bindTileEntitySpecialRenderer(StargateTileEntity.class, r);
		}
		
	}

}

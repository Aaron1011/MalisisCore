package net.malisis.core.client.gui.component.decoration;

import net.malisis.core.client.gui.component.UIComponent;
import net.malisis.core.client.gui.util.Size;
import net.malisis.core.util.RenderHelper;
import net.minecraft.util.ResourceLocation;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;

/**
 * UIImage
 *
 * @author PaleoCrafter
 */
public class UIImage extends UIComponent
{

    private float scale;
    private Size baseSize;
    private ResourceLocation texture;

    public UIImage()
    {
        this(1F, new ResourceLocation("minecraft", "null"));
    }

    public UIImage(float scale, ResourceLocation texture)
    {
        this.scale = scale;
        this.texture = texture;
        try
        {
            BufferedImage temp = ImageIO.read(mc.getResourceManager().getResource(texture).getInputStream());
            this.baseSize = new Size(temp.getWidth(), temp.getHeight());
            if (texture.getResourcePath().toLowerCase().contains("textures/blocks") && texture.getResourcePath().toLowerCase().contains("textures/items"))
                this.setSize((int) (16 * scale), (int) (16 * scale));
            else
                this.setSize((int) (temp.getWidth() * scale), (int) (temp.getHeight() * scale));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void setScale(float scale)
    {
        this.scale = scale;
        this.setSize((int) (baseSize.getWidth() * scale), (int) (baseSize.getHeight() * scale));
    }

    public void setTexture(ResourceLocation texture)
    {
        this.texture = texture;
        try
        {
            BufferedImage temp = ImageIO.read(mc.getResourceManager().getResource(texture).getInputStream());
            this.baseSize = new Size(temp.getWidth(), temp.getHeight());
            if (texture.getResourcePath().toLowerCase().contains("textures/blocks") && texture.getResourcePath().toLowerCase().contains("textures/items"))
                this.setSize((int) (16 * scale), (int) (16 * scale));
            else
                this.setSize((int) (temp.getWidth() * scale), (int) (temp.getHeight() * scale));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void draw(int mouseX, int mouseY)
    {
        if (texture.getResourcePath().toLowerCase().contains("textures/blocks") && texture.getResourcePath().toLowerCase().contains("textures/items"))
        {
            RenderHelper.drawRectangle(texture, getScreenX(), getScreenY(), zIndex, this.getWidth(), this.getHeight(), 0, 0);
        }
        else
        {
            RenderHelper.drawRectangle(texture, getScreenX(), getScreenY(), zIndex, this.getWidth(), this.getHeight(), 0, 0, this.getWidth(), this.getHeight());
        }
    }

    @Override
    public void update(int mouseX, int mouseY)
    {

    }

    @Override
    public String toString()
    {
        return this.getClass().getName() + "[ scale=" + scale + ", texture=" + this.texture + ", " + this.getPropertyString() + " ]";
    }
}

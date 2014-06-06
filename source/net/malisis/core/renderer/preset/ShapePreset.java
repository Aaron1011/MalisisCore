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

package net.malisis.core.renderer.preset;

import net.malisis.core.renderer.element.Face;
import net.malisis.core.renderer.element.Shape;

public class ShapePreset
{
	/** order of vertexes IS important **/
	//@formatter:off
	private static Shape Cube = new Shape(new Face[] { FacePreset.North(), FacePreset.South(), FacePreset.East(), FacePreset.West(), FacePreset.Top(), FacePreset.Bottom() });
	private static Shape CubeSides = new Shape(new Face[] { FacePreset.North(), FacePreset.South(), FacePreset.East(), FacePreset.West() });
	// corners
	private static Shape NorthWest = new Shape(new Face[] { FacePreset.NorthWest(), FacePreset.TriangleTopNorthWest(), FacePreset.TriangleBottomNorthWest() });
	private static Shape NorthEast = new Shape(new Face[] { FacePreset.NorthEast(), FacePreset.TriangleTopNorthEast(), FacePreset.TriangleBottomNorthEast() });
	private static Shape SouthWest = new Shape(new Face[] { FacePreset.SouthWest(), FacePreset.TriangleTopSouthWest(), FacePreset.TriangleBottomSouthWest() });
	private static Shape SouthEast = new Shape(new Face[] { FacePreset.SouthEast(), FacePreset.TriangleTopSouthEast(), FacePreset.TriangleBottomSouthEast() });
	// slopes
	private static Shape TopNorth = new Shape(new Face[] { FacePreset.TopNorth(), FacePreset.TriangleEastTopNorth(), FacePreset.TriangleWestTopNorth() });
	private static Shape TopSouth = new Shape(new Face[] { FacePreset.TopSouth(), FacePreset.TriangleEastTopSouth(), FacePreset.TriangleWestTopSouth() });
	private static Shape TopWest = new Shape(new Face[] { FacePreset.TopWest(), FacePreset.TriangleNorthTopWest(), FacePreset.TriangleSouthTopWest() });
	private static Shape TopEast = new Shape(new Face[] { FacePreset.TopEast(), FacePreset.TriangleNorthTopEast(), FacePreset.TriangleSouthTopEast() });
	// corner slopes
	private static Shape TopNorthWest = new Shape(new Face[] { FacePreset.TopNorthWest() });
	private static Shape TopNorthEast = new Shape(new Face[] { FacePreset.TopNorthEast() });
	private static Shape TopSouthEast = new Shape(new Face[] { FacePreset.TopSouthEast() });
	private static Shape TopSouthWest = new Shape(new Face[] { FacePreset.TopSouthWest() });
	// inverted corner slopes
	private static Shape InvTopNorthWest = new Shape(new Face[] { FacePreset.InvTopNorthWest(), FacePreset.TriangleTopNorthWest() });
	private static Shape InvTopNorthEast = new Shape(new Face[] { FacePreset.InvTopNorthEast(), FacePreset.TriangleTopNorthEast() });
	private static Shape InvTopSouthWest = new Shape(new Face[] { FacePreset.InvTopSouthWest(), FacePreset.TriangleTopSouthWest() });
	private static Shape InvTopSouthEast = new Shape(new Face[] { FacePreset.InvTopSouthEast(), FacePreset.TriangleTopSouthEast() });

	
	public static Shape Cube() { return new Shape(Cube); }
	public static Shape CubeSides() {  return new Shape(CubeSides); }
	public static Shape NorthWest() { 	return new Shape(NorthWest); }
	public static Shape NorthEast() { return new Shape(NorthEast); }
	public static Shape SouthWest() { return new Shape(SouthWest); }
	public static Shape SouthEast() { return new Shape(SouthEast); }
	public static Shape TopNorth() { return new Shape(TopNorth); }
	public static Shape TopSouth() { return new Shape(TopSouth); }
	public static Shape TopWest() { return new Shape(TopWest); }
	public static Shape TopEast() { return new Shape(TopEast); }
	public static Shape TopNorthWest() { return new Shape(TopNorthWest); }
	public static Shape TopNorthEast() { return new Shape(TopNorthEast); }
	public static Shape TopSouthEast() { return new Shape(TopSouthEast); }
	public static Shape TopSouthWest() { return new Shape(TopSouthWest); }
	public static Shape InvTopNorthWest() { return new Shape(InvTopNorthWest); }
	public static Shape InvTopNorthEast() { return new Shape(InvTopNorthEast); }
	public static Shape InvTopSouthWest() { return new Shape(InvTopSouthWest); }
	public static Shape InvTopSouthEast() { return new Shape(InvTopSouthEast); }
	//@formatter:on

	// GUI
	public static Shape GuiElement(int width, int height)
	{
		return new Shape(new Face[] { FacePreset.Gui().factor(width, height, 0) });
	}

	public static Shape GuiXYResizable(int width, int height, int cornerWidth, int cornerHeight)
	{
		width = Math.max(width - 2 * cornerWidth, 0);
		height = Math.max(height - 2 * cornerHeight, 0);
		Face[] faces = new Face[] { FacePreset.Gui().factor(cornerWidth, cornerHeight, 0),
				FacePreset.Gui().factor(width, cornerHeight, 0).translate(cornerWidth, 0, 0),
				FacePreset.Gui().factor(cornerWidth, cornerHeight, 0).translate(cornerWidth + width, 0, 0),
				FacePreset.Gui().factor(cornerWidth, height, 0).translate(0, cornerHeight, 0),
				FacePreset.Gui().factor(width, height, 0).translate(cornerWidth, cornerHeight, 0),
				FacePreset.Gui().factor(cornerWidth, height, 0).translate(cornerWidth + width, cornerHeight, 0),
				FacePreset.Gui().factor(cornerWidth, cornerHeight, 0).translate(0, cornerHeight + height, 0),
				FacePreset.Gui().factor(width, cornerHeight, 0).translate(cornerWidth, cornerHeight + height, 0),
				FacePreset.Gui().factor(cornerWidth, cornerHeight, 0).translate(cornerWidth + width, cornerHeight + height, 0) };

		return new Shape(faces);
	}

	public static Shape GuiXYResizable(int width, int height)
	{
		return GuiXYResizable(width, height, 5, 5);
	}

	public static Shape GuiXResizable(int width, int height, int sideWidth)
	{
		width = Math.max(width - 2 * sideWidth, 0);
		Face[] faces = new Face[] { FacePreset.Gui().factor(sideWidth, height, 0),
				FacePreset.Gui().factor(width, height, 0).translate(sideWidth, 0, 0),
				FacePreset.Gui().factor(sideWidth, height, 0).translate(sideWidth + width, 0, 0) };

		return new Shape(faces);
	}

	public static Shape GuiXResizable(int width, int height)
	{
		return GuiXResizable(width, height, 5);
	}

}

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

package net.malisis.core.util.chunkblock;

import static org.objectweb.asm.Opcodes.*;
import net.malisis.core.asm.AsmHook;
import net.malisis.core.asm.MalisisClassTransformer;
import net.malisis.core.asm.mappings.McpMethodMapping;

import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

/**
 * @author Ordinastie
 *
 */
public class ChunkBlockTransformer extends MalisisClassTransformer
{
	@Override
	public void registerHooks()
	{
		register(updateCoordsHook());
	}

	@SuppressWarnings("deprecation")
	private AsmHook updateCoordsHook()
	{
		McpMethodMapping setBlockState = new McpMethodMapping("setBlockState", "func_177436_a", "net.minecraft.world.chunk.Chunk",
				"(Lnet/minecraft/util/BlockPos;Lnet/minecraft/block/state/IBlockState;)Lnet/minecraft/block/state/IBlockState;");
		McpMethodMapping set = new McpMethodMapping("set", "func_177484_a", "net.minecraft.world.chunk.storage.ExtendedBlockStorage",
				"(IIILnet/minecraft/block/state/IBlockState;)V");

		AsmHook ah = new AsmHook(setBlockState);

		//L:714 extendedblockstorage.set(i, j & 15, k, state);
		//		 L25
		//		    LINENUMBER 714 L25
		//		    ALOAD 11
		//		    ILOAD 3
		//		    ILOAD 4
		//		    BIPUSH 15
		//		    IAND
		//		    ILOAD 5
		//		    ALOAD 2
		//		    INVOKEVIRTUAL net/minecraft/world/chunk/storage/ExtendedBlockStorage.set (IIILnet/minecraft/block/state/IBlockState;)V

		InsnList match = new InsnList();
		match.add(new VarInsnNode(ALOAD, 11));
		match.add(new VarInsnNode(ILOAD, 3));
		match.add(new VarInsnNode(ILOAD, 4));
		match.add(new IntInsnNode(BIPUSH, 15));
		match.add(new InsnNode(IAND));
		match.add(new VarInsnNode(ILOAD, 5));
		match.add(new VarInsnNode(ALOAD, 2));
		match.add(set.getInsnNode(INVOKEVIRTUAL));

		//		if (!ChunkBlockHandler.get().updateCoordinates(this, pos, blockState1, blockState))
		//			return false;

		LabelNode falseLabel = new LabelNode();
		InsnList insert = new InsnList();
		insert.add(new MethodInsnNode(INVOKESTATIC, "net/malisis/core/util/chunkblock/ChunkBlockHandler", "get",
				"()Lnet/malisis/core/util/chunkblock/ChunkBlockHandler;"));
		insert.add(new VarInsnNode(ALOAD, 0));
		insert.add(new VarInsnNode(ALOAD, 1));
		insert.add(new VarInsnNode(ALOAD, 8));
		insert.add(new VarInsnNode(ALOAD, 2));
		insert.add(new MethodInsnNode(
				INVOKEVIRTUAL,
				"net/malisis/core/util/chunkblock/ChunkBlockHandler",
				"updateCoordinates",
				"(Lnet/minecraft/world/chunk/Chunk;Lnet/minecraft/util/BlockPos;Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/block/state/IBlockState;)Z"));
		insert.add(new JumpInsnNode(IFNE, falseLabel));
		insert.add(new InsnNode(ACONST_NULL));
		insert.add(new InsnNode(ARETURN));
		insert.add(falseLabel);

		ah.jumpTo(match).insert(insert);

		return ah;
	}
}

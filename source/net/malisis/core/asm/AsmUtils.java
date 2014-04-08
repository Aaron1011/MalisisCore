package net.malisis.core.asm;

import static org.objectweb.asm.tree.AbstractInsnNode.FIELD_INSN;
import static org.objectweb.asm.tree.AbstractInsnNode.IINC_INSN;
import static org.objectweb.asm.tree.AbstractInsnNode.INT_INSN;
import static org.objectweb.asm.tree.AbstractInsnNode.LDC_INSN;
import static org.objectweb.asm.tree.AbstractInsnNode.METHOD_INSN;
import static org.objectweb.asm.tree.AbstractInsnNode.TYPE_INSN;
import static org.objectweb.asm.tree.AbstractInsnNode.VAR_INSN;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.IincInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

public class AsmUtils
{
	/**
	 * find the method with the given name. If multiple methods with the same
	 * parameters exist, the first one will be returned
	 * 
	 * @param clazz the class
	 * @param name the method name to search for
	 * @return the first method with the given name or null if no such method is
	 *         found
	 */
	public static MethodNode findMethod(ClassNode clazz, String name)
	{
		for (MethodNode method : clazz.methods)
		{
			if (method.name.equals(name))
			{
				return method;
			}
		}
		return null;
	}

	/**
	 * find the method with the given name and method descriptor.
	 * 
	 * @param clazz the class
	 * @param name the method name to search for
	 * @param desc the method descriptor to search for
	 * @return the method with the given name and descriptor or null if no such
	 *         method is found
	 * @see org.objectweb.asm.Type#getMethodDescriptor
	 */
	public static MethodNode findMethod(ClassNode clazz, String name, String desc)
	{
		for (MethodNode method : clazz.methods)
		{
			if (method.name.equals(name) && method.desc.equals(desc))
			{
				return method;
			}
		}
		return null;
	}

	public static AbstractInsnNode findInstruction(MethodNode method, InsnList matches)
	{
		AbstractInsnNode node = method.instructions.getFirst();
		AbstractInsnNode match = matches.getFirst();
		while (node != null)
		{
			if (insnEqual(node, match))
			{
				AbstractInsnNode m = match.getNext();
				AbstractInsnNode n = node.getNext();
				while(m != null && n != null && insnEqual(m, n))
				{
					m = m.getNext();
					n = n.getNext();
				}
				if(m == null)
					return node;					
			}

			node = node.getNext();
		}
		return null;
	}

	public static boolean insnEqual(AbstractInsnNode node1, AbstractInsnNode node2)
	{
		if (node1 == null || node2 == null || node1.getOpcode() != node2.getOpcode())
			return false;

		switch (node2.getType())
		{
			case VAR_INSN:
				return varInsnEqual((VarInsnNode) node1, (VarInsnNode) node2);
			case TYPE_INSN:
				return typeInsnEqual((TypeInsnNode) node1, (TypeInsnNode) node2);
			case FIELD_INSN:
				return fieldInsnEqual((FieldInsnNode) node1, (FieldInsnNode) node2);
			case METHOD_INSN:
				return methodInsnEqual((MethodInsnNode) node1, (MethodInsnNode) node2);
			case LDC_INSN:
				return ldcInsnEqual((LdcInsnNode) node1, (LdcInsnNode) node2);
			case IINC_INSN:
				return iincInsnEqual((IincInsnNode) node1, (IincInsnNode) node2);
			case INT_INSN:
				return intInsnEqual((IntInsnNode) node1, (IntInsnNode) node2);
			default:
				return true;
		}
	}

	public static boolean varInsnEqual(VarInsnNode insn1, VarInsnNode insn2)
	{
		if (insn1.var == -1 || insn2.var == -1)
			return true;

		return insn1.var == insn2.var;
	}

	public static boolean methodInsnEqual(MethodInsnNode insn1, MethodInsnNode insn2)
	{
		return insn1.owner.equals(insn2.owner) && insn1.name.equals(insn2.name) && insn1.desc.equals(insn2.desc);
	}

	public static boolean fieldInsnEqual(FieldInsnNode insn1, FieldInsnNode insn2)
	{
		return insn1.owner.equals(insn2.owner) && insn1.name.equals(insn2.name) && insn1.desc.equals(insn2.desc);
	}

	public static boolean ldcInsnEqual(LdcInsnNode insn1, LdcInsnNode insn2)
	{
		if (insn1.cst.equals("~") || insn2.cst.equals("~"))
			return true;

		return insn1.cst.equals(insn2.cst);
	}

	public static boolean typeInsnEqual(TypeInsnNode insn1, TypeInsnNode insn2)
	{
		if (insn1.desc.equals("~") || insn2.desc.equals("~"))
			return true;

		return insn1.desc.equals(insn2.desc);
	}

	public static boolean iincInsnEqual(IincInsnNode node1, IincInsnNode node2)
	{
		return node1.var == node2.var && node1.incr == node2.incr;
	}

	public static boolean intInsnEqual(IntInsnNode node1, IntInsnNode node2)
	{
		if (node1.operand == -1 || node2.operand == -1)
			return true;

		return node1.operand == node2.operand;
	}

}

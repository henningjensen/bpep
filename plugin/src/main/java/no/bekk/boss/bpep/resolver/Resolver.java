package no.bekk.boss.bpep.resolver;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;

public class Resolver {
	public static String getName(final IField field) {
		return field.getElementName();
	}

	public static String getType(final IField field) {
		try {
			return Signature.toString(field.getTypeSignature());
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static List<IField> findAllFields(final ICompilationUnit compilationUnit) {
		List<IField> fields = new ArrayList<IField>();
		try {
			IType clazz = compilationUnit.getTypes()[0];

			for (IField field : clazz.getFields()) {
				int flags = field.getFlags();
				boolean notStatic = !Flags.isStatic(flags);
				if (notStatic) {
					fields.add(field);
				}
			}

		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		return fields;
	}
}

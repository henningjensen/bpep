package no.bekk.boss.bpep.generator;

import static no.bekk.boss.bpep.resolver.Resolver.getName;
import static no.bekk.boss.bpep.resolver.Resolver.getType;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.NamingConventions;
import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;

public class BuilderGenerator implements Generator {

	private static final String BUILDER_METHOD_PARAMETER_SUFFIX = "Param";

	private final boolean createBuilderConstructor;
	private final boolean createCopyConstructor;
	private final boolean formatSource;

	public void generate(ICompilationUnit cu, List<IField> fields) {

		try {
			removeOldClassConstructor(cu);
			removeOldBuilderClass(cu);

			IBuffer buffer = cu.getBuffer();
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			pw.println();
			pw.println("public static class Builder {");

			IType clazz = cu.getTypes()[0];

			int pos = clazz.getSourceRange().getOffset() + clazz.getSourceRange().getLength() - 1;

			createFieldDeclarations(pw, fields);

			if (createCopyConstructor) {
				createCopyConstructor(pw, clazz, fields);
			}

			createBuilderMethods(pw, fields);
			if (createBuilderConstructor) {
				createPrivateBuilderConstructor(pw, clazz, fields);
				pw.println("}");
			} else {
				createClassBuilderConstructor(pw, clazz, fields);
				pw.println("}");
				createClassConstructor(pw, clazz, fields);
			}
			
			if (formatSource) {
				pw.println();
				buffer.replace(pos, 0, sw.toString());
				String builderSource = buffer.getContents();
			
				TextEdit text = ToolFactory.createCodeFormatter(null).format(CodeFormatter.K_COMPILATION_UNIT, builderSource, 0, builderSource.length(), 0, "\n");
				// text is null if source cannot be formatted
				if (text != null) {
					Document simpleDocument = new Document(builderSource);
					text.apply(simpleDocument);
					buffer.setContents(simpleDocument.get());
				} 
			} else {
				buffer.replace(pos, 0, sw.toString());	
			}
		} catch (JavaModelException e) {
			e.printStackTrace();
		} catch (MalformedTreeException e) {
			e.printStackTrace();
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	private void removeOldBuilderClass(ICompilationUnit cu) throws JavaModelException {
		for (IType type : cu.getTypes()[0].getTypes()) {
			if (type.getElementName().equals("Builder") && type.isClass()) {
				type.delete(true, null);
				break;
			}
		}
	}

	private void removeOldClassConstructor(ICompilationUnit cu) throws JavaModelException {
		for (IMethod method : cu.getTypes()[0].getMethods()) {
			if (method.isConstructor() && method.getParameterTypes().length == 1 && method.getParameterTypes()[0].equals("QBuilder;")) {
				method.delete(true, null);
				break;
			}
		}
	}

	private void createCopyConstructor(PrintWriter pw, IType clazz, List<IField> fields) {
		String clazzName = clazz.getElementName();
		pw.println("public Builder(){}");
		pw.println("public Builder(" + clazzName + " bean){");
		for (IField field : fields) {
			pw.println("this." + getName(field) + "=bean." + getName(field)
					+ ";");
		}
		pw.println("}");

	}

	private void createClassConstructor(PrintWriter pw, IType clazz, List<IField> fields) throws JavaModelException {
		String clazzName = clazz.getElementName();
		pw.println(clazzName + "(Builder builder){");
		for (IField field : fields) {
			pw.println("this." + getName(field) + "=builder." + getName(field) + ";");
		}
		pw.println("}");
	}

	private void createClassBuilderConstructor(PrintWriter pw, IType clazz, List<IField> fields) {
		String clazzName = clazz.getElementName();
		pw.println("public " + clazzName + " build(){");
		pw.println("return new " + clazzName + "(this);\n}");
	}

	private void createPrivateBuilderConstructor(PrintWriter pw, IType clazz, List<IField> fields) {
		String clazzName = clazz.getElementName();
		String clazzVariable = clazzName.substring(0, 1).toLowerCase() + clazzName.substring(1);
		pw.println("public " + clazzName + " build(){");
		pw.println(clazzName + " " + clazzVariable + "=new " + clazzName + "();");
		for (IField field : fields) {
			String name = getName(field);
			pw.println(clazzVariable + "." + name + "=" + name + ";");
		}
		pw.println("return " + clazzVariable + ";\n}");
	}

	private void createBuilderMethods(PrintWriter pw, List<IField> fields) throws JavaModelException {
		for (IField field : fields) {
			String fieldName = getName(field);
			String fieldType = getType(field);
			String baseName = getFieldBaseName(fieldName);
			String parameterName = baseName + BUILDER_METHOD_PARAMETER_SUFFIX;
			pw.println("public Builder " + baseName + "(" + fieldType + " " + parameterName + ") {");
			pw.println("this." + fieldName + "=" + parameterName + ";");
			pw.println("return this;\n}");
		}
	}

	private String getFieldBaseName(String fieldName) {
		IJavaProject javaProject = JavaCore.create(ResourcesPlugin.getWorkspace().getRoot().getProject());
		return NamingConventions.getBaseName(NamingConventions.VK_INSTANCE_FIELD, fieldName, javaProject);
	}

	private void createFieldDeclarations(PrintWriter pw, List<IField> fields) throws JavaModelException {
		for (IField field : fields) {
			pw.println(getType(field) + " " + getName(field) + ";");
		}
	}

	public static class Builder {
		boolean createBuilderConstructor;
		boolean createCopyConstructor;
		boolean formatSource;

		public Builder createBuilderConstructor(boolean createBuilderConstructorParam) {
			this.createBuilderConstructor = createBuilderConstructorParam;
			return this;
		}

		public Builder createCopyConstructor(boolean createCopyConstructorParam) {
			this.createCopyConstructor = createCopyConstructorParam;
			return this;
		}

		public Builder formatSource(boolean formatSourceParam) {
			this.formatSource = formatSourceParam;
			return this;
		}

		public BuilderGenerator build() {
			return new BuilderGenerator(this);
		}
	}

	BuilderGenerator(Builder builder) {
		this.createBuilderConstructor = builder.createBuilderConstructor;
		this.createCopyConstructor = builder.createCopyConstructor;
		this.formatSource = builder.formatSource;
	}
}

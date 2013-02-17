package no.bekk.boss.bpep.generator;

import java.util.List;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.JavaModelException;

public class MockGenerator implements Generator {

    public void generate(ICompilationUnit compilationUnit, boolean createPrivateConstructor, boolean createCopyConstructor, boolean formatSource, List<IField> selectedFields) {
        try {
            System.out.println(compilationUnit.getSource());
        } catch (JavaModelException e) {
            e.printStackTrace();
        }
    }

}

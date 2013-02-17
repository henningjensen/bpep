package no.bekk.boss.bpep.generator;

import java.util.List;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;

public interface Generator {
    void generate(ICompilationUnit compilationUnit, List<IField> selectedFields);
}

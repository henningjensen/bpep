package no.bekk.boss.bpep.generator;

import java.util.List;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;

public interface Generator {
    void generate(ICompilationUnit compilationUnit, boolean createBuilderConstructor, boolean formatSource, List<IField> selectedFields);
    List<IField> findAllFIelds(ICompilationUnit compilationUnit);
    String getName(IField field);
    String getType(IField field);
}

package no.bekk.boss.bpep.regex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum FileExtension {
    JAVA_EXTENSION(".java$");
    
    private Pattern pattern;
    
    private FileExtension(String regex) {
        pattern = Pattern.compile(regex);
    }
    
    public String removedFrom(String str) {
        Matcher matcher = pattern.matcher(str);
        if(matcher.find()) {
            return matcher.replaceAll("");
        }
        return str;
    }
    
}

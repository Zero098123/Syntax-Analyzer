import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class JackAnalyzer {
    static List<JackTokenizer> jackTokenizerList = new ArrayList<>();

    public static void main(String[] args) {
        String filePath = args[0];
        traverseFolder(filePath);
        for (JackTokenizer jackTokenizer : jackTokenizerList) {
            //generate T.xml
            // new CompilationEngine(jackTokenizer,"");
            System.out.println(jackTokenizer.getFileName() + " is being compiled");
            jackTokenizer.initPointer();
            new XMLCompilationEngine(jackTokenizer);
        }
    }

    public static void traverseFolder(String path) {
        File file = new File(path);
        if (file.exists()) {
            if (file.getName().endsWith(".jack")) {
                JackTokenizer jackTokenizer = new JackTokenizer(path);
                jackTokenizerList.add(jackTokenizer);
                return;
            }
            File[] files = file.listFiles();
            if (files.length == 0) {
                System.err.println("The folder is empty!");
                return;
            } else {
                for (File item : files) {
                    String filePath = item.getAbsolutePath();
                    if (filePath.endsWith(".jack")) {
                        JackTokenizer jackTokenizer = new JackTokenizer(filePath);
                        jackTokenizerList.add(jackTokenizer);
                    }
                }
            }
        } else {
            System.err.println("no .jack file!");
        }
    }
}


import javax.tools.*;
import com.sun.source.util.JavacTask;
import java.nio.file.*;
import java.util.*;

/** JDK parser only: deliberately not a substitute for Android compilation. */
public final class ParseJava {
    public static void main(String[] args) throws Exception {
        JavaCompiler compiler=ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> diagnostics=new DiagnosticCollector<>();
        try(StandardJavaFileManager manager=compiler.getStandardFileManager(diagnostics,null,null)) {
            List<java.io.File> files=new ArrayList<>();
            try(var paths=Files.walk(Path.of(args.length==0?"src":args[0]))) {
                paths.filter(p->p.toString().endsWith(".java")).forEach(p->files.add(p.toFile()));
            }
            JavacTask task=(JavacTask)compiler.getTask(null,manager,diagnostics,List.of("-proc:none"),null,manager.getJavaFileObjectsFromFiles(files));
            task.parse();int errors=0;
            for(var diagnostic:diagnostics.getDiagnostics())if(diagnostic.getKind()==Diagnostic.Kind.ERROR){System.out.println(diagnostic);errors++;}
            System.out.println("Parsed "+files.size()+" Java files; syntax errors="+errors);
            if(errors>0)System.exit(1);
        }
    }
}

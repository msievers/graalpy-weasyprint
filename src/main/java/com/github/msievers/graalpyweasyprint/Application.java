package com.github.msievers.graalpyweasyprint;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;

import java.nio.file.Paths;

public class Application  {

    public static final String PYTHON = "python";

    public static void main(String[] args) {

        final String VENV_EXECUTABLE = Application.class.getClassLoader()
            .getResource(Paths.get("vfs", "venv", "bin", "graalpy").toString()).getPath();

        final Context.Builder contextBuilder = Context.newBuilder(PYTHON)
            .allowAllAccess(true)
            .option("python.Executable", VENV_EXECUTABLE)
            .option("python.ForceImportSite", "true");

        try (final Context context = contextBuilder.build()) {

            System.out.println("Context started");

            // the nonWorkingSource will create the fatal error
            context.eval(nonWorkingSource);

            // the workingSource will be executed without errors
//            context.eval(workingSource);

        } finally {
            System.out.println("Closing context");
        }
    }

    private static final Source workingSource = Source.create(PYTHON, """
        from weasyprint import HTML, CSS
        from weasyprint.text.fonts import FontConfiguration

        font_config = FontConfiguration()
        html = HTML(string='<h1>My title</h1>')
        css = CSS(string='''
            @font-face {
                font-family: Gentium;
                src: url(https://example.com/fonts/Gentium.otf);
            }
            h1 { font-family: Gentium }''', font_config=font_config)
        html.write_pdf(
            '/tmp/example.pdf', stylesheets=[css],
            font_config=font_config)
        """);

    /**
     * The non-working source differs from the working source in the inline CSS being used
     */
    private static final Source nonWorkingSource = Source.create(PYTHON, """
        from weasyprint import HTML, CSS
        from weasyprint.text.fonts import FontConfiguration

        font_config = FontConfiguration()
        html = HTML(string='<h1>My title</h1>')
        css = CSS(string='''
            @page {
              font-family: Pacifico;
              margin: 3cm;
              @bottom-left {
                color: #1ee494;
                content: '♥ Thank you!';
              }
            }''', font_config=font_config)
        html.write_pdf(
            '/tmp/example.pdf', stylesheets=[css],
            font_config=font_config)
        """);
}

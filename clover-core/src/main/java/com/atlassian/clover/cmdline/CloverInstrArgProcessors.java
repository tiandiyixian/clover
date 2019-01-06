package com.atlassian.clover.cmdline;

import clover.org.apache.commons.lang3.StringUtils;
import com.atlassian.clover.Logger;
import com.atlassian.clover.api.CloverException;
import com.atlassian.clover.api.command.ArgProcessor;
import com.atlassian.clover.cfg.instr.MethodContextDef;
import com.atlassian.clover.cfg.instr.StatementContextDef;
import com.atlassian.clover.cfg.instr.java.JavaInstrumentationConfig;
import com.atlassian.clover.cfg.instr.java.LambdaInstrumentation;
import com.atlassian.clover.remote.DistributedConfig;

import java.io.File;
import java.util.Locale;

public class CloverInstrArgProcessors {
    protected static void usage(String message) {
        System.err.println("  *** ERROR: " + message);
    }

    public static ArgProcessor<JavaInstrumentationConfig> SrcDir = new ArgProcessor<JavaInstrumentationConfig>() {
        @Override
        public boolean matches(String[] args, int i) {
            return args[i].equals("-s") || args[i].equals("--srcdir");
        }

        @Override
        public int process(String[] args, int i, JavaInstrumentationConfig cfg) {
            i++;
            cfg.setSourceDir(new File(args[i]).getAbsoluteFile());
            return i;
        }

        @Override
        public String help() {
            return "    -s, --srcdir <dir>\t\t Directory containing source files to be instrumented. If omitted\n" +
                    "\t\t\t\t individual source files should be specified on the command line.";
        }
    };

    public static ArgProcessor<JavaInstrumentationConfig> DestDir = new ArgProcessor<JavaInstrumentationConfig>() {
        @Override
        public boolean matches(String[] args, int i) {
            return args[i].equals("-d") || args[i].equals("--destdir");
        }

        @Override
        public int process(String[] args, int i, JavaInstrumentationConfig cfg) {
            i++;
            cfg.setDestDir(new File(args[i]).getAbsoluteFile());
            return i;
        }

        @Override
        public String help() {
            return "    -d, --destdir <dir>\t\t Directory where Clover should place the instrumented sources.";
        }
    };

    public static ArgProcessor<JavaInstrumentationConfig> InitString = new ArgProcessor<JavaInstrumentationConfig>() {
        @Override
        public boolean matches(String[] args, int i) {
            return args[i].equals("-i") || args[i].equals("--initstring");
        }

        @Override
        public int process(String[] args, int i, JavaInstrumentationConfig cfg) {
            i++;
            cfg.setInitstring(args[i]);
            return i;
        }

        @Override
        public String help() {
            return "    -i, --initstring <file>\t Clover initstring. This is the path to the dbfile that"
                    + "\t\t\t\t will be used to construct/update to store coverage data.";
        }
    };

    public static ArgProcessor<JavaInstrumentationConfig> DistributedCoverage = new ArgProcessor<JavaInstrumentationConfig>() {
        @Override
        public boolean matches(String[] args, int i) {
            return args[i].equals("-dc") || args[i].equals("--distributedCoverage");
        }

        @Override
        public int process(String[] args, int i, JavaInstrumentationConfig cfg) {
            i++;
            cfg.setDistributedConfig(new DistributedConfig(args[i]));
            return i;
        }

        @Override
        public String help() {
            return "    -dc, --distributedCoverage <string>\t Configuration for recording distributed pre-test coverage.\n"
                    + "\t\t\t\t Valid keys and default values are: ON | OFF | " + new DistributedConfig();
        }
    };

    public static ArgProcessor<JavaInstrumentationConfig> Relative = new ArgProcessor<JavaInstrumentationConfig>() {
        @Override
        public boolean matches(String[] args, int i) {
            return args[i].equals("-r") || args[i].equals("--relative");
        }

        @Override
        public int process(String[] args, int i, JavaInstrumentationConfig cfg) {
            cfg.setRelative(true);
            return i;
        }

        @Override
        public String help() {
            return "    -r, --relative\t If specified, the initstring is treated as a relative path, "
                    + "\t\t\t\t rather than being converted to an absolute path."
                    + "\t\t\t\t This is useful for distributed testing environments.";
        }
    };

    public static ArgProcessor<JavaInstrumentationConfig> FlushPolicy = new ArgProcessor<JavaInstrumentationConfig>() {
        @Override
        public boolean matches(String[] args, int i) {
            return args[i].equals("-p") || args[i].equals("--flushpolicy");
        }

        @Override
        public int process(String[] args, int i, JavaInstrumentationConfig cfg) {
            i++;
            try {
                cfg.setFlushPolicyFromString(args[i]);
            } catch (CloverException e) {
                usage(e.getMessage());
            }
            return i;
        }

        @Override
        public String help() {
            return "   -p, --flushpolicy <policy>\t Set the flushpolicy Clover will use during coverage recording."
                    + "\t\t\t\t legal values are \"directed\",\"interval\",\"threaded\". Default is \"directed\". If"
                    + "\t\t\t\t either \"interval\" or \"threaded\" policies is used, the flushinterval must also be set"
                    + "\t\t\t\t using the -f option.";
        }
    };

    public static ArgProcessor<JavaInstrumentationConfig> FlushInterval = new ArgProcessor<JavaInstrumentationConfig>() {
        @Override
        public boolean matches(String[] args, int i) {
            return args[i].equals("-f") || args[i].equals("--flushinterval");
        }

        @Override
        public int process(String[] args, int i, JavaInstrumentationConfig cfg) {
            i++;
            try {
                final int flushInterval = Integer.parseInt(args[i]);
                if (flushInterval <= 0) {
                    usage("Invalid flush interval. Should be a positive integer.");
                } else {
                    cfg.setFlushInterval(flushInterval);
                }
            } catch (NumberFormatException e) {
                usage("Invalid flush interval. Should be a positive integer.");
            }
            return i;
        }

        @Override
        public String help() {
            return "   -f, --flushinterval <int>\t Set the interval between flushes (in millisecs). Only applies to\n"
                    + "\t\t\t\t \"interval\" or \"threaded\" flush policies.";
        }
    };

    public static ArgProcessor<JavaInstrumentationConfig> Encoding = new ArgProcessor<JavaInstrumentationConfig>() {
        @Override
        public boolean matches(String[] args, int i) {
            return args[i].equals("-e") || args[i].equals("--encoding");
        }

        @Override
        public int process(String[] args, int i, JavaInstrumentationConfig cfg) {
            i++;
            cfg.setEncoding(args[i]);
            return i;
        }

        @Override
        public String help() {
            return "   -e, --encoding <encoding>\t Set the File encoding to use when reading source files.";
        }
    };

    public static ArgProcessor<JavaInstrumentationConfig> InstrStrategy = new ArgProcessor<JavaInstrumentationConfig>() {
        @Override
        public boolean matches(String[] args, int i) {
            return args[i].equals("--instrumentation");
        }

        @Override
        public int process(String[] args, int i, JavaInstrumentationConfig cfg) {
            i++;
            cfg.setInstrStrategy(args[i]);
            return i;
        }

        @Override
        public String help() {
            return "   --instrumentation <string>\t Set the instrumentation strategy. Valid values are \"field\" and\n"
                    + "\t\t\t\t \"class\". Default is \"class\".";
        }
    };

    public static ArgProcessor<JavaInstrumentationConfig> InstrLevel = new ArgProcessor<JavaInstrumentationConfig>() {
        @Override
        public boolean matches(String[] args, int i) {
            return args[i].equals("--instrlevel");
        }

        @Override
        public int process(String[] args, int i, JavaInstrumentationConfig cfg) {
            i++;
            cfg.setInstrLevelStrategy(args[i]);
            return i;
        }

        @Override
        public String help() {
            return "    --instrlevel <string>\t Set the instrumentation level. Valid values are \"statement\" and"
                    + "\t\t\t\t \"method\". Default is \"statement\".";
        }
    };

    public static ArgProcessor<JavaInstrumentationConfig> InstrLambda = new ArgProcessor<JavaInstrumentationConfig>() {
        @Override
        public boolean matches(String[] args, int i) {
            return args[i].equals("--instrlambda");
        }

        @Override
        public int process(String[] args, int i, JavaInstrumentationConfig cfg) {
            i++;
            try {
                cfg.setInstrumentLambda(LambdaInstrumentation.valueOf(args[i].toUpperCase(Locale.ENGLISH)));
            } catch (IllegalArgumentException ex) {
                usage("Invalid value: " + args[i] + ". " + ex.getMessage());
            }
            return i;
        }

        @Override
        public String help() {
            return "    --instrlambda <string>\t Set whether lambda functions shall be instrumented. Valid values are: "
                    + StringUtils.join(LambdaInstrumentation.values(), ", ").toLowerCase(Locale.ENGLISH) + "."
                    + "\t\t\t\t Default is " + LambdaInstrumentation.NONE + ".";
        }
    };

    public static ArgProcessor<JavaInstrumentationConfig> SourceLevel = new ArgProcessor<JavaInstrumentationConfig>() {
        @Override
        public boolean matches(String[] args, int i) {
            return args[i].equals("--source");
        }

        @Override
        public int process(String[] args, int i, JavaInstrumentationConfig cfg) {
            i++;
            cfg.setSourceLevel(args[i]);
            return i;
        }

        @Override
        public String help() {
            return "    --source <level>\t\t Set the language level for Clover to use when parsing files.";
        }
    };

    public static ArgProcessor<JavaInstrumentationConfig> RecordTestResults = new ArgProcessor<JavaInstrumentationConfig>() {
        @Override
        public boolean matches(String[] args, int i) {
            return args[i].equals("--recordTestResults");
        }

        @Override
        public int process(String[] args, int i, JavaInstrumentationConfig cfg) {
            i++;
            cfg.setRecordTestResults(Boolean.parseBoolean(args[i]));
            return i;
        }

        @Override
        public String help() {
            return "    --recordTestResults <true|false> \t If set to false, Clover will not record test results.";
        }
    };

    public static ArgProcessor<JavaInstrumentationConfig> DontQualifyJavaLang = new ArgProcessor<JavaInstrumentationConfig>() {
        @Override
        public boolean matches(String[] args, int i) {
            return args[i].equals("--dontFullyQualifyJavaLang");
        }

        @Override
        public int process(String[] args, int i, JavaInstrumentationConfig cfg) {
            cfg.setFullyQualifyJavaLang(false);
            return i;
        }

        @Override
        public String help() {
            return "    --dontFullyQualifyJavaLang \t\t If set, then java.lang will not be used in instrumented source.";
        }
    };

    public static ArgProcessor<JavaInstrumentationConfig> MethodContext = new ArgProcessor<JavaInstrumentationConfig>() {
        @Override
        public boolean matches(String[] args, int i) {
            return args[i].equals("-mc") || args[i].equals("--methodContext");
        }

        @Override
        public int process(String[] args, int i, JavaInstrumentationConfig cfg) {
            i++;
            try {
                // format: name=value, where value may have one or more '='
                cfg.addMethodContext(parseContextDef(args[i]));
            } catch (CloverException e) {
                usage("Could not parse custom method context definition: " + args[i] + ". " + e.getMessage());
            }
            return i;
        }

        @Override
        public String help() {
            return "    -mc --methodContext <name>=<regexp> \t\t Defines a single custom method context. May be supplied more than once. (\\ may be needed to prevent shell expansion)";
        }

        private MethodContextDef parseContextDef(String line) throws CloverException {
            final int index = line.indexOf('=');
            if (index <= 0 || (index+1 == line.length())) {
                throw new CloverException("Custom context definitions must be of the form: 'name=regexp'");
            }
            final MethodContextDef contextDef = new MethodContextDef();
            contextDef.setName(line.substring(0, index));
            contextDef.setRegexp(line.substring(index+1));
            return contextDef;
        }
    };

    public static ArgProcessor<JavaInstrumentationConfig> StatementContext = new ArgProcessor<JavaInstrumentationConfig>() {
        @Override
        public boolean matches(String[] args, int i) {
            return args[i].equals("-sc") || args[i].equals("--statementContext");
        }

        @Override
        public int process(String[] args, int i, JavaInstrumentationConfig cfg) {
            i++;
            try {
                // expected format: name=value, where value may have one or more '='
                cfg.addStatementContext(parseContextDef(args[i]));
            } catch (CloverException e) {
                usage("Could not parse custom statement context definition: " + args[i] + ". " + e.getMessage());
            }
            return i;
        }

        @Override
        public String help() {
            return "    -sc --statementContext <name>=<regexp>\t\t Defines a single custom statement context. May be supplied more than once.(\\ may be needed to prevent shell expansion)";
        }

        private StatementContextDef parseContextDef(String line) throws CloverException {
            final int index = line.indexOf('=');
            if (index <= 0 || (index+1 == line.length())) {
                throw new CloverException("Custom context definitions must be of the form: 'name=regexp'");
            }
            final StatementContextDef contextDef = new StatementContextDef();
            contextDef.setName(line.substring(0, index));
            contextDef.setRegexp(line.substring(index+1));
            return contextDef;
        }
    };

    public static ArgProcessor<JavaInstrumentationConfig> Verbose = new ArgProcessor<JavaInstrumentationConfig>() {
        @Override
        public boolean matches(String[] args, int i) {
            return args[i].equals("-v") || args[i].equals("--verbose");
        }

        @Override
        public int process(String[] args, int i, JavaInstrumentationConfig cfg) {
            Logger.setVerbose(true);
            return i;
        }

        @Override
        public String help() {
            return "    -v, --verbose\t\t Enable verbose logging.";
        }
    };

    public static ArgProcessor<JavaInstrumentationConfig> JavaSourceFile = new ArgProcessor<JavaInstrumentationConfig>() {

        @Override
        public boolean matches(String[] args, int i) {
            return args[i].endsWith(".java");
        }

        @Override
        public int process(String[] args, int i, JavaInstrumentationConfig cfg) {
            cfg.addSourceFile(args[i]);
            return i;
        }

        @Override
        public String help() {
            return "   *.java\\t\\t Specific source file to be instrumented.";
        }
    };

}
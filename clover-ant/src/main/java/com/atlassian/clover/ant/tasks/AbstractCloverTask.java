package com.atlassian.clover.ant.tasks;

import com.atlassian.clover.ant.AntLogger;
import com.atlassian.clover.CloverNames;
import com.atlassian.clover.CloverStartup;
import com.atlassian.clover.Logger;
import com.atlassian.clover.PrematureLibraryLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import java.io.File;

public abstract class AbstractCloverTask extends Task {
    protected AntInstrumentationConfig config;
    protected boolean debug;

    @Override
    public void init() throws BuildException {
        super.init();
        Logger.setInstance(new AntLogger(getProject(), this));
        config = new AntInstrumentationConfig(getProject());
        PrematureLibraryLoader.doOnce();
    }

    public String getInitString() {
        return config.getInitString(); 
    }

    public void setInitString(String initString) {
        config.setInitstring(initString);
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
        Logger.setDebug(debug); // no way to detect the -debug flag from Ant.
    }

    protected String resolveInitString() throws BuildException {
        return config.resolveInitString();
    }

    @Override
    public final void execute() {
        if (config == null) {
            throw new BuildException("Configuration is null. This Task not initialiased correctly. " +
                                     "Please ensure init() is called before execute().");
        }
        
        String antLicensePath = getProject().getProperty(CloverNames.PROP_LICENSE_PATH);
        if (antLicensePath != null && antLicensePath.length() > 0) {
            System.setProperty(CloverNames.PROP_LICENSE_PATH, antLicensePath);
        }

        CloverStartup.loadLicense(Logger.getInstance());

        if (validate()) {
            cloverExecute();
        }
    }

    public boolean validate() {
        return true; // no-op validation by default
    }

    public abstract void cloverExecute();

    /**
     * Temporary directory into which instrumented code is to be written
     *
     * @param tmpDir the temp directory to use to write instructmented code.
     */
    public void setTmpDir(File tmpDir) {
        config.setTmpDir(tmpDir);
    }
}

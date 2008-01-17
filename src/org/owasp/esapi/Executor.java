/**
 * OWASP Enterprise Security API (ESAPI)
 * 
 * This file is part of the Open Web Application Security Project (OWASP)
 * Enterprise Security API (ESAPI) project. For details, please see
 * http://www.owasp.org/esapi.
 *
 * Copyright (c) 2007 - The OWASP Foundation
 * 
 * The ESAPI is published by OWASP under the LGPL. You should read and accept the
 * LICENSE before you use, modify, and/or redistribute this software.
 * 
 * @author Jeff Williams <a href="http://www.aspectsecurity.com">Aspect Security</a>
 * @created 2007
 */
package org.owasp.esapi;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.owasp.esapi.errors.ExecutorException;

/**
 * Reference implementation of the Executor interface. This implementation is very restrictive. Commands must exactly
 * equal the canonical path to an executable on the system. Valid characters for parameters are alphanumeric,
 * forward-slash, and dash.
 * 
 * @author Jeff Williams (jeff.williams .at. aspectsecurity.com) <a href="http://www.aspectsecurity.com">Aspect Security</a>
 * @since June 1, 2007
 * @see org.owasp.esapi.interfaces.IExecutor
 */
public class Executor implements org.owasp.esapi.interfaces.IExecutor {

    /** The instance. */
    private static Executor instance = new Executor();

    /** The logger. */
    private static final Logger logger = Logger.getLogger("ESAPI", "Executor");

    /**
     * Hide the constructor for the Singleton pattern.
     */
    private Executor() {
        // hidden
    }

    /**
     * Gets the single instance of Executor.
     * 
     * @return single instance of Executor
     */
    public static Executor getInstance() {
        return instance;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.owasp.esapi.interfaces.IExecutor#executeSystemCommand(java.lang.String, java.util.List, java.io.File,
     * int)
     */
    public String executeSystemCommand(File executable, List params, File workdir, int timeoutSeconds) throws ExecutorException {
        BufferedReader br = null;
        try {
            logger.logTrace(Logger.SECURITY, "Initiating executable: " + executable + " " + params + " in " + workdir);
            Validator validator = Validator.getInstance();

            // command must exactly match the canonical path and must actually
            // exist on the file system
            if (!executable.getCanonicalPath().equals(executable.getPath())) {
                throw new ExecutorException("Execution failure", "Invalid path to executable file: " + executable);
            }
            if (!executable.exists()) {
                throw new ExecutorException("Execution failure", "No such executable: " + executable);
            }

            // parameters must only contain alphanumerics, dash, and forward slash
            // FIXME: ENHANCE make configurable regexes? Update comments!
            Iterator i = params.iterator();
            while (i.hasNext()) {
                String param = (String) i.next();
                if (!validator.isValidDataFromBrowser("fixme", "SystemCommand", param)) {
                    throw new ExecutorException("Execution failure", "Illegal characters in parameter to executable: " + param);
                }

            }

            // working directory must exist
            if (!workdir.exists()) {
                throw new ExecutorException("Execution failure", "No such working directory for running executable: " + workdir.getPath());
            }

            params.add(0, executable.getCanonicalPath());
            ProcessBuilder pb = new ProcessBuilder(params);
            Map env = pb.environment();
            // Security check - clear environment variables!
            env.clear();
            pb.directory(workdir);
            pb.redirectErrorStream(true);
            // FIXME: ENHANCE need a timer
            Process process = pb.start();
            InputStream is = process.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            br = new BufferedReader(isr);
            StringBuffer sb = new StringBuffer();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            logger.logTrace(Logger.SECURITY, "System command successful: " + params);
            return sb.toString();
        } catch (Exception e) {
            throw new ExecutorException("Execution failure", "Exception thrown during execution of system command: " + e.getMessage(), e);
        } finally {
            try {
                if ( br != null ) {
                    br.close();
                }
            } catch (IOException e) {
                // give up
            }
        }
    }

}

package com.q1httpserver.browsers;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/***
 * The exceptions generated by this class are not caught here and handled at the
 * path handler to translate to right error messages
 */
public abstract class AbstractBrowser implements Browser {
    private static final int DEFAULT_TIMEOUT = 10;
    protected boolean isRunning = false;
    protected Process process;
    @Override
    public void start() throws BrowserException, IOException {
        //This logic is applied to avoid wasting time doing a system call
        //if the object state itself says that the browser is not started
        if(!isRunning || !isActuallyRunning()){
            startBrowser();
        }
        else{
            throw(new BrowserException("Internal error, the specified browser object is already running"));
        }
    }

    private void startBrowser() throws IOException {
        //If the process object is not null and if it is running
        //the execution wont reach here. Still calling destroy to be safe
        if(null != process){
            try{
                process.destroy();
            }
            catch (Exception e){
                //ignore this, nothing to be done
            }
        }
        process = new ProcessBuilder(getExecutablePath()).start();
        isRunning = true;
    }

    private boolean isActuallyRunning() {
        if(null != process){
            try{
                final int ret = process.exitValue();
            }
            catch (IllegalThreadStateException ex){
                return true;
            }
        }
        return false;
    }

    //TODO: STOP testing not yet complete
    @Override
    public void stop() {
        if(null != process){
            sendShutdownSignal();
            try {
                //To avoid blocking of the HTTP thread here
                //a better design can be to add these browser objects to a timer pool
                Thread.sleep(getTimeOut());
            process.destroy();
            doForceKill();
            isRunning = false;
            } catch (InterruptedException e) {
                //nothing to do here.
            } catch (IOException e) {
                //nothing to do here.
            }
        }
    }

    private void doForceKill() throws IOException {
        //This destroys all processes of the browser in that user session
        //There is a potential danger that if the user is superuser (root)
        //this can result in the browser getting terminated for all user logins
        //Need a better handling for this.
        final OSType osType = getOSType();
        String killCommand = "";
        switch (osType){
            case WINDOWS:
                killCommand = "taskkill /IM";
                break;
            case NIX:
            case MAC:
                killCommand = "killall";
                break;
            default:
                killCommand = "killall";
        }
        new ProcessBuilder(killCommand).start();
    }

    //These are abstracted here as it is specific to this AbstractBrowser implementation
    public abstract void sendShutdownSignal();

    public abstract String getMacExecutablePath();
    public abstract String getWinExecutablePath();
    public abstract String getNixExecutablePath();

    public abstract void clearMacCache();
    public abstract void clearWinCache();
    public abstract void clearNixCache();

    @Override
    public void clearCache() {
        final OSType osType = getOSType();
        switch (osType){
            case MAC:
                clearMacCache();
            case WINDOWS:
                clearWinCache();
            case NIX:
                clearNixCache();
            default:
                clearNixCache();
        }
    }

    public String getExecutablePath(){
        final OSType osType = getOSType();
        switch (osType){
            case MAC:
                return getMacExecutablePath();
            case WINDOWS:
                return getWinExecutablePath();
            case NIX:
                return getNixExecutablePath();
            default:
                return getNixExecutablePath();
        }
    }
    protected int getTimeOut() {
        return DEFAULT_TIMEOUT;
    }
    protected OSType getOSType(){
        String type = System.getProperty("os.name");
        if (type.toLowerCase().contains("win")) {
            return OSType.WINDOWS;
        }
        if (type.toLowerCase().contains("mac")) {
            return OSType.MAC;
        }
        //defaults to *NIX
        return OSType.NIX;
    }

    protected void safeDeletePath(String path){
        try
        {
            final File dir = new File(path);
            if(dir.exists()){
                FileUtils.deleteDirectory(dir);
            }
        }
        catch (Exception ex){
            //Not of much interest
        }
    }

    protected enum OSType{
        WINDOWS,
        MAC,
        NIX
    }
}
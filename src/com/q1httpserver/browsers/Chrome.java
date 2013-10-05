package com.q1httpserver.browsers;

public class Chrome extends AbstractBrowser {
    @Override
    public void sendShutdownSignal() {
        //Todo Implement a method to gracefully the process if there is a way
    }

    @Override
    public String getMacExecutablePath() {
        //More intelligent logic can be implemented here if Mac using the package information in Mac to find the binary
        //This path is the standard path in most (which is mostly okay if we are using standard servers built using pre-built images
        return "/Applications/Google Chrome.app/Contents/MacOS/Google Chrome";
    }

    @Override
    public String getWinExecutablePath() {
        //TODO Need to handle different variants of Windows flavours - reference https://code.google.com/p/selenium/wiki/ChromeDriver
        return System.getenv("HOMEPATH") + "\\Local Settings\\Application Data\\Google\\Chrome\\Application\\chrome.exe";
        //OR C:\Users\%USERNAME%\AppData\Local\Google\Chrome\Application\chrome.exe
    }

    @Override
    public String getNixExecutablePath() {
        //TODO: To be implemented based on the flavours of NIX that we are supporting
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void clearMacCache() {
        //TODO some more research is needed if this is the only path to be managed

        //This is the original path, testing with a dummy path as I do not want to lose my chrome profile :)
        //safeDeletePath(System.getProperty("user.home") + "/Library/Application Support/Google/Chrome/Default");
        safeDeletePath("/Users/rpillai/testcache");
    }

    @Override
    public void clearWinCache() {
        safeDeletePath(System.getenv("USERPROFILE") + "\\Local Settings\\Application Data\\Google\\Chrome\\User Data\\Default");
        safeDeletePath(System.getenv("LOCALAPPDATA") + "\\Google\\Chrome\\User Data\\Default");
    }

    @Override
    public void clearNixCache() {
        safeDeletePath(System.getProperty("user.home") + "/.config/google-chrome/");
    }

}

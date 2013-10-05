package com.q1httpserver.browsers;

public class Safari extends AbstractBrowser {
    @Override
    public void sendShutdownSignal() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public String getMacExecutablePath() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public String getWinExecutablePath() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public String getNixExecutablePath() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void clearMacCache() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void clearWinCache() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void clearNixCache() {
        throw new UnsupportedOperationException("Not implemented");
    }
}

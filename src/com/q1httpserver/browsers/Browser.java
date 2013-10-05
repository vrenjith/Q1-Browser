package com.q1httpserver.browsers;

public interface Browser {
    public void clearCache();
    public void start() throws Exception;
    public void stop();
}

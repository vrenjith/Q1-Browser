package com.q1httpserver.browsers;

public class DefaultBrowserFactory implements BrowserFactory{

    @Override
    public Browser getBrowser(String type) {
        if(type.equalsIgnoreCase("chrome")){
            return new Chrome();
        }
        if(type.equalsIgnoreCase("ie")){
            return new IE();
        }
        if(type.equalsIgnoreCase("firefox")){
            return new FireFox();
        }
        if(type.equalsIgnoreCase("safari")){
            return new Safari();
        }
        else{
            return null;
        }
    }
}

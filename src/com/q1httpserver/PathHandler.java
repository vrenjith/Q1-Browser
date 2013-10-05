package com.q1httpserver;

import com.q1httpserver.browsers.Browser;
import com.q1httpserver.browsers.BrowserFactory;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static java.net.HttpURLConnection.*;

public class PathHandler implements HttpHandler {
    private BrowserFactory browserFactory;
    Map<String, Browser> runningBrowsers = new ConcurrentHashMap<>();

    public PathHandler(BrowserFactory browserFactory) {
        this.browserFactory = browserFactory;
    }

    public void handle(HttpExchange exch) throws IOException {
        final Map<String, String> params = parseParams(exch);
        if(params.containsKey("action")){
            String action = params.get("action");

            if(action.equalsIgnoreCase("start")){
                if(!params.containsKey("browser")){
                    setErrorResponse(exch, "Browser type missing", HTTP_BAD_REQUEST);
                    return;
                }
                String type = params.get("browser");
                final Browser browser = browserFactory.getBrowser(type);
                try {
                    browser.start();
                    final UUID uuid = UUID.randomUUID();
                    runningBrowsers.put(String.valueOf(uuid),browser);
                    setSuccessResponse(exch, "Browser started", HTTP_OK, String.valueOf(uuid));
                } catch (Exception e) {
                    setErrorResponse(exch, "Launching of browser failed", HTTP_INTERNAL_ERROR);
                }
            }
            else if(action.equalsIgnoreCase("cleanup")){
                if(!params.containsKey("browser")){
                    setErrorResponse(exch, "Browser type missing", HTTP_BAD_REQUEST);
                    return;
                }
                String type = params.get("browser");
                try
                {
                    final Browser browser = browserFactory.getBrowser(type);
                    browser.clearCache();
                }
                catch (Exception excep){
                    setErrorResponse(exch, "Clearing of browser cache failed, please retry - " + excep.getMessage(),
                            HTTP_INTERNAL_ERROR);
                }
            }
            else if(action.equalsIgnoreCase("stop"))
            {
                //Other commands cannot proceed without UUID
                if(!params.containsKey("uuid") || null == runningBrowsers.get(params.get("uuid"))){
                    setErrorResponse(exch, "UUID Not Found", HTTP_NOT_FOUND);
                    return;
                }
                final Browser browser = runningBrowsers.get(params.containsKey("uuid"));
                if(action.equalsIgnoreCase("stop")){
                    try
                    {
                        browser.stop();
                    }
                    catch (Exception excep){
                        setErrorResponse(exch, "Stopping of browser failed, please retry - " + excep.getMessage(),
                                HTTP_INTERNAL_ERROR);
                    }
                }
            }
        }
        else {
            setErrorResponse(exch, "Action parameter missing in request", HTTP_BAD_REQUEST);
        }


    }

    //This helper function also is not needed if am implementation is done with standard methods
    //for example a servlet, which has functions to get the parameters
    public Map<String,String> parseParams(HttpExchange exchange){
        String queryString = exchange.getRequestURI().getRawQuery();
        Map<String,String> params = new HashMap<String, String>();
        if(null != queryString && !queryString.isEmpty()){
            for(String  queryParam : queryString.split("&")){
                String[] parts = queryParam.split("=");
                if(parts != null && parts.length == 2 && !parts[0].isEmpty() && !parts[1].isEmpty()){
                    params.put(parts[0],parts[1]);
                }
            }
        }
        return params;
    }
    protected void setSuccessResponse(HttpExchange exchange, final String message, int errorCode, String uuid) throws IOException {
        //A simple JSON builder, can go for well known libraries for this (GSON etc.), which is a an overkill here
        setResponse(exchange,
                String.format("{\"message\" : \"%s\", \"uuid\" : \"%s\"}", message, uuid), errorCode);
    }
    protected void setErrorResponse(HttpExchange exchange, final String message, int errorCode) throws IOException {
        //A simple JSON builder, can go for well known libraries for this (GSON etc.), which is a an overkill here
        setResponse(exchange,
                String.format("{\"message\" : \"%s\"}", message), errorCode);
    }
    protected void setResponse(HttpExchange exchange, final String message, int errorCode) throws IOException {
        exchange.sendResponseHeaders(errorCode, message.length());
        OutputStream os = exchange.getResponseBody();
        os.write(message.getBytes());
        os.close();
    }


}
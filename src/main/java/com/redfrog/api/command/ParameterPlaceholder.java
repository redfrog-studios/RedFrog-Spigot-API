package com.redfrog.api.command;

import com.redfrog.api.PlaceholderParameterMethod;

public class ParameterPlaceholder {

    public String DisplayName;
    public PlaceholderParameterMethod Method;

    public ParameterPlaceholder(String displayName, PlaceholderParameterMethod method)
    {
        this.DisplayName = displayName;
        this.Method = method;
    }
}

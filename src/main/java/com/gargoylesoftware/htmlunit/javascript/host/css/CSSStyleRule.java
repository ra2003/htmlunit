/*
 * Copyright (c) 2002-2015 Gargoyle Software Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.gargoylesoftware.htmlunit.javascript.host.css;

import static com.gargoylesoftware.htmlunit.BrowserVersionFeatures.JS_SELECTOR_ID_LOWERCASE;
import static com.gargoylesoftware.htmlunit.BrowserVersionFeatures.JS_SELECTOR_TEXT_LOWERCASE;
import static com.gargoylesoftware.htmlunit.BrowserVersionFeatures.JS_SELECTOR_TEXT_UPPERCASE;
import static com.gargoylesoftware.htmlunit.javascript.configuration.BrowserName.CHROME;
import static com.gargoylesoftware.htmlunit.javascript.configuration.BrowserName.EDGE;
import static com.gargoylesoftware.htmlunit.javascript.configuration.BrowserName.FF;
import static com.gargoylesoftware.htmlunit.javascript.configuration.BrowserName.IE;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.javascript.configuration.JsxClass;
import com.gargoylesoftware.htmlunit.javascript.configuration.JsxClasses;
import com.gargoylesoftware.htmlunit.javascript.configuration.JsxConstructor;
import com.gargoylesoftware.htmlunit.javascript.configuration.JsxGetter;
import com.gargoylesoftware.htmlunit.javascript.configuration.JsxSetter;
import com.gargoylesoftware.htmlunit.javascript.configuration.WebBrowser;
import com.gargoylesoftware.htmlunit.util.StringUtils;

/**
 * A JavaScript object for {@code CSSStyleRule}.
 *
 * @author Ahmed Ashour
 * @author Marc Guillemot
 */
@JsxClasses({
        @JsxClass(browsers = { @WebBrowser(CHROME), @WebBrowser(FF), @WebBrowser(value = IE, minVersion = 11),
                @WebBrowser(EDGE) }),
        @JsxClass(isJSObject = false, browsers = @WebBrowser(value = IE, maxVersion = 8))
    })
public class CSSStyleRule extends CSSRule {
    private static final Pattern SELECTOR_PARTS_PATTERN = Pattern.compile("[\\.#]?[a-zA-Z]+");
    private static final Pattern SELECTOR_REPLACE_PATTERN = Pattern.compile("\\*([\\.#])");

    /**
     * Creates a new instance.
     */
    @JsxConstructor({ @WebBrowser(CHROME), @WebBrowser(EDGE) })
    public CSSStyleRule() {
    }

    /**
     * Creates a new instance.
     * @param stylesheet the Stylesheet of this rule.
     * @param rule the wrapped rule
     */
    protected CSSStyleRule(final CSSStyleSheet stylesheet, final org.w3c.dom.css.CSSStyleRule rule) {
        super(stylesheet, rule);
    }

    /**
     * Returns the textual representation of the selector for the rule set.
     * @return the textual representation of the selector for the rule set
     */
    @JsxGetter
    public String getSelectorText() {
        String selectorText = ((org.w3c.dom.css.CSSStyleRule) getRule()).getSelectorText();
        final Matcher m = SELECTOR_PARTS_PATTERN.matcher(selectorText);
        final StringBuffer sb = new StringBuffer();
        while (m.find()) {
            String fixedName = m.group();
            // this should be handled with the right regex but...
            if (!fixedName.isEmpty() && ('.' == fixedName.charAt(0)) || ('#' == fixedName.charAt(0))) {
                if (getBrowserVersion().hasFeature(JS_SELECTOR_ID_LOWERCASE)
                        && ((HtmlPage) getWindow().getWebWindow().getEnclosedPage()).isQuirksMode()) {
                    fixedName = fixedName.toLowerCase(Locale.ROOT);
                }
            }
            else if (getBrowserVersion().hasFeature(JS_SELECTOR_TEXT_UPPERCASE)) {
                fixedName = fixedName.toUpperCase(Locale.ROOT);
            }
            else if (getBrowserVersion().hasFeature(JS_SELECTOR_TEXT_LOWERCASE)) {
                fixedName = fixedName.toLowerCase(Locale.ROOT);
            }
            fixedName = StringUtils.sanitizeForAppendReplacement(fixedName);
            m.appendReplacement(sb, fixedName);
        }
        m.appendTail(sb);

        // ".foo" and not "*.foo"
        selectorText = SELECTOR_REPLACE_PATTERN.matcher(sb.toString()).replaceAll("$1");
        return selectorText;
    }

    /**
     * Sets the textual representation of the selector for the rule set.
     * @param selectorText the textual representation of the selector for the rule set
     */
    @JsxSetter
    public void setSelectorText(final String selectorText) {
        ((org.w3c.dom.css.CSSStyleRule) getRule()).setSelectorText(selectorText);
    }

    /**
     * Returns the declaration-block of this rule set.
     * @return the declaration-block of this rule set
     */
    @JsxGetter
    public CSSStyleDeclaration getStyle() {
        return new CSSStyleDeclaration(getParentScope(), ((org.w3c.dom.css.CSSStyleRule) getRule()).getStyle());
    }

    /**
     * Returns the readonly property.
     * @return the readonly value.
     */
    @JsxGetter({ @WebBrowser(IE) })
    public boolean getReadOnly() {
        return false;
    }
}

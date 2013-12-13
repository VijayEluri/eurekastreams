/*
 * Copyright (c) 2009-2011 Lockheed Martin Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.eurekastreams.web.client.ui.common;

import java.util.HashMap;

import org.eurekastreams.server.domain.Page;
import org.eurekastreams.server.search.modelview.AuthenticationType;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.eurekastreams.server.search.modelview.PersonModelView.Role;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.SwitchedHistoryViewEvent;
import org.eurekastreams.web.client.history.CreateUrlRequest;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.notification.NotificationCountWidget;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;
import org.eurekastreams.web.client.ui.pages.search.GlobalSearchComposite;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.data.GotSystemSettingsResponseEvent;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Hyperlink;

/**
 * HeaderComposite draws the header bar for the user.
 */
public class HeaderComposite extends Composite
{
    /**
     * Link Panel to encapsulate external links in header.
     */
    FlowPanel startPageLinkPanel = new FlowPanel();
    /**
     * Link Panel to encapsulate external links in header.
     */
    FlowPanel externalPageLinkPanel = new FlowPanel();
    /**
     * Link Panel to encapsulate external links in header.
     */
    FlowPanel activityLinkPanel = new FlowPanel();
    /**
     * Link Panel to encapsulate external links in header.
     */
    FlowPanel settingsLinkPanel = new FlowPanel();
    /**
     * Link Panel to encapsulate external links in header.
     */
    FlowPanel directoryLinkPanel = new FlowPanel();
    /**
     * Link Panel to encapsulate external links in header.
     */
    FlowPanel galleryLinkPanel = new FlowPanel();
    
    /**
     * Link Panel to encapsulate external links in header.
     */
    FlowPanel learnMoreLinkPanel = new FlowPanel();

    /** The search box. */
    private final GlobalSearchComposite profileSearchBox = new GlobalSearchComposite("Find a Stream");

    /**
     * Notification Count widget.
     */
    private final NotificationCountWidget notif = new NotificationCountWidget();

    /**
     * The link map.
     */
    private final HashMap<Page, Hyperlink> linkMap = new HashMap<Page, Hyperlink>();

    /**
     * Primary constructor for the Header composite.
     */
    public HeaderComposite()
    {
        Session.getInstance().getEventBus().addObserver(SwitchedHistoryViewEvent.class,
                new Observer<SwitchedHistoryViewEvent>()
                {
                    public void update(final SwitchedHistoryViewEvent eventArg)
                    {
                        if (eventArg != null)
                        {
                            if (eventArg.getPage() != null)
                            {
                                setActive(eventArg.getPage());
                            }
                        }
                    }
                }, true);
    }

    /**
     * Render the header.
     *
     * @param viewer
     *            - user to display.
     */
    public void render(final PersonModelView viewer)
    {
        HorizontalULPanel userNav;
        FlowPanel panel = new FlowPanel();
        FlowPanel navPanel = new FlowPanel();

        Anchor externalLink = new Anchor("EurekaStreams.org", "http://www.eurekastreams.org", "_blank");
        externalLink.addStyleName(StaticResourceBundle.INSTANCE.coreCss().navBarButton());

        Hyperlink startPageLink = new Hyperlink("Start", Session.getInstance().generateUrl(
                new CreateUrlRequest(Page.START)));
        startPageLink.addStyleName(StaticResourceBundle.INSTANCE.coreCss().navBarButton());
        Hyperlink activityLink = new Hyperlink("Activity", Session.getInstance().generateUrl(
                new CreateUrlRequest(Page.ACTIVITY)));
        activityLink.addStyleName(StaticResourceBundle.INSTANCE.coreCss().navBarButton());
        Hyperlink directoryLink = new Hyperlink("Discover", Session.getInstance().generateUrl(
                new CreateUrlRequest(Page.DISCOVER)));
        directoryLink.addStyleName(StaticResourceBundle.INSTANCE.coreCss().navBarButton());
        
        Hyperlink settingsLink = new Hyperlink("Settings", Session.getInstance().generateUrl(
                new CreateUrlRequest(Page.SETTINGS)));
        settingsLink.addStyleName(StaticResourceBundle.INSTANCE.coreCss().navBarButton());
        Hyperlink myProfileLink = new Hyperlink("My Stream", Session.getInstance().generateUrl(
                new CreateUrlRequest(Page.PEOPLE, viewer.getAccountId())));
        myProfileLink.addStyleName(StaticResourceBundle.INSTANCE.coreCss().navBarButton());

        externalPageLinkPanel.add(externalLink);
        externalPageLinkPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().externalHeaderButton());
        startPageLinkPanel.add(startPageLink);
        startPageLinkPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().startHeaderButton());
        activityLinkPanel.add(activityLink);
        activityLinkPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().activityHeaderButton());
        directoryLinkPanel.add(directoryLink);
        directoryLinkPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().discoverHeaderButton());
        
        linkMap.put(Page.START, startPageLink);
        linkMap.put(Page.ACTIVITY, activityLink);
        linkMap.put(Page.DISCOVER, directoryLink);
        linkMap.put(Page.GROUPS, directoryLink);
        linkMap.put(Page.PEOPLE, directoryLink);
        linkMap.put(Page.GROUP_SETTINGS, directoryLink);
        linkMap.put(Page.PERSONAL_SETTINGS, directoryLink);
        linkMap.put(Page.SETTINGS, settingsLink);

        final HorizontalULPanel mainNav = new HorizontalULPanel();

        userNav = new HorizontalULPanel();

        mainNav.add(externalPageLinkPanel);

        // The user IS logged in
        mainNav.add(startPageLinkPanel);
        mainNav.add(activityLinkPanel);
        mainNav.add(directoryLinkPanel);
        mainNav.add(galleryLinkPanel);
        
        // Add learn more conditionally if support stream exists
        EventBus.getInstance().addObserver(GotSystemSettingsResponseEvent.class,
                new Observer<GotSystemSettingsResponseEvent>()
        {
            public void update(final GotSystemSettingsResponseEvent event)
            {
              if (event.getResponse().getSupportStreamGroupShortName() != null
                       && event.getResponse().getSupportStreamGroupShortName().length() > 0 
                       && event.getResponse().getSupportStreamWebsite() != null
                            && event.getResponse().getSupportStreamWebsite().length() > 0)
                     {
						
						Anchor learnMoreLink = new Anchor("Learn", event.getResponse().getSupportStreamWebsite(),
														   "_blank");
						learnMoreLink.addStyleName(StaticResourceBundle.INSTANCE.coreCss().navBarButton());   
						learnMoreLinkPanel.add(learnMoreLink);
						learnMoreLinkPanel.addStyleName(
						                   StaticResourceBundle.INSTANCE.coreCss().learnmoreHeaderButton());
						mainNav.add(learnMoreLinkPanel);
                     }
               }
        });
        
        notif.init();

        FlowPanel myProfileLinkPanel = new FlowPanel();
        myProfileLinkPanel.add(myProfileLink);
        userNav.add(myProfileLinkPanel);

        userNav.add(notif, StaticResourceBundle.INSTANCE.coreCss().notifHeader());

        if (Session.getInstance().getCurrentPersonRoles().contains(Role.SYSTEM_ADMIN))
        {
            settingsLinkPanel.add(settingsLink);
            settingsLinkPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().settingsHeaderButton());
            userNav.add(settingsLinkPanel);
        }

        if (Session.getInstance().getAuthenticationType() == AuthenticationType.FORM)
        {
            userNav.add(new Anchor("Logout", "/j_spring_security_logout"));
        }

        // Note: The profile search box is created at constructor time because it registers listeners on the event
        // bus which needs to happen before the call to bufferObservers. The profile search box is created only once
        // (not replaced on page changes), so its listeners must be buffered, else they would be lost on the first
        // page change.
        userNav.add(profileSearchBox, StaticResourceBundle.INSTANCE.coreCss().globalSearchBoxNav());

        // Style the Elements
        panel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().headerBar());
        navPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().navBar());

        mainNav.addStyleName(StaticResourceBundle.INSTANCE.coreCss().mainNav());
        userNav.addStyleName(StaticResourceBundle.INSTANCE.coreCss().userBar());

        // Add the elements to the main panel
        navPanel.add(mainNav);
        navPanel.add(userNav);
        panel.add(navPanel);

        initWidget(panel);
        setActive(Session.getInstance().getUrlPage());
    }


    /**
     * Set the top button as active.
     *
     * @param page
     *            the page to activate.
     */
    public void setActive(final Page page)
    {
        for (Page specificPage : linkMap.keySet())
        {
            linkMap.get(specificPage).removeStyleName(StaticResourceBundle.INSTANCE.coreCss().active());
        }

        if (linkMap.containsKey(page))
        {
            linkMap.get(page).addStyleName(StaticResourceBundle.INSTANCE.coreCss().active());
        }
    }
}

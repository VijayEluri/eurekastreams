/*
 * Copyright (c) 2011 Lockheed Martin Corporation
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
package org.eurekastreams.web.client.ui.pages.discover;

import java.util.HashMap;

import org.eurekastreams.server.domain.Page;
import org.eurekastreams.server.domain.dto.StreamDTO;
import org.eurekastreams.server.domain.dto.StreamDiscoverListsDTO;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.UpdateHistoryEvent;
import org.eurekastreams.web.client.events.data.GotFeaturedStreamsPageResponseEvent;
import org.eurekastreams.web.client.events.data.GotStreamDiscoverListsDTOResponseEvent;
import org.eurekastreams.web.client.history.CreateUrlRequest;
import org.eurekastreams.web.client.jsni.WidgetJSNIFacadeImpl;
import org.eurekastreams.web.client.model.StreamsDiscoveryModel;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.LabeledTextBox;
import org.eurekastreams.web.client.ui.common.pager.PagerComposite;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;

/**
 * Discover Page.
 */
public class DiscoverContent extends Composite
{
    /**
     * Binder for building UI.
     */
    private static LocalUiBinder binder = GWT.create(LocalUiBinder.class);

    /**
     * Number of streams to show in the "Most Active Streams" section.
     */
    private static final int MOST_ACTIVE_STREAMS_PAGE_SIZE = 9;

    /**
     * Number of featured streams to show.
     */
    private static final int FEATURED_STREAMS_PAGE_SIZE = 3;

    /**
     * CSS resource.
     */
    interface DiscoverStyle extends CssResource
    {
        /** @return CSS style for search bar when it is the topmost item because the feature streams panel is hidden. */
        String searchBarAtTop();
    }

    /** Local styles. */
    @UiField
    DiscoverStyle style;

    /** Search bar (accessible for styling). */
    @UiField
    DivElement searchBar;

    /**
     * Flow Panel to contain the stream search box.
     */
    @UiField
    FlowPanel searchFlowPanel;

    /**
     * Search button.
     */
    @UiField
    Label goSearch;

    /**
     * UI element for streams.
     */
    @UiField
    PagerComposite featuredStreamsComposite;

    /**
     * UI element for streams.
     */
    @UiField
    PagerComposite mostActiveStreamsComposite;

    /**
     * UI element for streams.
     */
    @UiField
    FlowPanel suggestedStreamsPanel;

    /**
     * UI element for streams.
     */
    @UiField
    FlowPanel mostViewedStreamsPanel;

    /**
     * UI element for streams.
     */
    @UiField
    FlowPanel mostFollowedStreamsPanel;

    /**
     * UI element for streams.
     */
    @UiField
    FlowPanel mostRecentStreamsPanel;

    /**
     * Search box.
     */
    @UiField
    LabeledTextBox searchBox;

    /** Link to create group page. */
    @UiField
    Hyperlink createGroupButton;

    /** Element showing message when list is empty. */
    @UiField
    DivElement suggestionsEmptyLabel;

    /** Element showing message when list is empty. */
    @UiField
    DivElement mostViewedEmptyLabel;

    /**
     * JSNI.
     */
    private final WidgetJSNIFacadeImpl jsniFacade = new WidgetJSNIFacadeImpl();

    /**
     * Default constructor.
     */
    public DiscoverContent()
    {
        initWidget(binder.createAndBindUi(this));

        UIObject.setVisible(suggestionsEmptyLabel, false);
        UIObject.setVisible(mostViewedEmptyLabel, false);
        createGroupButton.setTargetHistoryToken(Session.getInstance()
                .generateUrl(new CreateUrlRequest(Page.NEW_GROUP)));

        EventBus.getInstance().addObserver(GotStreamDiscoverListsDTOResponseEvent.class,
                new Observer<GotStreamDiscoverListsDTOResponseEvent>()
                {
                    public void update(final GotStreamDiscoverListsDTOResponseEvent event)
                    {
                        EventBus.getInstance().removeObserver(GotStreamDiscoverListsDTOResponseEvent.class, this);
                        buildPage(event.getResponse());
                    }
                });
        EventBus.getInstance().addObserver(GotFeaturedStreamsPageResponseEvent.class,
                new Observer<GotFeaturedStreamsPageResponseEvent>()
                {
                    public void update(final GotFeaturedStreamsPageResponseEvent ev)
                    {
                        boolean featuredVisible = ev.getResponse().getTotal() > 0;
                        featuredStreamsComposite.setVisible(featuredVisible);
                        if (featuredVisible)
                        {
                            searchBar.removeClassName(style.searchBarAtTop());
                        }
                        else
                        {
                            searchBar.addClassName(style.searchBarAtTop());
                        }
                    }
                });

        StreamsDiscoveryModel.getInstance().fetch(null, true);
    }

    /**
     * Build the page.
     *
     * @param inDiscoverLists
     *            the data to display
     */
    private void buildPage(final StreamDiscoverListsDTO inDiscoverLists)
    {
        if (inDiscoverLists == null)
        {
            return;
        }
        suggestedStreamsPanel.clear();
        suggestedStreamsPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().discoverPageList());

        mostViewedStreamsPanel.clear();
        mostViewedStreamsPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().discoverPageList());

        mostFollowedStreamsPanel.clear();
        mostFollowedStreamsPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().discoverPageList());

        mostRecentStreamsPanel.clear();
        mostRecentStreamsPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().discoverPageList());

        // --------------------
        // FEATURED STREAMS
        // Note: the data needed for this list is built from the FeaturedStreamsModel, but is actually fetched and
        // stored in the StreamsDiscoveryModel cache, so this request won't hit the server
        featuredStreamsComposite.setHeader("Featured Streams");
        featuredStreamsComposite.init(new FeaturedStreamsPagerUiStrategy(FEATURED_STREAMS_PAGE_SIZE));
        featuredStreamsComposite.load();

        // --------------------
        // MOST ACTIVE STREAMS
        // Note: the data needed for this list is built from the MostActiveStreamsModel, but is actually fetched and
        // stored in the StreamsDiscoveryModel cache, so this request won't hit the server
        mostActiveStreamsComposite.setHeader("Most Active Streams");
        mostActiveStreamsComposite.init(new MostActiveStreamsPagerUiStrategy(MOST_ACTIVE_STREAMS_PAGE_SIZE));
        mostActiveStreamsComposite.load();

        if (inDiscoverLists.getSuggestedStreams() != null)
        {
            for (final StreamDTO stream : inDiscoverLists.getSuggestedStreams())
            {
                suggestedStreamsPanel.add(new DiscoverListItemPanel(stream,
                        DiscoverListItemPanel.ListItemType.MUTUAL_FOLLOWERS, true));
            }
        }
        UIObject.setVisible(suggestionsEmptyLabel, suggestedStreamsPanel.getWidgetCount() == 0);
        if (inDiscoverLists.getMostViewedStreams() != null)
        {
            for (StreamDTO stream : inDiscoverLists.getMostViewedStreams())
            {
                mostViewedStreamsPanel.add(new DiscoverListItemPanel(stream,
                        DiscoverListItemPanel.ListItemType.DAILY_VIEWERS));
            }
        }
        UIObject.setVisible(mostViewedEmptyLabel, mostViewedStreamsPanel.getWidgetCount() == 0);
        if (inDiscoverLists.getMostFollowedStreams() != null)
        {
            for (StreamDTO stream : inDiscoverLists.getMostFollowedStreams())
            {
                mostFollowedStreamsPanel.add(new DiscoverListItemPanel(stream,
                        DiscoverListItemPanel.ListItemType.FOLLOWERS));
            }
        }
        if (inDiscoverLists.getMostRecentStreams() != null)
        {
            for (StreamDTO stream : inDiscoverLists.getMostRecentStreams())
            {
                mostRecentStreamsPanel.add(new DiscoverListItemPanel(stream,
                        DiscoverListItemPanel.ListItemType.TIME_AGO));
            }
        }

        searchBox.addKeyUpHandler(new KeyUpHandler()
        {
            public void onKeyUp(final KeyUpEvent ev)
            {
                if (ev.getNativeKeyCode() == KeyCodes.KEY_ENTER && !ev.isAnyModifierKeyDown())
                {
                    doSearch();
                }
            }
        });

        goSearch.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent arg0)
            {
                doSearch();
            }
        });
    }

    /**
     * Performs the search.
     */
    private void doSearch()
    {
        EventBus.getInstance().notifyObservers(
                new UpdateHistoryEvent(new CreateUrlRequest(Page.SEARCH, generateParams(searchBox.getText()), false)));
    }

    /**
     * Creates a hashmap for the history parameters to pass to the search page.
     *
     * @param query
     *            the search string.
     * @return the hashmap of all necessary initial search parameters.
     */
    private HashMap<String, String> generateParams(final String query)
    {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("query", query);
        params.put("startIndex", "0");
        params.put("endIndex", "9");
        return params;
    }

    /**
     * Binder for building UI.
     */
    interface LocalUiBinder extends UiBinder<Widget, DiscoverContent>
    {
    }
}
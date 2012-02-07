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
package org.eurekastreams.web.client.ui.common.stream;

import java.util.HashMap;

import org.eurekastreams.web.client.history.CreateUrlRequest;
import org.eurekastreams.web.client.jsni.WidgetJSNIFacadeImpl;
import org.eurekastreams.web.client.model.BaseActivitySubscriptionModel;
import org.eurekastreams.web.client.model.GadgetModel;
import org.eurekastreams.web.client.model.StreamBookmarksModel;
import org.eurekastreams.web.client.model.requests.AddGadgetToStartPageRequest;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.dialog.BaseDialogContent;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * Dialog content for creating or editing a stream view.
 */
public class FollowDialogContent extends BaseDialogContent
{
    /**
     * Stream to URL transformer.
     * */
    private static final StreamToUrlTransformer STREAM_URL_TRANSFORMER = new StreamToUrlTransformer();

    /**
     * Container flow panel.
     */
    private final FlowPanel container = new FlowPanel();

    /**
     * Main flow panel.
     */
    private final FlowPanel body = new FlowPanel();

    /**
     * Tips flow panel.
     */
    private final FlowPanel tips = new FlowPanel();

    /**
     * Default constructor.
     * 
     * @param inStreamName
     *            the stream name.
     * @param streamRequest
     *            the stream request.
     * @param inStreamId
     *            the stream id.
     * @param inSubscribeModel
     *            Model for subscribing to activity on the stream.
     * @param inEntityUniqueId
     *            Unique ID of entity owning the stream.
     */
    public FollowDialogContent(final String inStreamName, final String streamRequest, final Long inStreamId,
            final BaseActivitySubscriptionModel inSubscribeModel, final String inEntityUniqueId)
    {
        Label saveButton = new Label("");
        Label closeButton = new Label("No Thanks");

        closeButton.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent event)
            {
                close();
            }
        });

        body.add(new Label("You are now following the:"));

        Label streamTitle = new Label(inStreamName + " Stream");
        streamTitle.addStyleName(StaticResourceBundle.INSTANCE.coreCss().title());

        body.add(streamTitle);

        FlowPanel options = new FlowPanel();

        Label optionsText = new Label("Below are additional ways to subscribe:");
        options.add(optionsText);

        final CheckBox addToStartPage = new CheckBox("Add this Stream to my Start Page");
        final CheckBox bookmarkStream = new CheckBox("Bookmark this stream");
        final CheckBox notifyViaEmail = new CheckBox("Notify me via Email");

        saveButton.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent event)
            {
                if (addToStartPage.getValue())
                {
                    // For the app's location, use the current URL minus a few parameters we know we don't want. (They
                    // are used by other lists, but get left in the URL when switching tabs.)
                    // We don't build the URL from the stream id, since that doesn't take search terms into account.
                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put("listId", null);
                    params.put("listFilter", null);
                    params.put("listSort", null);
                    params.put("startIndex", null);
                    params.put("endIndex", null);
                    String url = Session.getInstance().generateUrl(new CreateUrlRequest(params));

                    String prefs = "{\"streamQuery\":"
                            + WidgetJSNIFacadeImpl.makeJsonString(STREAM_URL_TRANSFORMER.getUrl(null, streamRequest))
                            + ",\"gadgetTitle\":" + WidgetJSNIFacadeImpl.makeJsonString(inStreamName)
                            + ",\"streamLocation\":" + WidgetJSNIFacadeImpl.makeJsonString(url) + "}";

                    GadgetModel.getInstance().insert(
                            new AddGadgetToStartPageRequest("{d7a58391-5375-4c76-b5fc-a431c42a7555}", null,
                                    STREAM_URL_TRANSFORMER.getUrl(null, prefs)));
                }

                if (bookmarkStream.getValue())
                {
                    StreamBookmarksModel.getInstance().insert(inStreamId);
                }

                if (notifyViaEmail.getValue())
                {
                    inSubscribeModel.insert(inEntityUniqueId);
                }

                close();
            }
        });

        options.add(addToStartPage);
        options.add(bookmarkStream);
        options.add(notifyViaEmail);

        body.add(options);

        FlowPanel buttonPanel = new FlowPanel();
        buttonPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().followDialogButtonPanel());

        saveButton.addStyleName(StaticResourceBundle.INSTANCE.coreCss().saveChangesButton());
        buttonPanel.add(saveButton);
        buttonPanel.add(closeButton);

        body.add(buttonPanel);

        container.add(body);

        Label tipsTitle = new Label("Tips");
        tips.add(tipsTitle);

        tips.add(new Label("These options allow you to have control over how to access the activity of this stream."));
        tips.add(new Label(
                "Don't worry, these selections are not permanent.  You can always change them in the future."));

        container.add(tips);

        body.addStyleName(StaticResourceBundle.INSTANCE.coreCss().followDialogBody());
        tips.addStyleName(StaticResourceBundle.INSTANCE.coreCss().followDialogTips());

    }

    /**
     * Gets the body panel.
     * 
     * @return the body.
     */
    public Widget getBody()
    {
        return container;
    }

    /**
     * Gets the CSS name.
     * 
     * @return the class.
     */
    @Override
    public String getCssName()
    {
        return StaticResourceBundle.INSTANCE.coreCss().followDialog();
    }

    /**
     * Gets the title.
     * 
     * @return the title.
     */
    public String getTitle()
    {
        return "Subscribe";
    }
}

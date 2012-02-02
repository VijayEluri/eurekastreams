/*
 * Copyright (c) 2010-2012 Lockheed Martin Corporation
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
package org.eurekastreams.web.client.ui.common.stream.renderers.object;

import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

/**
 * Renders videos.
 */
public class VideoRenderer extends ActivityWithContentRenderer
{
    /**
     * Renders the bookmark attachment.
     *
     * @param activity
     *            the activity.
     * @return the widget.
     */
    @Override
    public Widget getAttachmentWidget(final ActivityDTO activity)
    {
        String html = "<div class='" + StaticResourceBundle.INSTANCE.coreCss().messageLink() + " "
                + StaticResourceBundle.INSTANCE.coreCss().hasThumbnail()
                + StaticResourceBundle.INSTANCE.coreCss().video() + "'><div>";

        if (activity.getBaseObjectProperties().get("thumbnail") != null
                && activity.getBaseObjectProperties().get("thumbnail").length() > 0)
        {
            html += "<a class=\"" + StaticResourceBundle.INSTANCE.coreCss().thumbnail() + "\" href=\""
                    + activity.getBaseObjectProperties().get("videoPageUrl");
            html += "\" target=\"_blank\">";
            html += "<img class='" + StaticResourceBundle.INSTANCE.coreCss().thumbnail() + "' src='";
            html += activity.getBaseObjectProperties().get("thumbnail") + "'></a></div>";
        }

        html += "<div><a class=\"" + StaticResourceBundle.INSTANCE.coreCss().title() + "\" href=\""
                + activity.getBaseObjectProperties().get("videoPageUrl") + "\" target=\"_blank\">"
                + activity.getBaseObjectProperties().get("title") + "</a>" + "</div><div class='gwt-Label "
                + StaticResourceBundle.INSTANCE.coreCss().metaDescription() + "'>"
                + activity.getBaseObjectProperties().get("description") + "</div></div>";

        return new HTML(html);
    }
}

/*
 * Copyright (c) 2010 Lockheed Martin Corporation
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
package org.eurekastreams.server.service.actions.strategies.activity.plugins.rome;

import java.io.IOException;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;

/**
 * Fetches a feed (for use by the stream plugin framework).
 */
public interface FeedFetcher
{
    /**
     * Fetches a feed.
     *
     * @param inFeedUrl
     *            the Url for the feed to feed in.
     * @param inHttpHeaders
     *            HTTP headers to add to the request.
     * @param inProxyHost
     *            host name to use (if desires) for proxying http requests.
     * @param inProxyPort
     *            port for http proxy server.
     * @param inTimeout
     *            the timeout period to wait for the feed to return (in ms).
     * @return a Syndicated Feed.
     * @throws FeedException
     *             if Exception.
     * @throws ParserConfigurationException
     *             if Exception.
     * @throws SAXException
     *             if Exception.
     * @throws IOException
     *             if Exception.
     */
    SyndFeed fetchFeed(final String inFeedUrl, final Map<String, String> inHttpHeaders, final String inProxyHost,
            final String inProxyPort, final int inTimeout) throws IOException, ParserConfigurationException,
            FeedException, SAXException;
}
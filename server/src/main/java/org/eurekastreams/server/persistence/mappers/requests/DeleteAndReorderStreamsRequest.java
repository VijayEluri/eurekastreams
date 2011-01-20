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
package org.eurekastreams.server.persistence.mappers.requests;

/**
 * Request for DeleteAndReorderStreamsDbMapper.
 * 
 */
public class DeleteAndReorderStreamsRequest
{
    /**
     * Person id.
     */
    private Long personId;

    /**
     * Stream id.
     */
    private Long streamId;

    /**
     * Constructor.
     * 
     * @param inPersonId
     *            Person id.
     * @param inStreamId
     *            Stream id.
     */
    public DeleteAndReorderStreamsRequest(final Long inPersonId, final Long inStreamId)
    {
        personId = inPersonId;
        streamId = inStreamId;
    }

    /**
     * @return the personId
     */
    public Long getPersonId()
    {
        return personId;
    }

    /**
     * @param inPersonId
     *            the personId to set
     */
    public void setPersonId(final Long inPersonId)
    {
        personId = inPersonId;
    }

    /**
     * @return the streamId
     */
    public Long getStreamId()
    {
        return streamId;
    }

    /**
     * @param inStreamId
     *            the streamId to set
     */
    public void setStreamId(final Long inStreamId)
    {
        streamId = inStreamId;
    }

}

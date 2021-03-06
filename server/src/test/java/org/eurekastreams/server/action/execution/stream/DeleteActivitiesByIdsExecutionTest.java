/*
 * Copyright (c) 2010-2011 Lockheed Martin Corporation
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
package org.eurekastreams.server.action.execution.stream;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;
import org.eurekastreams.server.persistence.mappers.db.DeleteActivities;
import org.eurekastreams.server.persistence.mappers.db.GetListsContainingActivities;
import org.eurekastreams.server.persistence.mappers.requests.BulkActivityDeleteResponse;
import org.eurekastreams.server.testing.TestContextCreator;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * This test suite tests the {@link DeleteActivitiesByIdsExecution}.
 *
 */
public class DeleteActivitiesByIdsExecutionTest
{
    /**
     * System under test.
     */
    private DeleteActivitiesByIdsExecution sut;

    /**
     * Context for building mock objects.
     */
    private final Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };
    /**
     * Mock Mapper to get a list of cache keys that contain references to expired activities.
     */
    private final GetListsContainingActivities listsMapper = context.mock(GetListsContainingActivities.class);

    /**
     * Mock Mapper to remove expired activities from the database.
     */
    private final DeleteActivities deleteMapper = context.mock(DeleteActivities.class);

    /**
     * Prepare the sut.
     */
    @Before
    public void setup()
    {
        sut = new DeleteActivitiesByIdsExecution(listsMapper, deleteMapper);
    }

    /**
     * Test the execution fo the {@link DeleteActivitiesByIdsExecution} class.
     */
    @Test
    public void testExecute()
    {
        final ArrayList<Long> activityIds = new ArrayList<Long>();
        activityIds.add(1L);
        activityIds.add(2L);
        final List<String> listsToUpdate = new ArrayList<String>();
        listsToUpdate.add(CacheKeys.ACTIVITIES_BY_FOLLOWING + 1L);
        final List<Long> commentIds = new ArrayList<Long>();
        commentIds.add(1L);
        commentIds.add(2L);
        final Map<Long, Set<Long>> starredActivityIds = new HashMap<Long, Set<Long>>();
        final BulkActivityDeleteResponse deleteResponse = new BulkActivityDeleteResponse(activityIds, commentIds,
                starredActivityIds);

        context.checking(new Expectations()
        {
            {
                oneOf(listsMapper).execute(with(any(List.class)));
                will(returnValue(listsToUpdate));

                oneOf(deleteMapper).execute(with(any(List.class)));
                will(returnValue(deleteResponse));
            }
        });

        TaskHandlerActionContext<ActionContext> actionContext = TestContextCreator
                .createTaskHandlerAsyncContext(activityIds);
        sut.execute(actionContext);
        context.assertIsSatisfied();

        // assert that the UserActionRequest list that came out is valid.
        final List<UserActionRequest> userActionRequests = actionContext.getUserActionRequests();
        assertEquals(8, userActionRequests.size());
        assertEquals("deleteFromSearchIndexAction", userActionRequests.get(0).getActionKey());
        assertEquals("deleteIdsFromLists", userActionRequests.get(1).getActionKey());
        // There should be four individual deleteCacheKeysAction UserActionRequests, 2 for the two activities,
        // and 2 for the 2 comments to be deleted.
        assertEquals("deleteCacheKeysAction", userActionRequests.get(2).getActionKey());
        assertEquals("deleteCacheKeysAction", userActionRequests.get(3).getActionKey());
        assertEquals("deleteCacheKeysAction", userActionRequests.get(4).getActionKey());
        assertEquals("deleteCacheKeysAction", userActionRequests.get(5).getActionKey());
    }
}

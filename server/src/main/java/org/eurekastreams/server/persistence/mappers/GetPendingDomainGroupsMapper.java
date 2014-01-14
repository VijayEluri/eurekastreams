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
package org.eurekastreams.server.persistence.mappers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eurekastreams.commons.hibernate.ModelViewResultTransformer;
import org.eurekastreams.server.domain.DomainGroup;
import org.eurekastreams.server.domain.PagedSet;
import org.eurekastreams.server.persistence.mappers.requests.GetPendingDomainGroupsRequest;
import org.eurekastreams.server.search.factories.DomainGroupModelViewFactory;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

/**
 * The get pending groups action mapper - gets pending groups from the DB. Keep in mind that pending groups aren't
 * cached.
 */
public class GetPendingDomainGroupsMapper extends
        BaseArgDomainMapper<GetPendingDomainGroupsRequest, PagedSet<DomainGroupModelView>>
{
    /**
     * Mapper to get people by IDs, using cache.
     */
    private DomainMapper<List<Long>, List<PersonModelView>> getPeopleByIdsMapper;
    
    /**
     * Max pending groups.
     */
    private static final int MAX_RESULTS = 10;


    /**
     * Execute getting the groups pending for an org.
     * 
     * @param inRequest
     *            the request for the data.
     * @return A pageset of Groups pending for the Org in the request.
     */
    @SuppressWarnings("unchecked")
    @Override
    public PagedSet<DomainGroupModelView> execute(final GetPendingDomainGroupsRequest inRequest)
    {
        // create our query
        Criteria criteria = getHibernateSession().createCriteria(DomainGroup.class);

        // tell hibernate which fields we want back
        ProjectionList fields = Projections.projectionList();

        fields.add(getColumn("id"));
        fields.add(getColumn("shortName"));
        fields.add(getColumn("name"));
        fields.add(getColumn("description"));
        fields.add(Projections.property("publicGroup").as("isPublic"));

        fields.add(Projections.property("createdBy.id").as("personCreatedById"));

        fields.add(getColumn("dateAdded"));

        criteria.setProjection(fields);

        // add restrictions (where clauses)

        // pending groups
        criteria.add(Restrictions.eq("isPending", true));

        // set the sort order
        criteria.addOrder(Order.asc("dateAdded"));

        // set the result transformer - transforms tuples into
        // DomainGroupModelViews
        ModelViewResultTransformer<DomainGroupModelView> resultTransformer;
        resultTransformer = new ModelViewResultTransformer<DomainGroupModelView>(new DomainGroupModelViewFactory());
        criteria.setResultTransformer(resultTransformer);

        //The First result sets the start row offset for the dataset 
        //The Max result denotes the no. of rows fetched from the dataset
        criteria.setFirstResult(inRequest.getPageStart());
        criteria.setMaxResults(MAX_RESULTS);

        // get the results
        List<DomainGroupModelView> results = criteria.list();

        // populate info from the cache
        populateCachedFields(results);

        // Get Total Row Count
        Criteria rowCountCriteria = getHibernateSession().createCriteria(DomainGroup.class);
        rowCountCriteria.setProjection(Projections.rowCount());
        rowCountCriteria.add(Restrictions.eq("isPending", true));

        // Create Page Set
        PagedSet<DomainGroupModelView> pagedSet = new PagedSet<DomainGroupModelView>();
        pagedSet.setPagedSet(results);
        pagedSet.setFromIndex(inRequest.getPageStart());
        pagedSet.setToIndex(inRequest.getPageStart() + results.size() - 1);
        pagedSet.setTotal((Integer) rowCountCriteria.list().get(0));

        return pagedSet;

    }

    /**
     * Populate the rest of the model view object with data from the cache.
     * 
     * @param inResults
     *            The groups to populate with extra data.
     */
    private void populateCachedFields(final List<DomainGroupModelView> inResults)
    {
        // loop over the results to collect all of the people we need to ask cache for
        List<Long> peopleIdsToFetch = new ArrayList<Long>();
        for (DomainGroupModelView result : inResults)
        {
            if (!peopleIdsToFetch.contains(result.getPersonCreatedById()))
            {
                peopleIdsToFetch.add(result.getPersonCreatedById());
            }
        }

        // fetch the people
        List<PersonModelView> people = getPeopleByIdsMapper.execute(peopleIdsToFetch);

        // put them in a map for lookup
        HashMap<Long, PersonModelView> peopleByIdMap = new HashMap<Long, PersonModelView>();
        for (PersonModelView person : people)
        {
            peopleByIdMap.put(person.getEntityId(), person);
        }

        for (DomainGroupModelView result : inResults)
        {
            if (result.getPersonCreatedById() != null)
            {
                PersonModelView person = peopleByIdMap.get(result.getPersonCreatedById());
                result.setPersonCreatedByAccountId(person.getAccountId());
                result.setPersonCreatedByDisplayName(person.getDisplayName());
                result.setPersonCreatedByCompanyName(person.getCompanyName());
            }
        }
    }

    /**
     * @param inGetPeopleByIdsMapper
     *            the getPeopleByIdsMapper to set
     */
    public void setGetPeopleByIdsMapper(final DomainMapper<List<Long>, List<PersonModelView>> inGetPeopleByIdsMapper)
    {
        getPeopleByIdsMapper = inGetPeopleByIdsMapper;
    }
}

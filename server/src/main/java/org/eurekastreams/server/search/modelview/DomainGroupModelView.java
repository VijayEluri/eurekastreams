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
package org.eurekastreams.server.search.modelview;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.eurekastreams.commons.search.modelview.ModelView;
import org.eurekastreams.server.domain.ActivityRestrictionEntity;
import org.eurekastreams.server.domain.AvatarEntity;
import org.eurekastreams.server.domain.BackgroundItem;
import org.eurekastreams.server.domain.Bannerable;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.Followable;
import org.eurekastreams.server.domain.Follower.FollowerStatus;
import org.eurekastreams.server.domain.Identifiable;
import org.eurekastreams.server.domain.dto.StreamDTO;
import org.eurekastreams.server.domain.stream.ActivityDTO;

/**
 * ModelView for DomainGroup.
 */
public class DomainGroupModelView extends ModelView implements Followable, ActivityRestrictionEntity, Bannerable,
        AvatarEntity, Identifiable, StreamDTO
{
    /**
     * Serial version uid.
     */
    private static final long serialVersionUID = -5023052368092297971L;

    /**
     * form Key.
     */
    public static final String ID_KEY = "id";
    /**
     * form Key.
     */
    public static final String NAME_KEY = "name";
    /**
     * form Key.
     */
    public static final String SHORT_NAME_KEY = "shortName";

    /**
     * form Key.
     */
    public static final String URL_KEY = "url";

    /**
     * form Key.
     */
    public static final String COORDINATORS_KEY = "coordinators";
    /**
     * form Key.
     */
    public static final String PRIVACY_KEY = "publicGroup";
    /**
     * form Key.
     */
    public static final String DESCRIPTION_KEY = "description";
    /**
     * form Key.
     */
    public static final String OVERVIEW_KEY = "overview";
    /**
     * form Key.
     */
    public static final String KEYWORDS_KEY = "keywords";

    /**
     * form Key.
     */
    public static final String STREAM_POSTABLE_KEY = "streamPostable";
    /**
     * form Key.
     */
    public static final String STREAM_COMMENTABLE_KEY = "commentable";

    /**
     * form error message.
     */
    public static final String KEYWORD_MESSAGE = "Keywords must be no more than "
            + BackgroundItem.MAX_BACKGROUND_ITEM_NAME_LENGTH + " characters each.";

    /**
     * The number of people following this group.
     */
    private int followersCount = UNINITIALIZED_INTEGER_VALUE;

    /**
     * The name of this group.
     */
    private String name = UNINITIALIZED_STRING_VALUE;

    /**
     * The short name of this group.
     */
    private String shortName = UNINITIALIZED_STRING_VALUE;

    /**
     * The description of this group.
     */
    private String description = UNINITIALIZED_STRING_VALUE;

    /**
     * The date this group was added to the system.
     */
    private Date dateAdded = UNINITIALIZED_DATE_VALUE;

    /**
     * The number of updates this group has.
     */
    private int updatesCount = UNINITIALIZED_INTEGER_VALUE;

    /**
     * The group's avatar id.
     */
    private String avatarId = UNINITIALIZED_STRING_VALUE;

    /**
     * The group's url.
     */
    private String url = UNINITIALIZED_STRING_VALUE;

    /**
     * Avatar crop size.
     */
    private Integer avatarCropSize = null;

    /**
     * Avatar crop value x.
     */
    private Integer avatarCropX = null;

    /**
     * Avatar crop value y.
     */
    private Integer avatarCropY = null;

    /**
     * If the group is public.
     */
    private Boolean isPublic = false;

    /**
     * Flag for restricted access.
     */
    private Boolean restricted = true;

    /**
     * The account id of the person that created this group.
     */
    private String personCreatedByAccountId = UNINITIALIZED_STRING_VALUE;

    /**
     * The ID of the person that created this group - used internally, not sent to client. "transient" omits it from
     * serialization.
     */
    private transient Long personCreatedById = UNINITIALIZED_LONG_VALUE;

    /**
     * The display name of the person that created this group.
     */
    private String personCreatedByDisplayName = UNINITIALIZED_STRING_VALUE;
    
    /**
     * The display name of the company name of the person that created this group.
     */
    private String personCreatedByCompanyName = UNINITIALIZED_STRING_VALUE;

    /**
     * Stream id for this group.
     */
    private long streamId = UNINITIALIZED_LONG_VALUE;

    /**
     * Flag to determine if group's activities can be commented on.
     */
    private boolean commentable = true;

    /**
     * Flag to determine if group's stream can be posted on.
     */
    private boolean streamPostable = true;

    /**
     * If group is pending approval.
     */
    private boolean isPending = false;

    /**
     * Banner id.
     */
    private String bannerId = null;

    /**
     * Group overview.
     */
    private String overview = null;

    /**
     * Banner entityId.
     */
    private Long bannerEntityId = UNINITIALIZED_LONG_VALUE;

    /**
     * Group coordinator personModelViews.
     */
    private List<PersonModelView> coordinators;

    /**
     * List of capability names.
     */
    private List<String> capabilities;

    /**
     * The stream scope id.
     */
    private Long streamScopeId;

    /** ID of the activity stuck at the top of the stream. Null if none. */
    private Long stickyActivityId;

    /** Activity stuck at the top of the stream. Null if none. Used to send to client; not stored in memcache. */
    private ActivityDTO stickyActivity;

    /**
     * Constructor.
     */
    public DomainGroupModelView()
    {

    }

    /**
     * Constructor.
     *
     * @param inId
     *            the domain group id
     * @param inShortName
     *            the domain group short name
     * @param inName
     *            the domain group name
     * @param inFollowerCount
     *            the number of followers
     * @param inDateAdded
     *            the date added
     * @param inStreamScopeId
     *            the stream scope id
     */
    public DomainGroupModelView(final Long inId, final String inShortName, final String inName,
            final Long inFollowerCount, final Date inDateAdded, final Long inStreamScopeId)
    {
        setEntityId(inId);
        setShortName(inShortName);
        setName(inName);
        setFollowersCount(inFollowerCount.intValue());
        setDateAdded(inDateAdded);
        setStreamScopeId(inStreamScopeId);
    }

    /**
     * Constructor.
     *
     * @param inId
     *            the domain group id
     * @param inShortName
     *            the domain group short name
     * @param inName
     *            the domain group name
     * @param inFollowerCount
     *            the number of followers
     * @param inDateAdded
     *            the date added
     * @param inStreamScopeId
     *            the stream scope id
     * @param inIsPublic
     *            whether the group is public
     */
    public DomainGroupModelView(final Long inId, final String inShortName, final String inName,
            final Long inFollowerCount, final Date inDateAdded, final Long inStreamScopeId, final Boolean inIsPublic)
    {
        setEntityId(inId);
        setShortName(inShortName);
        setName(inName);
        setFollowersCount(inFollowerCount.intValue());
        setDateAdded(inDateAdded);
        setStreamScopeId(inStreamScopeId);
        setIsPublic(inIsPublic);
    }

    /**
     * Constructor.
     *
     * @param inId
     *            the domain group id
     * @param inShortName
     *            the domain group short name
     * @param inName
     *            the domain group name
     * @param inFollowerCount
     *            the number of followers
     * @param inDateAdded
     *            the date added
     * @param inStreamScopeId
     *            the stream scope id
     */
    public DomainGroupModelView(final Long inId, final String inShortName, final String inName,
            final Integer inFollowerCount, final Date inDateAdded, final Long inStreamScopeId)
    {
        setEntityId(inId);
        setShortName(inShortName);
        setName(inName);
        setFollowersCount(inFollowerCount.intValue());
        setDateAdded(inDateAdded);
        setStreamScopeId(inStreamScopeId);
    }

    /**
     * Constructor.
     *
     * @param inId
     *            the domain group id
     * @param inShortName
     *            the domain group short name
     * @param inName
     *            the domain group name
     * @param inFollowerCount
     *            the number of followers
     * @param inDateAdded
     *            the date added
     * @param inStreamScopeId
     *            the stream scope id
     * @param inIsPublic
     *            whether the group is public
     */
    public DomainGroupModelView(final Long inId, final String inShortName, final String inName,
            final Integer inFollowerCount, final Date inDateAdded, final Long inStreamScopeId, final Boolean inIsPublic)
    {
        setEntityId(inId);
        setShortName(inShortName);
        setName(inName);
        setFollowersCount(inFollowerCount.intValue());
        setDateAdded(inDateAdded);
        setStreamScopeId(inStreamScopeId);
        setIsPublic(inIsPublic);
    }

    /**
     * Follower status.
     */
    private FollowerStatus followerStatus;

    /**
     * Load this object's properties from the input Map.
     *
     * @param properties
     *            the Map of the properties to load
     */
    @Override
    public void loadProperties(final Map<String, Object> properties)
    {
        // let the parent class get its properties first
        super.loadProperties(properties);

        if (properties.containsKey("dateAdded"))
        {
            setDateAdded((Date) properties.get("dateAdded"));
        }
        if (properties.containsKey("avatarId"))
        {
            setAvatarId((String) properties.get("avatarId"));
        }
        if (properties.containsKey("avatarCropSize"))
        {
            setAvatarCropSize((Integer) properties.get("avatarCropSize"));
        }
        if (properties.containsKey("avatarCropX"))
        {
            setAvatarCropX((Integer) properties.get("avatarCropX"));
        }
        if (properties.containsKey("avatarCropY"))
        {
            setAvatarCropY((Integer) properties.get("avatarCropY"));
        }
        if (properties.containsKey("followersCount"))
        {
            setFollowersCount((Integer) properties.get("followersCount"));
        }
        if (properties.containsKey("updatesCount"))
        {
            setUpdatesCount((Integer) properties.get("updatesCount"));
        }
        if (properties.containsKey("name"))
        {
            setName((String) properties.get("name"));
        }
        if (properties.containsKey("description"))
        {
            setDescription((String) properties.get("description"));
        }
        if (properties.containsKey("shortName"))
        {
            setShortName(((String) properties.get("shortName")));
        }
        if (properties.containsKey("isPublic"))
        {
            setIsPublic((Boolean) properties.get("isPublic"));
        }
        if (properties.containsKey("personCreatedByAccountId"))
        {
            setPersonCreatedByAccountId((String) properties.get("personCreatedByAccountId"));
        }
        if (properties.containsKey("personCreatedByDisplayName"))
        {
            setPersonCreatedByDisplayName((String) properties.get("personCreatedByDisplayName"));
        }
        if (properties.containsKey("personCreatedById"))
        {
            personCreatedById = (Long) properties.get("personCreatedById");
        }
        if (properties.containsKey("streamId"))
        {
            setStreamId((Long) properties.get("streamId"));
        }
        if (properties.containsKey("commentable"))
        {
            setCommentable((Boolean) properties.get("commentable"));
        }
        if (properties.containsKey("streamPostable"))
        {
            setStreamPostable((Boolean) properties.get("streamPostable"));
        }
        if (properties.containsKey("isPending"))
        {
            setStreamPostable((Boolean) properties.get("isPending"));
        }
        if (properties.containsKey("bannerId"))
        {
            setBannerId((String) properties.get("bannerId"));
        }
        if (properties.containsKey("overview"))
        {
            setOverview((String) properties.get("overview"));
        }
        if (properties.containsKey("url"))
        {
            setUrl((String) properties.get("url"));
        }
        if (properties.containsKey("stickyActivityId"))
        {
            setStickyActivityId((Long) properties.get("stickyActivityId"));
        }
        if (properties.containsKey("personCreatedByCompanyName"))
        {
            setPersonCreatedByCompanyName((String) properties.get("personCreatedByCompanyName"));
        }
    }

    /**
     * Sets is public.
     *
     * @param inIsPublic
     *            sets isPublic.
     */
    public void setIsPublic(final Boolean inIsPublic)
    {
        isPublic = inIsPublic;
    }

    /**
     * @return isPublic.
     */
    public Boolean isPublic()
    {
        return isPublic;
    }

    /**
     * Get the entity name.
     *
     * @return the entity name
     */
    @Override
    protected String getEntityName()
    {
        return "DomainGroup";
    }

    /**
     * Get the group's avatar id.
     *
     * @return the avatarId
     */
    @Override
    public String getAvatarId()
    {
        return avatarId;
    }

    /**
     * Set the group's avatar id.
     *
     * @param inAvatarId
     *            the avatarId to set
     */
    @Override
    public void setAvatarId(final String inAvatarId)
    {
        avatarId = inAvatarId;
    }

    /**
     * Get the number of people following this group.
     *
     * @return the followersCount
     */
    @Override
    public int getFollowersCount()
    {
        return followersCount;
    }

    /**
     * Set the number of people following this group.
     *
     * @param inFollowersCount
     *            the followersCount to set
     */
    public void setFollowersCount(final int inFollowersCount)
    {
        followersCount = inFollowersCount;
    }

    /**
     * The the number of updates for this group.
     *
     * @return the updatesCount
     */
    public int getUpdatesCount()
    {
        return updatesCount;
    }

    /**
     * Set the number of updates for this group.
     *
     * @param inUpdatesCount
     *            the updatesCount to set
     */
    public void setUpdatesCount(final int inUpdatesCount)
    {
        updatesCount = inUpdatesCount;
    }

    /**
     * Get the shortname.
     *
     * @return the shortname.
     */
    public String getShortName()
    {
        return shortName;
    }

    /**
     * Set the shortname.
     *
     * @param inShortName
     *            the shortname.
     */
    public void setShortName(final String inShortName)
    {
        shortName = inShortName;
    }

    /**
     * Get the name of the group.
     *
     * @return the name of the group
     */
    public String getName()
    {
        return name;
    }

    /**
     * Set the name of the group.
     *
     * @param inName
     *            the name to set
     */
    public void setName(final String inName)
    {
        name = inName;
    }

    /**
     * Get the description of the group.
     *
     * @return the description
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * Set the description of the group.
     *
     * @param inDescription
     *            the description to set
     */
    public void setDescription(final String inDescription)
    {
        description = inDescription;
    }

    /**
     * Get the date the group was added to the system.
     *
     * @return the dateAdded
     */
    public Date getDateAdded()
    {
        return dateAdded;
    }

    /**
     * Set the date the group was added to the system.
     *
     * @param inDateAdded
     *            the dateAdded to set
     */
    public void setDateAdded(final Date inDateAdded)
    {
        dateAdded = inDateAdded;
    }

    /**
     *
     * @return the person created by display name.
     */
    public String getPersonCreatedByDisplayName()
    {
        return personCreatedByDisplayName;
    }

    /**
     * @param inPersonCreatedByDisplayName
     *            The created by display name to set.
     */
    public void setPersonCreatedByDisplayName(final String inPersonCreatedByDisplayName)
    {
        personCreatedByDisplayName = inPersonCreatedByDisplayName;
    }
    
    /**
    *
    * @return the person created by company name.
    */
   public String getPersonCreatedByCompanyName()
   {
       return personCreatedByCompanyName;
   }

   /**
    * @param inPersonCreatedByCompanyName
    *            The created by company name to set.
    */
   public void setPersonCreatedByCompanyName(final String inPersonCreatedByCompanyName)
   {
       personCreatedByCompanyName = inPersonCreatedByCompanyName;
   }

    /**
     * @return The person created by account ID.
     */
    public String getPersonCreatedByAccountId()
    {
        return personCreatedByAccountId;
    }

    /**
     * @return The Person created by Id.
     */
    public Long getPersonCreatedById()
    {
        return personCreatedById;
    }

    /**
     * This is only available on the server and is not serialized and sent over the wire.
     *
     * @param inPersonCreatedById
     *            The person created by id.
     */
    public void setPersonCreatedById(final Long inPersonCreatedById)
    {
        personCreatedById = inPersonCreatedById;
    }

    /**
     * @param inPersonCreatedByAccountId
     *            The person created my account ID.
     */
    public void setPersonCreatedByAccountId(final String inPersonCreatedByAccountId)
    {
        personCreatedByAccountId = inPersonCreatedByAccountId;
    }

    /**
     * Set the entity id.
     *
     * @param inEntityId
     *            the entity id of the domain group.
     */
    // TODO: pull this out - this is temporary
    @Override
    public void setEntityId(final long inEntityId)
    {
        super.setEntityId(inEntityId);
    }

    /**
     * @return the streamId
     */
    public long getStreamId()
    {
        return streamId;
    }

    /**
     * @param inStreamId
     *            the streamId to set
     */
    public void setStreamId(final long inStreamId)
    {
        streamId = inStreamId;
    }

    /**
     * @param inCommentable
     *            The commentable flag value to set.
     */
    public void setCommentable(final boolean inCommentable)
    {
        commentable = inCommentable;
    }

    /**
     * @return The commentable flag.
     */
    @Override
    public boolean isCommentable()
    {
        return commentable;
    }

    /**
     * @return The streamPostable flag.
     */
    @Override
    public boolean isStreamPostable()
    {
        return streamPostable;
    }

    /**
     * @param inStreamPostable
     *            The streamPostable flag value to set.
     */
    public void setStreamPostable(final boolean inStreamPostable)
    {
        streamPostable = inStreamPostable;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getId()
    {
        return super.getEntityId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUniqueId()
    {
        return shortName;
    }

    /**
     * @return the isPending
     */
    public boolean isPending()
    {
        return isPending;
    }

    /**
     * @param inIsPending
     *            the isPending to set
     */
    public void setPending(final boolean inIsPending)
    {
        isPending = inIsPending;
    }

    @Override
    public Long getBannerEntityId()
    {
        return bannerEntityId;
    }

    @Override
    public String getBannerId()
    {
        return bannerId;
    }

    @Override
    public void setBannerEntityId(final Long inBannerEntityId)
    {
        bannerEntityId = inBannerEntityId;
    }

    @Override
    public void setBannerId(final String inBannerId)
    {
        bannerId = inBannerId;
    }

    /**
     * @return the coordinators
     */
    public List<PersonModelView> getCoordinators()
    {
        return coordinators;
    }

    /**
     * @param inCoordinators
     *            the coordinators to set
     */
    public void setCoordinators(final List<PersonModelView> inCoordinators)
    {
        coordinators = inCoordinators;
    }

    /**
     * @return the overview
     */
    public String getOverview()
    {
        return overview;
    }

    /**
     * @param inOverview
     *            the overview to set
     */
    public void setOverview(final String inOverview)
    {
        overview = inOverview;
    }

    /**
     * @return the restricted
     */
    public Boolean isRestricted()
    {
        return restricted;
    }

    /**
     * @param inRestricted
     *            the restricted to set
     */
    public void setRestricted(final Boolean inRestricted)
    {
        restricted = inRestricted;
    }

    /**
     * @return the capabilities
     */
    public List<String> getCapabilities()
    {
        return capabilities;
    }

    /**
     * @param inCapabilities
     *            the capabilities to set
     */
    public void setCapabilities(final List<String> inCapabilities)
    {
        capabilities = inCapabilities;
    }

    /**
     * @return the avatarCropSize
     */
    @Override
    public Integer getAvatarCropSize()
    {
        return avatarCropSize;
    }

    /**
     * @param inAvatarCropSize
     *            the avatarCropSize to set
     */
    @Override
    public void setAvatarCropSize(final Integer inAvatarCropSize)
    {
        avatarCropSize = inAvatarCropSize;
    }

    /**
     * @return the avatarCropX
     */
    @Override
    public Integer getAvatarCropX()
    {
        return avatarCropX;
    }

    /**
     * @param inAvatarCropX
     *            the avatarCropX to set
     */
    @Override
    public void setAvatarCropX(final Integer inAvatarCropX)
    {
        avatarCropX = inAvatarCropX;
    }

    /**
     * @return the avatarCropY
     */
    @Override
    public Integer getAvatarCropY()
    {
        return avatarCropY;
    }

    /**
     * @param inAvatarCropY
     *            the avatarCropY to set
     */
    @Override
    public void setAvatarCropY(final Integer inAvatarCropY)
    {
        avatarCropY = inAvatarCropY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EntityType getEntityType()
    {
        return EntityType.GROUP;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDisplayName()
    {
        return name;
    }

    @Override
    public String getTitle()
    {
        return null;
    }

    @Override
    public FollowerStatus getFollowerStatus()
    {
        return followerStatus;
    }

    @Override
    public void setFollowerStatus(final FollowerStatus inFollowerStatus)
    {
        followerStatus = inFollowerStatus;
    }

    /**
     * @return the streamScopeId
     */
    public Long getStreamScopeId()
    {
        return streamScopeId;
    }

    /**
     * @param inStreamScopeId
     *            the streamScopeId to set
     */
    public void setStreamScopeId(final Long inStreamScopeId)
    {
        streamScopeId = inStreamScopeId;
    }

    /**
     * Get the unique key for this stream.
     *
     * @return the short name
     */
    @Override
    public String getStreamUniqueKey()
    {
        return shortName;
    }

    /**
     * Set the display name.
     *
     * @param inDisplayName
     *            the display name to set
     */
    @Override
    public void setDisplayName(final String inDisplayName)
    {
        setName(inDisplayName);
    }

    /**
     * @return the url
     */
    public String getUrl()
    {
        return url;
    }

    /**
     * @param inUrl
     *            the url to set
     */
    public void setUrl(final String inUrl)
    {
        url = inUrl;
    }

    /**
     * @return the stuckActivityId
     */
    public Long getStickyActivityId()
    {
        return stickyActivityId;
    }

    /**
     * @param inStickyActivityId
     *            the stuckActivityId to set
     */
    public void setStickyActivityId(final Long inStickyActivityId)
    {
        stickyActivityId = inStickyActivityId;
    }

    /**
     * @return the stuckActivity
     */
    public ActivityDTO getStickyActivity()
    {
        return stickyActivity;
    }

    /**
     * @param inStickyActivity
     *            the stuckActivity to set
     */
    public void setStickyActivity(final ActivityDTO inStickyActivity)
    {
        stickyActivity = inStickyActivity;
    }
}

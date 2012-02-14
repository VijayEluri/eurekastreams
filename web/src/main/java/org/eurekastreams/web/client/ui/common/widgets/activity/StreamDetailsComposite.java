/*
 * Copyright (c) 2009-2012 Lockheed Martin Corporation
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
package org.eurekastreams.web.client.ui.common.widgets.activity;

import java.util.List;

import org.eurekastreams.server.action.request.profile.GetCurrentUserFollowingStatusRequest;
import org.eurekastreams.server.action.request.profile.SetFollowingStatusRequest;
import org.eurekastreams.server.action.request.stream.GetFeaturedStreamsPageRequest;
import org.eurekastreams.server.action.request.stream.StreamPopularHashTagsRequest;
import org.eurekastreams.server.domain.DailyUsageSummary;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.Follower;
import org.eurekastreams.server.domain.PagedSet;
import org.eurekastreams.server.domain.dto.FeaturedStreamDTO;
import org.eurekastreams.server.domain.stream.StreamScope.ScopeType;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.eurekastreams.server.search.modelview.PersonModelView.Role;
import org.eurekastreams.server.search.modelview.UsageMetricSummaryDTO;
import org.eurekastreams.server.service.actions.requests.UsageMetricStreamSummaryRequest;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.GotStreamPopularHashTagsEvent;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.PagerResponseEvent;
import org.eurekastreams.web.client.events.ShowNotificationEvent;
import org.eurekastreams.web.client.events.data.AddedFeaturedStreamResponseEvent;
import org.eurekastreams.web.client.events.data.DeletedFeaturedStreamResponse;
import org.eurekastreams.web.client.events.data.DeletedRequestForGroupMembershipResponseEvent;
import org.eurekastreams.web.client.events.data.GotFeaturedStreamsPageResponseEvent;
import org.eurekastreams.web.client.events.data.GotGroupModelViewInformationResponseEvent;
import org.eurekastreams.web.client.events.data.GotPersonFollowerStatusResponseEvent;
import org.eurekastreams.web.client.events.data.GotPersonalInformationResponseEvent;
import org.eurekastreams.web.client.events.data.GotStreamResponseEvent;
import org.eurekastreams.web.client.events.data.GotUsageMetricSummaryEvent;
import org.eurekastreams.web.client.events.data.InsertedGroupMemberResponseEvent;
import org.eurekastreams.web.client.model.BaseActivitySubscriptionModel;
import org.eurekastreams.web.client.model.BaseModel;
import org.eurekastreams.web.client.model.CurrentUserPersonFollowingStatusModel;
import org.eurekastreams.web.client.model.Deletable;
import org.eurekastreams.web.client.model.FeaturedStreamModel;
import org.eurekastreams.web.client.model.GroupActivitySubscriptionModel;
import org.eurekastreams.web.client.model.GroupMembersModel;
import org.eurekastreams.web.client.model.Insertable;
import org.eurekastreams.web.client.model.PersonActivitySubscriptionModel;
import org.eurekastreams.web.client.model.PersonFollowersModel;
import org.eurekastreams.web.client.model.PopularHashTagsModel;
import org.eurekastreams.web.client.model.UsageMetricModel;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.animation.ExpandCollapseAnimation;
import org.eurekastreams.web.client.ui.common.avatar.AvatarWidget.Size;
import org.eurekastreams.web.client.ui.common.charts.StreamAnalyticsChart;
import org.eurekastreams.web.client.ui.common.dialog.Dialog;
import org.eurekastreams.web.client.ui.common.notifier.Notification;
import org.eurekastreams.web.client.ui.common.pager.CoordinatorsPagerUiStrategy;
import org.eurekastreams.web.client.ui.common.pager.FollowerPagerUiStrategy;
import org.eurekastreams.web.client.ui.common.pager.FollowingPagerUiStrategy;
import org.eurekastreams.web.client.ui.common.pager.GroupMembershipRequestPagerUiStrategy;
import org.eurekastreams.web.client.ui.common.pager.PagerComposite;
import org.eurekastreams.web.client.ui.common.stream.FeatureDialogContent;
import org.eurekastreams.web.client.ui.common.stream.FollowDialogContent;
import org.eurekastreams.web.client.ui.common.stream.renderers.AvatarRenderer;
import org.eurekastreams.web.client.ui.common.stream.transformers.StreamSearchLinkBuilder;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * Post box.
 */
public class StreamDetailsComposite extends Composite
{
    /**
     * Binder for building UI.
     */
    private static LocalUiBinder binder = GWT.create(LocalUiBinder.class);

    /**
     * CSS resource.
     */
    interface StreamDetailsStyle extends CssResource
    {
        /**
         * Condensed Stream view.
         *
         * @return Condensed Stream view.
         */
        String condensedStream();

        /**
         * Active option.
         *
         * @return active option style.
         */
        String activeOption();

        /**
         * Everyone avatar.
         *
         * @return everyone avatar.
         */
        String everyoneAvatar();

        /**
         * Following avatar.
         *
         * @return following avatar.
         */
        String followingAvatar();

        /**
         * Private avatar.
         *
         * @return Private avatar.
         */
        String privateAvatar();

        /**
         * Hide details button.
         *
         * @return hide details button.
         */
        String hideDetails();

        /**
         * Featured item header link style.
         *
         * @return Featured item header link style.
         */
        String headerFeatured();

        /**
         * Empty detail style.
         *
         * @return Empty detail style.
         */
        String emptyDetailStyle();

        /** @return Style for the group contact info link (website URL). */
        @ClassName("group-contact-info")
        String groupContactInfo();
    }

    /**
     * CSS style.
     */
    @UiField
    StreamDetailsStyle style;

    /**
     *
     * Binder for building UI.
     */
    interface LocalUiBinder extends UiBinder<Widget, StreamDetailsComposite>
    {
    }

    /**
     * Default constructor.
     */
    public StreamDetailsComposite()
    {
        initWidget(binder.createAndBindUi(this));
        buildPage();
    }

    /**
     * Empty chart.
     */
    @UiField
    DivElement chartEmpty;

    /**
     * UI element for stream about panel.
     */
    @UiField
    HTMLPanel streamAbout;

    /**
     * UI element for stream details.
     */
    @UiField
    DivElement streamDetailsContainer;

    /**
     * UI element for chart.
     */
    @UiField
    HTMLPanel analyticsChartContainer;

    /**
     * UI element for about link.
     */
    @UiField
    Label aboutLink;

    /**
     * UI element for followers link.
     */
    @UiField
    Label followersLink;

    /**
     * UI element for following link.
     */
    @UiField
    Label followingLink;

    /**
     * UI element for featuring a stream.
     */
    @UiField
    Label featureLink;

    /**
     * UI element for admin link.
     */
    @UiField
    Label adminLink;

    /**
     * UI element for coordinators link.
     */
    @UiField
    Label coordinatorsLink;

    /**
     * Contact info title.
     */
    @UiField
    Element contactInfoTitle;

    /**
     * UI element for toggling details.
     */
    @UiField
    Anchor toggleDetails;

    /**
     * UI element for configure link.
     */
    @UiField
    Anchor configureLink;

    /**
     * UI element for follower count.
     */
    @UiField
    SpanElement followerCount;

    /**
     * UI element for following count.
     */
    @UiField
    SpanElement followingCount;

    /**
     * UI element for stream name.
     */
    @UiField
    DivElement streamName;

    /**
     * UI element for stream meta info.
     */
    @UiField
    DivElement streamMeta;

    /**
     * UI element for stream avatar.
     */
    @UiField
    HTMLPanel streamAvatar;

    /**
     * UI element for condensed stream avatar.
     */
    @UiField
    HTMLPanel condensedAvatar;

    /**
     * UI element for stream description.
     */
    @UiField
    DivElement contactInfo;

    /**
     * UI element for stream description.
     */
    @UiField
    DivElement streamDescription;

    /**
     * UI element for stream interests.
     */
    @UiField
    DivElement streamInterests;

    /**
     * UI element for follow link.
     */
    @UiField
    Label followLink;

    /**
     * UI element for stream hash tags.
     */
    @UiField
    FlowPanel streamHashtags;

    /**
     * UI element for stream followers.
     */
    @UiField
    PagerComposite streamFollowers;

    /**
     * UI element for stream following.
     */
    @UiField
    PagerComposite streamFollowing;

    /**
     * UI element for stream coordinators.
     */
    @UiField
    PagerComposite streamCoordinators;

    /**
     * UI element admin tab content.
     */
    @UiField
    PagerComposite adminContent;

    /**
     * Show following link.
     */
    @UiField
    Label showFollowing;

    /**
     * Show followers link.
     */
    @UiField
    Label showFollowers;

    /**
     * Chart.
     */
    @UiField
    StreamAnalyticsChart chart;

    /**
     * Average viewers.
     */
    @UiField
    SpanElement avgViewers;

    /**
     * Average views.
     */
    @UiField
    SpanElement avgViews;

    /**
     * Average contributors.
     */
    @UiField
    SpanElement avgContributors;

    /**
     * Stream info container.
     */
    @UiField
    DivElement streamInfoContainer;

    /**
     * Average messages.
     */
    @UiField
    SpanElement avgMessages;

    /**
     * Average comments.
     */
    @UiField
    SpanElement avgComments;

    /**
     * Total contributors.
     */
    @UiField
    SpanElement totalContributors;

    /**
     * Total activities.
     */
    @UiField
    SpanElement totalActivities;

    /**
     * Total activities.
     */
    @UiField
    SpanElement totalComments;

    /**
     * Current status.
     */
    private Follower.FollowerStatus status;

    /**
     * Expand animation duration.
     */
    private static final int EXPAND_ANIMATION_DURATION = 500;

    /**
     * Content padding for details.
     */
    private static final int CONTENT_PADDING = 0;

    /**
     * Number of days to gather metrics for.
     */
    private static final Integer NUM_DAYS_FOR_METRICS = 30;

    /**
     * Avatar Renderer.
     */
    private final AvatarRenderer avatarRenderer = new AvatarRenderer();

    /**
     * The helper to build hyperlinks to stream search.
     */
    private final StreamSearchLinkBuilder streamSearchLinkBuilder = new StreamSearchLinkBuilder();

    /**
     * Expand/Collapse animation.
     */
    private ExpandCollapseAnimation detailsContainerAnimation;

    /**
     * Last following handler.
     */
    private HandlerRegistration lastHandler;

    /**
     * Last feature handler.
     */
    private HandlerRegistration lastFeatureHandler;

    /**
     * Model used to set following status.
     */
    private BaseModel followModel;

    /**
     * Stream request.
     */
    private String streamReq;

    /**
     * Stream ID.
     */
    private Long streamId;

    /**
     * Stream is featured.
     */
    private boolean inFeatured;

    /**
     * FeaturedStreamDTO object representing current person/group.
     */
    private FeaturedStreamDTO currentFeaturedStreamDTO;

    /**
     * Featured streams.
     */
    private PagedSet<FeaturedStreamDTO> featuredStreams;

    /**
     * Custom Avatars.
     */
    public enum CustomAvatar
    {
        /**
         * Everyone avatar.
         */
        EVERYONE,
        /**
         * Following avatar.
         */
        FOLLOWING,
        /**
         * Custom stream avatar.
         */
        CUSTOM
    };

    /**
     * Build page.
     */
    private void buildPage()
    {
        // Default style. Prevent flashing.
        streamName.setInnerText("");
        addStyleName(style.condensedStream());
        followLink.setVisible(false);
        featureLink.setVisible(Session.getInstance().getCurrentPersonRoles().contains(Role.SYSTEM_ADMIN));
        detailsContainerAnimation = new ExpandCollapseAnimation(streamDetailsContainer, EXPAND_ANIMATION_DURATION);

        streamAvatar.add(avatarRenderer.render(0L, null, EntityType.PERSON, Size.Normal));

        streamFollowers.init(new FollowerPagerUiStrategy());
        streamFollowing.init(new FollowingPagerUiStrategy());
        adminContent.init(new GroupMembershipRequestPagerUiStrategy());
        streamCoordinators.init(new CoordinatorsPagerUiStrategy());

        streamFollowers.setVisible(false);
        streamFollowing.setVisible(false);
        configureLink.setVisible(false);
        adminLink.setVisible(false);
        coordinatorsLink.setVisible(false);

        showFollowing.setVisible(false);
        followingCount.getStyle().setDisplay(Display.NONE);
        followingLink.setVisible(false);
        adminContent.setVisible(false);

        followersLink.addClickHandler(new ClickHandler()
        {

            public void onClick(final ClickEvent event)
            {
                openFollower();
            }
        });

        followingLink.addClickHandler(new ClickHandler()
        {

            public void onClick(final ClickEvent event)
            {
                openFollowing();
            }
        });

        coordinatorsLink.addClickHandler(new ClickHandler()
        {

            public void onClick(final ClickEvent event)
            {
                openCoordinators();
            }
        });

        adminLink.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent event)
            {
                openAdmin();
            }
        });

        showFollowers.addClickHandler(new ClickHandler()
        {

            public void onClick(final ClickEvent event)
            {
                openFollower();
            }
        });

        showFollowing.addClickHandler(new ClickHandler()
        {

            public void onClick(final ClickEvent event)
            {
                openFollowing();
            }
        });

        aboutLink.addClickHandler(new ClickHandler()
        {

            public void onClick(final ClickEvent event)
            {
                openAbout();
            }
        });

        toggleDetails.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent event)
            {
                if (detailsContainerAnimation.isExpanded())
                {
                    toggleDetails.removeStyleName(style.hideDetails());
                    detailsContainerAnimation.collapse();
                }
                else
                {
                    openAbout();
                }
            }
        });

        addEvents();
    }

    /**
     * Add events.
     */
    private void addEvents()
    {
        EventBus.getInstance().addObserver(GotStreamPopularHashTagsEvent.class,
                new Observer<GotStreamPopularHashTagsEvent>()
                {
                    public void update(final GotStreamPopularHashTagsEvent event)
                    {
                        // Note: using widgets since IE will lose all history when navigating using a simple anchor
                        streamHashtags.clear();

                        boolean empty = true;
                        for (String tag : event.getPopularHashTags())
                        {
                            if (empty)
                            {
                                empty = false;
                            }
                            else
                            {
                                streamHashtags.add(new InlineLabel(" "));
                            }
                            streamHashtags.add(new InlineHyperlink(tag, streamSearchLinkBuilder
                                    .buildHashtagSearchLink(tag, null).substring(1)));
                        }

                        if (empty)
                        {
                            streamHashtags.add(new InlineLabel("No popular hashtags."));
                            streamHashtags.addStyleName(style.emptyDetailStyle());
                        }
                        else
                        {
                            streamHashtags.removeStyleName(style.emptyDetailStyle());
                        }
                    }
                });

        EventBus.getInstance().addObserver(PagerResponseEvent.class, new Observer<PagerResponseEvent>()
        {
            public void update(final PagerResponseEvent event)
            {
                detailsContainerAnimation.expandWithPadding(CONTENT_PADDING);
            }
        });

        EventBus.getInstance().addObserver(GotUsageMetricSummaryEvent.class,
                new Observer<GotUsageMetricSummaryEvent>()
                {
                    public void update(final GotUsageMetricSummaryEvent event)
                    {
                        UsageMetricSummaryDTO data = event.getResponse();

                        List<DailyUsageSummary> stats = data.getDailyStatistics();

                        chart.setVisible(stats != null && stats.size() > 0);
                        chartEmpty.getStyle().setDisplay(chart.isVisible() ? Display.NONE : Display.BLOCK);

                        if (stats != null)
                        {
                            for (int i = 0; i < stats.size(); i++)
                            {
                                if (null == stats.get(i))
                                {
                                    chart.addPoint(i, 0.0);
                                }
                                else
                                {
                                    chart.addPoint(i, stats.get(i).getStreamViewCount());
                                }
                            }
                        }

                        avgComments.setInnerText(NumberFormat.getDecimalFormat().format(
                                data.getAverageDailyCommentPerActivityCount()));
                        avgContributors.setInnerText(NumberFormat.getDecimalFormat().format(
                                data.getAverageDailyStreamContributorCount()));
                        avgMessages.setInnerText(NumberFormat.getDecimalFormat().format(
                                data.getAverageDailyMessageCount()));
                        avgViewers.setInnerText(//
                                NumberFormat.getDecimalFormat().format(data.getAverageDailyStreamViewerCount()));
                        avgViews.setInnerText(NumberFormat.getDecimalFormat().format(
                                data.getAverageDailyStreamViewCount()));

                        totalContributors.setInnerText(""
                                + NumberFormat.getDecimalFormat().format(data.getTotalContributorCount()));
                        totalActivities.setInnerText(NumberFormat.getDecimalFormat().format(
                                data.getTotalActivityCount()));
                        totalComments.setInnerText(NumberFormat.getDecimalFormat().format(data.getTotalCommentCount()));
                        chart.update();
                    }
                });

        EventBus.getInstance().addObserver(GotFeaturedStreamsPageResponseEvent.class,
                new Observer<GotFeaturedStreamsPageResponseEvent>()
                {
                    public void update(final GotFeaturedStreamsPageResponseEvent response)
                    {
                        featuredStreams = response.getResponse();
                        updateFeatureLink(currentFeaturedStreamDTO);
                    }
                });

        EventBus.getInstance().addObserver(GotPersonalInformationResponseEvent.class,
                new Observer<GotPersonalInformationResponseEvent>()
                {
                    public void update(final GotPersonalInformationResponseEvent event)
                    {
                        gotPerson(event.getResponse());
                    }

                });

        EventBus.getInstance().addObserver(GotGroupModelViewInformationResponseEvent.class,
                new Observer<GotGroupModelViewInformationResponseEvent>()
                {
                    public void update(final GotGroupModelViewInformationResponseEvent event)
                    {
                        gotGroup(event.getResponse());
                    }
                });

        EventBus.getInstance().addObserver(GotStreamResponseEvent.class, new Observer<GotStreamResponseEvent>()
        {
            public void update(final GotStreamResponseEvent event)
            {
                streamReq = event.getJsonRequest();
            }
        });

        EventBus.getInstance().addObserver(DeletedRequestForGroupMembershipResponseEvent.class,
                new Observer<DeletedRequestForGroupMembershipResponseEvent>()
                {
                    public void update(final DeletedRequestForGroupMembershipResponseEvent event)
                    {
                        openAdmin();
                    }
                }, true);

        EventBus.getInstance().addObserver(InsertedGroupMemberResponseEvent.class,
                new Observer<InsertedGroupMemberResponseEvent>()
                {
                    public void update(final InsertedGroupMemberResponseEvent event)
                    {
                        openAdmin();
                    }
                }, true);

        EventBus.getInstance().addObserver(AddedFeaturedStreamResponseEvent.class,
                new Observer<AddedFeaturedStreamResponseEvent>()
                {
                    public void update(final AddedFeaturedStreamResponseEvent event)
                    {
                        if (Session.getInstance().getCurrentPersonRoles().contains(Role.SYSTEM_ADMIN))
                        {
                            FeaturedStreamModel.getInstance().fetch(
                                    new GetFeaturedStreamsPageRequest(0, Integer.MAX_VALUE), true);
                        }
                    }
                });

        EventBus.getInstance().addObserver(DeletedFeaturedStreamResponse.class,
                new Observer<DeletedFeaturedStreamResponse>()
                {
                    public void update(final DeletedFeaturedStreamResponse event)
                    {
                        if (Session.getInstance().getCurrentPersonRoles().contains(Role.SYSTEM_ADMIN))
                        {
                            FeaturedStreamModel.getInstance().fetch(
                                    new GetFeaturedStreamsPageRequest(0, Integer.MAX_VALUE), true);
                        }
                    }
                });
    }

    /**
     * Set the stream title and avatar.
     *
     * @param inStreamTitle
     *            the title.
     * @param avatar
     *            the avatar.
     */
    public void setStreamTitle(final String inStreamTitle, final CustomAvatar avatar)
    {
        Session.getInstance().setPageTitle(inStreamTitle);
        setStreamName(inStreamTitle);

        switch (avatar)
        {
        case EVERYONE:
            condensedAvatar.addStyleName(style.followingAvatar());
            break;
        case FOLLOWING:
            condensedAvatar.addStyleName(style.everyoneAvatar());
            break;
        case CUSTOM:
            break;
        default:
            break;
        }

    }

    /**
     * Initialize the view.
     */
    public void init()
    {
        chart.clearPoints();
        chart.update();

        // Collapse right away if open.
        streamDetailsContainer.getStyle().setHeight(0.0, Unit.PX);
        toggleDetails.removeStyleName(style.hideDetails());

        condensedAvatar.removeStyleName(style.everyoneAvatar());
        condensedAvatar.removeStyleName(style.followingAvatar());
        condensedAvatar.removeStyleName(style.privateAvatar());
    }

    /**
     * Set Condensed mode.
     *
     * @param isCondensed
     *            condensed mode.
     */
    public void setCondensedMode(final boolean isCondensed)
    {
        if (isCondensed)
        {
            addStyleName(style.condensedStream());

        }
        else
        {
            removeStyleName(style.condensedStream());
        }
    }

    /**
     * Sets the displayed stream name (in a way that the elided style will update properly).
     *
     * @param name
     *            New name.
     */
    private void setStreamName(final String name)
    {
        streamName.removeFromParent();
        streamName.setInnerText(name);
        streamName.setTitle(name);
        streamInfoContainer.insertFirst(streamName);
    }

    /**
     * Go the group.
     *
     * @param group
     *            the group.
     */
    private void gotGroup(final DomainGroupModelView group)
    {
        showFollowing.setVisible(false);
        followingCount.getStyle().setDisplay(Display.NONE);
        followingLink.setVisible(false);
        coordinatorsLink.setVisible(true);

        Session.getInstance().setPageTitle(group.getName());
        setStreamName(group.getName());

        if (group.isRestricted())
        {
            condensedAvatar.addStyleName(style.privateAvatar());
            addStyleName(style.condensedStream());
        }
        else
        {
            streamId = group.getStreamId();

            boolean isCoordinator = false;

            for (PersonModelView coordinator : group.getCoordinators())
            {
                if (coordinator.getAccountId().equals(Session.getInstance().getCurrentPerson().getAccountId()))
                {
                    isCoordinator = true;
                    break;
                }
            }

            if (isCoordinator || Session.getInstance().getCurrentPersonRoles().contains(Role.SYSTEM_ADMIN))
            {
                if (!group.isPublic())
                {
                    adminLink.setVisible(true);
                    String detailTabValue = Session.getInstance().getParameterValue("detailtab");
                    if ((detailTabValue != null) && detailTabValue.compareToIgnoreCase("admin") == 0)
                    {
                        openAdmin();
                    }
                }
                configureLink.setVisible(true);
                configureLink.setHref("#groupsettings/" + group.getShortName());
            }
            else
            {
                configureLink.setVisible(false);
            }

            contactInfoTitle.setInnerText("Website");

            String groupUrl = group.getUrl();
            if (groupUrl == null || groupUrl.isEmpty())
            {
                contactInfo.setInnerHTML("No contact information entered.");
                contactInfo.addClassName(style.emptyDetailStyle());
            }
            else
            {
                // insert wbr tags to help IE break line somewhere nice.
                String displayUrl = groupUrl.replaceAll("([/?&=]+)", "$1<wbr />");
                contactInfo.setInnerHTML("<a href=\"" + groupUrl + "\">" + displayUrl + "</a>");
                contactInfo.removeClassName(style.emptyDetailStyle());
                contactInfo.addClassName(style.groupContactInfo());
            }

            updateFollowLink(group.getShortName(), EntityType.GROUP);
            FeaturedStreamDTO featuredStreamDTO = new FeaturedStreamDTO();
            featuredStreamDTO.setDescription(group.getDescription());
            featuredStreamDTO.setStreamId(group.getStreamId());
            featuredStreamDTO.setStreamType(ScopeType.GROUP);
            featuredStreamDTO.setDisplayName(group.getDisplayName());

            currentFeaturedStreamDTO = featuredStreamDTO;

            if (Session.getInstance().getCurrentPersonRoles().contains(Role.SYSTEM_ADMIN))
            {
                FeaturedStreamModel.getInstance().fetch(new GetFeaturedStreamsPageRequest(0, Integer.MAX_VALUE), true);
            }

            streamMeta.setInnerText("");
            streamAvatar.clear();
            streamAvatar.add(avatarRenderer.render(group.getEntityId(), group.getAvatarId(), EntityType.GROUP,
                    Size.Normal));

            followerCount.setInnerText(Integer.toString(group.getFollowersCount()));
            streamDescription.setInnerText(group.getDescription());

            if (group.getDescription() == null || group.getDescription().length() == 0)
            {
                streamDescription.setInnerHTML("No group description entered.");
                streamDescription.addClassName(style.emptyDetailStyle());
            }
            else
            {
                streamDescription.removeClassName(style.emptyDetailStyle());
            }

            String interestString = "";
            for (String interest : group.getCapabilities())
            {
                interestString += "<a href='#search?query=" + interest + "'>" + interest + "</a> ";
            }
            streamInterests.setInnerHTML(interestString);

            if (interestString.length() == 0)
            {
                streamInterests.setInnerHTML("No interested entered.");
                streamInterests.addClassName(style.emptyDetailStyle());
            }
            else
            {
                streamInterests.removeClassName(style.emptyDetailStyle());
            }

            PopularHashTagsModel.getInstance().fetch(
                    new StreamPopularHashTagsRequest(ScopeType.GROUP, group.getShortName()), true);
            UsageMetricModel.getInstance().fetch(
                    new UsageMetricStreamSummaryRequest(NUM_DAYS_FOR_METRICS, group.getStreamId()), true);
        }

    }

    /**
     * Update the feature link.
     *
     * @param featuredStreamDTO
     *            the stream.
     */
    public void updateFeatureLink(final FeaturedStreamDTO featuredStreamDTO)
    {
        if (Session.getInstance().getCurrentPersonRoles().contains(Role.SYSTEM_ADMIN))
        {
            inFeatured = false;
            featureLink.removeStyleName(style.headerFeatured());

            for (FeaturedStreamDTO featured : featuredStreams.getPagedSet())
            {
                if (featured.getStreamId().longValue() == streamId.longValue())
                {
                    inFeatured = true;
                    featuredStreamDTO.setId(featured.getId());
                    featureLink.addStyleName(style.headerFeatured());
                    break;
                }
            }

            if (lastFeatureHandler != null)
            {
                lastFeatureHandler.removeHandler();
            }

            lastFeatureHandler = featureLink.addClickHandler(new ClickHandler()
            {
                public void onClick(final ClickEvent event)
                {
                    if (inFeatured)
                    {
                        FeaturedStreamModel.getInstance().delete(featuredStreamDTO.getId());
                        EventBus.getInstance().notifyObservers(
                                new ShowNotificationEvent(new Notification(
                                        "Stream has been removed from the featured streams list.")));
                    }
                    else
                    {
                        Dialog.showCentered(new FeatureDialogContent(featuredStreamDTO));
                    }
                }
            });

        }
    }

    /**
     * Update the following element.
     *
     * @param entityId
     *            the id of the entity.
     * @param type
     *            the type.
     */
    public void updateFollowLink(final String entityId, final EntityType type)
    {
        if (!entityId.equals(Session.getInstance().getCurrentPerson().getAccountId()))
        {
            followModel = GroupMembersModel.getInstance();
            final BaseActivitySubscriptionModel subscribeModel = EntityType.PERSON.equals(type) ? // \n
            PersonActivitySubscriptionModel.getInstance()
                    : GroupActivitySubscriptionModel.getInstance();

            if (type.equals(EntityType.PERSON))
            {
                followModel = PersonFollowersModel.getInstance();
            }

            if (lastHandler != null)
            {
                lastHandler.removeHandler();
            }

            lastHandler = followLink.addClickHandler(new ClickHandler()
            {
                public void onClick(final ClickEvent event)
                {
                    SetFollowingStatusRequest request;

                    switch (status)
                    {
                    case FOLLOWING:
                        request = new SetFollowingStatusRequest(Session.getInstance().getCurrentPerson()
                                .getAccountId(), entityId, type, false, Follower.FollowerStatus.NOTFOLLOWING);
                        ((Deletable<SetFollowingStatusRequest>) followModel).delete(request);
                        onFollowerStatusChanged(Follower.FollowerStatus.NOTFOLLOWING);
                        break;
                    case NOTFOLLOWING:
                        request = new SetFollowingStatusRequest(Session.getInstance().getCurrentPerson()
                                .getAccountId(), entityId, type, false, Follower.FollowerStatus.FOLLOWING);
                        ((Insertable<SetFollowingStatusRequest>) followModel).insert(request);
                        Dialog.showCentered(new FollowDialogContent(streamName.getInnerText(), streamReq, streamId,
                                type, subscribeModel, entityId));
                        onFollowerStatusChanged(Follower.FollowerStatus.FOLLOWING);
                        break;
                    default:
                        // do nothing.
                        break;
                    }
                }
            });

            Session.getInstance()
                    .getEventBus()
                    .addObserver(GotPersonFollowerStatusResponseEvent.class,
                            new Observer<GotPersonFollowerStatusResponseEvent>()
                            {
                                public void update(final GotPersonFollowerStatusResponseEvent event)
                                {
                                    onFollowerStatusChanged(event.getResponse());
                                }
                            });

            CurrentUserPersonFollowingStatusModel.getInstance().fetch(
                    new GetCurrentUserFollowingStatusRequest(entityId, type), true);
        }
        else
        {
            followLink.setVisible(false);
        }
    }

    /**
     * Open the about panel.
     */
    private void openAbout()
    {
        toggleDetails.addStyleName(style.hideDetails());
        aboutLink.addStyleName(style.activeOption());
        followingLink.removeStyleName(style.activeOption());
        followersLink.removeStyleName(style.activeOption());
        adminLink.removeStyleName(style.activeOption());

        streamFollowing.setVisible(false);
        streamAbout.setVisible(true);
        streamFollowers.setVisible(false);
        adminContent.setVisible(false);
        streamCoordinators.setVisible(false);
        coordinatorsLink.removeStyleName(style.activeOption());

        detailsContainerAnimation.expandWithPadding(CONTENT_PADDING);
    }

    /**
     * Open the following panel.
     */
    private void openFollowing()
    {
        toggleDetails.addStyleName(style.hideDetails());
        aboutLink.removeStyleName(style.activeOption());
        followingLink.addStyleName(style.activeOption());
        followersLink.removeStyleName(style.activeOption());
        adminLink.removeStyleName(style.activeOption());

        streamFollowers.setVisible(false);
        streamAbout.setVisible(false);
        streamFollowing.setVisible(true);
        adminContent.setVisible(false);
        streamCoordinators.setVisible(false);
        coordinatorsLink.removeStyleName(style.activeOption());

        streamFollowing.load();
    }

    /**
     * Open the followers panel.
     */
    private void openFollower()
    {
        toggleDetails.addStyleName(style.hideDetails());
        aboutLink.removeStyleName(style.activeOption());
        followingLink.removeStyleName(style.activeOption());
        followersLink.addStyleName(style.activeOption());
        adminLink.removeStyleName(style.activeOption());

        streamFollowing.setVisible(false);
        streamAbout.setVisible(false);
        streamFollowers.setVisible(true);
        adminContent.setVisible(false);
        streamCoordinators.setVisible(false);
        coordinatorsLink.removeStyleName(style.activeOption());

        streamFollowers.load();
    }

    /**
     * Open the coordinators panel.
     */
    private void openCoordinators()
    {
        toggleDetails.addStyleName(style.hideDetails());
        aboutLink.removeStyleName(style.activeOption());
        followingLink.removeStyleName(style.activeOption());
        followersLink.removeStyleName(style.activeOption());
        adminLink.removeStyleName(style.activeOption());

        streamFollowing.setVisible(false);
        streamAbout.setVisible(false);
        streamFollowers.setVisible(false);
        adminContent.setVisible(false);
        streamCoordinators.setVisible(true);
        coordinatorsLink.addStyleName(style.activeOption());

        streamCoordinators.load();
    }

    /**
     * Open the Admin panel.
     */
    private void openAdmin()
    {
        toggleDetails.addStyleName(style.hideDetails());
        aboutLink.removeStyleName(style.activeOption());
        followingLink.removeStyleName(style.activeOption());
        followersLink.removeStyleName(style.activeOption());
        coordinatorsLink.removeStyleName(style.activeOption());
        adminLink.addStyleName(style.activeOption());

        streamFollowing.setVisible(false);
        streamAbout.setVisible(false);
        streamFollowers.setVisible(false);
        streamCoordinators.setVisible(false);
        adminContent.setVisible(true);
        adminContent.load();
    }

    /**
     * When the following status changes.
     *
     * @param inStatus
     *            status.
     */
    private void onFollowerStatusChanged(final Follower.FollowerStatus inStatus)
    {
        followLink.setVisible(true);
        status = inStatus;

        switch (inStatus)
        {
        case FOLLOWING:
            followLink.addStyleName(StaticResourceBundle.INSTANCE.coreCss().unFollowLink());
            break;
        case NOTFOLLOWING:
            followLink.removeStyleName(StaticResourceBundle.INSTANCE.coreCss().unFollowLink());
            break;
        default:
            break;
        }
    }

    /**
     * Got the person.
     *
     * @param person
     *            the person.
     */
    private void gotPerson(final PersonModelView person)
    {
        showFollowing.setVisible(true);
        followingCount.getStyle().setDisplay(Display.INLINE);
        followingLink.setVisible(true);
        adminLink.setVisible(false);
        coordinatorsLink.setVisible(false);

        streamId = person.getStreamId();
        Session.getInstance().setPageTitle(person.getDisplayName());

        if (person.getAccountId().equals(Session.getInstance().getCurrentPerson().getAccountId()))
        {
            configureLink.setVisible(true);
            configureLink.setHref("#personalsettings/" + person.getAccountId());
        }
        else
        {
            configureLink.setVisible(false);
        }

        updateFollowLink(person.getAccountId(), EntityType.PERSON);
        FeaturedStreamDTO featuredStreamDTO = new FeaturedStreamDTO();
        featuredStreamDTO.setDescription(person.getDescription());
        featuredStreamDTO.setStreamId(person.getStreamId());
        featuredStreamDTO.setStreamType(ScopeType.PERSON);
        featuredStreamDTO.setDisplayName(person.getDisplayName());

        currentFeaturedStreamDTO = featuredStreamDTO;

        if (Session.getInstance().getCurrentPersonRoles().contains(Role.SYSTEM_ADMIN))
        {
            FeaturedStreamModel.getInstance().fetch(new GetFeaturedStreamsPageRequest(0, Integer.MAX_VALUE), true);
        }

        setStreamName(person.getDisplayName());

        streamMeta.setInnerText(person.getTitle());
        streamAvatar.clear();
        streamAvatar.add(avatarRenderer.render(person.getEntityId(), person.getAvatarId(), EntityType.PERSON,
                Size.Normal));

        followerCount.setInnerText(Integer.toString(person.getFollowersCount()));
        followingCount.setInnerText(Integer.toString(person.getFollowingCount() + person.getGroupsCount()));
        streamDescription.setInnerText(person.getJobDescription());

        if (person.getJobDescription() == null || person.getJobDescription().length() == 0)
        {
            streamDescription.setInnerText("No job description entered.");
            streamDescription.addClassName(style.emptyDetailStyle());
        }
        else
        {
            streamDescription.removeClassName(style.emptyDetailStyle());
        }

        String interestString = "";
        for (String interest : person.getInterests())
        {
            interestString += "<a href='#search?query=" + interest + "'>" + interest + "</a> ";
        }
        streamInterests.setInnerHTML(interestString);

        if (interestString.length() == 0)
        {
            streamInterests.setInnerHTML("No interests entered.");
            streamInterests.addClassName(style.emptyDetailStyle());
        }
        else
        {
            streamInterests.removeClassName(style.emptyDetailStyle());
        }

        contactInfoTitle.setInnerText("Contact Information");
        String contact = "";
        String email = person.getEmail();
        if (email != null)
        {
            contact = "<a href=\"mailto:" + email + "\">" + email + "</a>";
        }
        if (person.getWorkPhone() != null)
        {
            if (email != null)
            {
                contact += "<br />";
            }
            contact += person.getWorkPhone();
        }
        contactInfo.setInnerHTML(contact);

        if (contact.length() == 0)
        {
            contactInfo.setInnerHTML("No contact information entered.");
            contactInfo.addClassName(style.emptyDetailStyle());
        }
        else
        {
            contactInfo.removeClassName(style.emptyDetailStyle());
        }

        PopularHashTagsModel.getInstance().fetch(
                new StreamPopularHashTagsRequest(ScopeType.PERSON, person.getAccountId()), true);

        UsageMetricModel.getInstance().fetch(
                new UsageMetricStreamSummaryRequest(NUM_DAYS_FOR_METRICS, person.getStreamId()), true);

    }

}

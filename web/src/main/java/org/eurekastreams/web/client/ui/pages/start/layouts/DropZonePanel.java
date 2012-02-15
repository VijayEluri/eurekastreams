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
package org.eurekastreams.web.client.ui.pages.start.layouts;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.eurekastreams.server.domain.Page;
import org.eurekastreams.server.domain.TabGroupType;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.UpdateHistoryEvent;
import org.eurekastreams.web.client.events.UpdatedHistoryParametersEvent;
import org.eurekastreams.web.client.events.data.GotStartPageTabsResponseEvent;
import org.eurekastreams.web.client.history.CreateUrlRequest;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;
import org.eurekastreams.web.client.ui.pages.start.GadgetPanel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * DropZonePanel creates a bunch of different width drop zones.
 */
public class DropZonePanel extends VerticalPanel
{
    /**
     * The zone number.
     */
    private int zoneNumber = 0;

    /**
     * Last history event.
     */
    private UpdatedHistoryParametersEvent lastHistoryEvent = null;

    /**
     * List of gadget zones.
     */
    private final List<GadgetPanel> gadgetZones = new LinkedList<GadgetPanel>();

    /**
     * Create a 33% width drop zone column.
     *
     * @param inZoneNumber
     *            the zone number for the panel.
     * @return the created drop zone
     */
    public static DropZonePanel getThirdColumnDropZone(final int inZoneNumber)
    {
        DropZonePanel retPanel = new DropZonePanel();

        retPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().dropZoneColumn33());
        retPanel.setZoneNumber(inZoneNumber);
        return retPanel;
    }

    /**
     * Create a 66% width drop zone column.
     *
     * @param inZoneNumber
     *            the zone number for the panel.
     * @return the created drop zone
     */
    public static DropZonePanel getTwoThirdColumnDropZone(final int inZoneNumber)
    {
        DropZonePanel retPanel = new DropZonePanel();
        retPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().dropZoneColumn66());
        retPanel.setZoneNumber(inZoneNumber);
        return retPanel;
    }

    /**
     * Create a 50% width drop zone column.
     *
     * @param inZoneNumber
     *            the zone number for the panel.
     * @return the created drop zone
     */
    public static DropZonePanel getHalfColumnDropZone(final int inZoneNumber)
    {
        DropZonePanel retPanel = new DropZonePanel();
        retPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().dropZoneColumn50());
        retPanel.setZoneNumber(inZoneNumber);
        return retPanel;
    }

    /**
     * Create a 25% width drop zone column.
     *
     * @param inZoneNumber
     *            the zone number for the panel.
     * @return the created drop zone
     */
    public static DropZonePanel getQuarterColumnDropZone(final int inZoneNumber)
    {
        DropZonePanel retPanel = new DropZonePanel();
        retPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().dropZoneColumn25());
        retPanel.setZoneNumber(inZoneNumber);
        return retPanel;
    }

    /**
     * Create a 100% width drop zone row.
     *
     * @param inZoneNumber
     *            the zone number for the panel.
     * @return the created drop zone
     */
    public static DropZonePanel getFullRowDropZone(final int inZoneNumber)
    {
        DropZonePanel retPanel = new DropZonePanel();
        retPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().dropZoneRow());
        retPanel.setZoneNumber(inZoneNumber);
        return retPanel;
    }

    /**
     * This spacer is here so the drop zone doesn't collapse when all gadgets are removed fis the rom it.
     */
    private FlowPanel spacer;

    /**
     * The current tab id.
     */
    private String tabId;

    /**
     * Creates a a DropZone panel and passes in the Gadget Renderer.
     */
    public DropZonePanel()
    {
        // this class is used for maximizing the drop zone.
        addStyleName(StaticResourceBundle.INSTANCE.coreCss().dropZone());
        Session.getInstance().getEventBus().addObserver(UpdatedHistoryParametersEvent.class,
                new Observer<UpdatedHistoryParametersEvent>()
                {
                    public void update(final UpdatedHistoryParametersEvent event)
                    {
                        lastHistoryEvent = event;
                        if (event.getParameters().get("tab") != null)
                        {
                            tabId = event.getParameters().get("tab");
                        }
                    }
                }, true);

        Session.getInstance().getEventBus().addObserver(GotStartPageTabsResponseEvent.class,
                new Observer<GotStartPageTabsResponseEvent>()
                {
                    public void update(final GotStartPageTabsResponseEvent event)
                    {
                        tabId = Long.toString(event.getResponse().getTabs(TabGroupType.START).get(0).getId());
                    }
                }, true);
    }

    /**
     * How many gadgets do we have?
     *
     * @return the number of gadgets.
     */
    public int getVisibleGadgetCount()
    {
        int count = 0;

        for (Widget panel : getChildren())
        {
            if (panel.isVisible())
            {
                count++;
            }
        }

        return count;
    }

    /**
     * Retrieve the position of the current gadget based on visible widgets. (Deleted gadgets are hidden and can throw
     * off the position).
     *
     * @param target
     *            - widget for which the locatin is to be determined.
     * @return location.
     */
    public int getVisibleGadgetPosition(final Widget target)
    {
        int count = 0;

        for (Widget panel : getChildren())
        {
            if (panel.equals(target))
            {
                break;
            }
            if (panel.isVisible())
            {
                count++;
            }
        }

        return count;
    }

    /**
     * Insert a Gadget.
     *
     * @param gadget
     *            the gadget to insert.
     */
    public void insertGadget(final GadgetPanel gadget)
    {
        gadget.setDropZone(this);
        this.add(gadget);
        gadgetZones.add(gadget);
    }

    /**
     * Insert a gadget with index.
     *
     * @param gadget
     *            the gadget panel.
     * @param index
     *            the index.
     */
    public void insertGadget(final GadgetPanel gadget, final int index)
    {
        gadget.setDropZone(this);

        if (index <= getWidgetCount())
        {
            this.insert(gadget, index);
        }
        else
        {
            // Should never happen, but was added to 'robustify' ;)
            this.add(gadget);
        }

        gadgetZones.add(gadget);
    }

    /**
     * Set the space.
     *
     * @param inSpacer
     *            the spacer.
     */
    public void setSpacer(final FlowPanel inSpacer)
    {
        spacer = inSpacer;
        Anchor addAGadget = new Anchor("Add an App");
        addAGadget.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent event)
            {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("dropzone", Integer.toString(getZoneNumber()));

                if (lastHistoryEvent == null || lastHistoryEvent.getParameters().get("tab") == null)
                {
                    params.put("tab", tabId);
                }

                Session.getInstance().getEventBus().notifyObservers(
                        new UpdateHistoryEvent(new CreateUrlRequest(Page.GALLERY, params, false)));
            }
        });
        spacer.add(addAGadget);
        spacer.addStyleName(StaticResourceBundle.INSTANCE.coreCss().layoutSpacer());
        spacer.setVisible(false);
        this.add(spacer);
    }

    /**
     * Getter for the gadget zones.
     *
     * @return the gadget zones.
     */

    public List<GadgetPanel> getGadgetZones()
    {
        return gadgetZones;
    }

    /**
     * Set the zone number.
     *
     * @param inZoneNumber
     *            the zone number.
     */
    public void setZoneNumber(final int inZoneNumber)
    {
        zoneNumber = inZoneNumber;
    }

    /**
     * Get the zone numebr.
     *
     * @return the zone number.
     */
    public int getZoneNumber()
    {
        return zoneNumber;
    }
}

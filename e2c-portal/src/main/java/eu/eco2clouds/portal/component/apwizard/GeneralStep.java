/* 
 * Copyright 2014 Politecnico di Milano.
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

 *  @author: Pierluigi Plebani, Politecnico di Milano, Italy
 *  e-mail pierluigi.plebani@polimi.it

 */
package eu.eco2clouds.portal.component.apwizard;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import eu.eco2clouds.accounting.datamodel.parser.Testbed;
import eu.eco2clouds.applicationProfile.datamodel.Compute;
import eu.eco2clouds.applicationProfile.datamodel.Contexts;
import eu.eco2clouds.applicationProfile.datamodel.ExperimentDescriptor;
import eu.eco2clouds.applicationProfile.datamodel.Resource;
import eu.eco2clouds.applicationProfile.datamodel.ResourceCompute;
import eu.eco2clouds.portal.E2CPortal;
import eu.eco2clouds.portal.page.APWizardLayout;
import eu.eco2clouds.portal.scheduler.SchedulerManagerFactory;
import java.util.ArrayList;

/**
 *
 *  
 */
public class GeneralStep extends VerticalLayout {

    final private TextField name = new TextField("Application name", "");
    final private TextArea description = new TextArea("Description", "");

    final private TextField duration = new TextField("Duration (min)", "");
    final private ComboBox monitoring = new ComboBox("Monitor site", this.getSites());
    
    final private CheckBox optimize = new CheckBox("optimze");

    private APWizardLayout mainLayout;

    public static String MONITOR_RESOURCE_NAME = "BonFIRE-Monitor";
    public static String MONITOR_INSTANCE_TYPE = "small";
    public static String MONITOR_STORAGE_NAME = "@BonFIRE Zabbix Aggregator v8";
    public static String MONITOR_NETWORK_NAME = "@BonFIRE WAN";
    public static String MONITOR_CONTEXT_USAGE = "zabbix-agent;zabbix-aggr-extend;infra-monitoring-init;log-MQevents-in-zabbix";

    public GeneralStep(APWizardLayout mainLayout) {
        super();
        this.setMargin(true);
        this.setSpacing(true);

        this.mainLayout = mainLayout;

        this.render();
    }

    private void render() {
        this.removeAllComponents();

        this.addComponent(this.name);
        this.addComponent(this.description);

        this.addComponent(this.duration);
        
        this.optimize.setValue(true);
        this.addComponent(this.optimize);

        this.monitoring.setItemCaptionPropertyId("name");

        this.addComponent(this.monitoring);

        Button btnSave = new Button("Save");
        btnSave.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {

                if (name.getValue() == null || name.getValue().trim().equals("")) {

                    Notification.show("Application name is mandatory");

                } else {

                    if (description.getValue() == null || description.getValue().trim().equals("")) {

                        Notification.show("Application description is mandatory");

                    } else {
                        ExperimentDescriptor ed = ((E2CPortal) UI.getCurrent()).getSessionStatus().getApplicationProfile().getExperimentDescriptor();

                        if (ed == null) {
                            ed = new ExperimentDescriptor();
                        }

                        try {
                            long d = Long.parseLong(duration.getValue());
                            ed.setDuration(d);
                            ed.setName(name.getValue());
                            ed.setDescription(description.getValue());

                        } catch (Exception ex) {
                            Notification.show("Value for duration not valid", Notification.Type.ERROR_MESSAGE);
                        }

                        ArrayList<ResourceCompute> rcList = ed.getResourcesCompute();
                        if (rcList == null) {
                            rcList = new ArrayList<ResourceCompute>();
                        }
                        //remove current monitoring resource, if exists
                        ResourceCompute rctodelete = null;
                        if (!rcList.isEmpty()) {
                            for (ResourceCompute rc : rcList) {
                                if (rc.getCompute().getName().equals(GeneralStep.MONITOR_RESOURCE_NAME)) {
                                    rctodelete = rc;
                                }
                            }
                            if (rctodelete != null) {
                                rcList.remove(rctodelete);
                            }
                        }
                        //update the context of other resources
                        if (!rcList.isEmpty()) {
                            for (ResourceCompute rc : rcList) {
                                ArrayList<Contexts> contextList = rc.getCompute().getContexts();
                                for (Contexts context : contextList) {
                                    if (context.getContextThings().containsKey("aggregator_ip")) {
                                        ArrayList<String> aggregatorValues = new ArrayList<String>();
                                        if (monitoring.getValue() != null) {
                                            aggregatorValues.add(GeneralStep.MONITOR_RESOURCE_NAME);

                                            aggregatorValues.add("BonFIRE WAN");
                                            context.setContextThings("aggregator_ip", aggregatorValues);
                                        } else {
                                            context.getContextThings().remove("aggregator_ip");
                                        }

                                        //contextList.add(context);
                                    }

                                }
                            }
                            if (rctodelete != null) {
                                rcList.remove(rctodelete);
                            }
                        }
                        //add the new monitoring resource, if requested
                        if (monitoring.getValue() != null) {
                            ResourceCompute rc = new ResourceCompute();
                            Compute c = new Compute();

                            c.setName(GeneralStep.MONITOR_RESOURCE_NAME);
                            c.setMin(1);

                            ArrayList<String> locations = new ArrayList<String>();

                            locations.add(((Testbed) monitoring.getValue()).getName());

                            c.setLocations(locations);

                            c.setInstanceType(GeneralStep.MONITOR_INSTANCE_TYPE);

                            ArrayList<Resource> resources = new ArrayList<Resource>();
                            Resource storager = new Resource();
                            storager.setStorage(GeneralStep.MONITOR_STORAGE_NAME);
                            Resource networkr = new Resource();
                            networkr.setNetwork(GeneralStep.MONITOR_NETWORK_NAME);

                            resources.add(storager);
                            resources.add(networkr);

                            c.setResourceCompute(resources);

                            ArrayList<Contexts> contexts = new ArrayList<Contexts>();
                            Contexts usage = new Contexts();
                            usage.setContextThings("usage", GeneralStep.MONITOR_CONTEXT_USAGE);
                            contexts.add(usage);
                            c.setContexts(contexts);

                            rc.setCompute(c);

                            rcList.add(0, rc);
                            ed.setResourcesCompute(rcList);

                        }

                        ((E2CPortal) UI.getCurrent()).getSessionStatus().getApplicationProfile().setExperimentDescriptor(ed);
                         ((E2CPortal) UI.getCurrent()).getSessionStatus().getApplicationProfile().setOptimize(optimize.getValue());
                        mainLayout.getAptext().update();
                    }
                }
            }
        });
        this.addComponent(btnSave);

    }

    private BeanItemContainer<Testbed> getSites() {

        BeanItemContainer<Testbed> items = new BeanItemContainer<Testbed>(Testbed.class);

        items.addAll(SchedulerManagerFactory.getInstance().getSites());

        return items;
    }

    public ComboBox getMonitoring() {
        return monitoring;
    }

    public TextField getDuration() {
        return duration;
    }

}

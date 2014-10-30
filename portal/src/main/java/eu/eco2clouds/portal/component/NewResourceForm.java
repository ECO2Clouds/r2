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
package eu.eco2clouds.portal.component;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import eu.eco2clouds.accounting.datamodel.parser.Testbed;
import eu.eco2clouds.applicationProfile.datamodel.Compute;
import eu.eco2clouds.applicationProfile.datamodel.Contexts;
import eu.eco2clouds.applicationProfile.datamodel.ExperimentDescriptor;
import eu.eco2clouds.applicationProfile.datamodel.Resource;
import eu.eco2clouds.applicationProfile.datamodel.ResourceCompute;
import eu.eco2clouds.portal.E2CPortal;
import eu.eco2clouds.portal.component.apwizard.GeneralStep;
import eu.eco2clouds.portal.page.APWizardLayout;
import eu.eco2clouds.portal.scheduler.SchedulerManagerFactory;
import java.util.ArrayList;
import java.util.Set;

/**
 *
 *  
 */
public class NewResourceForm extends FormLayout implements Button.ClickListener {

    final NewResourceTable newResourceTable;

    private APWizardLayout mainLayout;

    final TextField name = new TextField("VM Name", "VMName");
    final ListSelect lsSites = new ListSelect("Preferred sites", this.getSites());
    final ComboBox lsInstanceType = new ComboBox("Type", this.getInstanceType());
    final TextField storage = new TextField("Storage", "@BonFIRE Debian Squeeze 10G v6");
    final TextField network = new TextField("Network", "@BonFIRE WAN");

    public NewResourceForm(NewResourceTable rl, APWizardLayout mainLayout) {
        super();

        this.newResourceTable = rl;
        this.mainLayout = mainLayout;

        this.addComponent(name);

        lsSites.setItemCaptionMode(AbstractSelect.ItemCaptionMode.PROPERTY);
        lsSites.setItemCaptionPropertyId("name");
        lsSites.setNullSelectionAllowed(true);
        lsSites.setMultiSelect(true);
        lsSites.setRows(3);

        this.addComponent(this.lsSites);

        lsInstanceType.setNullSelectionAllowed(false);

        this.addComponent(this.lsInstanceType);

        Button btnSave = new Button("Add");
        btnSave.addClickListener(this);

        this.addComponent(btnSave);

    }

    @Override
    public void buttonClick(Button.ClickEvent event) {

        if (this.name.getValue() == null || this.name.getValue().equals("")) {

            Notification.show("Please specify a name", Notification.Type.ERROR_MESSAGE);
        } else {

            if (this.lsInstanceType.getValue() == null || ((String) this.lsInstanceType.getValue()).equals("")) {

                Notification.show("Please specify one instance type", Notification.Type.ERROR_MESSAGE);
            } else {

                StringBuffer sb = new StringBuffer();
                if (lsSites.getValue() == null || ((Set<Testbed>) lsSites.getValue()).isEmpty()) {
                    lsSites.setValue(lsSites.getItemIds());
                }

                for (Testbed tb : (Set<Testbed>) lsSites.getValue()) {
                    sb.append(tb.getName() + " ");
                }

                NewResourceTableBean rb = new NewResourceTableBean(name.getValue(), sb.toString(), (String) this.lsInstanceType.getValue());

                this.newResourceTable.addItem(rb);

                ResourceCompute rc = new ResourceCompute();
                Compute c = new Compute();

                c.setName(this.name.getValue());
                c.setMin(1);

                ArrayList<String> locations = new ArrayList<String>();

                if (((Set<Testbed>) this.lsSites.getValue()).size() < 3) {
                    for (Testbed site : (Set<Testbed>) this.lsSites.getValue()) {

                        locations.add(site.getName());
                    }
                }
                c.setLocations(locations);

                c.setInstanceType((String) this.lsInstanceType.getValue());

                ArrayList<Resource> resources = new ArrayList<Resource>();
                Resource storager = new Resource();
                storager.setStorage(this.storage.getValue());
                Resource networkr = new Resource();
                networkr.setNetwork(this.network.getValue());

                resources.add(storager);
                resources.add(networkr);

                c.setResourceCompute(resources);

                ArrayList<Contexts> contexts = new ArrayList<Contexts>();
                Contexts aggregator = new Contexts();
                ArrayList<String> aggregatorValues = new ArrayList<String>();

                Testbed monitoringSite = (Testbed) this.mainLayout.getMenu().getGeneralStep().getMonitoring().getValue();
                if (monitoringSite != null && monitoringSite.getName() != "") {
                    aggregatorValues.add(GeneralStep.MONITOR_RESOURCE_NAME);

                    aggregatorValues.add("BonFIRE WAN");
                    aggregator.setContextThings("aggregator_ip", aggregatorValues);
                    contexts.add(aggregator);
                }
                Contexts usage = new Contexts();
                usage.setContextThings("usage", "zabbix-agent");
                contexts.add(usage);

                c.setContexts(contexts);

                rc.setCompute(c);

                ExperimentDescriptor ed = ((E2CPortal) UI.getCurrent()).getSessionStatus().getApplicationProfile().getExperimentDescriptor();

                if (ed == null) {
                    ed = new ExperimentDescriptor();
                }
                ArrayList<ResourceCompute> rcList = ed.getResourcesCompute();
                if (rcList == null) {
                    rcList = new ArrayList<ResourceCompute>();
                }
                rcList.add(rc);
                ed.setResourcesCompute(rcList);

                ((E2CPortal) UI.getCurrent()).getSessionStatus().getApplicationProfile().setExperimentDescriptor(ed);

                this.lsSites.setValue(null);

                this.mainLayout.getAptext().update();
                this.mainLayout.getMenu().getRequirementStep().getNewRequirementForm().updateElementList();

            }
        }
    }

    private BeanItemContainer<Testbed> getSites() {

        BeanItemContainer<Testbed> items = new BeanItemContainer<Testbed>(Testbed.class);

        items.addAll(SchedulerManagerFactory.getInstance().getSites());

        return items;
    }

    private BeanItemContainer<String> getInstanceType() {

        BeanItemContainer<String> items = new BeanItemContainer<String>(String.class);

        items.addBean("lite");
        items.addBean("small");
        items.addBean("medium");
        items.addBean("large");

        return items;
    }

}

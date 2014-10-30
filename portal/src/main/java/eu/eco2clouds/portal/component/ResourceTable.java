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

import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.Page;
import com.vaadin.server.Resource;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;
import eu.eco2clouds.accounting.datamodel.parser.VM;
import eu.eco2clouds.accounting.datamodel.parser.VM;
import eu.eco2clouds.portal.Configuration;
import eu.eco2clouds.portal.E2CPortal;
import eu.eco2clouds.portal.scheduler.SchedulerManager;
import java.util.Collection;

/**
 *
 *  
 */
public class ResourceTable extends Table {

    public ResourceTable() {
        super("Resources");
        this.render();
    }

    private void render() {

        this.setHeight("200px");
        this.setWidth("100%");
        this.setImmediate(true);
        this.setSelectable(true);

        BeanItemContainer<ResourceTableBean> container = new BeanItemContainer<ResourceTableBean>(ResourceTableBean.class);
        
        
        Collection<VM> resources = this.loadData(((E2CPortal)UI.getCurrent()).getSessionStatus().getSelectedExperiment().getId());

        if (resources != null) {

            for (VM r : resources) {
                container.addBean(new ResourceTableBean(r));
            }

            this.setContainerDataSource(container);
            for (Object s : this.getVisibleColumns()) {
                System.out.println((String) s);
            }
            this.setVisibleColumns(new Object[]{"href", "name", "ip", "host"});

            this.addValueChangeListener(new Property.ValueChangeListener() {
                @Override
                public void valueChange(final Property.ValueChangeEvent event) {

                        //nothing to do so far

                }
            });
        } else {
            System.out.println("no resources");
        }
    }

    private Collection<VM> loadData(int id) {

        SchedulerManager scheduler;
        try {
            scheduler = new SchedulerManager(Configuration.schedulerUrl, Configuration.keystoreFilename, Configuration.keystorePwd);
            return scheduler.getExperimentVMs(id);
        } catch (Exception ex) {
            //LinkedList<Experiment> list = new LinkedList<Experiment>();
            //list.add(new Experiment(0, "null", "null", new Long(0), new Long(0), new Long(0), "0", "0"));
            //return list;
            return null;
        }

    }
}


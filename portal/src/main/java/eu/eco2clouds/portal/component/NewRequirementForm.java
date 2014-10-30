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

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import eu.eco2clouds.applicationProfile.datamodel.ApplicationProfile;
import eu.eco2clouds.applicationProfile.datamodel.Constraint;
import eu.eco2clouds.applicationProfile.datamodel.Requirement;
import eu.eco2clouds.portal.E2CPortal;
import eu.eco2clouds.portal.page.APWizardLayout;
import java.util.ArrayList;

/**
 *
 *  
 */
public class NewRequirementForm extends FormLayout implements Button.ClickListener {

    final private APWizardLayout mainLayout;

    final ComboBox element = new ComboBox("element", this.getElements());
    final TextField metric = new TextField("metric", "");
    final ListSelect operator = new ListSelect("operator", this.getOperators());
    final TextField value1 = new TextField("value1", "");
    final TextField value2 = new TextField("value2", "");

    public NewRequirementForm(APWizardLayout mainLayout) {
        super();

        this.mainLayout = mainLayout;

        this.element.setNullSelectionAllowed(false);

        this.addComponent(this.element);

        this.operator.setNullSelectionAllowed(false);
        this.operator.setImmediate(true);
        this.operator.addValueChangeListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {

                if (String.valueOf(event.getProperty()
                        .getValue()).equals("in")) {
                    value2.setVisible(true);
                    
                } else {
                    value2.setVisible(false);
                }
            }
        });

        value2.setVisible(false);

        this.operator.setRows(3);

        this.addComponent(metric);
        this.addComponent(operator);
        this.addComponent(value1);
        this.addComponent(value2);

        Button btnSave = new Button("Add");
        btnSave.addClickListener(this);

        this.addComponent(btnSave);

    }

    @Override
    public void buttonClick(Button.ClickEvent event) {

        if (this.element.getValue() == null) {
            Notification.show("Please specify the task to which apply the requirements", Notification.Type.ERROR_MESSAGE);

        } else if (this.metric.getValue() == null || this.metric.getValue().equals("")) {

            Notification.show("Please specify a name for the metric", Notification.Type.ERROR_MESSAGE);

        } else if (mainLayout.getMenu().getRequirementStep().getElementTable().getItemIds().contains(this.metric.getValue())) {

            Notification.show("Constr " + this.metric.getValue() + " already exists", Notification.Type.ERROR_MESSAGE);

        } else if (this.metric.getValue() == null || this.metric.getValue().equals("")) {

            Notification.show("Please specify a name for the metric", Notification.Type.ERROR_MESSAGE);
        } else if (this.value1.getValue() == null || this.metric.getValue().equals("")) {

            Notification.show("Please specify a value for the metric", Notification.Type.ERROR_MESSAGE);
        } else if (this.operator.getValue().equals("in") && (this.value1.getValue() == null || this.metric.getValue().equals(""))) {

            Notification.show("Please specify a max value for the metric", Notification.Type.ERROR_MESSAGE);
        } else {
            ApplicationProfile ap = ((E2CPortal) UI.getCurrent()).getSessionStatus().getApplicationProfile();
            if (ap.getRequirement() == null) {
                ap.setRequirement(new Requirement());
            }

            Requirement requirement = ap.getRequirement();
            if (requirement.getConstraints() == null) {
                requirement.setConstraints(new ArrayList<Constraint>());
            }

            String sb = new String();
            ArrayList<Constraint> constraints = requirement.getConstraints();

            Constraint constraint = new Constraint();
            constraint.setElement((String) this.element.getValue());
            constraint.setIndicator(this.metric.getValue());
            constraint.setOperator((String) this.operator.getValue());
            if (this.operator.getValue().equals("in")) {
                ArrayList<String> values = new ArrayList<String>();
                sb = "[" + this.value1.getValue() + "," + this.value2.getValue() + "]";
                values.add(this.value1.getValue());
                values.add(this.value2.getValue());
                constraint.setValues(values);
            } else {
                sb = this.value1.getValue();
                constraint.setValue(this.value1.getValue());
            }

            constraints.add(constraint);
            requirement.setConstraints(constraints);

            RequirementTableBean rb = new RequirementTableBean((String)element.getValue(), metric.getValue(), (String)operator.getValue(), sb);

            this.mainLayout.getMenu().getRequirementStep().getElementTable().addItem(rb);

            mainLayout.getAptext().update();
        }

    }

    private BeanItemContainer<String> getOperators() {

        BeanItemContainer<String> items = new BeanItemContainer<String>(String.class);

        items.addBean(">");
        items.addBean("<");
        items.addBean("in");

        return items;
    }

    private BeanItemContainer<String> getElements() {

        BeanItemContainer<String> items = new BeanItemContainer<String>(String.class);

        if (this.mainLayout != null && this.mainLayout.getMenu() != null) {
            if (this.mainLayout.getMenu().getResourcesStep() != null) {
                items.addAll(this.mainLayout.getMenu().getResourcesStep().getResourceList());
            }
            if (this.mainLayout.getMenu().getFlowStep() != null) {
                items.addAll(this.mainLayout.getMenu().getFlowStep().getTaskList());
            }
        }
        return items;
    }

    public void updateElementList() {
        this.element.setContainerDataSource(this.getElements());
    }

}

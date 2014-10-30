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

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Slider;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;
import eu.eco2clouds.portal.component.CO2CountryChart;
import eu.eco2clouds.portal.component.DeploymentTable;
import eu.eco2clouds.portal.component.DeploymentTableBean;
import eu.eco2clouds.portal.component.NewResourceTableBean;
import eu.eco2clouds.portal.compute.CO2Item;
import eu.eco2clouds.portal.compute.DeploymentUtils;
import eu.eco2clouds.portal.compute.OctaveManager;
import eu.eco2clouds.portal.compute.PowerEstimator;
import eu.eco2clouds.portal.compute.PowerItem;
import eu.eco2clouds.portal.compute.SourcePercentage;
import eu.eco2clouds.portal.page.APWizardLayout;
import eu.eco2clouds.portal.scheduler.MonitoringManager;
import eu.eco2clouds.portal.scheduler.MonitoringManagerFactory;
import eu.eco2clouds.portal.scheduler.SchedulerManager;
import eu.eco2clouds.portal.scheduler.SchedulerManagerFactory;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 *
 *  
 */
public class SuggestionStep extends VerticalLayout {

    APWizardLayout mainLayout;

    Slider sliderCpuLoad = new Slider(0, 100);
    Button btnRefresh = new Button("Refresh");

    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-YYYY HH:mm");

    CO2CountryChart co2CountryChart = new CO2CountryChart(sdf.format(new Date()), 0, null, null, null, this.sliderCpuLoad.getValue());
    DeploymentTable deploymentTable = new DeploymentTable(this.getCombination());

    HashMap<Integer, IndexedContainer> co2predictions = new HashMap<Integer, IndexedContainer>();

    DeploymentUtils du = new DeploymentUtils();

    HorizontalLayout chartHl = new HorizontalLayout();

    double[] cpu_fr;
    double[] cpu_uk;
    double[] cpu_de;
    CO2Item[] co2_fr;
    CO2Item[] co2_uk;
    CO2Item[] co2_de;
    PowerItem[] power_fr;
    PowerItem[] power_uk;
    PowerItem[] power_de;

    PowerEstimator powerEstimator = new PowerEstimator();

    public SuggestionStep(APWizardLayout mainLayout) {
        super();

        this.mainLayout = mainLayout;

        this.setSizeFull();
        this.setMargin(true);
        this.setSpacing(true);

        this.sliderCpuLoad.setValue(100.00);
        this.compute();

        this.render();
    }

    private void render() {

        this.removeAllComponents();

        if (this.mainLayout != null && this.mainLayout.getMenu() != null && this.mainLayout.getMenu().getResourcesStep() != null && this.mainLayout.getMenu().getResourcesStep().getNewResourceTable() != null && this.mainLayout.getMenu().getGeneralStep().getDuration() != null && this.mainLayout.getMenu().getGeneralStep().getDuration().getValue() != null && !this.mainLayout.getMenu().getGeneralStep().getDuration().getValue().equals("")) {

            VerticalLayout vlCpuLoad = new VerticalLayout();
            vlCpuLoad.setMargin(false);
            vlCpuLoad.setSpacing(true);
            Label lblCpuLoad = new Label("Average Cpu Load");
            vlCpuLoad.addComponent(lblCpuLoad);

            this.sliderCpuLoad.addValueChangeListener(new Property.ValueChangeListener() {

                @Override
                public void valueChange(Property.ValueChangeEvent event) {
                    updateChart();
                }
            });

            HorizontalLayout hl = new HorizontalLayout();
            hl.setMargin(false);
            hl.setSpacing(true);
            hl.addComponent(new Label("min 0", ContentMode.HTML));
            hl.addComponent(sliderCpuLoad);
            hl.addComponent(new Label("100 max", ContentMode.HTML));

            vlCpuLoad.addComponent(hl);

            vlCpuLoad.setComponentAlignment(lblCpuLoad, Alignment.MIDDLE_LEFT);
            vlCpuLoad.setComponentAlignment(hl, Alignment.MIDDLE_LEFT);

            this.deploymentTable.setContainerDataSource(this.getCombination());
            this.deploymentTable.addValueChangeListener(new Property.ValueChangeListener() {
                @Override
                public void valueChange(final Property.ValueChangeEvent event) {

                    DeploymentTableBean item = (DeploymentTableBean) deploymentTable.getValue();
                    System.out.println("item is " + item);
                    int selected = item.getId() - 1;

                    long duration = Long.parseLong(mainLayout.getMenu().getGeneralStep().getDuration().getValue());

                    System.out.println("selected " + selected);
                    System.out.println("power fr" + power_fr[selected]);
                    System.out.println("power uk" + power_uk[selected]);
                    System.out.println("power de" + power_de[selected]);

                    TrendWindow trendWindow = new TrendWindow(power_fr[selected], power_uk[selected], power_de[selected], duration);
                    UI.getCurrent().addWindow(trendWindow);

                }
            });

            this.deploymentTable.addGeneratedColumn("GEC", new ColumnGenerator() {

                @Override
                public Object generateCell(final Table source, final Object itemId, Object columnId) {

                    ArrayList<String> locations = new ArrayList<String>();
                    String[] vms = ((DeploymentTableBean) itemId).getCombination().split(" ");
                    for (int i = 0; i < vms.length; i++) {

                        String vmName = vms[i].split("@")[0];
                        String vmLocation = vms[i].split("@")[1];
                        if (!locations.contains(vmLocation)) {
                            locations.add(vmLocation);
                        }

                    }

                    SourcePercentage sp = SchedulerManagerFactory.getInstance().getGEC(locations);

                    return new Label(Double.toString(sp.getGEC()));
                }
            });

            this.deploymentTable.addGeneratedColumn("", new ColumnGenerator() {

                @Override
                public Object generateCell(final Table source, final Object itemId, Object columnId) {

                    Button button = new Button("");
                    button.setStyleName(Reindeer.BUTTON_LINK);
                    button.setIcon(new ThemeResource("img/pie.png"));
                    button.setDescription("GEC");

                    button.addClickListener(new Button.ClickListener() {

                        @Override
                        public void buttonClick(Button.ClickEvent event) {

                            ArrayList<String> locations = new ArrayList<String>();
                            String[] vms = ((DeploymentTableBean) itemId).getCombination().split(" ");
                            for (int i = 0; i < vms.length; i++) {

                                String vmName = vms[i].split("@")[0];
                                String vmLocation = vms[i].split("@")[1];
                                if (!locations.contains(vmLocation)) {
                                    locations.add(vmLocation);
                                }

                            }

                            SourcePercentage sp = SchedulerManagerFactory.getInstance().getGEC(locations);
                            GECWindow gecWindow = new GECWindow(((DeploymentTableBean) itemId).getCombination(), sp);
                            UI.getCurrent().addWindow(gecWindow);

                        }
                    });

                    return button;
                }
            });

            this.deploymentTable.addGeneratedColumn(" ", new ColumnGenerator() {

                @Override
                public Object generateCell(final Table source, final Object itemId, Object columnId) {

                    Button button = new Button("");
                    button.setStyleName(Reindeer.BUTTON_LINK);
                    button.setIcon(new ThemeResource("img/trend.png"));
                    button.setDescription("CO2 trend");
                    button.addClickListener(new Button.ClickListener() {

                        @Override
                        public void buttonClick(Button.ClickEvent event) {

                            //DeploymentTableBean item = (DeploymentTableBean) deploymentTable.getValue();
                            System.out.println("item is " + itemId);
                            int selected = ((DeploymentTableBean) itemId).getId() - 1;

                            long duration = Long.parseLong(mainLayout.getMenu().getGeneralStep().getDuration().getValue());

                            System.out.println("selected " + selected);
                            System.out.println("power fr" + power_fr[selected]);
                            System.out.println("power uk" + power_uk[selected]);
                            System.out.println("power de" + power_de[selected]);

                            TrendWindow trendWindow = new TrendWindow(power_fr[selected], power_uk[selected], power_de[selected], duration);
                            UI.getCurrent().addWindow(trendWindow);

                        }
                    });

                    return button;
                }
            });

            vlCpuLoad.addComponent(deploymentTable);

            OptionGroup chartAspect = new OptionGroup("Chart aspect");
            chartAspect.addItem(1);
            chartAspect.setItemCaption(1, "NORMAL");
            chartAspect.addItem(2);
            chartAspect.setItemCaption(2, "PERCENTAGE");

            chartAspect.select(1);
            chartAspect.setNullSelectionAllowed(false);
            chartAspect.setImmediate(true);

            chartAspect.addValueChangeListener(new ValueChangeListener() {
                @Override
                public void valueChange(final ValueChangeEvent event) {
                    //final String valueString = String.valueOf(event.getProperty()
                    //        .getValue());

                    if (co2CountryChart != null) {
                        co2CountryChart.toggleAspect();
                    }
                }
            });

            vlCpuLoad.addComponent(chartAspect);

            chartHl.addComponent(vlCpuLoad);

            this.compute();

            this.co2CountryChart = new CO2CountryChart(sdf.format(new Date()), this.deploymentTable.getItemIds().size(), co2_fr, co2_uk, co2_de, this.sliderCpuLoad.getValue());

            chartHl.addComponent(this.co2CountryChart);

            chartHl.setComponentAlignment(vlCpuLoad, Alignment.TOP_LEFT);
            chartHl.setComponentAlignment(this.co2CountryChart, Alignment.TOP_RIGHT);

            this.addComponent(chartHl);

        } else {
            Label lblEmpty = new Label("No resources defined.");
            this.addComponent(lblEmpty);
        }

    }

    private void updateChart() {

        CO2CountryChart chart = new CO2CountryChart(sdf.format(new Date()), this.deploymentTable.getItemIds().size(), co2_fr, co2_uk, co2_de, this.sliderCpuLoad.getValue());

        chartHl.replaceComponent(this.co2CountryChart, chart);

        this.co2CountryChart = chart;

    }

    private void compute() {

        SchedulerManager sm = SchedulerManagerFactory.getInstance();
        MonitoringManager mm = MonitoringManagerFactory.getInstance();

        HashMap<String, Double> cpuload_fr = mm.getCpuLoads("fr-inria");
        HashMap<String, Double> cpuload_uk = mm.getCpuLoads("uk-epcc");
        HashMap<String, Double> cpuload_de = mm.getCpuLoads("de-hlrs");

        double em_fr = mm.getEmissionFactor("fr-inria");
        double em_uk = mm.getEmissionFactor("uk-epcc");
        double em_de = mm.getEmissionFactor("de-hlrs");

        if (this.mainLayout != null && this.mainLayout.getMenu() != null && this.mainLayout.getMenu().getResourcesStep() != null && this.mainLayout.getMenu().getResourcesStep().getNewResourceTable() != null && this.mainLayout.getMenu().getGeneralStep().getDuration() != null && this.mainLayout.getMenu().getGeneralStep().getDuration().getValue() != null && !this.mainLayout.getMenu().getGeneralStep().getDuration().getValue().equals("")) {

            OctaveManager om = new OctaveManager();

            long duration = Long.parseLong(this.mainLayout.getMenu().getGeneralStep().getDuration().getValue());
            Date startDate = new Date(); // TODO: updated with the slider

            Collection<DeploymentTableBean> combinations = (Collection<DeploymentTableBean>) this.deploymentTable.getItemIds();
            if (combinations != null && !combinations.isEmpty()) {
                this.power_fr = new PowerItem[combinations.size()];
                this.power_uk = new PowerItem[combinations.size()];
                this.power_de = new PowerItem[combinations.size()];

                this.cpu_fr = new double[combinations.size()];
                this.cpu_uk = new double[combinations.size()];
                this.cpu_de = new double[combinations.size()];

                this.co2_fr = new CO2Item[combinations.size()];
                this.co2_uk = new CO2Item[combinations.size()];
                this.co2_de = new CO2Item[combinations.size()];

                Collection<NewResourceTableBean> resources = (Collection<NewResourceTableBean>) this.mainLayout.getMenu().getResourcesStep().getNewResourceTable().getItemIds();

                int position = 0;
                for (DeploymentTableBean combination : combinations) {
                    String[] vms = combination.getCombination().split(" ");
                    for (int i = 0; i < vms.length; i++) {

                        String vmName = vms[i].split("@")[0];
                        String vmLocation = vms[i].split("@")[1];
                        for (NewResourceTableBean resource : resources) {

                            if (resource.getName().equals(vmName)) {
                                double no_cpu = 0;
                                if (resource.getType().equals("lite")) {
                                    no_cpu = 0.5;
                                } else if (resource.getType().equals("small")) {
                                    no_cpu = 1;
                                } else if (resource.getType().equals("medium")) {
                                    no_cpu = 2;
                                } else if (resource.getType().equals("large")) {
                                    no_cpu = 4;
                                }

                                //TODO ADD THE dependecy between power and cpuload for the sites 
                                if (vmLocation.contains("fr-inria")) {
                                    //power_fr[position] = power_fr[position] + (no_cpu/24 * (this.sliderCpuLoad.getValue() * 4 / 5) + 120);
                                    cpu_fr[position] = cpu_fr[position] + no_cpu;
                                } else if (vmLocation.contains("uk-epcc")) {
                                    //power_uk[position] = power_uk[position] + (no_cpu/24 * (this.sliderCpuLoad.getValue() * 4 / 5) + 120);
                                    cpu_uk[position] = cpu_uk[position] + no_cpu;
                                } else if (vmLocation.contains("de-hlrs")) {
                                    //power_de[position] = power_de[position] + (no_cpu/24 * (this.sliderCpuLoad.getValue() * 4 / 5) + 120);
                                    cpu_de[position] = cpu_de[position] + no_cpu;
                                }
                            }

                        }

                    }

                    System.out.println("cpu " + position + " = " + cpu_fr[position] + ", " + cpu_uk[position] + ", " + cpu_de[position]);

                    if (cpu_fr[position] > 0) {
                        power_fr[position] = om.powerSite(cpu_fr[position], cpuload_fr, "fr-inria");
                    } else {
                        power_fr[position] = new PowerItem(0.0, 0.0);
                    }
                    if (cpu_uk[position] > 0) {
                        power_uk[position] = om.powerSite(cpu_uk[position], cpuload_uk, "uk-epcc");
                    } else {
                        power_uk[position] = new PowerItem(0.0, 0.0);
                    }
                    if (cpu_de[position] > 0) {
                        power_de[position] = om.powerSite(cpu_de[position], cpuload_de, "de-hlrs");
                    } else {
                        power_de[position] = new PowerItem(0.0, 0.0);
                    }
                    System.out.println("power " + position + " = " + power_fr[position] + ", " + power_uk[position] + ", " + power_de[position]);

                    Date start = new Date();
                    Calendar finish = Calendar.getInstance();
                    finish.setTime(start);
                    finish.add(Calendar.MINUTE, (int) duration);
                    if (this.power_fr[position].getMin() > 0) {

                        this.co2_fr[position] = om.co2Site(power_fr[position], em_fr, "fr-inria", start, duration);
                    } else {
                        this.co2_fr[position] = new CO2Item(0.0, 0.0, 0.0);
                    }

                    if (this.power_uk[position].getMin() > 0) {
                        this.co2_uk[position] = om.co2Site(power_uk[position], em_uk, "uk-epcc", start, duration);
                    } else {
                        this.co2_uk[position] = new CO2Item(0.0, 0.0, 0.0);
                    }

                    if (this.power_de[position].getMin() > 0) {
                        this.co2_de[position] = om.co2Site(power_de[position], em_de, "de-hlrs", start, duration);
                    } else {
                        this.co2_de[position] = new CO2Item(0.0, 0.0, 0.0);
                    }
                    position++;
                }

                for (int i = 0; i < combinations.size(); i++) {

                    //this.co2predictions.put(i, this.co2prevision(fr_emPredictor, uk_emPredictor, de_emPredictor, startDate, power_fr[i], power_de[i], power_uk[i], duration));
                }

            }
        }

    }

    private BeanItemContainer<DeploymentTableBean> getCombination() {

        BeanItemContainer<DeploymentTableBean> container = new BeanItemContainer<DeploymentTableBean>(DeploymentTableBean.class);

        if (this.mainLayout != null && this.mainLayout.getMenu() != null && this.mainLayout.getMenu().getResourcesStep() != null && this.mainLayout.getMenu().getResourcesStep().getNewResourceTable() != null) {

            BeanItemContainer<NewResourceTableBean> resources = (BeanItemContainer<NewResourceTableBean>) this.mainLayout.getMenu().getResourcesStep().getNewResourceTable().getContainerDataSource();

            List<String> combinations = du.getCombinations(resources.getItemIds());

            int id = 1;
            for (String combination : combinations) {
                container.addBean(new DeploymentTableBean(id, combination));
                id++;
            }

        }

        return container;

    }

    /*private List<String> getCombination(List<NewResourceTableBean> resourceList) {
     LinkedList<String> result = new LinkedList<String>();

     if (resourceList.size() == 1) {

     String[] locationList = resourceList.get(0).getLocation().split(" ");

     for (int i = 0; i < locationList.length; i++) {
     result.add(resourceList.get(0).getName() + "@" + locationList[i]);
     }

     } else {

     List<NewResourceTableBean> subList = resourceList.subList(1, resourceList.size());
     List<String> combinations = getCombination(subList);

     String[] locationList = resourceList.get(0).getLocation().split(" ");

     for (int i = 0; i < locationList.length; i++) {
     for (String s : combinations) {
     result.add(resourceList.get(0).getName() + "@" + locationList[i] + " " + s);
     }
     }

     }

     return result;

     }*/
    public static void main(String args[]) {

        SuggestionStep ss = new SuggestionStep(null);
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();

        c2.add(Calendar.HOUR, 3);

        //IndexedContainer co2 = ss.co2prevision(c1.getTime(), 100, 100, 100, 1);
        //System.out.println(co2);
    }

}

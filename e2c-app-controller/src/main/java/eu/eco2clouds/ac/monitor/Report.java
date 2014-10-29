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
package eu.eco2clouds.ac.monitor;

import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 *  
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "report", namespace = Report.E2C_NAMESPACE)
public class Report {

    public static final String E2C_NAMESPACE = "http://accounting.eco2clouds.eu/doc/schemas/xml";
    @XmlAttribute(namespace = E2C_NAMESPACE)
    private String date;
    @XmlAttribute(namespace = E2C_NAMESPACE)
    private String experiment;
    @XmlElement(name = "app", namespace = E2C_NAMESPACE)
    private ReportApp app;
    @XmlElement(name = "vm", namespace = E2C_NAMESPACE)
    private List<ReportVM> vmReportList;

    public Report() {
    }

    public Report(Date date, String experiment, List<VMStatus> vmStatusList, AppStatus app) {

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-YYYY hh:mm");

        this.date = sdf.format(date);
        this.experiment = experiment;

        this.vmReportList = new LinkedList<ReportVM>();
        for (VMStatus vmStatus : vmStatusList) {
            this.vmReportList.add(new ReportVM(vmStatus.vm.getName(), vmStatus.getMetrics()));
        }

        if (app!=null)
            this.app = new ReportApp(app.getApplication(), app.getMetrics());

    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getExperiment() {
        return experiment;
    }

    public void setExperiment(String experiment) {
        this.experiment = experiment;
    }

    public List<ReportVM> getVm() {
        return vmReportList;
    }

    public void setVm(List<ReportVM> vm) {
        this.vmReportList = vm;
    }

    public static void main(String args[]) throws Exception {

        List<Item> items = new LinkedList<Item>();
        Item item1 = new Item("item1", "1111");
        Item item2 = new Item("item2", "2222");
        items.add(item1);
        items.add(item2);

        LinkedList<VMStatus> vms = new LinkedList<VMStatus>();
        VMStatus vm1 = new VMStatus(new VM("1.1.1.1", "1", "host1"), items);
        vm1.getMetrics().get(item1).add(new ItemValue(1111, 1.0));
        vm1.getMetrics().get(item1).add(new ItemValue(1112, 1.1));
        vm1.getMetrics().get(item1).add(new ItemValue(1113, 1.2));
        vm1.getMetrics().get(item1).add(new ItemValue(1114, 1.3));
        vm1.getMetrics().get(item2).add(new ItemValue(1111, 1.0));
        vm1.getMetrics().get(item2).add(new ItemValue(1112, 1.1));
        vm1.getMetrics().get(item2).add(new ItemValue(1113, 1.2));
        vm1.getMetrics().get(item2).add(new ItemValue(1114, 1.3));
        
        VMStatus vm2 = new VMStatus(new VM("2.2.2.2", "2", "host2"), items);
        vms.add(vm1);
        vms.add(vm2);


        Report report = new Report(new Date(), "111", vms, new AppStatus("pippo", items));

        StringWriter s = new StringWriter();

        JAXBContext jaxbContext = JAXBContext.newInstance(Report.class);
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        jaxbMarshaller.marshal(report, s);

        System.out.println(s);




    }
}

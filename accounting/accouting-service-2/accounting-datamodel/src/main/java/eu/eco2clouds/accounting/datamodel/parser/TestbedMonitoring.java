package eu.eco2clouds.accounting.datamodel.parser;

import static eu.eco2clouds.accounting.Dictionary.E2C_NAMESPACE;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import eu.eco2clouds.accounting.datamodel.parser.Co2;
import eu.eco2clouds.accounting.datamodel.parser.Coal;
import eu.eco2clouds.accounting.datamodel.parser.Cost;
import eu.eco2clouds.accounting.datamodel.parser.Gaz;
import eu.eco2clouds.accounting.datamodel.parser.Hydraulic;
import eu.eco2clouds.accounting.datamodel.parser.Link;
import eu.eco2clouds.accounting.datamodel.parser.Nuclear;
import eu.eco2clouds.accounting.datamodel.parser.Oil;
import eu.eco2clouds.accounting.datamodel.parser.Other;
import eu.eco2clouds.accounting.datamodel.parser.PDUFr;
import eu.eco2clouds.accounting.datamodel.parser.Total;
import eu.eco2clouds.accounting.datamodel.parser.Wind;

/**
 * 
 * Copyright 2014 ATOS SPAIN S.A. 
 * 
 * Licensed under the Apache License, Version 2.0 (the License);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * @author: David Garcia Perez. Atos Research and Innovation, Atos SPAIN SA
 * e-mail david.garciaperez@atos.net
 *
 * Represents the results of the energy infrastruture monitoring of an specific testbed
 * 
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="testbed_monitoring", namespace = E2C_NAMESPACE)
public class TestbedMonitoring {
	@XmlAttribute
	private String href;
	@XmlElement(name = "Availability", namespace = E2C_NAMESPACE)
	private Availability availability;
	@XmlElement(name = "Biomass", namespace = E2C_NAMESPACE)
	private Biomass biomass;
	@XmlElement(name = "CCGT", namespace = E2C_NAMESPACE)
	private CCGT ccgt;
	@XmlElement(name = "Center", namespace = E2C_NAMESPACE)
	private Center center;
	@XmlElement(name = "Co2", namespace = E2C_NAMESPACE)
	private Co2 co2;
	@XmlElement(name = "Coal", namespace = E2C_NAMESPACE)
	private Coal coal;
	@XmlElement(name = "Cogeneration", namespace = E2C_NAMESPACE)
	private Cogeneration cogeneration;
	@XmlElement(name = "Cost", namespace = E2C_NAMESPACE)
	private Cost cost;
	@XmlElement(name = "Exported", namespace = E2C_NAMESPACE)
	private Exported exported;
	@XmlElement(name = "Fossil", namespace = E2C_NAMESPACE)
	private Fossil fossil;
	@XmlElement(name = "Gaz", namespace = E2C_NAMESPACE)
	private Gaz gaz;
	@XmlElement(name = "Geothermal", namespace = E2C_NAMESPACE)
	private Geothermal geothermal;
	@XmlElement(name = "Hydraulic", namespace = E2C_NAMESPACE)
	private Hydraulic hydraulic;
	@XmlElement(name = "Imported", namespace = E2C_NAMESPACE)
	private Imported imported;
	@XmlElement(name = "nps_hydro", namespace = E2C_NAMESPACE)
	private NPSHydro npsHydro;
	@XmlElement(name = "Nuclear", namespace = E2C_NAMESPACE)
	private Nuclear nuclear;
	@XmlElement(name = "OCGT", namespace = E2C_NAMESPACE)
	private OCGT ocgt;
	@XmlElement(name = "Oil", namespace = E2C_NAMESPACE)
	private Oil oil;
	@XmlElement(name = "Other", namespace = E2C_NAMESPACE)
	private Other other;
	@XmlElement(name = "PUE", namespace = E2C_NAMESPACE)
	private PUE pue;
	@XmlElement(name = "pumped_storage", namespace = E2C_NAMESPACE)
	private PumpedStorage pumpedStorage;
	@XmlElement(name = "site_utilization", namespace = E2C_NAMESPACE)
	private SiteUtilization siteUtilization;
	@XmlElement(name = "Solar", namespace = E2C_NAMESPACE)
	private Solar solar;
	@XmlElement(name = "storage_utilization", namespace = E2C_NAMESPACE)
	private StorageUtilization storageUtilization;
	@XmlElement(name = "total_green", namespace = E2C_NAMESPACE)
	private TotalGreen totalGreen;
	@XmlElement(name = "Water", namespace = E2C_NAMESPACE)
	private Water water;
	@XmlElement(name = "Wind", namespace = E2C_NAMESPACE)
	private Wind wind;
	@XmlElement(name = "link", namespace = E2C_NAMESPACE)
	private List<Link> links;
	//To be deleted
	@XmlElement(name = "PDU_fr", namespace = E2C_NAMESPACE)
	private PDUFr pDUFr;
	@XmlElement(name = "Total", namespace = E2C_NAMESPACE)
	private Total total;
	@XmlElement(name = "one_utilization_cpu", namespace = E2C_NAMESPACE)
	private OneUtilizationCpu oneUtlizationCpu;
	@XmlElement(name = "ps", namespace = E2C_NAMESPACE)
	private Ps ps;
	
	
	public Ps getPs() {
		return ps;
	}
	public void setPs(Ps ps) {
		this.ps = ps;
	}
	public OneUtilizationCpu getOneUtlizationCpu() {
		return oneUtlizationCpu;
	}
	public void setOneUtlizationCpu(OneUtilizationCpu oneUtlizationCpu) {
		this.oneUtlizationCpu = oneUtlizationCpu;
	}
	public String getHref() {
		return href;
	}
	public void setHref(String href) {
		this.href = href;
	}
	
	public Co2 getCo2() {
		return co2;
	}
	public void setCo2(Co2 co2) {
		this.co2 = co2;
	}
	
	public Coal getCoal() {
		return coal;
	}
	public void setCoal(Coal coal) {
		this.coal = coal;
	}
	
	public Gaz getGaz() {
		return gaz;
	}
	public void setGaz(Gaz gaz) {
		this.gaz = gaz;
	}
	
	public Hydraulic getHydraulic() {
		return hydraulic;
	}
	public void setHydraulic(Hydraulic hydraulic) {
		this.hydraulic = hydraulic;
	}
	
	public Nuclear getNuclear() {
		return nuclear;
	}
	public void setNuclear(Nuclear nuclear) {
		this.nuclear = nuclear;
	}
	
	public Oil getOil() {
		return oil;
	}
	public void setOil(Oil oil) {
		this.oil = oil;
	}
	
	public Other getOther() {
		return other;
	}
	public void setOther(Other other) {
		this.other = other;
	}
	
	public PDUFr getpDUFr() {
		return pDUFr;
	}
	public void setpDUFr(PDUFr pDUFr) {
		this.pDUFr = pDUFr;
	}
	
	public Total getTotal() {
		return total;
	}
	public void setTotal(Total total) {
		this.total = total;
	}
	
	public Wind getWind() {
		return wind;
	}
	public void setWind(Wind wind) {
		this.wind = wind;
	}
	
	public List<Link> getLinks() {
		return links;
	}
	public void setLinks(List<Link> links) {
		this.links = links;
	}
	
	public Cost getCost() {
		return cost;
	}
	public void setCost(Cost cost) {
		this.cost = cost;
	}
	public Availability getAvailability() {
		return availability;
	}
	public void setAvailability(Availability availability) {
		this.availability = availability;
	}
	public Biomass getBiomass() {
		return biomass;
	}
	public void setBiomass(Biomass biomass) {
		this.biomass = biomass;
	}
	public CCGT getCcgt() {
		return ccgt;
	}
	public void setCcgt(CCGT ccgt) {
		this.ccgt = ccgt;
	}
	public Center getCenter() {
		return center;
	}
	public void setCenter(Center center) {
		this.center = center;
	}
	public Cogeneration getCogeneration() {
		return cogeneration;
	}
	public void setCogeneration(Cogeneration cogeneration) {
		this.cogeneration = cogeneration;
	}
	public Exported getExported() {
		return exported;
	}
	public void setExported(Exported exported) {
		this.exported = exported;
	}
	public Fossil getFossil() {
		return fossil;
	}
	public void setFossil(Fossil fossil) {
		this.fossil = fossil;
	}
	public Geothermal getGeothermal() {
		return geothermal;
	}
	public void setGeothermal(Geothermal geothermal) {
		this.geothermal = geothermal;
	}
	public Imported getImported() {
		return imported;
	}
	public void setImported(Imported imported) {
		this.imported = imported;
	}
	public NPSHydro getNpsHydro() {
		return npsHydro;
	}
	public void setNpsHydro(NPSHydro npsHydro) {
		this.npsHydro = npsHydro;
	}
	public OCGT getOcgt() {
		return ocgt;
	}
	public void setOcgt(OCGT ocgt) {
		this.ocgt = ocgt;
	}
	public PUE getPue() {
		return pue;
	}
	public void setPue(PUE pue) {
		this.pue = pue;
	}
	public PumpedStorage getPumpedStorage() {
		return pumpedStorage;
	}
	public void setPumpedStorage(PumpedStorage pumpedStorage) {
		this.pumpedStorage = pumpedStorage;
	}
	public SiteUtilization getSiteUtilization() {
		return siteUtilization;
	}
	public void setSiteUtilization(SiteUtilization siteUtilization) {
		this.siteUtilization = siteUtilization;
	}
	public Solar getSolar() {
		return solar;
	}
	public void setSolar(Solar solar) {
		this.solar = solar;
	}
	public StorageUtilization getStorageUtilization() {
		return storageUtilization;
	}
	public void setStorageUtilization(StorageUtilization storageUtilization) {
		this.storageUtilization = storageUtilization;
	}
	public TotalGreen getTotalGreen() {
		return totalGreen;
	}
	public void setTotalGreen(TotalGreen totalGreen) {
		this.totalGreen = totalGreen;
	}
	public Water getWater() {
		return water;
	}
	public void setWater(Water water) {
		this.water = water;
	}
	
	
}

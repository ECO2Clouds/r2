package eu.eco2clouds.accounting.datamodel.parser;

import static eu.eco2clouds.accounting.Dictionary.E2C_NAMESPACE;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import eu.eco2clouds.accounting.datamodel.parser.AbstractMonitoringItem;


/**
 * Represents the Co2GenerationRate Energy Metric that can come from the monitoring collector database
 * @author Pedro Rey Estrada - AtoS
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="co2_generation_rate", namespace = E2C_NAMESPACE)
public class CO2GenerationRate extends AbstractMonitoringItem {}

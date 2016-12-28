//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.11 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.12.28 at 05:15:49 PM EST 
//


package org.xframium.driver.xsd;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * Defines test dependencies for execution of functions on newly acquired devices or before and after each test
 * 
 * <p>Java class for xDependency complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="xDependency"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;attribute name="beforeDevice" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="beforeTest" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="afterTest" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "xDependency")
public class XDependency {

    @XmlAttribute(name = "beforeDevice")
    protected String beforeDevice;
    @XmlAttribute(name = "beforeTest")
    protected String beforeTest;
    @XmlAttribute(name = "afterTest")
    protected String afterTest;

    /**
     * Gets the value of the beforeDevice property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBeforeDevice() {
        return beforeDevice;
    }

    /**
     * Sets the value of the beforeDevice property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBeforeDevice(String value) {
        this.beforeDevice = value;
    }

    /**
     * Gets the value of the beforeTest property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBeforeTest() {
        return beforeTest;
    }

    /**
     * Sets the value of the beforeTest property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBeforeTest(String value) {
        this.beforeTest = value;
    }

    /**
     * Gets the value of the afterTest property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAfterTest() {
        return afterTest;
    }

    /**
     * Sets the value of the afterTest property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAfterTest(String value) {
        this.afterTest = value;
    }

}

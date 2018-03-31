//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.03.28 at 09:41:21 AM EDT 
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
 * &lt;complexType name="xDependency">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="beforeDevice" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="beforeTest" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="afterTest" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="beforeStep" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="afterStep" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
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
    @XmlAttribute(name = "beforeStep")
    protected String beforeStep;
    @XmlAttribute(name = "afterStep")
    protected String afterStep;

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

    /**
     * Gets the value of the beforeStep property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBeforeStep() {
        return beforeStep;
    }

    /**
     * Sets the value of the beforeStep property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBeforeStep(String value) {
        this.beforeStep = value;
    }

    /**
     * Gets the value of the afterStep property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAfterStep() {
        return afterStep;
    }

    /**
     * Sets the value of the afterStep property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAfterStep(String value) {
        this.afterStep = value;
    }

}

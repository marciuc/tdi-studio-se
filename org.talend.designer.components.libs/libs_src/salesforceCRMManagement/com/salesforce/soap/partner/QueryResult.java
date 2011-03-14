/**
 * QueryResult.java
 * 
 * This file was auto-generated from WSDL by the Apache Axis2 version: 1.5.2 Built on : Sep 06, 2010 (09:42:47 CEST)
 */

package com.salesforce.soap.partner;

/**
 * QueryResult bean class
 */

public class QueryResult implements org.apache.axis2.databinding.ADBBean {

    /*
     * This type was generated from the piece of schema that had name = QueryResult Namespace URI =
     * urn:partner.soap.sforce.com Namespace Prefix = ns1
     */

    private static java.lang.String generatePrefix(java.lang.String namespace) {
        if (namespace.equals("urn:partner.soap.sforce.com")) {
            return "ns1";
        }
        return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
    }

    /**
     * field for Done
     */

    protected boolean localDone;

    /**
     * Auto generated getter method
     * 
     * @return boolean
     */
    public boolean getDone() {
        return localDone;
    }

    /**
     * Auto generated setter method
     * 
     * @param param Done
     */
    public void setDone(boolean param) {

        this.localDone = param;

    }

    /**
     * field for QueryLocator
     */

    protected com.salesforce.soap.partner.QueryLocator localQueryLocator;

    /**
     * Auto generated getter method
     * 
     * @return com.salesforce.soap.partner.QueryLocator
     */
    public com.salesforce.soap.partner.QueryLocator getQueryLocator() {
        return localQueryLocator;
    }

    /**
     * Auto generated setter method
     * 
     * @param param QueryLocator
     */
    public void setQueryLocator(com.salesforce.soap.partner.QueryLocator param) {

        this.localQueryLocator = param;

    }

    /**
     * field for Records This was an Array!
     */

    protected com.salesforce.soap.partner.sobject.SObject[] localRecords;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this attribute. It will be
     * used to determine whether to include this field in the serialized XML
     */
    protected boolean localRecordsTracker = false;

    /**
     * Auto generated getter method
     * 
     * @return com.salesforce.soap.partner.sobject.SObject[]
     */
    public com.salesforce.soap.partner.sobject.SObject[] getRecords() {
        return localRecords;
    }

    /**
     * validate the array for Records
     */
    protected void validateRecords(com.salesforce.soap.partner.sobject.SObject[] param) {

    }

    /**
     * Auto generated setter method
     * 
     * @param param Records
     */
    public void setRecords(com.salesforce.soap.partner.sobject.SObject[] param) {

        validateRecords(param);

        if (param != null) {
            // update the setting tracker
            localRecordsTracker = true;
        } else {
            localRecordsTracker = true;

        }

        this.localRecords = param;
    }

    /**
     * Auto generated add method for the array for convenience
     * 
     * @param param com.salesforce.soap.partner.sobject.SObject
     */
    public void addRecords(com.salesforce.soap.partner.sobject.SObject param) {
        if (localRecords == null) {
            localRecords = new com.salesforce.soap.partner.sobject.SObject[] {};
        }

        // update the setting tracker
        localRecordsTracker = true;

        java.util.List list = org.apache.axis2.databinding.utils.ConverterUtil.toList(localRecords);
        list.add(param);
        this.localRecords = (com.salesforce.soap.partner.sobject.SObject[]) list
                .toArray(new com.salesforce.soap.partner.sobject.SObject[list.size()]);

    }

    /**
     * field for Size
     */

    protected int localSize;

    /**
     * Auto generated getter method
     * 
     * @return int
     */
    public int getSize() {
        return localSize;
    }

    /**
     * Auto generated setter method
     * 
     * @param param Size
     */
    public void setSize(int param) {

        this.localSize = param;

    }

    /**
     * isReaderMTOMAware
     * 
     * @return true if the reader supports MTOM
     */
    public static boolean isReaderMTOMAware(javax.xml.stream.XMLStreamReader reader) {
        boolean isReaderMTOMAware = false;

        try {
            isReaderMTOMAware = java.lang.Boolean.TRUE.equals(reader
                    .getProperty(org.apache.axiom.om.OMConstants.IS_DATA_HANDLERS_AWARE));
        } catch (java.lang.IllegalArgumentException e) {
            isReaderMTOMAware = false;
        }
        return isReaderMTOMAware;
    }

    /**
     * 
     * @param parentQName
     * @param factory
     * @return org.apache.axiom.om.OMElement
     */
    public org.apache.axiom.om.OMElement getOMElement(final javax.xml.namespace.QName parentQName,
            final org.apache.axiom.om.OMFactory factory) throws org.apache.axis2.databinding.ADBException {

        org.apache.axiom.om.OMDataSource dataSource = new org.apache.axis2.databinding.ADBDataSource(this, parentQName) {

            public void serialize(org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
                    throws javax.xml.stream.XMLStreamException {
                QueryResult.this.serialize(parentQName, factory, xmlWriter);
            }
        };
        return new org.apache.axiom.om.impl.llom.OMSourcedElementImpl(parentQName, factory, dataSource);

    }

    public void serialize(final javax.xml.namespace.QName parentQName, final org.apache.axiom.om.OMFactory factory,
            org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
            throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException {
        serialize(parentQName, factory, xmlWriter, false);
    }

    public void serialize(final javax.xml.namespace.QName parentQName, final org.apache.axiom.om.OMFactory factory,
            org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter, boolean serializeType)
            throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException {

        java.lang.String prefix = null;
        java.lang.String namespace = null;

        prefix = parentQName.getPrefix();
        namespace = parentQName.getNamespaceURI();

        if ((namespace != null) && (namespace.trim().length() > 0)) {
            java.lang.String writerPrefix = xmlWriter.getPrefix(namespace);
            if (writerPrefix != null) {
                xmlWriter.writeStartElement(namespace, parentQName.getLocalPart());
            } else {
                if (prefix == null) {
                    prefix = generatePrefix(namespace);
                }

                xmlWriter.writeStartElement(prefix, parentQName.getLocalPart(), namespace);
                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
            }
        } else {
            xmlWriter.writeStartElement(parentQName.getLocalPart());
        }

        if (serializeType) {

            java.lang.String namespacePrefix = registerPrefix(xmlWriter, "urn:partner.soap.sforce.com");
            if ((namespacePrefix != null) && (namespacePrefix.trim().length() > 0)) {
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", namespacePrefix + ":QueryResult",
                        xmlWriter);
            } else {
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", "QueryResult", xmlWriter);
            }

        }

        namespace = "urn:partner.soap.sforce.com";
        if (!namespace.equals("")) {
            prefix = xmlWriter.getPrefix(namespace);

            if (prefix == null) {
                prefix = generatePrefix(namespace);

                xmlWriter.writeStartElement(prefix, "done", namespace);
                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);

            } else {
                xmlWriter.writeStartElement(namespace, "done");
            }

        } else {
            xmlWriter.writeStartElement("done");
        }

        if (false) {

            throw new org.apache.axis2.databinding.ADBException("done cannot be null!!");

        } else {
            xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localDone));
        }

        xmlWriter.writeEndElement();

        if (localQueryLocator == null) {

            java.lang.String namespace2 = "urn:partner.soap.sforce.com";

            if (!namespace2.equals("")) {
                java.lang.String prefix2 = xmlWriter.getPrefix(namespace2);

                if (prefix2 == null) {
                    prefix2 = generatePrefix(namespace2);

                    xmlWriter.writeStartElement(prefix2, "queryLocator", namespace2);
                    xmlWriter.writeNamespace(prefix2, namespace2);
                    xmlWriter.setPrefix(prefix2, namespace2);

                } else {
                    xmlWriter.writeStartElement(namespace2, "queryLocator");
                }

            } else {
                xmlWriter.writeStartElement("queryLocator");
            }

            // write the nil attribute
            writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
            xmlWriter.writeEndElement();
        } else {
            localQueryLocator.serialize(new javax.xml.namespace.QName("urn:partner.soap.sforce.com", "queryLocator"), factory,
                    xmlWriter);
        }
        if (localRecordsTracker) {
            if (localRecords != null) {
                for (int i = 0; i < localRecords.length; i++) {
                    if (localRecords[i] != null) {
                        localRecords[i].serialize(new javax.xml.namespace.QName("urn:partner.soap.sforce.com", "records"),
                                factory, xmlWriter);
                    } else {

                        // write null attribute
                        java.lang.String namespace2 = "urn:partner.soap.sforce.com";
                        if (!namespace2.equals("")) {
                            java.lang.String prefix2 = xmlWriter.getPrefix(namespace2);

                            if (prefix2 == null) {
                                prefix2 = generatePrefix(namespace2);

                                xmlWriter.writeStartElement(prefix2, "records", namespace2);
                                xmlWriter.writeNamespace(prefix2, namespace2);
                                xmlWriter.setPrefix(prefix2, namespace2);

                            } else {
                                xmlWriter.writeStartElement(namespace2, "records");
                            }

                        } else {
                            xmlWriter.writeStartElement("records");
                        }

                        // write the nil attribute
                        writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                        xmlWriter.writeEndElement();

                    }

                }
            } else {

                // write null attribute
                java.lang.String namespace2 = "urn:partner.soap.sforce.com";
                if (!namespace2.equals("")) {
                    java.lang.String prefix2 = xmlWriter.getPrefix(namespace2);

                    if (prefix2 == null) {
                        prefix2 = generatePrefix(namespace2);

                        xmlWriter.writeStartElement(prefix2, "records", namespace2);
                        xmlWriter.writeNamespace(prefix2, namespace2);
                        xmlWriter.setPrefix(prefix2, namespace2);

                    } else {
                        xmlWriter.writeStartElement(namespace2, "records");
                    }

                } else {
                    xmlWriter.writeStartElement("records");
                }

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();

            }
        }
        namespace = "urn:partner.soap.sforce.com";
        if (!namespace.equals("")) {
            prefix = xmlWriter.getPrefix(namespace);

            if (prefix == null) {
                prefix = generatePrefix(namespace);

                xmlWriter.writeStartElement(prefix, "size", namespace);
                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);

            } else {
                xmlWriter.writeStartElement(namespace, "size");
            }

        } else {
            xmlWriter.writeStartElement("size");
        }

        if (localSize == java.lang.Integer.MIN_VALUE) {

            throw new org.apache.axis2.databinding.ADBException("size cannot be null!!");

        } else {
            xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localSize));
        }

        xmlWriter.writeEndElement();

        xmlWriter.writeEndElement();

    }

    /**
     * Util method to write an attribute with the ns prefix
     */
    private void writeAttribute(java.lang.String prefix, java.lang.String namespace, java.lang.String attName,
            java.lang.String attValue, javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
        if (xmlWriter.getPrefix(namespace) == null) {
            xmlWriter.writeNamespace(prefix, namespace);
            xmlWriter.setPrefix(prefix, namespace);

        }

        xmlWriter.writeAttribute(namespace, attName, attValue);

    }

    /**
     * Util method to write an attribute without the ns prefix
     */
    private void writeAttribute(java.lang.String namespace, java.lang.String attName, java.lang.String attValue,
            javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
        if (namespace.equals("")) {
            xmlWriter.writeAttribute(attName, attValue);
        } else {
            registerPrefix(xmlWriter, namespace);
            xmlWriter.writeAttribute(namespace, attName, attValue);
        }
    }

    /**
     * Util method to write an attribute without the ns prefix
     */
    private void writeQNameAttribute(java.lang.String namespace, java.lang.String attName, javax.xml.namespace.QName qname,
            javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {

        java.lang.String attributeNamespace = qname.getNamespaceURI();
        java.lang.String attributePrefix = xmlWriter.getPrefix(attributeNamespace);
        if (attributePrefix == null) {
            attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
        }
        java.lang.String attributeValue;
        if (attributePrefix.trim().length() > 0) {
            attributeValue = attributePrefix + ":" + qname.getLocalPart();
        } else {
            attributeValue = qname.getLocalPart();
        }

        if (namespace.equals("")) {
            xmlWriter.writeAttribute(attName, attributeValue);
        } else {
            registerPrefix(xmlWriter, namespace);
            xmlWriter.writeAttribute(namespace, attName, attributeValue);
        }
    }

    /**
     * method to handle Qnames
     */

    private void writeQName(javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
            throws javax.xml.stream.XMLStreamException {
        java.lang.String namespaceURI = qname.getNamespaceURI();
        if (namespaceURI != null) {
            java.lang.String prefix = xmlWriter.getPrefix(namespaceURI);
            if (prefix == null) {
                prefix = generatePrefix(namespaceURI);
                xmlWriter.writeNamespace(prefix, namespaceURI);
                xmlWriter.setPrefix(prefix, namespaceURI);
            }

            if (prefix.trim().length() > 0) {
                xmlWriter.writeCharacters(prefix + ":" + org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
            } else {
                // i.e this is the default namespace
                xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
            }

        } else {
            xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
        }
    }

    private void writeQNames(javax.xml.namespace.QName[] qnames, javax.xml.stream.XMLStreamWriter xmlWriter)
            throws javax.xml.stream.XMLStreamException {

        if (qnames != null) {
            // we have to store this data until last moment since it is not possible to write any
            // namespace data after writing the charactor data
            java.lang.StringBuffer stringToWrite = new java.lang.StringBuffer();
            java.lang.String namespaceURI = null;
            java.lang.String prefix = null;

            for (int i = 0; i < qnames.length; i++) {
                if (i > 0) {
                    stringToWrite.append(" ");
                }
                namespaceURI = qnames[i].getNamespaceURI();
                if (namespaceURI != null) {
                    prefix = xmlWriter.getPrefix(namespaceURI);
                    if ((prefix == null) || (prefix.length() == 0)) {
                        prefix = generatePrefix(namespaceURI);
                        xmlWriter.writeNamespace(prefix, namespaceURI);
                        xmlWriter.setPrefix(prefix, namespaceURI);
                    }

                    if (prefix.trim().length() > 0) {
                        stringToWrite.append(prefix).append(":")
                                .append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                    } else {
                        stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                    }
                } else {
                    stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                }
            }
            xmlWriter.writeCharacters(stringToWrite.toString());
        }

    }

    /**
     * Register a namespace prefix
     */
    private java.lang.String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter, java.lang.String namespace)
            throws javax.xml.stream.XMLStreamException {
        java.lang.String prefix = xmlWriter.getPrefix(namespace);

        if (prefix == null) {
            prefix = generatePrefix(namespace);

            while (xmlWriter.getNamespaceContext().getNamespaceURI(prefix) != null) {
                prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
            }

            xmlWriter.writeNamespace(prefix, namespace);
            xmlWriter.setPrefix(prefix, namespace);
        }

        return prefix;
    }

    /**
     * databinding method to get an XML representation of this object
     * 
     */
    public javax.xml.stream.XMLStreamReader getPullParser(javax.xml.namespace.QName qName)
            throws org.apache.axis2.databinding.ADBException {

        java.util.ArrayList elementList = new java.util.ArrayList();
        java.util.ArrayList attribList = new java.util.ArrayList();

        elementList.add(new javax.xml.namespace.QName("urn:partner.soap.sforce.com", "done"));

        elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localDone));

        elementList.add(new javax.xml.namespace.QName("urn:partner.soap.sforce.com", "queryLocator"));

        elementList.add(localQueryLocator == null ? null : localQueryLocator);
        if (localRecordsTracker) {
            if (localRecords != null) {
                for (int i = 0; i < localRecords.length; i++) {

                    if (localRecords[i] != null) {
                        elementList.add(new javax.xml.namespace.QName("urn:partner.soap.sforce.com", "records"));
                        elementList.add(localRecords[i]);
                    } else {

                        elementList.add(new javax.xml.namespace.QName("urn:partner.soap.sforce.com", "records"));
                        elementList.add(null);

                    }

                }
            } else {

                elementList.add(new javax.xml.namespace.QName("urn:partner.soap.sforce.com", "records"));
                elementList.add(localRecords);

            }

        }
        elementList.add(new javax.xml.namespace.QName("urn:partner.soap.sforce.com", "size"));

        elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localSize));

        return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(qName, elementList.toArray(),
                attribList.toArray());

    }

    /**
     * Factory class that keeps the parse method
     */
    public static class Factory {

        /**
         * static method to create the object Precondition: If this object is an element, the current or next start
         * element starts this object and any intervening reader events are ignorable If this object is not an element,
         * it is a complex type and the reader is at the event just after the outer start element Postcondition: If this
         * object is an element, the reader is positioned at its end element If this object is a complex type, the
         * reader is positioned at the end element of its outer element
         */
        public static QueryResult parse(javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception {
            QueryResult object = new QueryResult();

            int event;
            java.lang.String nillableValue = null;
            java.lang.String prefix = "";
            java.lang.String namespaceuri = "";
            try {

                while (!reader.isStartElement() && !reader.isEndElement())
                    reader.next();

                if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type") != null) {
                    java.lang.String fullTypeName = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type");
                    if (fullTypeName != null) {
                        java.lang.String nsPrefix = null;
                        if (fullTypeName.indexOf(":") > -1) {
                            nsPrefix = fullTypeName.substring(0, fullTypeName.indexOf(":"));
                        }
                        nsPrefix = nsPrefix == null ? "" : nsPrefix;

                        java.lang.String type = fullTypeName.substring(fullTypeName.indexOf(":") + 1);

                        if (!"QueryResult".equals(type)) {
                            // find namespace for the prefix
                            java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                            return (QueryResult) com.salesforce.soap.partner.sobject.ExtensionMapper.getTypeObject(nsUri, type,
                                    reader);
                        }

                    }

                }

                // Note all attributes that were handled. Used to differ normal attributes
                // from anyAttributes.
                java.util.Vector handledAttributes = new java.util.Vector();

                reader.next();

                java.util.ArrayList list3 = new java.util.ArrayList();

                while (!reader.isStartElement() && !reader.isEndElement())
                    reader.next();

                if (reader.isStartElement()
                        && new javax.xml.namespace.QName("urn:partner.soap.sforce.com", "done").equals(reader.getName())) {

                    java.lang.String content = reader.getElementText();

                    object.setDone(org.apache.axis2.databinding.utils.ConverterUtil.convertToBoolean(content));

                    reader.next();

                } // End of if for expected property start element

                else {
                    // A start element we are not expecting indicates an invalid parameter was passed
                    throw new org.apache.axis2.databinding.ADBException("Unexpected subelement " + reader.getLocalName());
                }

                while (!reader.isStartElement() && !reader.isEndElement())
                    reader.next();

                if (reader.isStartElement()
                        && new javax.xml.namespace.QName("urn:partner.soap.sforce.com", "queryLocator").equals(reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                        object.setQueryLocator(null);
                        reader.next();

                        reader.next();

                    } else {

                        object.setQueryLocator(com.salesforce.soap.partner.QueryLocator.Factory.parse(reader));

                        reader.next();
                    }
                } // End of if for expected property start element

                else {
                    // A start element we are not expecting indicates an invalid parameter was passed
                    throw new org.apache.axis2.databinding.ADBException("Unexpected subelement " + reader.getLocalName());
                }

                while (!reader.isStartElement() && !reader.isEndElement())
                    reader.next();

                if (reader.isStartElement()
                        && new javax.xml.namespace.QName("urn:partner.soap.sforce.com", "records").equals(reader.getName())) {

                    // Process the array and step past its final element's end.

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                        list3.add(null);
                        reader.next();
                    } else {
                        list3.add(com.salesforce.soap.partner.sobject.SObject.Factory.parse(reader));
                    }
                    // loop until we find a start element that is not part of this array
                    boolean loopDone3 = false;
                    while (!loopDone3) {
                        // We should be at the end element, but make sure
                        while (!reader.isEndElement())
                            reader.next();
                        // Step out of this element
                        reader.next();
                        // Step to next element event.
                        while (!reader.isStartElement() && !reader.isEndElement())
                            reader.next();
                        if (reader.isEndElement()) {
                            // two continuous end elements means we are exiting the xml structure
                            loopDone3 = true;
                        } else {
                            if (new javax.xml.namespace.QName("urn:partner.soap.sforce.com", "records").equals(reader.getName())) {

                                nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                                if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                                    list3.add(null);
                                    reader.next();
                                } else {
                                    list3.add(com.salesforce.soap.partner.sobject.SObject.Factory.parse(reader));
                                }
                            } else {
                                loopDone3 = true;
                            }
                        }
                    }
                    // call the converter utility to convert and set the array

                    object.setRecords((com.salesforce.soap.partner.sobject.SObject[]) org.apache.axis2.databinding.utils.ConverterUtil
                            .convertToArray(com.salesforce.soap.partner.sobject.SObject.class, list3));

                } // End of if for expected property start element

                else {

                }

                while (!reader.isStartElement() && !reader.isEndElement())
                    reader.next();

                if (reader.isStartElement()
                        && new javax.xml.namespace.QName("urn:partner.soap.sforce.com", "size").equals(reader.getName())) {

                    java.lang.String content = reader.getElementText();

                    object.setSize(org.apache.axis2.databinding.utils.ConverterUtil.convertToInt(content));

                    reader.next();

                } // End of if for expected property start element

                else {
                    // A start element we are not expecting indicates an invalid parameter was passed
                    throw new org.apache.axis2.databinding.ADBException("Unexpected subelement " + reader.getLocalName());
                }

                while (!reader.isStartElement() && !reader.isEndElement())
                    reader.next();

                if (reader.isStartElement())
                    // A start element we are not expecting indicates a trailing invalid property
                    throw new org.apache.axis2.databinding.ADBException("Unexpected subelement " + reader.getLocalName());

            } catch (javax.xml.stream.XMLStreamException e) {
                throw new java.lang.Exception(e);
            }

            return object;
        }

    }// end of factory class

}

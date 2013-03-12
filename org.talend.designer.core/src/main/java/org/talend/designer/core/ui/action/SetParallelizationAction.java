// ============================================================================
//
// Copyright (C) 2006-2013 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.designer.core.ui.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.ui.IWorkbenchPart;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.process.EConnectionType;
import org.talend.core.model.process.EParameterFieldType;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.IConnectionCategory;
import org.talend.core.model.process.IElementParameter;
import org.talend.core.model.process.INode;
import org.talend.designer.core.i18n.Messages;
import org.talend.designer.core.model.components.EParameterName;
import org.talend.designer.core.model.components.ElementParameter;
import org.talend.designer.core.ui.editor.cmd.SetParallelizationCommand;
import org.talend.designer.core.ui.editor.nodecontainer.NodeContainer;
import org.talend.designer.core.ui.editor.nodecontainer.NodeContainerPart;
import org.talend.designer.core.ui.editor.nodes.Node;
import org.talend.designer.core.ui.editor.nodes.NodePart;
import org.talend.designer.core.ui.editor.subjobcontainer.SubjobContainer;
import org.talend.designer.core.ui.editor.subjobcontainer.SubjobContainerPart;

public class SetParallelizationAction extends SelectionAction {

    public static final String ID = "org.talend.designer.core.ui.editor.action.SetParallelizationAction"; //$NON-NLS-1$

    private static final String INPUT = "Input";

    private static final String OUTPUT = "Output";

    IWorkbenchPart part;

    public SetParallelizationAction(IWorkbenchPart part) {
        super(part);
        this.part = part;
        setId(ID);
        setText(Messages.getString("PropertiesContextAction.parallelization")); //$NON-NLS-1$
    }

    @Override
    protected boolean calculateEnabled() {
        List parts = getSelectedObjects();
        if (parts.isEmpty()) {
            return false;
        }
        if (parts.size() == 1) {
            Object o = parts.get(0);
            if (o instanceof SubjobContainerPart) {
                SubjobContainerPart part = (SubjobContainerPart) o;
                SubjobContainer subjob = (SubjobContainer) part.getModel();
                if (subjob.isDisplayed()) {
                    return true;
                } else {
                    return false;
                }
            } else if (o instanceof NodePart) {
                NodePart part = (NodePart) o;
                Node node = (Node) part.getModel();
                if (node.isStart()) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
        return false;
    }

    @Override
    public void run() {
        List editparts = getSelectedObjects();
        if (editparts.size() == 1) {
            Object o = editparts.get(0);
            if (o instanceof NodePart) {
                NodePart part = (NodePart) o;
                Node node = (Node) part.getModel();
                getCommandStack().execute(new SetParallelizationCommand(node));
            } else if (o instanceof SubjobContainerPart) {
                boolean hasStartNode = false;
                List<NodeContainerPart> childNodes = ((SubjobContainerPart) o).getChildren();
                for (NodeContainerPart childNode : childNodes) {
                    NodeContainerPart part = (NodeContainerPart) childNode;
                    NodeContainer node = (NodeContainer) part.getModel();
                    if (node.getNode().isStart()) {
                        hasStartNode = true;
                        getCommandStack().execute(new SetParallelizationCommand(node.getNode()));
                    }
                }
                if (!hasStartNode) {
                    for (NodeContainerPart childNode : childNodes) {
                        NodeContainerPart part = (NodeContainerPart) childNode;
                        NodeContainer node = (NodeContainer) part.getModel();
                        if (node.getNode().isSubProcessStart()) {
                            getCommandStack().execute(new SetParallelizationCommand(node.getNode()));
                        }
                    }
                }
            }
        }
    }

    private boolean existPreviousParCon(Node currentNode) {
        // To judge if there has par/col on previous connection
        boolean hasParInPreviousCon = false;
        for (IConnection con : currentNode.getIncomingConnections()) {
            Node sourceNode = (Node) con.getSource();
            if (sourceNode.getIncomingConnections().size() > 0) {
                for (IConnection con1 : sourceNode.getIncomingConnections()) {
                    if (con1.getElementParameter(EParameterName.PARTITIONER.getName()) != null
                            && con1.getElementParameter(EParameterName.PARTITIONER.getName()).getValue().equals(true)) {
                        hasParInPreviousCon = true;
                    }
                }
            }
        }
        return hasParInPreviousCon;
    }

    private void setParallelization(INode node) {
        if (node.getOutgoingConnections().size() > 0) {
            for (IConnection con : node.getOutgoingConnections()) {
                EConnectionType lineStyle = con.getLineStyle();
                if (lineStyle.hasConnectionCategory(IConnectionCategory.MAIN)
                        || lineStyle.hasConnectionCategory(IConnectionCategory.MERGE)) {
                    if (isComponentCanParlization(con, (Node) con.getTarget())) {
                        if (existPreviousParCon((Node) con.getTarget())) {
                            con.getElementParameter(EParameterName.PARTITIONER.getName()).setValue(Boolean.FALSE);
                            con.getElementParameter(EParameterName.DEPARTITIONER.getName()).setValue(Boolean.FALSE);
                            con.setPropertyValue(EParameterName.NONE.getName(), Boolean.TRUE);
                            node = con.getTarget();
                            setParallelization(node);
                        } else {
                            List<String> conKeyColumnList = getKeyColumnList(con.getMetadataTable());
                            con.getElementParameter(EParameterName.DEPARTITIONER.getName()).setValue(Boolean.FALSE);
                            con.getElementParameter(EParameterName.NONE.getName()).setValue(Boolean.FALSE);
                            con.setPropertyValue(EParameterName.PARTITIONER.getName(), Boolean.TRUE);

                            // set the keys for hash keys
                            IElementParameter parTableCon = con.getElementParameter("HASH_KEYS");
                            if (parTableCon != null) {
                                Object[] itemCon = parTableCon.getListItemsValue();
                                String clumnKeyListName = "";
                                for (Object itemList : itemCon) {
                                    if (((ElementParameter) itemList).getFieldType().equals(EParameterFieldType.PREV_COLUMN_LIST)
                                            || ((ElementParameter) itemList).getFieldType().equals(
                                                    EParameterFieldType.COLUMN_LIST)) {
                                        clumnKeyListName = ((ElementParameter) itemList).getName();
                                    }
                                }
                                for (String partionValue : conKeyColumnList) {
                                    Map partionKeyMap = new HashMap<String, String>();
                                    partionKeyMap.put(clumnKeyListName, partionValue);
                                    ((List) parTableCon.getValue()).add(partionKeyMap);
                                }

                            }
                            if (con.getTarget() != null) {
                                setParallelization(con.getTarget());
                            }
                        }
                    } else {
                        if (!con.getSource().isStart()) {
                            setDeparallelization(con.getTarget());
                        }
                    }
                } else {
                    node = con.getTarget();
                    setParallelization(node);
                }
            }
        } else {
            if (!node.isStart()) {
                setDeparallelization(node);
            }
        }
    }

    private static List<String> getKeyColumnList(IMetadataTable table) {
        List<String> columnList = new ArrayList<String>();
        if (table != null) {
            for (IMetadataColumn column : table.getListColumns()) {
                if (column.isKey()) {
                    String label = column.getLabel();
                    columnList.add(label);
                }
            }
        }
        return columnList;
    }

    private void setDeparallelization(INode node) {
        for (IConnection con : node.getIncomingConnections()) {
            EConnectionType lineStyle = con.getLineStyle();
            if (lineStyle.hasConnectionCategory(IConnectionCategory.MAIN)
                    || lineStyle.hasConnectionCategory(IConnectionCategory.MERGE)) {
                con.getElementParameter(EParameterName.PARTITIONER.getName()).setValue(Boolean.FALSE);
                con.getElementParameter(EParameterName.NONE.getName()).setValue(Boolean.FALSE);
                con.setPropertyValue(EParameterName.DEPARTITIONER.getName(), Boolean.TRUE);
            }
        }
        if (node.getOutgoingConnections().size() > 0) {
            setParallelization(node);
        }
    }

    private boolean isComponentCanParlization(IConnection parConnection, Node needToPar) {

        // TODO:Temply fix,later need a parameter in components to judge this node can be paralization or not
        if (needToPar.getComponent().getName().contains("tMatchGroup") || needToPar.getComponent().getName().contains("tSortRow")) { // ||
            return true;
        } else if (needToPar.getComponent().getName().contains("tAggregate")) {
            return false;
        }
        // some components have no field table but can be paralization such as tMap
        for (IConnection outCon : needToPar.getOutgoingConnections()) {
            if (outCon.getLineStyle().hasConnectionCategory(IConnectionCategory.MAIN)
                    || outCon.getLineStyle().hasConnectionCategory(IConnectionCategory.MERGE)) {
                IMetadataTable metaTable = outCon.getMetadataTable();
                if (parConnection.getMetadataTable().sameMetadataAs(metaTable)) {
                    return true;
                }
            }
        }
        return false;
    }
    //
    // public CommandStack getCommandStack() {
    // return part == null ? null : (CommandStack) (part.getTalendEditor().getAdapter(CommandStack.class));
    // }
}

// ============================================================================
//
// Talend Community Edition
//
// Copyright (C) 2006-2007 Talend - www.talend.com
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
//
// ============================================================================
package org.talend.designer.mapper.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.talend.commons.ui.swt.tableviewer.TableViewerCreator;
import org.talend.commons.utils.generation.CodeGenerationUtils;
import org.talend.commons.utils.threading.ExecutionLimiter;
import org.talend.core.language.ECodeLanguage;
import org.talend.core.language.ICodeProblemsChecker;
import org.talend.core.model.components.IODataComponent;
import org.talend.core.model.components.IODataComponentContainer;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.process.AbstractConnection;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.IExternalNode;
import org.talend.core.model.process.INode;
import org.talend.core.model.process.Problem;
import org.talend.core.model.process.Problem.ProblemStatus;
import org.talend.designer.codegen.IAloneProcessNodeConfigurer;
import org.talend.designer.mapper.language.ILanguage;
import org.talend.designer.mapper.language.LanguageProvider;
import org.talend.designer.mapper.language.generation.JavaGenerationManager;
import org.talend.designer.mapper.language.generation.JavaGenerationManager.PROBLEM_KEY_FIELD;
import org.talend.designer.mapper.model.table.AbstractInOutTable;
import org.talend.designer.mapper.model.tableentry.ExpressionFilterEntry;
import org.talend.designer.mapper.model.tableentry.FilterTableEntry;
import org.talend.designer.mapper.model.tableentry.IColumnEntry;
import org.talend.designer.mapper.model.tableentry.IDataMapTableEntry;
import org.talend.designer.mapper.ui.visualmap.table.DataMapTableView;
import org.talend.designer.mapper.ui.visualmap.zone.Zone;

/**
 * DOC amaumont class global comment. Detailled comment <br/>
 * 
 * $Id$
 * 
 */
public class ProblemsManager {

    private static final String EMPTY_STRING = ""; //$NON-NLS-1$

    private MapperManager mapperManager;

    private ICodeProblemsChecker codeChecker;

    private ECodeLanguage codeLanguage;

    private IAloneProcessNodeConfigurer nodeConfigurer;

    private CheckProblemForEntryLimiter checkProblemForEntryLimiter;

    private Boolean hasProblems;

    private boolean refreshTableEntries;

    /**
     * DOC amaumont ProblemsManager constructor comment.
     */
    private ProblemsManager() {
        super();
    }

    /**
     * DOC amaumont ProblemsManager constructor comment.
     * 
     * @param manager
     */
    public ProblemsManager(final MapperManager mapperManager) {
        this.mapperManager = mapperManager;
        ILanguage currentLanguage = LanguageProvider.getCurrentLanguage();
        codeLanguage = currentLanguage.getCodeLanguage();
        codeChecker = currentLanguage.getCodeChecker();
        this.nodeConfigurer = new IAloneProcessNodeConfigurer() {

            public void configure(INode processNode) {

                IExternalNode mapperNode = mapperManager.getComponent();
                if (processNode.getUniqueName().equals(mapperNode.getUniqueName())) {

                    IExternalNode processExternalNode = (IExternalNode) processNode;
                    processExternalNode.setExternalData(mapperNode.getExternalData());

                    IODataComponentContainer dataComponents = mapperNode.getIODataComponents();

                    List<IODataComponent> mapperInputsDataComponent = (List<IODataComponent>) dataComponents
                            .getInputs();
                    HashMap<String, IMetadataTable> connectionNameToInputMetadataTable = new HashMap<String, IMetadataTable>();
                    for (IODataComponent dataComponent : mapperInputsDataComponent) {
                        connectionNameToInputMetadataTable.put(dataComponent.getConnection().getName(), dataComponent
                                .getTable());
                    }
                    List<IConnection> processIncomingConnections = (List<IConnection>) processExternalNode
                            .getIncomingConnections();
                    for (IConnection connection : processIncomingConnections) {
                        if (connection instanceof AbstractConnection) {
                            IMetadataTable metadataTable = connectionNameToInputMetadataTable.get(connection.getName());
                            ((AbstractConnection) connection).setMetadataTable(metadataTable);
                        }
                    }

                    List<IMetadataTable> metadataListOut = new ArrayList<IMetadataTable>();

                    List<IODataComponent> mapperOutputsDataComponent = (List<IODataComponent>) dataComponents
                            .getOuputs();
                    HashMap<String, IMetadataTable> connectionNameToOutputMetadataTable = new HashMap<String, IMetadataTable>();
                    for (IODataComponent dataComponent : mapperOutputsDataComponent) {
                        connectionNameToOutputMetadataTable.put(dataComponent.getConnection().getName(), dataComponent
                                .getTable());
                    }
                    List<IConnection> processOutgoingConnections = (List<IConnection>) processExternalNode
                            .getOutgoingConnections();
                    for (IConnection connection : processOutgoingConnections) {
                        if (connection instanceof AbstractConnection) {
                            IMetadataTable metadataTable = connectionNameToOutputMetadataTable
                                    .get(connection.getName());
                            ((AbstractConnection) connection).setMetadataTable(metadataTable);
                            metadataListOut.add(metadataTable);
                        }
                    }
                    processExternalNode.setMetadataList(metadataListOut);

                } else {
                    throw new IllegalArgumentException("Should be same instance..."); //$NON-NLS-1$
                }

            }

        };
    }

    /**
     * 
     * Check all problems and save in cache for Java only.
     */
    public void checkProblems() {
        if (codeLanguage == ECodeLanguage.JAVA) {
            codeChecker.checkProblems(nodeConfigurer);
        }
    }

    /**
     * DOC amaumont Comment method "checkJavaProblemsForEntry".
     * 
     * @param codeChecker
     * @param problemKeyField
     * @param tableName
     * @param entryName
     * @param forceGenerateJavaCode
     * @return
     */
    private List<Problem> checkJavaProblemsForEntry(PROBLEM_KEY_FIELD problemKeyField, String tableName,
            String entryName, boolean forceGenerateJavaCode) {
        String key = mapperManager.buildProblemKey(problemKeyField, tableName, entryName);
        if (forceGenerateJavaCode) {
            return codeChecker.checkProblemsFromKey(key, nodeConfigurer);
        } else {
            return codeChecker.getProblemsFromKey(key);
        }
    }

    /**
     * DOC amaumont Comment method "buildProblemKey".
     * 
     * @param uniqueName
     * @param problemKeyField
     * @param tableName
     * @param entryName
     */
    public String buildProblemKey(String uniqueName, PROBLEM_KEY_FIELD problemKeyField, String tableName,
            String entryName) {
        return CodeGenerationUtils.buildProblemKey(uniqueName, problemKeyField.toString(), tableName, entryName);
    }

    /**
     * DOC amaumont Comment method "checkExpressionSyntax".
     * 
     * @param expression
     */
    public List<Problem> checkExpressionSyntax(String expression) {
        ICodeProblemsChecker codeChecker = LanguageProvider.getCurrentLanguage().getCodeChecker();
        return codeChecker.checkProblemsForExpression(expression);
    }


    /**
     * 
     * DOC amaumont Comment method "checkProblemsForAllEntriesOfAllTables".
     * @param forceRefreshData
     * @return true if has errors
     */
    public boolean checkProblemsForAllEntriesOfAllTables(boolean forceRefreshData) {
        hasProblems = Boolean.FALSE;
        List<DataMapTableView> tablesView = mapperManager.getUiManager().getInputsTablesView();
        tablesView.addAll(mapperManager.getUiManager().getVarsTablesView());
        tablesView.addAll(mapperManager.getUiManager().getOutputsTablesView());
        if (forceRefreshData) {
            mapperManager.getComponent().refreshMapperConnectorData();
            checkProblems();
        }
        for (DataMapTableView view : tablesView) {
            checkProblemsForAllEntries(view, false);
        }
        boolean returnedValue = hasProblems;
        hasProblems = null;
        return returnedValue;
    }

    /**
     * 
     * DOC amaumont Comment method "checkProblemsForAllEntries".
     * @param dataMapTableView
     * @param forceRefreshData
     * @return true if has errors
     */
    @SuppressWarnings("unchecked")//$NON-NLS-1$
    public boolean checkProblemsForAllEntries(DataMapTableView dataMapTableView, boolean forceRefreshData) {
        if (forceRefreshData) {
            mapperManager.getComponent().refreshMapperConnectorData();
            checkProblems();
        }

        boolean hasProblemsWasNull = false;
        if (hasProblems == null) {
            hasProblems = Boolean.FALSE;
            hasProblemsWasNull = true;
        }

        if (dataMapTableView.getDataMapTable() instanceof AbstractInOutTable) {
            AbstractInOutTable table = (AbstractInOutTable) dataMapTableView.getDataMapTable();
            if (table.isActivateExpressionFilter()) {
                checkProblemsForTableEntry(table.getExpressionFilter(), false);
            }
        }
        List<IColumnEntry> columnsEntriesList = dataMapTableView.getDataMapTable().getColumnEntries();
        checkProblemsForAllEntries(columnsEntriesList);
        if (refreshTableEntries) {
            dataMapTableView.getTableViewerCreatorForColumns().getTableViewer().refresh(true);
        }
        if (dataMapTableView.getZone() == Zone.OUTPUTS) {
            List<IDataMapTableEntry> constraintEntriesList = dataMapTableView.getTableViewerCreatorForFilters().getInputList();
            checkProblemsForAllEntries(constraintEntriesList);
            if (refreshTableEntries) {
                dataMapTableView.getTableViewerCreatorForFilters().getTableViewer().refresh(true);
            }
        }
        boolean returnedValue = hasProblems;
        if (hasProblemsWasNull) {
            hasProblems = null;
        }
        return returnedValue;
    }

    /**
     * 
     * DOC amaumont Comment method "checkProblemsForAllEntries".
     * 
     * @param entriesList
     * @return true if has errors
     */
    private boolean checkProblemsForAllEntries(List<? extends IDataMapTableEntry> entriesList) {
        boolean stateErrorsHasChanged = false;
        refreshTableEntries = false;
        boolean hasProblemsWasNull = false;
        if (hasProblems == null) {
            hasProblems = Boolean.FALSE;
            hasProblemsWasNull = true;
        }

        for (IDataMapTableEntry entry : entriesList) {
            boolean haveProblemsBefore = entry.getProblems() != null;
            mapperManager.getProblemsManager().checkProblemsForTableEntry(entry, false);
            boolean haveProblemsAfter = entry.getProblems() != null;
            if (haveProblemsAfter) {
                hasProblems = Boolean.TRUE;
            }
            if (haveProblemsBefore != haveProblemsAfter) {
                stateErrorsHasChanged = true;
            }
        }
        refreshTableEntries = stateErrorsHasChanged;
        boolean returnedValue = hasProblems;
        if (hasProblemsWasNull) {
            hasProblems = null;
        }
        return returnedValue;
    }

    public void checkProblemsForTableEntryWithDelayLimiter(IDataMapTableEntry tableEntry) {

        if (this.checkProblemForEntryLimiter == null) {
            this.checkProblemForEntryLimiter = new CheckProblemForEntryLimiter(2000, true);
        }
        this.checkProblemForEntryLimiter.setCurrentTableEntry(tableEntry);
        if (tableEntry != this.checkProblemForEntryLimiter.getPreviousTableEntry()) {
            this.checkProblemForEntryLimiter.execute(false);
        } else {
            this.checkProblemForEntryLimiter.startIfExecutable(true);
        }

    }

    /**
     * 
     * DOC amaumont Comment method "checkProblemsForTableEntry".
     * 
     * @param tableEntry
     * @param forceRefreshData
     * @return true if at least one problem has been detected
     */
    public boolean checkProblemsForTableEntry(IDataMapTableEntry tableEntry, boolean forceRefreshData) {

        if (forceRefreshData) {
            mapperManager.getComponent().refreshMapperConnectorData();
            checkProblems();
        }

        String expression = tableEntry.getExpression();
        List<Problem> problems = null;
        if (expression == null || EMPTY_STRING.equals(expression.trim())) {
            problems = null;
        } else {
            // System.out.println("check=" + expression);
            if (codeLanguage == ECodeLanguage.PERL) {
                problems = codeChecker.checkProblemsForExpression(expression);
            } else if (codeLanguage == ECodeLanguage.JAVA) {
                PROBLEM_KEY_FIELD problemKeyField = JavaGenerationManager.PROBLEM_KEY_FIELD.METADATA_COLUMN;
                String entryName = tableEntry.getName();
                if (tableEntry instanceof FilterTableEntry || tableEntry instanceof ExpressionFilterEntry) {
                    problemKeyField = JavaGenerationManager.PROBLEM_KEY_FIELD.FILTER;
                    entryName = null;
                }
                problems = checkJavaProblemsForEntry(problemKeyField, tableEntry.getParent().getName(), entryName,
                        forceRefreshData);
            }
            if (problems != null) {
                for (Iterator iter = problems.iterator(); iter.hasNext();) {
                    Problem problem = (Problem) iter.next();
                    ProblemStatus status = problem.getStatus();
                    if (status != ProblemStatus.ERROR) {
                        iter.remove();
                    }

                }

            } else {
                problems = null;
            }

        }
        tableEntry.setProblems(problems);

        TableViewerCreator tableViewerCreator = mapperManager.retrieveTableViewerCreator(tableEntry);
        if (tableViewerCreator != null) {
            tableViewerCreator.getTableViewer().refresh(tableEntry, true);
        }

        if (problems != null) {
            hasProblems = problems != null;
        }
        return problems != null;
    }

    /**
     * 
     * DOC amaumont ProblemsManager class global comment. Detailled comment <br/>
     * 
     * $Id: talend-code-templates.xml 1 2006-09-29 17:06:40Z nrousseau $
     * 
     */
    class CheckProblemForEntryLimiter extends ExecutionLimiter {

        private IDataMapTableEntry previousTableEntry;

        private IDataMapTableEntry currentTableEntry;

        /**
         * DOC amaumont CheckProblemForEntryLimiter constructor comment.
         */
        public CheckProblemForEntryLimiter() {
            super();
        }

        /**
         * DOC amaumont CheckProblemForEntryLimiter constructor comment.
         * 
         * @param timeBeforeNewExecute
         * @param finalExecute
         */
        public CheckProblemForEntryLimiter(int timeBeforeNewExecute, boolean finalExecute) {
            super(timeBeforeNewExecute, finalExecute);
        }

        /**
         * DOC amaumont CheckProblemForEntryLimiter constructor comment.
         * 
         * @param timeBeforeNewExecute
         */
        public CheckProblemForEntryLimiter(int timeBeforeNewExecute) {
            super(timeBeforeNewExecute);
        }

        @Override
        protected void execute(boolean isFinalExecution) {
            mapperManager.getUiManager().getDisplay().syncExec(new Runnable() {

                public void run() {
                    checkProblemsForTableEntry(getCurrentTableEntry(), true);
                    previousTableEntry = getCurrentTableEntry();
                }

            });
        }

        public IDataMapTableEntry getCurrentTableEntry() {
            return this.currentTableEntry;
        }

        public void setCurrentTableEntry(IDataMapTableEntry currentTableEntry) {
            this.currentTableEntry = currentTableEntry;
        }

        public IDataMapTableEntry getPreviousTableEntry() {
            return this.previousTableEntry;
        }

    }

}

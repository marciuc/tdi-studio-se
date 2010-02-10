// ============================================================================
//
// Copyright (C) 2006-2009 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.repository.ui.views;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.internal.ui.util.StringMatcher;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.TextActionHandler;
import org.eclipse.ui.commands.ActionHandler;
import org.eclipse.ui.contexts.IContextActivation;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.internal.dialogs.EventLoopProgressMonitor;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertySheetPageContributor;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.talend.commons.exception.ExceptionHandler;
import org.talend.commons.exception.MessageBoxExceptionHandler;
import org.talend.commons.ui.image.ImageProvider;
import org.talend.commons.ui.swt.actions.ITreeContextualAction;
import org.talend.commons.ui.swt.dialogs.ProgressDialog;
import org.talend.commons.ui.swt.tooltip.AbstractTreeTooltip;
import org.talend.commons.utils.Timer;
import org.talend.core.CorePlugin;
import org.talend.core.context.Context;
import org.talend.core.context.RepositoryContext;
import org.talend.core.model.general.Project;
import org.talend.core.model.migration.IMigrationToolService;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.ItemState;
import org.talend.core.model.properties.Property;
import org.talend.core.model.properties.User;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryObject;
import org.talend.core.model.repository.IRepositoryPrefConstants;
import org.talend.core.model.repository.RepositoryManager;
import org.talend.core.ui.images.ECoreImage;
import org.talend.designer.runprocess.IRunProcessService;
import org.talend.repository.IRepositoryChangedListener;
import org.talend.repository.RepositoryChangedEvent;
import org.talend.repository.RepositoryPlugin;
import org.talend.repository.i18n.Messages;
import org.talend.repository.model.ProjectRepositoryNode;
import org.talend.repository.model.ProxyRepositoryFactory;
import org.talend.repository.model.RepositoryNode;
import org.talend.repository.model.RepositoryNode.ENodeType;
import org.talend.repository.model.RepositoryNode.EProperties;
import org.talend.repository.model.actions.MoveObjectAction;
import org.talend.repository.plugin.integration.SwitchProjectAction;
import org.talend.repository.ui.actions.ActionsHelper;
import org.talend.repository.ui.actions.CopyAction;
import org.talend.repository.ui.actions.DeleteAction;
import org.talend.repository.ui.actions.PasteAction;
import org.talend.repository.ui.actions.RefreshAction;
import org.talend.repository.ui.actions.RepositoryDoubleClickAction;

/**
 * 
 * View that presents all the content of the repository.<br/>
 * 
 * $Id$
 * 
 */
public class RepositoryView extends ViewPart implements IRepositoryView, ITabbedPropertySheetPageContributor,
        IRepositoryChangedListener, ISelectionListener {

    public final static String ID = "org.talend.repository.views.repository"; //$NON-NLS-1$

    private static final String SEPARATOR = ":";

    private static Logger log = Logger.getLogger(RepositoryView.class);

    private TreeViewer viewer;

    private RepositoryContentProvider contentProvider = null;

    private static List<ISelectionChangedListener> listenersNeedTobeAddedIntoTreeviewer = new ArrayList<ISelectionChangedListener>();

    private static ProjectRepositoryNode root = new ProjectRepositoryNode(null, null, ENodeType.STABLE_SYSTEM_FOLDER);

    private List<ITreeContextualAction> contextualsActions;

    private static boolean codeGenerationEngineInitialised;

    private Action doubleClickAction;

    private Action refreshAction;

    private Listener dragDetectListener;

    private MenuManager rootMenu = null;

    public RepositoryView() {
    }

    /**
     * yzhang Comment method "addPreparedListeners".
     * 
     * @param listeners
     */
    public static void addPreparedListeners(ISelectionChangedListener listeners) {
        if (listeners != null) {
            listenersNeedTobeAddedIntoTreeviewer.add(listeners);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.part.ViewPart#init(org.eclipse.ui.IViewSite)
     */
    @Override
    public void init(IViewSite site) throws PartInitException {
        super.init(site);
        CorePlugin.getDefault().getRepositoryService().initializePluginMode();
        if (!codeGenerationEngineInitialised && !CorePlugin.getDefault().getRepositoryService().isRCPMode()) {

            if (!CorePlugin.getDefault().getLibrariesService().isLibSynchronized()) {
                CorePlugin.getDefault().getLibrariesService().syncLibraries();
            }
            codeGenerationEngineInitialised = true;
        }
        getSite().getWorkbenchWindow().getSelectionService().addSelectionListener(this);
    }

    public static IRepositoryView show() {
        IViewPart part = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(IRepositoryView.VIEW_ID);
        if (part == null) {
            try {
                part = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(IRepositoryView.VIEW_ID);
            } catch (Exception e) {
                ExceptionHandler.process(e);
            }
        }

        return (IRepositoryView) part;
    }

    protected TreeViewer createTreeViewer(Composite parent) {
        return new RepositoryTreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
    }

    @Override
    public void createPartControl(Composite parent) {
        viewer = createTreeViewer(parent);
        if (viewer instanceof ITreeViewerListener) {
            viewer.addTreeListener((ITreeViewerListener) viewer);
        }
        viewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
        contentProvider = new RepositoryContentProvider(this);
        viewer.setContentProvider(contentProvider);
        viewer.setLabelProvider(new RepositoryLabelProvider(this));
        viewer.setSorter(new RepositoryNameSorter());
        IViewSite viewSite = getViewSite();
        viewer.setInput(viewSite);
        getSite().setSelectionProvider(viewer);

        addFilters();

        // This only tree listener aim is to change open/close icons on folders :
        viewer.addTreeListener(new ITreeViewerListener() {

            public void treeCollapsed(TreeExpansionEvent event) {
                RepositoryNode node = (RepositoryNode) event.getElement();
                if (node.getType().equals(ENodeType.SIMPLE_FOLDER)) {
                    TreeItem item = getObject(viewer.getTree(), event.getElement());
                    if (item != null) {
                        item.setImage(ImageProvider.getImage(ECoreImage.FOLDER_CLOSE_ICON));
                    }
                }
            }

            public void treeExpanded(TreeExpansionEvent event) {
                RepositoryNode node = (RepositoryNode) event.getElement();
                if (node.getType().equals(ENodeType.SIMPLE_FOLDER)) {
                    TreeItem item = getObject(viewer.getTree(), event.getElement());
                    if (item != null) {
                        item.setImage(ImageProvider.getImage(ECoreImage.FOLDER_OPEN_ICON));
                    }
                }
            }
        });
        createTreeTooltip(viewer.getTree());
        makeActions();
        hookContextMenu();
        contributeToActionBars();
        initDragAndDrop();
        hookDoubleClickAction();

        setPartName(Messages.getString("repository.title", ((RepositoryContext) CorePlugin.getContext().getProperty( //$NON-NLS-1$
                Context.REPOSITORY_CONTEXT_KEY)).getProject()));

        viewer.getTree().addFocusListener(new FocusListener() {

            public void focusGained(FocusEvent e) {
                log.trace("Repository gain focus"); //$NON-NLS-1$
                IContextService contextService = (IContextService) RepositoryPlugin.getDefault().getWorkbench().getAdapter(
                        IContextService.class);
                ca = contextService.activateContext("talend.repository"); //$NON-NLS-1$
            }

            public void focusLost(FocusEvent e) {
                log.trace("Repository lost focus"); //$NON-NLS-1$
                if (ca != null) {
                    IContextService contextService = (IContextService) RepositoryPlugin.getDefault().getWorkbench().getAdapter(
                            IContextService.class);
                    contextService.deactivateContext(ca);
                }
            }
        });

        if (listenersNeedTobeAddedIntoTreeviewer.size() > 0) {
            for (ISelectionChangedListener listener : listenersNeedTobeAddedIntoTreeviewer) {
                viewer.addSelectionChangedListener(listener);
            }
            listenersNeedTobeAddedIntoTreeviewer.clear();
        }

        CorePlugin.getDefault().getRepositoryService().registerRepositoryChangedListenerAsFirst(this);

        if (!CorePlugin.getDefault().getRepositoryService().isRCPMode()) {
            IMigrationToolService toolService = CorePlugin.getDefault().getMigrationToolService();
            toolService.executeMigration(SwitchProjectAction.PLUGIN_MODEL);

            IRunProcessService runService = CorePlugin.getDefault().getRunProcessService();
            runService.deleteAllJobs(SwitchProjectAction.PLUGIN_MODEL);

            final RepositoryContext repositoryContext = (RepositoryContext) CorePlugin.getContext().getProperty(
                    Context.REPOSITORY_CONTEXT_KEY);
            final Project project = repositoryContext.getProject();

            final IWorkbenchWindow activedWorkbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
            activedWorkbenchWindow.getPartService().addPartListener(new IPartListener() {

                public void partActivated(IWorkbenchPart part) {
                    if (part instanceof RepositoryView) {
                        String title = activedWorkbenchWindow.getShell().getText();
                        if (!title.contains("|")) { //$NON-NLS-1$
                            title += " | " + repositoryContext.getUser() + " | " + project.getLabel(); //$NON-NLS-1$ //$NON-NLS-2$
                            activedWorkbenchWindow.getShell().setText(title);
                        }
                    }
                }

                public void partBroughtToTop(IWorkbenchPart part) {

                }

                public void partClosed(IWorkbenchPart part) {

                }

                public void partDeactivated(IWorkbenchPart part) {

                }

                public void partOpened(IWorkbenchPart part) {
                    if (part instanceof RepositoryView) {
                        String title = activedWorkbenchWindow.getShell().getText();
                        if (!title.contains("|")) { //$NON-NLS-1$
                            title += " | " + repositoryContext.getUser() + " | " + project.getLabel(); //$NON-NLS-1$ //$NON-NLS-2$
                            activedWorkbenchWindow.getShell().setText(title);
                        }
                    }
                }

            });
        }
        if (root.getChildren().size() == 1) {
            viewer.setExpandedState(root.getChildren().get(0), true);
        }
    }

    public void addFilters() {
        // filter by node : filter stable talend elements
        viewer.addFilter(new ViewerFilter() {

            @Override
            public boolean select(Viewer viewer, Object parentElement, Object element) {
                String[] uncheckedNodesFromFilter = RepositoryManager
                        .getFiltersByPreferenceKey(IRepositoryPrefConstants.FILTER_BY_NODE);

                RepositoryNode node = (RepositoryNode) element;
                ERepositoryObjectType contentType = node.getContentType();
                if (uncheckedNodesFromFilter == null || contentType == null || node.isBin()) {
                    return true;
                }
                String technicalLabel = node.getRoot().getProject().getEmfProject().getTechnicalLabel();
                String uniqueSymbol = technicalLabel + SEPARATOR;
                // sql patterns like Generic ,Mysql
                if (contentType != null && ERepositoryObjectType.SQLPATTERNS.equals(contentType) && node.getId() != "-1") {
                    uniqueSymbol = uniqueSymbol + contentType.name() + SEPARATOR + node.getProperties(EProperties.LABEL);
                } else {
                    uniqueSymbol = uniqueSymbol + contentType.name();
                    if (node instanceof ProjectRepositoryNode) {
                        uniqueSymbol = uniqueSymbol + SEPARATOR + "ROOT";//$NON-NLS-1$
                    }

                }
                List<String> filters = Arrays.asList(uncheckedNodesFromFilter);
                if (filters.contains(uniqueSymbol)) {
                    return false;
                }
                return true;
            }

        });

        // filter by status and users: filter user created nodes REPOSITORY_ELEMENT
        viewer.addFilter(new ViewerFilter() {

            private StringMatcher[] matchers;

            @Override
            public boolean select(Viewer viewer, Object parentElement, Object element) {
                String[] statusFilter = RepositoryManager.getFiltersByPreferenceKey(IRepositoryPrefConstants.FILTER_BY_STATUS);
                String[] userFilter = RepositoryManager.getFiltersByPreferenceKey(IRepositoryPrefConstants.FILTER_BY_USER);

                List items = new ArrayList();
                if (statusFilter != null && statusFilter.length > 0) {
                    items.addAll(Arrays.asList(statusFilter));
                }
                if (userFilter != null && userFilter.length > 0) {
                    items.addAll(Arrays.asList(userFilter));
                }
                boolean visible = true;
                RepositoryNode node = (RepositoryNode) element;
                if (ENodeType.REPOSITORY_ELEMENT.equals(node.getType())) {
                    String label = (String) node.getProperties(EProperties.LABEL);
                    if (node.getObject() != null) {
                        Property property = node.getObject().getProperty();
                        User author = node.getObject().getAuthor();
                        String statusCode = "";
                        if (property != null) {
                            statusCode = property.getStatusCode();
                        }
                        String user = "";
                        if (author != null) {
                            user = author.getLogin();
                        }
                        if (items.contains(statusCode) || items.contains(user)) {
                            visible = false;
                        }
                    }
                    // filter by name
                    if (isMatchNameFilterPattern(label)) {
                        visible = false;
                    }
                }

                return visible;
            }

            private boolean isMatchNameFilterPattern(String label) {
                boolean enable = RepositoryManager.getPreferenceStore().getBoolean(
                        IRepositoryPrefConstants.TAG_USER_DEFINED_PATTERNS_ENABLED);
                if (!enable) {
                    return false;
                }
                if (label != null && label.length() > 0) {
                    StringMatcher[] testMatchers = getMatchers();
                    if (testMatchers != null) {
                        for (int i = 0; i < testMatchers.length; i++) {
                            if (testMatchers[i].match(label))
                                return true;
                        }
                    }
                }
                return false;
            }

            private StringMatcher[] getMatchers() {
                String userFilterPattern = RepositoryManager.getPreferenceStore().getString(
                        IRepositoryPrefConstants.FILTER_BY_NAME);
                String[] newPatterns = null;
                if (userFilterPattern != null && !"".equals(userFilterPattern)) {
                    newPatterns = RepositoryManager.convertFromString(userFilterPattern, RepositoryManager.PATTERNS_SEPARATOR);
                }
                if (newPatterns != null) {
                    matchers = new StringMatcher[newPatterns.length];
                    for (int i = 0; i < newPatterns.length; i++) {
                        matchers[i] = new StringMatcher(newPatterns[i], true, false);
                    }
                }
                return matchers;
            }
        });

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.part.WorkbenchPart#dispose()
     */
    @Override
    public void dispose() {
        super.dispose();
        getSite().getWorkbenchWindow().getSelectionService().removeSelectionListener(this);
    }

    /**
     * DOC bqian Comment method "createTreeTooltip".
     * 
     * @param tree
     */
    private void createTreeTooltip(Tree tree) {
        AbstractTreeTooltip tooltip = new AbstractTreeTooltip(tree) {

            /*
             * (non-Javadoc)
             * 
             * @see
             * org.talend.commons.ui.swt.tooltip.AbstractTreeTooltip#getTooltipContent(org.eclipse.swt.widgets.TreeItem)
             */
            @Override
            public String getTooltipContent(TreeItem item) {

                RepositoryNode node = (RepositoryNode) item.getData();
                IRepositoryObject object = node.getObject();
                if (object == null) {
                    return null;
                }
                // add for feature 10281
                String content = null;
                Property property = object.getProperty();
                User currentLoginUser = ((RepositoryContext) CorePlugin.getContext().getProperty(Context.REPOSITORY_CONTEXT_KEY))
                        .getUser();
                String crurentLogin = null;
                if (currentLoginUser != null) {
                    crurentLogin = currentLoginUser.getLogin();
                }
                String login = null;
                if (property != null) {
                    Item item2 = property.getItem();
                    if (item2 != null) {
                        ItemState state = item2.getState();
                        if (state != null) {
                            User locker = state.getLocker();
                            if (locker != null) {
                                String lockerLogin = locker.getLogin();
                                if (lockerLogin != null) {
                                    if (!lockerLogin.equals(crurentLogin)) {
                                        login = lockerLogin;
                                    }
                                }
                            }
                        }
                    }
                }
                if (login != null && !"".equals(login)) {//$NON-NLS-1$
                    content = "  locked by :" + login;//$NON-NLS-1$
                }
                String description = object.getDescription();
                if (content == null || "".equals(content)) { //$NON-NLS-1$
                    if (description == null || "".equals(description)) {//$NON-NLS-1$
                        return null;
                    }
                    return description;
                } else {
                    if (description == null || "".equals(description)) {//$NON-NLS-1$
                        return content;
                    }
                    return content + "\n" + "  Description: " + description;//$NON-NLS-1$//$NON-NLS-1$
                }
            }
        };

    }

    IContextActivation ca;

    private TreeItem getObject(Tree tree, Object objectToFind) {
        for (TreeItem item : tree.getItems()) {
            TreeItem toReturn = getObject(item, objectToFind);
            if (toReturn != null) {
                return toReturn;
            }
        }
        return null;
    }

    private TreeItem getObject(TreeItem parent, Object objectToFind) {
        for (TreeItem currentChild : parent.getItems()) {
            if (objectToFind.equals(currentChild.getData())) {
                return currentChild;
            }
            TreeItem toReturn = getObject(currentChild, objectToFind);
            if (toReturn != null) {
                return toReturn;
            }
        }
        return null;
    }

    protected void initDragAndDrop() {
        int ops = DND.DROP_COPY | DND.DROP_MOVE | DND.DROP_LINK;
        Transfer[] transfers = new Transfer[] { LocalSelectionTransfer.getTransfer() };

        viewer.addDragSupport(ops, transfers, new DragSourceAdapter() {

            private static final long FFFFFFFFL = 0xFFFFFFFFL;

            /*
             * (non-Javadoc)
             * 
             * @see org.eclipse.swt.dnd.DragSourceAdapter#dragSetData(org.eclipse.swt.dnd.DragSourceEvent)
             */
            @Override
            public void dragSetData(DragSourceEvent event) {
                event.data = LocalSelectionTransfer.getTransfer().getSelection();
            }

            /*
             * (non-Javadoc)
             * 
             * @see org.eclipse.swt.dnd.DragSourceAdapter#dragStart(org.eclipse.swt.dnd.DragSourceEvent)
             */
            @Override
            public void dragStart(DragSourceEvent event) {
                ISelection selection = viewer.getSelection();

                for (Object obj : ((StructuredSelection) selection).toArray()) {
                    RepositoryNode sourceNode = (RepositoryNode) obj;

                    // As i don't know how to get event operation i test on MoveOperation
                    event.doit = MoveObjectAction.getInstance().validateAction(sourceNode, null);
                }

                LocalSelectionTransfer.getTransfer().setSelection(selection);
                LocalSelectionTransfer.getTransfer().setSelectionSetTime(event.time & FFFFFFFFL);
            }

            /*
             * (non-Javadoc)
             * 
             * @see org.eclipse.swt.dnd.DragSourceAdapter#dragFinished(org.eclipse.swt.dnd.DragSourceEvent)
             */
            @Override
            public void dragFinished(DragSourceEvent event) {
                RepositoryView.this.dragFinished();
            }
        });
        RepositoryDropAdapter adapter = new RepositoryDropAdapter(viewer);
        adapter.setFeedbackEnabled(false);
        viewer.addDropSupport(ops | DND.DROP_DEFAULT, transfers, adapter);
        dragDetectListener = new Listener() {

            public void handleEvent(Event event) {
                // dragDetected = true;
            }
        };
        viewer.getControl().addListener(SWT.DragDetect, dragDetectListener);
    }

    public void dragFinished() {
        refresh();
        LocalSelectionTransfer.getTransfer().setSelection(null);
        LocalSelectionTransfer.getTransfer().setSelectionSetTime(0);
    }

    protected void makeActions() {
        IHandlerService handlerService = (IHandlerService) getSite().getService(IHandlerService.class);

        refreshAction = new RefreshAction(this);
        IHandler handler1 = new ActionHandler(refreshAction);
        handlerService.activateHandler(refreshAction.getActionDefinitionId(), handler1);

        contextualsActions = ActionsHelper.getRepositoryContextualsActions();
        for (ITreeContextualAction action : contextualsActions) {
            action.setWorkbenchPart(this);
            if (action.getActionDefinitionId() != null) {
                handler1 = new ActionHandler(action);
                handlerService.activateHandler(action.getActionDefinitionId(), handler1);
            }
        }
        doubleClickAction = new RepositoryDoubleClickAction(this, contextualsActions);

        TextActionHandler textActionHandler = new TextActionHandler(getViewSite().getActionBars());
        textActionHandler.setCopyAction(CopyAction.getInstance());
        textActionHandler.setPasteAction(PasteAction.getInstance());
        textActionHandler.setDeleteAction(DeleteAction.getInstance());

        getViewSite().getActionBars().setGlobalActionHandler(ActionFactory.COPY.getId(), CopyAction.getInstance());
        getViewSite().getActionBars().setGlobalActionHandler(ActionFactory.PASTE.getId(), PasteAction.getInstance());
        getViewSite().getActionBars().setGlobalActionHandler(ActionFactory.DELETE.getId(), DeleteAction.getInstance());
    }

    protected void hookDoubleClickAction() {
        viewer.addDoubleClickListener(new IDoubleClickListener() {

            public void doubleClick(DoubleClickEvent event) {
                doubleClickAction.run();
            }
        });
    }

    protected void hookContextMenu() {
        MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
        menuMgr.setRemoveAllWhenShown(true);

        menuMgr.addMenuListener(new IMenuListener() {

            public void menuAboutToShow(IMenuManager manager) {
                RepositoryView.this.fillContextMenu(manager);
            }
        });

        Menu menu = menuMgr.createContextMenu(viewer.getControl());
        viewer.getControl().setMenu(menu);
        getSite().registerContextMenu(menuMgr, viewer);
    }

    protected void contributeToActionBars() {
        IActionBars bars = getViewSite().getActionBars();
        fillLocalPullDown(bars.getMenuManager());
        fillLocalToolBar(bars.getToolBarManager());
    }

    private void fillLocalPullDown(IMenuManager manager) {
        // Project project = ProjectManager.getInstance().getCurrentProject();
        //
        // IConfigurationElement[] elems = Platform.getExtensionRegistry().getConfigurationElementsFor(
        // "org.talend.repository.repository_menu_provider");
        // for (IConfigurationElement elem : elems) {
        // RepositoryMenuAction createExecutableExtension;
        // try {
        // createExecutableExtension = (RepositoryMenuAction) elem.createExecutableExtension("class");
        // if (project.isLocal() && !(createExecutableExtension instanceof RepositoryFilterAction)) {
        // continue;
        // }
        // createExecutableExtension.initialize(this);
        // manager.add(createExecutableExtension);
        // } catch (CoreException e) {
        // ExceptionHandler.process(e);
        // }
        // }
    }

    private void fillContextMenu(IMenuManager manager) {
        IStructuredSelection sel = (IStructuredSelection) viewer.getSelection();

        final MenuManager[] menuManagerGroups = org.talend.core.ui.actions.ActionsHelper.getRepositoryContextualsActionGroups();
        // find group
        Set<String> processedGroupIds = new HashSet<String>();
        for (ITreeContextualAction action : contextualsActions) {
            action.init(getViewer(), sel);
            if (action.isVisible() && action.isEnabled()) {
                IMenuManager groupMenu = findMenuManager(menuManagerGroups, action.getGroupId(), true); // find root
                if (groupMenu != null) { // existed
                    final String rootId = groupMenu.getId();
                    if (!processedGroupIds.contains(rootId)) {
                        manager.add(groupMenu);
                        processedGroupIds.add(rootId);
                    }
                }
                groupMenu = findMenuManager(menuManagerGroups, action.getGroupId(), false); // find last child
                if (groupMenu != null) { // existed
                    groupMenu.add(action);
                } else { // child
                    manager.add(action);
                }
            }
        }
        manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
    }

    private MenuManager findMenuManager(final MenuManager[] menuManagerGroups, String groupId, boolean findParent) {
        for (MenuManager groupMenu : menuManagerGroups) {
            if (groupMenu.getId().equals(groupId)) {
                if (findParent) {
                    final MenuManager parent = (MenuManager) groupMenu.getParent();
                    if (parent == null) {
                        return groupMenu;
                    } else {
                        return findMenuManager(menuManagerGroups, parent.getId(), findParent);
                    }
                } else {
                    return groupMenu;
                }
            }
        }
        return null;
    }

    private void fillLocalToolBar(IToolBarManager manager) {
        manager.add(refreshAction);
    }

    @Override
    public void setFocus() {
        viewer.getControl().setFocus();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.ui.repository.views.IRepositoryView#getViewer()
     */
    public TreeViewer getViewer() {
        return viewer;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.ui.repository.views.IRepositoryView#refresh()
     */
    public void refresh() {
        /*
         * fix bug 4040. Sometimes Display.getCurrent.getActiveShell() get null result we not expect.
         */
        // Shell shell = Display.getCurrent().getActiveShell();
        Shell shell = getSite().getShell();

        if (shell == null) {
            return;
        }

        ProgressDialog progressDialog = new ProgressDialog(shell, 1000) {

            private IProgressMonitor monitorWrap;

            @Override
            public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                Timer timer = Timer.getTimer("repositoryView"); //$NON-NLS-1$
                timer.start();
                monitorWrap = new EventLoopProgressMonitor(monitor);
                try {
                    ProxyRepositoryFactory.getInstance().initialize();
                } catch (Exception e) {
                    throw new InvocationTargetException(e);
                }

                root = new ProjectRepositoryNode(null, null, ENodeType.STABLE_SYSTEM_FOLDER);
                viewer.refresh();

                // unsetting the selection will prevent the propertyView from displaying dirty data
                viewer.setSelection(new TreeSelection());

                if (root.getChildren().size() == 1) {
                    viewer.setExpandedState(root.getChildren().get(0), true);
                }
                timer.stop();
                // timer.print();
            }
        };

        try {
            progressDialog.executeProcess();
        } catch (InvocationTargetException e) {
            ExceptionHandler.process(e);
            return;
        } catch (Exception e) {
            MessageBoxExceptionHandler.process(e);
            return;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.repository.ui.views.IRepositoryView#refresh(java.lang.Object)
     */
    public void refresh(Object object) {

        if (object != null) {
            // maybe, no effect.
            viewer.refresh(object);
            viewer.setExpandedState(object, true);
            if (object instanceof RepositoryNode) {
                RepositoryNode node = (RepositoryNode) object;
                ERepositoryObjectType type = node.getObjectType();
                if (type == null) {
                    type = node.getContentType();
                }
                refresh(type);
            }
        } else {
            // refresh();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.repository.ui.views.IRepositoryView#refresh(org.talend.core.model.repository.ERepositoryObjectType)
     */
    public void refresh(ERepositoryObjectType type) {
        if (type != null && !type.isSubItem()) {
            RepositoryNode rootNode = researchRootRepositoryNode(type);
            refreshAllChildNodes(rootNode);
        } else if (type != null && type.name().equals("METADATA_SAP_FUNCTION")) { //$NON-NLS-1$
            RepositoryNode rootNode = researchRootRepositoryNode(type);
            refreshAllChildNodes(rootNode);
        }
    }

    private RepositoryNode researchRootRepositoryNode(ERepositoryObjectType type) {

        if (type != null && getRoot() instanceof ProjectRepositoryNode) {
            ProjectRepositoryNode pRoot = (ProjectRepositoryNode) getRoot();
            return pRoot.getRootRepositoryNode(type);

        }
        return null;
    }

    /**
     * only refresh the child of root node.
     */
    public void refreshAllChildNodes(RepositoryNode rootNode) {
        if (rootNode != null) {
            rootNode.setInitialized(false);
            rootNode.getChildren().clear();
            contentProvider.getChildren(rootNode); // retrieve child
            viewer.refresh(rootNode);
        }
    }

    public void expand(Object object) {
        if (object == null) {
            return;
        }
        boolean state = getExpandedState(object);
        expand(object, !state);
    }

    public boolean getExpandedState(Object object) {
        if (object == null) {
            return false;
        }
        boolean state = viewer.getExpandedState(object);
        return state;
    }

    public void expand(Object object, boolean state) {
        if (object == null) {
            return;
        }
        viewer.setExpandedState(object, state);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.ui.repository.views.IRepositoryView#getSystemFolders()
     */
    public RepositoryNode getRoot() {
        return root;
    }

    public List<IRepositoryObject> getAll(ERepositoryObjectType type) {
        // find the system folder
        RepositoryNode container = findContainer(root, type);

        if (container == null) {
            throw new IllegalArgumentException(type + Messages.getString("RepositoryView.notfound")); //$NON-NLS-1$
        }

        List<IRepositoryObject> result = new ArrayList<IRepositoryObject>();
        addElement(result, type, container);
        return result;
    }

    // see RepositoryContentProvider.initialize();
    private RepositoryNode findContainer(RepositoryNode node, ERepositoryObjectType type) {
        if (node.getType() == ENodeType.SYSTEM_FOLDER || node.getType() == ENodeType.STABLE_SYSTEM_FOLDER) {
            if (type == node.getProperties(EProperties.CONTENT_TYPE)) {
                return node;
            }
            for (RepositoryNode repositoryNode : node.getChildren()) {
                RepositoryNode result = findContainer(repositoryNode, type);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;

    }

    private void addElement(List<IRepositoryObject> result, ERepositoryObjectType type, RepositoryNode node) {
        if (node.getType() == ENodeType.REPOSITORY_ELEMENT && node.getProperties(EProperties.CONTENT_TYPE) == type) {
            result.add(node.getObject());
        } else {
            for (RepositoryNode child : node.getChildren()) {
                addElement(result, type, child);
            }
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.views.properties.tabbed.ITabbedPropertySheetPageContributor#getContributorId()
     */
    public String getContributorId() {
        return getSite().getId();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.part.WorkbenchPart#getAdapter(java.lang.Class)
     */
    @Override
    public Object getAdapter(Class adapter) {
        if (adapter == IPropertySheetPage.class) {
            return new TabbedPropertySheetPage(this);
        }
        return super.getAdapter(adapter);
    }

    public void repositoryChanged(RepositoryChangedEvent event) {
        refresh();
    }

    public String[] gatherMetadataChildenLabels() {
        return contentProvider.gatherMetdataChildrens();
    }

    public void selectionChanged(IWorkbenchPart part, ISelection selection) {
        if (part == this) {
            CopyAction.getInstance().init(getViewer(), (IStructuredSelection) getViewer().getSelection());
            PasteAction.getInstance().init(getViewer(), (IStructuredSelection) getViewer().getSelection());
            DeleteAction.getInstance().init(getViewer(), (IStructuredSelection) getViewer().getSelection());
        }
    }

    public Action getDoubleClickAction() {
        return this.doubleClickAction;
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.talend.repository.ui.views.IRepositoryView#containsRepositoryType(org.talend.core.model.repository.
     * ERepositoryObjectType)
     */
    public boolean containsRepositoryType(ERepositoryObjectType type) {
        return researchRootRepositoryNode(type) != null;
    }
}

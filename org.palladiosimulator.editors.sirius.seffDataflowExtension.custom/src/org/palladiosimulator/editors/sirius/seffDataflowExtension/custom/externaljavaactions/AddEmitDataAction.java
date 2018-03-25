package org.palladiosimulator.editors.sirius.seffDataflowExtension.custom.externaljavaactions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Stream;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.sirius.tools.api.ui.IExternalJavaAction;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.palladiosimulator.editors.commons.dialogs.selection.PalladioSelectEObjectDialog;
import org.palladiosimulator.pcm.dataspec.repository.DataPort;
import org.palladiosimulator.pcm.dataspec.seff.EmitDataAction;
import org.palladiosimulator.pcm.repository.BasicComponent;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.seff.ServiceEffectSpecification;

public class AddEmitDataAction implements IExternalJavaAction {
	
	public static final Shell SHELL = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();

	@Override
	public void execute(Collection<? extends EObject> selections, Map<String, Object> parameters) {
		EmitDataAction emitDataAction = (EmitDataAction) parameters.get("instance");
		DataPort dataPort = getDataPort(emitDataAction);
		emitDataAction.setDataPort(dataPort);
	}
	
	private DataPort getDataPort(EmitDataAction emitDataAction) {	
		Collection<Object> filter = new ArrayList<Object>();
		filter.add(Repository.class);
		filter.add(BasicComponent.class);
		filter.add(DataPort.class);
			
		Collection<EReference> additionalChildReferences = new ArrayList<EReference>();
		PalladioSelectEObjectDialog dialog = new PalladioSelectEObjectDialog(SHELL, filter, additionalChildReferences,
				emitDataAction.eResource().getResourceSet());	
		dialog.setProvidedService(DataPort.class);
		
		ServiceEffectSpecification seff = (ServiceEffectSpecification) emitDataAction.getResourceDemandingBehaviour_AbstractAction();
		BasicComponent parent = seff.getBasicComponent_ServiceEffectSpecification();
		
		dialog.getTreeViewer().remove(this.getDataPortsToRemove(parent).toArray());
		dialog.getTreeViewer().remove(this.getBasicComponentsToRemove(parent).toArray());
						
		dialog.open();
		return (DataPort) dialog.getResult();
	}
	
	/**
	 * This method is used to get all irrelevant DataPorts, which should be removed from the Dialog
	 * 
	 * @param basicComponent  The container of the SEFF, which contains the emitDataAction
	 * @return a filtered stream consisting of DataPorts, that do not have an outbound datatype
	 * 		   or an empty stream, if the basicComponent does not contain DataPorts without outbound datatypes
	 */
	private Stream<DataPort> getDataPortsToRemove(BasicComponent basicComponent) {
		return basicComponent.getProvidedRoles_InterfaceProvidingEntity().stream()
				.filter(DataPort.class::isInstance)
				.map(DataPort.class::cast)
				.filter(dataPort -> dataPort.getPortSpecification().getOutboundDataType() == null);
	}
	
	/**
	 * This method is used to get all irrelevant BasicComponents, which should be removed from the Dialog
	 * 
	 * @param basicComponent  The container of the SEFF, which contains the emitDataAction
	 * @return a filtered stream consisting of BasicComponents, other than the container
	 * 		   or an empty stream, if repository does not contain other BasicComponents
	 */
	private Stream<BasicComponent> getBasicComponentsToRemove(BasicComponent basicComponent) {
		return basicComponent.getRepository__RepositoryComponent().getComponents__Repository().stream()
				.filter(BasicComponent.class::isInstance)
				.map(BasicComponent.class::cast)
				.filter(bc -> !bc.equals(basicComponent));
	}
	
	@Override
	public boolean canExecute(Collection<? extends EObject> selections) {
		return true;
	}
}

package org.palladiosimulator.editors.sirius.repositoryDataflowExtension.custom.externaljavaactions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.sirius.tools.api.ui.IExternalJavaAction;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.palladiosimulator.editors.commons.dialogs.selection.PalladioSelectEObjectDialog;
import org.palladiosimulator.pcm.dataspec.repository.InteractiveDataPort;
import org.palladiosimulator.pcm.repository.BasicComponent;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.seff.ServiceEffectSpecification;

public class SetInboundDataTriggersControlFlow implements IExternalJavaAction {
	
	public static final Shell SHELL = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();

	@Override
	public void execute(Collection<? extends EObject> selections, Map<String, Object> parameters) {
		
	InteractiveDataPort dataPort = (InteractiveDataPort) parameters.get("instance");
		
	ServiceEffectSpecification serviceEffectSpecification = getServiceEffectSpecification(dataPort);
	dataPort.setInboundDataTriggersControlFlow(serviceEffectSpecification);
	}
	
	private ServiceEffectSpecification getServiceEffectSpecification(InteractiveDataPort dataPort) {		
		Collection<Object> filter = new ArrayList<Object>();
		filter.add(Repository.class);
		filter.add(ServiceEffectSpecification.class);
		filter.add(BasicComponent.class);

		// Additional Child References
		Collection<EReference> additionalChildReferences = new ArrayList<EReference>();
		
		// Creating the dialog
		PalladioSelectEObjectDialog dialog = new PalladioSelectEObjectDialog(SHELL, filter, additionalChildReferences, dataPort.eResource().getResourceSet());
	
		// Setting the needed object type
		dialog.setProvidedService(ServiceEffectSpecification.class);
		
		BasicComponent basicComponent = (BasicComponent) dataPort.getProvidingEntity_ProvidedRole();
		for (Object o : dialog.getTreeViewer().getExpandedElements()) {			
			if (o instanceof BasicComponent) {
				if(!basicComponent.equals(o)) {
					dialog.getTreeViewer().remove(o);
				}			
			}
		}

		dialog.open();

		return (ServiceEffectSpecification) dialog.getResult();
	}

	@Override
	public boolean canExecute(Collection<? extends EObject> selections) {
		return true;
	}
}

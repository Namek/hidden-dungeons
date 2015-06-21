package net.hiddendungeons.system.base.events;


import java.util.List;

import net.mostlyoriginal.api.event.common.EventListener;
import net.mostlyoriginal.api.event.common.ListenerFinderStrategy;
import net.mostlyoriginal.api.event.common.SubscribeAnnotationFinder;

import com.artemis.utils.reflect.Annotation;
import com.artemis.utils.reflect.ClassReflection;
import com.artemis.utils.reflect.Method;

public class SignalAndEventFinder implements ListenerFinderStrategy {
	private SubscribeAnnotationFinder eventListenersFinder;
	
	public SignalAndEventFinder() {
		eventListenersFinder = new SubscribeAnnotationFinder();
	}

	@Override
	public List<EventListener> resolve(Object o) {
		// Find event listeners
		final List<EventListener> listeners = eventListenersFinder.resolve(o);

		// Find signal listeners
		for (Method method : ClassReflection.getDeclaredMethods(o.getClass())) {
			if (method.isAnnotationPresent(SubscribeSignal.class)) {
				final Annotation declaredAnnotation = method.getDeclaredAnnotation(SubscribeSignal.class);

				if (declaredAnnotation != null) {
					final SubscribeSignal signalCode = declaredAnnotation.getAnnotation(SubscribeSignal.class);
					listeners.add(new SignalListener(o, method, signalCode.value()));
				}
			}
		}

		return listeners;
	}
}

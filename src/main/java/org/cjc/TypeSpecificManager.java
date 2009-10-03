package org.cjc;

import java.util.HashMap;
import java.util.Map;

/**
 * A manager for creating/locating instances of a specific type.
 * 
 * <p>
 * The manager needs not be aware of the specific classes it provides, only one
 * common interface is required to support the creation or retrieval of the
 * implementation.
 * </p>
 * 
 * <p>
 * <b>Useful when:</b>
 * </p>
 * <ul>
 * <li>A different kind of operation is required for a group of similar classes
 * <li>Building an extensible framework with minimal coupling
 * </ul>
 * 
 * <p>
 * <b>Examples:</b>
 * <ul>
 * <li>Management of Renderers, Editors (like PropertyEditorManager), which may
 * be further extended by the user
 * </ul>
 * </p>
 * 
 * <p>
 * <b>Notes:</b>
 * <p>
 * The manager has a scope, which may be <code>SCOPE_SINGLETON</code> or
 * <code>SCOPE_PROTOTYPE</code>. In singleton mode, always the registered
 * instance is returned, while with prototype a new istance is created for every
 * <code>find()</code>.
 * </p>
 * <p>
 * The instances may be registered programmatically, or by applying a naming
 * convention.
 * </p>
 * <p>
 * With either <code>SCOPE_PROTOTYPE</code>, or naming conventions, the managed
 * instances <b>must have a default constructor</b>.
 * </p>
 * </p>
 * 
 * @param <T>
 *            The type of the managed instances
 * 
 * @author Gergely Kiss
 * 
 */
public class TypeSpecificManager<T> {
	private final Map<Class<?>, T> typeMap = new HashMap<Class<?>, T>();

	/** With this scope, the registered instances are returned as-is (default). */
	public static final int SCOPE_SINGLETON = 0;

	/**
	 * With this scope, every time <code>find()</code> is called, a new instance
	 * is created.
	 */
	public static final int SCOPE_PROTOTYPE = 1;

	/** The scope of the manager. */
	private int scope = SCOPE_SINGLETON;

	/** The postfix for finding the managed types, or null if not specified. */
	private String postfix;

	private final String packageName = getClass().getPackage().getName();

	/**
	 * Locates the managed instance for the specified type.
	 * 
	 * <p>
	 * If the manager's scope is <code>singleton</code>, then the registered
	 * instance is returned. If the scope is <code>prototype</code>, then a new
	 * instance is created. In this case, the instance must have a default
	 * constructor.
	 * </p>
	 * 
	 * @param type
	 *            The registered type to look for
	 * @return The instance, or null if not found
	 */
	@SuppressWarnings("unchecked")
	public T find(Class<?> type) {
		if (type == null) {
			return null;
		}

		T instance = typeMap.get(type);

		// Now trying naming convention
		if (postfix != null) {

			// In the same package as the manager
			if (instance == null) {
				try {
					String managedTypeName = packageName + '.' + type.getSimpleName() + postfix;
					instance = (T) Class.forName(managedTypeName).newInstance();

					if (scope == SCOPE_SINGLETON) {
						// We may cache the instance
						typeMap.put(type, instance);
					}
				} catch (Exception e) {
					// Silently ignoring errors
				}
			}

			// In the same package as the queried type
			if (instance == null) {
				try {
					String managedTypeName = type.getName() + postfix;
					instance = (T) Class.forName(managedTypeName).newInstance();

					if (scope == SCOPE_SINGLETON) {
						// We may cache the instance
						typeMap.put(type, instance);
					}
				} catch (Exception e) {
					// Silently ignoring errors
				}
			}
		}

		if (scope == SCOPE_SINGLETON) {
			return instance;
		} else {
			try {
				return (T) instance.getClass().newInstance();
			} catch (Exception e) {
				// Silently ignoring errors
				return null;
			}
		}
	}

	/**
	 * Registers the instance for the given type.
	 * 
	 * @param type
	 * @param instance
	 */
	public void register(Class<?> type, T instance) {
		typeMap.put(type, instance);
	}

	/**
	 * Sets the scope for this manager.
	 * 
	 * @param scope
	 */
	public void setScope(int scope) {
		this.scope = scope;
	}

	/**
	 * Sets the class name postfix to use for instance location.
	 * 
	 * <p>
	 * The pattern may be any string which will be appended to the fully
	 * qualified class name of the managed type, like 'Editor' for
	 * PropertyEditors, 'Renderer' for renderers, etc.
	 * </p>
	 * 
	 * @param postfix
	 */
	public void setConventionalPostfix(String postfix) {
		this.postfix = postfix;
	}
}

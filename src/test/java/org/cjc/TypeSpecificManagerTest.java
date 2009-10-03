package org.cjc;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.cjc.tsm.Renderer;
import org.cjc.tsm.RendererManager;
import org.cjc.tsm.Test2Renderer;
import org.cjc.tsm.type.Test1;
import org.cjc.tsm.type.Test1Renderer;
import org.cjc.tsm.type.Test2;
import org.junit.Test;

/**
 * Tests the {@link TypeSpecificManager}.
 * 
 * @author Gergely Kiss
 */
public class TypeSpecificManagerTest {

	/**
	 * Finding a programmatically registered renderer singleton.
	 */
	@Test
	public void testFindRegistered() {
		RendererManager m = new RendererManager();
		m.register(Test1.class, new Test1Renderer());

		Renderer r = m.find(Test1.class);

		assertNotNull(r);
		assertTrue(r instanceof Test1Renderer);
	}

	/**
	 * Finding a programmatically registered renderer prototype.
	 */
	@Test
	public void testFindRegisteredPrototype() {
		RendererManager m = new RendererManager();
		m.register(Test1.class, new Test1Renderer());
		m.setScope(RendererManager.SCOPE_PROTOTYPE);

		Renderer r1 = m.find(Test1.class);
		Renderer r2 = m.find(Test1.class);

		assertNotNull(r1);
		assertTrue(r1 instanceof Test1Renderer);
		assertNotNull(r2);
		assertTrue(r2 instanceof Test1Renderer);
		assertNotSame(r1, r2);
	}

	/**
	 * Finding a renderer by convention in the type's package.
	 */
	@Test
	public void testFindByConventionInTypePackage() {
		RendererManager m = new RendererManager();
		m.setConventionalPostfix("Renderer");
		
		Renderer r = m.find(Test1.class);

		assertNotNull(r);
		assertTrue(r instanceof Test1Renderer);
	}

	/**
	 * Finding a renderer by convention in the manager's package.
	 */
	@Test
	public void testFindByConventionInManagerPackage() {
		RendererManager m = new RendererManager();
		m.setConventionalPostfix("Renderer");

		Renderer r = m.find(Test2.class);

		assertNotNull(r);
		assertTrue(r instanceof Test2Renderer);
	}

	/**
	 * Covering degenerate and missing cases.
	 */
	@Test
	public void testMissingCases() {
		assertNull(new RendererManager().find(null));
	}
}

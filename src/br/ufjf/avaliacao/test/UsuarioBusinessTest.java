package br.ufjf.avaliacao.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.hibernate.HibernateException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import br.ufjf.avaliacao.business.UsuarioBusiness;
import br.ufjf.avaliacao.model.Usuario;

public class UsuarioBusinessTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testLoginTrue() throws HibernateException, Exception {
		UsuarioBusiness usuarioBusiness = new UsuarioBusiness();
		assertTrue("Deve retornar true",usuarioBusiness.login("t.rizuti@gmail.com", "123"));
	}
	
	@Test
	public void testLoginFalse() throws HibernateException, Exception {
		UsuarioBusiness usuarioBusiness = new UsuarioBusiness();
		assertFalse("Deve retornar false",usuarioBusiness.login("t.rizuti@gmail.com", "senhaincorreta"));
	}

	@Test
	public void testChecaLoginTrue() throws HibernateException, Exception {
		UsuarioBusiness usuarioBusiness = new UsuarioBusiness();
		Usuario usuario = new Usuario();
		usuario.setEmail("t.rizuti@gmail.com");
		usuario.setSenha("123");
		assertTrue("Deve retornar true",usuarioBusiness.checaLogin(usuario));
	}
	
	@Test
	public void testChecaLoginFalse() throws HibernateException, Exception {
		UsuarioBusiness usuarioBusiness = new UsuarioBusiness();
		Usuario usuario = new Usuario();
		usuario.setEmail("t.rizuti@gmail.com");
		usuario.setSenha("senhaincorreta");
		assertFalse("Deve retornar false",usuarioBusiness.checaLogin(usuario));
	}

}
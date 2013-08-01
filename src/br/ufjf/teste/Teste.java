package br.ufjf.teste;

import br.ufjf.model.Curso;
import br.ufjf.model.Usuario;
import br.ufjf.persistent.impl.UsuarioDAO;

public class Teste {
	
	public static void main(String[] args) {
		Usuario usuario = new Usuario();
		UsuarioDAO usuarioDAO = new UsuarioDAO();
		Curso curso = new Curso();
		usuario.setNome("thiago");
		usuario.setSenha("123");
		usuario.setEmail("t.rizuti@gmail.com");
		usuario.setCurso(curso);
		usuario.setTipoUsuario(false);
		usuarioDAO.salvar(usuario);
		
		
	}
}

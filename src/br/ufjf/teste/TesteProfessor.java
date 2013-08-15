package br.ufjf.teste;

import br.ufjf.model.Professor;
import br.ufjf.persistent.impl.ProfessorDAO;

public class TesteProfessor {
	
	public static void main (String[] args) {
		Professor professor = new Professor();
		ProfessorDAO professorDAO = new ProfessorDAO();
		professor.setEmail("a");
		professor.setNome("b");
		professor.setSenha("c");
		professorDAO.salvar(professor);
		
	}
}

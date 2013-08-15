package br.ufjf.controller;

import org.zkoss.bind.annotation.Command;

import br.ufjf.model.Professor;
import br.ufjf.persistent.impl.ProfessorDAO;

public class AcaoCoordenadorController {
		
	private Professor professor = new Professor();
	private ProfessorDAO professorDAO = new ProfessorDAO();
	
	private Integer idProfessor;
		
	@Command
	public void adicionar () {
		professorDAO.salvar(professor);
	}
	
	@Command
	public void exclui () {
		professor = (Professor) professorDAO.procuraId(idProfessor, Professor.class);
		professorDAO.exclui(professor);
	}	

	public Professor getProfessor() {
		return professor;
	}

	public void setProfessor(Professor professor) {
		this.professor = professor;
	}

	public ProfessorDAO getProfessorDAO() {
		return professorDAO;
	}

	public void setProfessorDAO(ProfessorDAO professorDAO) {
		this.professorDAO = professorDAO;
	}

	public Integer getIdProfessor() {
		return idProfessor;
	}

	public void setIdProfessor(Integer idProfessor) {
		this.idProfessor = idProfessor;
	}

}

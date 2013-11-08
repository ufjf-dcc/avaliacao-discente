package br.ufjf.avaliacao.controller;

import org.hibernate.HibernateException;
import org.zkoss.bind.annotation.Init;

import br.ufjf.avaliacao.model.Questionario;
import br.ufjf.avaliacao.persistent.impl.QuestionarioDAO;

public class AvaliarProfessoresController extends GenericController {

	private QuestionarioDAO questionarioDAO = new QuestionarioDAO();
	private Questionario questionario = questionarioDAO.retornaQuestinarioAtivo(usuario.getCurso(), 1);
	
	
	@Init
	public void init() throws HibernateException, Exception {
		if(testaLogado())
			testaPermissao(2);
	}
	
	public Questionario getQuestionario() {
		return questionario;
	}
	public void setQuestionario(Questionario questionario) {
		this.questionario = questionario;
	}
	
	
}

package br.ufjf.avaliacao.controller;

import java.util.List;

import org.hibernate.HibernateException;
import org.zkoss.bind.annotation.Init;

import br.ufjf.avaliacao.model.Questionario;
import br.ufjf.avaliacao.persistent.impl.QuestionarioDAO;

public class AvaliacoesDisponiveisController extends GenericController {

	private QuestionarioDAO questionarioDAO = new QuestionarioDAO();
	private List<Questionario> questionariosCoord = questionarioDAO.retornaQuestinariosCursoTipo(usuario.getCurso(),0);
	private List<Questionario> questionariosProfs = questionarioDAO.retornaQuestinariosCursoTipo(usuario.getCurso(),1);
	private List<Questionario> questionariosAuto = questionarioDAO.retornaQuestinariosCursoTipo(usuario.getCurso(),2);
	private List<Questionario> questionariosInfra = questionarioDAO.retornaQuestinariosCursoTipo(usuario.getCurso(),3);	
	
	@Init
	public void init() throws HibernateException, Exception {
		testaPermissaoAluno();
	}

	public List<Questionario> getQuestionariosCoord() {
		return questionariosCoord;
	}

	public void setQuestionariosCoord(List<Questionario> questionariosCoord) {
		this.questionariosCoord = questionariosCoord;
	}

	public List<Questionario> getQuestionariosProfs() {
		return questionariosProfs;
	}

	public void setQuestionariosProfs(List<Questionario> questionariosProfs) {
		this.questionariosProfs = questionariosProfs;
	}

	public List<Questionario> getQuestionariosAuto() {
		return questionariosAuto;
	}

	public void setQuestionariosAuto(List<Questionario> questionariosAuto) {
		this.questionariosAuto = questionariosAuto;
	}

	public List<Questionario> getQuestionariosInfra() {
		return questionariosInfra;
	}

	public void setQuestionariosInfra(List<Questionario> questionariosInfra) {
		this.questionariosInfra = questionariosInfra;
	}
	
}

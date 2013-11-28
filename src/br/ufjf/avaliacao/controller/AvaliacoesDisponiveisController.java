package br.ufjf.avaliacao.controller;

import java.util.List;

import org.hibernate.HibernateException;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Row;
import org.zkoss.zul.Window;

import br.ufjf.avaliacao.model.Avaliacao;
import br.ufjf.avaliacao.model.Questionario;
import br.ufjf.avaliacao.persistent.impl.QuestionarioDAO;

public class AvaliacoesDisponiveisController extends GenericController {

	private List<Avaliacao> avaliacaoProfs;
	private static Avaliacao avaliacaoAtual;
	private QuestionarioDAO questionarioDAO = new QuestionarioDAO();
	private List<Questionario> questionariosCoord = questionarioDAO.retornaQuestinariosParaUsuarioCoord(usuario);
	private List<Questionario> questionariosProfs = questionarioDAO.retornaQuestinariosParaUsuario(usuario);
	private List<Questionario> questionariosAuto = questionarioDAO
			.retornaQuestinariosCursoTipo(usuario.getCurso(), 2);
	private List<Questionario> questionariosInfra = questionarioDAO
			.retornaQuestinariosCursoTipo(usuario.getCurso(), 3);
	private static Questionario questionarioAtual;

	@Init
	public void init() throws HibernateException, Exception {
		testaPermissaoAluno();
	}

	@Command
	@SuppressWarnings("static-access")
	public void avaliar(@BindingParam("avaliacao") Avaliacao avaliacao) {
		this.avaliacaoAtual = avaliacao;
		this.questionarioAtual = avaliacaoAtual.getQuestionario();
		Window window = (Window) Executions.createComponents("/avaliar.zul",
				null, null);
		window.doModal();
	}

	@Command
	public void criarCampoResposta(@BindingParam("row") Row row,
			@BindingParam("tipoPergunta") Integer tipoPergunta) {
		Component component = row.getFirstChild();
		switch (tipoPergunta) {
		case 0:
			component.getNextSibling().getNextSibling().getNextSibling()
					.getNextSibling().detach();
			component.getNextSibling().getNextSibling().getNextSibling()
					.detach();
			component.getNextSibling().getNextSibling().detach();
			break;
		case 1:
			component.getNextSibling().getNextSibling().getNextSibling()
					.getNextSibling().detach();
			component.getNextSibling().getNextSibling().getNextSibling()
					.detach();
			component.getNextSibling().detach();
			break;
		case 2:
			component.getNextSibling().getNextSibling().getNextSibling()
					.getNextSibling().detach();
			component.getNextSibling().getNextSibling().detach();
			component.getNextSibling().detach();
			break;
		case 3:
			component.getNextSibling().getNextSibling().getNextSibling()
					.detach();
			component.getNextSibling().getNextSibling().detach();
			component.getNextSibling().detach();
			break;
		default:
			;
			break;
		}
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

	public Questionario getQuestionarioAtual() {
		return questionarioAtual;
	}

	public void setQuestionarioAtual(Questionario questionarioAtual) {
		AvaliacoesDisponiveisController.questionarioAtual = questionarioAtual;
	}

	public List<Avaliacao> getAvaliacaoProfs() {
		return avaliacaoProfs;
	}

	public void setAvaliacaoProfs(List<Avaliacao> avaliacaoProfs) {
		this.avaliacaoProfs = avaliacaoProfs;
	}

	public Avaliacao getAvaliacaoAtual() {
		return avaliacaoAtual;
	}

	public void setAvaliacaoAtual(Avaliacao avaliacaoAtual) {
		AvaliacoesDisponiveisController.avaliacaoAtual = avaliacaoAtual;
	}

}

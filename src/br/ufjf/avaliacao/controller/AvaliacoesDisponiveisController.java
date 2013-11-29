package br.ufjf.avaliacao.controller;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.HibernateException;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Window;

import br.ufjf.avaliacao.model.Avaliacao;
import br.ufjf.avaliacao.model.Pergunta;
import br.ufjf.avaliacao.model.Questionario;
import br.ufjf.avaliacao.model.Resposta;
import br.ufjf.avaliacao.persistent.impl.QuestionarioDAO;

public class AvaliacoesDisponiveisController extends GenericController {

	private QuestionarioDAO questionarioDAO = new QuestionarioDAO();
	private List<Questionario> questionariosCoord = questionarioDAO
			.retornaQuestinariosParaUsuarioCoord(usuario);
	private List<Questionario> questionariosProfs = questionarioDAO
			.retornaQuestinariosParaUsuario(usuario);
	private List<Questionario> questionariosAuto = questionarioDAO
			.retornaQuestinariosCursoTipo(usuario.getCurso(), 2);
	private List<Questionario> questionariosInfra = questionarioDAO
			.retornaQuestinariosCursoTipo(usuario.getCurso(), 3);
	private static Questionario questionarioAtual = new Questionario();
	private Avaliacao avaliacao = new Avaliacao();
	private Resposta resposta = new Resposta();
	private List<Resposta> respostas = new ArrayList<Resposta>();

	@Init
	public void init() throws HibernateException, Exception {
		testaPermissaoAluno();
	}

	@Command
	public void avaliar(@BindingParam("questionario") Questionario questionario) {
		AvaliacoesDisponiveisController.questionarioAtual = questionario;
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

	@Command
	public void terminarAvaliacao() {
		
		if(respostas.size()!=questionarioAtual.getPerguntas().size()) {
			Messagebox.show("Responda todas as perguntas antes de finalizar!");
		} else {
			for (Resposta r : respostas) {
				System.out.println(r.getResposta());
			}
			Messagebox.show("Avaliação Salva com Sucesso", "Concluído",
					Messagebox.OK, Messagebox.INFORMATION,
					new EventListener<Event>() {
						@Override
						public void onEvent(Event event)
								throws Exception {
							Executions.sendRedirect(null);
						}
					});
		}
		
	}

	@Command
	public void escolha(@BindingParam("string") String escolha,
			@BindingParam("pergunta") Pergunta perg) {

		resposta.setResposta(escolha);
		resposta.setPergunta(perg);

		for (int i = 0; i < respostas.size() ; i++)
			if (respostas.get(i).getPergunta() == resposta.getPergunta()) {
				respostas.remove(respostas.get(i));
				break;
			}

		respostas.add(resposta);
		resposta = new Resposta();
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

	public Avaliacao getAvaliacao() {
		return avaliacao;
	}

	public void setAvaliacao(Avaliacao avaliacao) {
		this.avaliacao = avaliacao;
	}

	public Resposta getResposta() {
		return resposta;
	}

	public void setResposta(Resposta resposta) {
		this.resposta = resposta;
	}

	public List<Resposta> getRespostas() {
		return respostas;
	}

	public void setRespostas(List<Resposta> respostas) {
		this.respostas = respostas;
	}

}

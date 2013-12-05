package br.ufjf.avaliacao.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Window;

import br.ufjf.avaliacao.model.Avaliacao;
import br.ufjf.avaliacao.model.Pergunta;
import br.ufjf.avaliacao.model.Questionario;
import br.ufjf.avaliacao.model.Resposta;
import br.ufjf.avaliacao.model.Usuario;
import br.ufjf.avaliacao.persistent.impl.AvaliacaoDAO;
import br.ufjf.avaliacao.persistent.impl.QuestionarioDAO;
import br.ufjf.avaliacao.persistent.impl.RespostaDAO;
import br.ufjf.avaliacao.persistent.impl.UsuarioDAO;

public class AvaliacoesDisponiveisController extends GenericController {

	private QuestionarioDAO questionarioDAO = new QuestionarioDAO();
	private UsuarioDAO usuarioDAO = new UsuarioDAO();
	private List<Questionario> questionariosCoord = questionarioDAO
			.retornaQuestinariosParaUsuarioCoord(usuario);
	private List<Questionario> questionariosProfs = questionarioDAO
			.retornaQuestinariosParaUsuario(usuario);
	private List<Questionario> questionariosAuto = questionarioDAO
			.retornaQuestinariosCursoTipo(usuario.getCurso(), 2);
	private List<Questionario> questionariosInfra = questionarioDAO
			.retornaQuestinariosCursoTipo(usuario.getCurso(), 3);
	private static Questionario questionarioAtual = new Questionario();
	private Resposta resposta = new Resposta();
	private List<Resposta> respostas = new ArrayList<Resposta>();
	private Usuario coordAvaliado = usuarioDAO.retornaCoordAvaliado(usuario);
	private boolean jaAvaliouCoord = false;

	@Init
	public void init() throws HibernateException, Exception {
		testaPermissaoAluno();
		jaAvaliouCoord = new AvaliacaoDAO().jaAvaliou(usuario,
				new QuestionarioDAO().getQuestCoord(usuario));

	}

	@Command
	public void disponibilidade(@BindingParam("button") Button b, @BindingParam("questionario") Questionario quest) {
		if (new Date().before(quest.getPrazos().)
				&& new Date().after(quest.getDataInicial())
				&& !jaAvaliouCoord) {
			b.setLabel("Avaliar");
			b.setDisabled(false);
		}
		else if (jaAvaliouCoord) {
			b.setLabel("Já Avaliado");
			b.setDisabled(true);
		} else {
			b.setLabel("Não Disponível");
			b.setDisabled(true);
		}
			
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
		Clients.showBusy("Salvando avaliação..");
		if (respostas.size() != questionarioAtual.getPerguntas().size()) {
			Messagebox.show("Responda todas as perguntas antes de finalizar!");
		} else {
			Avaliacao avaliacao = new Avaliacao();
			avaliacao.setAvaliando(usuario);
			avaliacao.setQuestionario(questionarioAtual);
			avaliacao.setAvaliado(coordAvaliado);
			new AvaliacaoDAO().salvar(avaliacao);

			for (Resposta r : respostas) {
				r.setAvaliacao(avaliacao);
			}

			new RespostaDAO().salvarLista(respostas);

			Clients.clearBusy();
			Messagebox.show("Avaliação Salva com Sucesso", "Concluído",
					Messagebox.OK, Messagebox.INFORMATION,
					new EventListener<Event>() {
						@Override
						public void onEvent(Event event) throws Exception {
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

		for (int i = 0; i < respostas.size(); i++)
			if (respostas.get(i).getPergunta() == resposta.getPergunta()) {
				respostas.remove(respostas.get(i));
				break;
			}

		respostas.add(resposta);
		resposta = new Resposta();
	}

	@Command
	public void avaliado(@BindingParam("label") Label l) {
		l.setValue(coordAvaliado.getNome() + " - " + "Coordenador "
				+ coordAvaliado.getCurso().getNomeCurso());
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

	public boolean isJaAvaliouCoord() {
		return jaAvaliouCoord;
	}

	public void setJaAvaliouCoord(boolean jaAvaliouCoord) {
		this.jaAvaliouCoord = jaAvaliouCoord;
	}

	public List<Questionario> getQuestionariosCoord() {
		return questionariosCoord;
	}

	public void setQuestionariosCoord(List<Questionario> questionariosCoord) {
		this.questionariosCoord = questionariosCoord;
	}

}

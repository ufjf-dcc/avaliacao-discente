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
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Window;

import br.ufjf.avaliacao.model.Avaliacao;
import br.ufjf.avaliacao.model.Pergunta;
import br.ufjf.avaliacao.model.PrazoQuestionario;
import br.ufjf.avaliacao.model.Questionario;
import br.ufjf.avaliacao.model.Resposta;
import br.ufjf.avaliacao.model.Usuario;
import br.ufjf.avaliacao.persistent.impl.AvaliacaoDAO;
import br.ufjf.avaliacao.persistent.impl.PrazoQuestionarioDAO;
import br.ufjf.avaliacao.persistent.impl.QuestionarioDAO;
import br.ufjf.avaliacao.persistent.impl.RespostaDAO;
import br.ufjf.avaliacao.persistent.impl.UsuarioDAO;

public class AvaliacoesDisponiveisController extends GenericController {

	private QuestionarioDAO questionarioDAO = new QuestionarioDAO();
	private UsuarioDAO usuarioDAO = new UsuarioDAO();
	private Questionario questionarioCoord = questionarioDAO
			.retornaQuestinarioParaUsuarioCoord(usuario);
	private List<PrazoQuestionario> prazoCoord = new PrazoQuestionarioDAO()
			.questionarioDisponivel(questionarioCoord);
	private List<Questionario> questionariosProfs = questionarioDAO
			.retornaQuestinariosParaUsuario(usuario);
	private Questionario questionarioAuto = questionarioDAO
			.retornaQuestinarioParaUsuarioAutoAvaliacao(usuario);
	private List<PrazoQuestionario> prazoAuto = new PrazoQuestionarioDAO()
	.questionarioDisponivel(questionarioAuto);
	private Questionario questionarioInfra = questionarioDAO.retornaQuestinarioParaUsuarioInfra(usuario);
	private List<PrazoQuestionario> prazoInfra = new PrazoQuestionarioDAO()
	.questionarioDisponivel(questionarioInfra);
	private static Questionario questionarioAtual = new Questionario();
	private Resposta resposta = new Resposta();
	private List<Resposta> respostas = new ArrayList<Resposta>();
	private Usuario coordAvaliado = usuarioDAO.retornaCoordAvaliado(usuario);

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

	public Questionario getQuestionarioCoord() {
		return questionarioCoord;
	}

	public void setQuestionarioCoord(Questionario questionarioCoord) {
		this.questionarioCoord = questionarioCoord;
	}

	public List<PrazoQuestionario> getPrazoCoord() {
		return prazoCoord;
	}

	public void setPrazoCoord(List<PrazoQuestionario> prazoCoord) {
		this.prazoCoord = prazoCoord;
	}

	public List<PrazoQuestionario> getPrazoAuto() {
		return prazoAuto;
	}

	public void setPrazoAuto(List<PrazoQuestionario> prazoAuto) {
		this.prazoAuto = prazoAuto;
	}

	public List<PrazoQuestionario> getPrazoInfra() {
		return prazoInfra;
	}

	public void setPrazoInfra(List<PrazoQuestionario> prazoInfra) {
		this.prazoInfra = prazoInfra;
	}

	public Questionario getQuestionarioAuto() {
		return questionarioAuto;
	}

	public void setQuestionarioAuto(Questionario questionarioAuto) {
		this.questionarioAuto = questionarioAuto;
	}

	public Questionario getQuestionarioInfra() {
		return questionarioInfra;
	}

	public void setQuestionarioInfra(Questionario questionarioInfra) {
		this.questionarioInfra = questionarioInfra;
	}

}

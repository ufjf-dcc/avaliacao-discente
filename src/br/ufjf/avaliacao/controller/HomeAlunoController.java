package br.ufjf.avaliacao.controller;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.HibernateException;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Window;

import br.ufjf.avaliacao.model.Avaliacao;
import br.ufjf.avaliacao.model.Pergunta;
import br.ufjf.avaliacao.model.PrazoQuestionario;
import br.ufjf.avaliacao.model.Questionario;
import br.ufjf.avaliacao.model.Resposta;
import br.ufjf.avaliacao.model.Turma;
import br.ufjf.avaliacao.model.Usuario;
import br.ufjf.avaliacao.persistent.impl.AvaliacaoDAO;
import br.ufjf.avaliacao.persistent.impl.PrazoQuestionarioDAO;
import br.ufjf.avaliacao.persistent.impl.QuestionarioDAO;
import br.ufjf.avaliacao.persistent.impl.RespostaDAO;
import br.ufjf.avaliacao.persistent.impl.TurmaDAO;
import br.ufjf.avaliacao.persistent.impl.UsuarioDAO;

public class HomeAlunoController extends GenericController {

	private QuestionarioDAO questionarioDAO = new QuestionarioDAO();
	private UsuarioDAO usuarioDAO = new UsuarioDAO();
	private AvaliacaoDAO avaliacaoDAO = new AvaliacaoDAO();
	private PrazoQuestionarioDAO prazoDAO = new PrazoQuestionarioDAO();
	private Questionario questionarioProf = questionarioDAO
			.retornaQuestionarioProf(usuario);
	private List<Turma> turmasDoUsuario = new TurmaDAO()
			.getTurmasUsuario(usuario);
	private PrazoQuestionario prazo = new PrazoQuestionarioDAO()
			.prazoQuestionario(questionarioProf);

	private Questionario questionarioCoord = questionarioDAO
			.retornaQuestinarioParaUsuarioCoord(usuario);
	private Questionario questionarioAuto = questionarioDAO
			.retornaQuestinarioParaUsuarioAutoAvaliacao(usuario);
	private Questionario questionarioInfra = questionarioDAO
			.retornaQuestinarioParaUsuarioInfra(usuario);

	private Resposta resposta = new Resposta();
	private List<Resposta> respostas = new ArrayList<Resposta>();
	private Usuario coordAvaliado = usuarioDAO.retornaCoordAvaliado(usuario);

	@Init
	public void init() throws HibernateException, Exception {
		testaPermissaoAluno();

	}

	@Command
	public void avaliar(
			@BindingParam("questionario") Questionario questionario,
			@BindingParam("turma") Turma turma) {
		session.setAttribute("questionario", questionario);
		session.setAttribute("turma", turma);
		session.setAttribute("questionarioInicial", questionario);
		session.setAttribute("questionarioAtual", questionario);
		session.setAttribute("numProfTurma", usuarioDAO
				.retornaProfessoresTurma(turma).size());
		avaliarAux();
	}

	@Command
	public void iniciarAvaliacao() {
		session.setAttribute("questionarioAtual",
				(Questionario) session.getAttribute("questionario"));
	}

	// essa função diz quem precisa ser avaliado agora
	private void avaliarAux() {
		// se ainda não fez a avaliação de coordenador e se tem uma avaliação de
		// coordenador pra fazer
		if (!avaliacaoDAO.jaAvaliouCoordenadorDataAtual(usuario)
				&& questionarioDAO.retornaQuestinarioParaUsuarioCoord(usuario) != null) {
			// setando qual é o questionario que deve ser avaliado
			session.setAttribute("questionarioAtual",
					questionarioDAO.retornaQuestinarioParaUsuarioCoord(usuario));// seta
																					// o
																					// questionario
																					// atual
																					// para
																					// um
																					// questionario
																					// de
																					// coordenador
																					// a
																					// ser
																					// avaliado
			Window window = (Window) Executions.createComponents(
					"/avaliar.zul", null, null);
			window.setTitle("Avaliação de Coordenador - "
					+ usuarioDAO.retornaCoordAvaliado(usuario).getNome());
			window.doModal();

		} else {
			// se ainda não fez a auto avaliação e se tem uma auto avaliação pra
			// fazer
			if (!avaliacaoDAO.jaSeAvaliorDataAtual(usuario)
					&& questionarioDAO
							.retornaQuestinarioParaUsuarioAutoAvaliacao(usuario) != null) {
				// setando qual é o questionario que deve ser avaliado
				session.setAttribute("questionarioAtual", questionarioDAO
						.retornaQuestinarioParaUsuarioAutoAvaliacao(usuario));// seta
																				// o
																				// questionario
																				// atual
																				// para
																				// um
																				// questionario
																				// de
																				// auto
																				// avaliação
																				// a
																				// ser
																				// avaliado
				Window window = (Window) Executions.createComponents(
						"/avaliar.zul", null, null);
				window.setTitle("Autoavaliação");
				window.doModal();
			} else {
				if (!avaliacaoDAO.jaAvaliouInfraestruturaDataAtual(usuario)
						&& questionarioDAO
								.retornaQuestinarioParaUsuarioInfra(usuario) != null) {
					// setando qual é o questionario que deve ser avaliado
					session.setAttribute("questionarioAtual", questionarioDAO
							.retornaQuestinarioParaUsuarioInfra(usuario));
					Window window = (Window) Executions.createComponents(
							"/avaliar.zul", null, null);
					window.setTitle("Avaliação de Infraestrutura");
					window.doModal();
				} else {
					if (!avaliacaoDAO.jaAvaliouTodosProfessoresTurma(usuario,
							(Turma) session.getAttribute("turma"))) {

						session.setAttribute("questionarioAtual",
								(Questionario) session
										.getAttribute("questionarioInicial"));
						Window window = (Window) Executions.createComponents(
								"/avaliar.zul", null, null);
						window.setTitle("Avaliação de Professor - "
								+ avaliacaoDAO
										.retornaProfessoresNaoAvaliados(
												usuario,
												(Turma) session
														.getAttribute("turma"))
										.get(0).getNome());
						window.doModal();
					}
				}
			}
		}
	}

	// salva a avaliação e verifica se vai precisar retornar na função que
	// verifica quem deve ser avalido agora (avaliarAuz)
	@Command
	public void terminarAvaliacao(@BindingParam("window") Window win) {

		Usuario avaliado = null;
		Turma turmaUsada = null;
		prazo = prazoDAO.getPrazoQuestionarioDisponivel((Questionario) session
				.getAttribute("questionarioAtual"));

		// setando o usuario que vai ser avaliado e a
		// turma-----------------------------------------------------
		if (((Questionario) session.getAttribute("questionarioAtual"))
				.getTipoQuestionario() == 0) { // verifica se o
			// questionario é do
			// tipo coodenador
			avaliado = usuarioDAO.retornaCoordenadorCurso(usuario.getCurso());
		}
		if (((Questionario) session.getAttribute("questionarioAtual"))
				.getTipoQuestionario() == 1) { // verifica se o
			// questionario é do
			// tipo professor
			avaliado = avaliacaoDAO.retornaProfessoresNaoAvaliados(usuario,
					(Turma) session.getAttribute("turma")).get(0);
			turmaUsada = (Turma) session.getAttribute("turma");
			session.setAttribute("numProfTurma",
					((int) session.getAttribute("numProfTurma") - 1));

		}
		if (((Questionario) session.getAttribute("questionarioAtual"))
				.getTipoQuestionario() == 2) { // verifica se o
			// questionario é do
			// tipo auto
			// avaliação
			avaliado = usuario;

		}
		if (((Questionario) session.getAttribute("questionarioAtual"))
				.getTipoQuestionario() == 3) { // verifica se o
			// questionario é do
			// tipo
			// infraestrutura
			if (avaliacaoDAO.retornaProfessoresNaoAvaliados(usuario,
					(Turma) session.getAttribute("turma")).size() == 1)
				avaliado = null;
		}
		// -------------------------------------------------------------------------------

		Clients.showBusy("Salvando avaliação..");
		if (respostas.size() < ((Questionario) session
				.getAttribute("questionarioAtual")).getPerguntas().size()) {
			Clients.clearBusy();
			Messagebox.show("Responda todas as perguntas antes de finalizar!");
		} else {
			Avaliacao avaliacao = new Avaliacao();
			avaliacao.setAvaliando(usuario);
			avaliacao.setAvaliado(avaliado);
			avaliacao.setPrazoQuestionario(prazo);
			avaliacao.setTurma(turmaUsada);
			new AvaliacaoDAO().salvar(avaliacao);

			for (Resposta r : respostas) {
				System.out.println("erro aqui");
				r.setAvaliacao(avaliacao);
			}
			new RespostaDAO().salvarLista(respostas);
			Clients.clearBusy();

			// se tiver mais de um professor a ser avaliado ou nao for uma
			// avaliação de professor, ele verifica o que mais precisa ser
			// avaliado
			if (((Questionario) session.getAttribute("questionarioAtual"))
					.getTipoQuestionario() != 1
					|| ((int) session.getAttribute("numProfTurma")) > 0) {
				Messagebox.show("Avaliação Salva com Sucesso", "Concluído",
						Messagebox.OK, Messagebox.INFORMATION,
						new EventListener<Event>() {

							@Override
							public void onEvent(Event event) throws Exception {
								avaliarAux();
							}
						});
				win.detach();

			}
			// se acabar de avaliar a ultima coisa(que seria o ultimo professor
			// da turma(ou o unico)) ele finaliza as avaliaçoes e da um refresh
			// na pagina
			else {
				Messagebox.show("Finalizado", "Concluído", Messagebox.OK,
						Messagebox.INFORMATION, new EventListener<Event>() {
							@Override
							public void onEvent(Event event) throws Exception {
								Executions.sendRedirect(null);
							}
						});
				win.detach();
			}
		}

	}

	@Command
	public void criarCampoResposta(@BindingParam("label") Label l,
			@BindingParam("pergunta") Pergunta p) {
		switch (p.getTipoPergunta()) {
		case 0:
			l.getNextSibling().getNextSibling().getNextSibling().detach();
			l.getNextSibling().getNextSibling().detach();
			break;
		case 1:
			l.getNextSibling().getNextSibling().getNextSibling().detach();
			l.getNextSibling().detach();
			break;
		case 2:
			l.getNextSibling().getNextSibling().detach();
			l.getNextSibling().detach();
			break;
		case 3:
			l.getNextSibling().getNextSibling().detach();
			l.getNextSibling().detach();
			break;
		default:
			;
			break;
		}
	}

	@Command
	public void jaAvaliou(@BindingParam("div") Div d,
			@BindingParam("turma") Turma t) {

		if (!new AvaliacaoDAO().jaAvaliou(usuario, t)) {
			d.getFirstChild().setVisible(false);
			d.getLastChild().setVisible(true);
		} else {
			d.getFirstChild().setVisible(true);
			d.getLastChild().setVisible(false);
		}
	}

	@Command
	public void questDisponivel(@BindingParam("questionario") Questionario q,
			@BindingParam("row") Row r) {
		if (!new PrazoQuestionarioDAO().questionarioEstaDisponivel(q)) {
			r.detach();
		}
	}

	@Command
	public void escolha(@BindingParam("string") String escolha,
			@BindingParam("pergunta") Pergunta perg) {

		resposta.setResposta(escolha);
		resposta.setPergunta(perg);
		resposta.setSemestre(((Turma) session.getAttribute("turma"))
				.getSemestre());

		for (int i = 0; i < respostas.size(); i++)
			if (respostas.get(i).getPergunta() == resposta.getPergunta()) {
				respostas.remove(respostas.get(i));
				break;
			}
		respostas.add(resposta);
		resposta = new Resposta();
	}

	@Command
	public void doChecked(@BindingParam("string") String escolha,
			@BindingParam("pergunta") Pergunta perg,
			@BindingParam("check") Checkbox box) {
		if (box.isChecked()) {
			resposta.setResposta(escolha);
			resposta.setPergunta(perg);
			resposta.setSemestre(((Turma) session.getAttribute("turma"))
					.getSemestre());
			respostas.add(resposta);
			resposta = new Resposta();
		} else {
			for (Resposta r : respostas) {
				if (r.getResposta() == escolha) {
					respostas.remove(r);
					break;
				}
			}
		}
	}

	@Command
	public void avaliado(@BindingParam("label") Label l) {
		l.setValue(coordAvaliado.getNome() + " - " + "Coordenador "
				+ coordAvaliado.getCurso().getNomeCurso());
	}

	public Questionario getQuestionarioAtual() {
		return ((Questionario) session.getAttribute("questionarioAtual"));
	}

	public void setQuestionarioAtual(Questionario questionarioAtual) {
		session.setAttribute("questionarioAtual", questionarioAtual);
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

	public List<Turma> getTurmasDoUsuario() {
		return turmasDoUsuario;
	}

	public void setTurmasDoUsuario(List<Turma> turmasDoUsuario) {
		this.turmasDoUsuario = turmasDoUsuario;
	}

	public Questionario getQuestionarioProf() {
		return questionarioProf;
	}

	public void setQuestionarioProf(Questionario questionarioProf) {
		this.questionarioProf = questionarioProf;
	}

	public PrazoQuestionario getPrazo() {
		return prazo;
	}

	public void setPrazo(PrazoQuestionario prazo) {
		this.prazo = prazo;
	}
}

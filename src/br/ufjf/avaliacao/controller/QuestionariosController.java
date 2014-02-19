package br.ufjf.avaliacao.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.hibernate.HibernateException;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import br.ufjf.avaliacao.business.GenericBusiness;
import br.ufjf.avaliacao.business.QuestionariosBusiness;
import br.ufjf.avaliacao.model.Avaliacao;
import br.ufjf.avaliacao.model.Pergunta;
import br.ufjf.avaliacao.model.PrazoQuestionario;
import br.ufjf.avaliacao.model.Questionario;
import br.ufjf.avaliacao.model.RespostaEspecifica;
import br.ufjf.avaliacao.persistent.impl.AvaliacaoDAO;
import br.ufjf.avaliacao.persistent.impl.PerguntaDAO;
import br.ufjf.avaliacao.persistent.impl.PrazoQuestionarioDAO;
import br.ufjf.avaliacao.persistent.impl.QuestionarioDAO;
import br.ufjf.avaliacao.persistent.impl.RespostaEspecificaDAO;

public class QuestionariosController extends GenericController {

	private QuestionarioDAO questionarioDAO = new QuestionarioDAO();
	private List<Questionario> questionariosCoord = questionarioDAO
			.retornaQuestinariosCursoTipo(usuario.getCurso(), 0);
	private List<Questionario> questionariosProf = questionarioDAO
			.retornaQuestinariosCursoTipo(usuario.getCurso(), 1);
	private List<Questionario> questionariosAuto = questionarioDAO
			.retornaQuestinariosCursoTipo(usuario.getCurso(), 2);
	private List<Questionario> questionariosInfra = questionarioDAO
			.retornaQuestinariosCursoTipo(usuario.getCurso(), 3);
	private boolean ativo;
	private List<Pergunta> perguntas = new ArrayList<Pergunta>();
	private List<Pergunta> perguntasSessao = new ArrayList<Pergunta>();
	private Pergunta pergunta = new Pergunta();
	private PerguntaDAO perguntaDAO = new PerguntaDAO();
	private AvaliacaoDAO avaliacaoDAO = new AvaliacaoDAO();
	private Avaliacao avaliacao = new Avaliacao();
	private Questionario questionario = new Questionario();
	private Questionario questSessao = new Questionario();
	private List<Integer> tiposQuestionario = Arrays.asList(0, 1, 2, 3);
	private List<Integer> tiposPergunta = Arrays.asList(0, 1, 2, 3);
	private PrazoQuestionario prazo = new PrazoQuestionario();
	private List<PrazoQuestionario> prazos = new ArrayList<PrazoQuestionario>();
	private List<PrazoQuestionario> prazosSessao = new ArrayList<PrazoQuestionario>();
	private PrazoQuestionarioDAO prazoDAO = new PrazoQuestionarioDAO();
	private Integer spinnerInicio = 0;
	private Integer spinnerFinal = 10;
	private List<RespostaEspecifica> respostas = new ArrayList<RespostaEspecifica>();
	private RespostaEspecifica resposta = new RespostaEspecifica();
	private RespostaEspecificaDAO respostaEspecificaDAO = new RespostaEspecificaDAO();

	@Init
	public void init() throws HibernateException, Exception {
		testaPermissaoCoord();
		if (((Questionario) session.getAttribute("questionario")) != null) {
			questSessao = (Questionario) session.getAttribute("questionario");
			prazosSessao = questSessao.getPrazos();
			perguntasSessao = questSessao.getPerguntas();
		}
	}

	@Command
	public void criarQuest() {
		Window window = (Window) Executions.createComponents(
				"/criarQuestionario.zul", null, null);
		window.doModal();
	}

	@Command
	public void criarQuestionario() {
		if ((new GenericBusiness().campoStrValido(questionario
				.getTituloQuestionario()))
				&& (questionario.getTipoQuestionario() != null)) {
			questionario.setCurso(usuario.getCurso());
			if (questionarioDAO.salvar(questionario)) {
				if (perguntaDAO.salvarLista(perguntas)) {
					for (Pergunta p : perguntas) {
						if (p.getTipoPergunta() != 0) {
							respostaEspecificaDAO.salvarLista(p
									.getRespostasEspecificas());
						}
					}
					Messagebox.show("Questionário criado com sucesso",
							"Concluído", Messagebox.OK, Messagebox.INFORMATION,
							new EventListener<Event>() {
								@Override
								public void onEvent(Event event)
										throws Exception {
									Executions.sendRedirect(null);
								}
							});
				}
			}
		}
	}

	@Command
	public void zerarQuestionario() {
		session.setAttribute("respostas", null);
	}

	@Command
	public void editarQuest(
			@BindingParam("questionario") Questionario questionario) {
		session.setAttribute("questionario", questionario);
		Window window = (Window) Executions.createComponents(
				"/editarQuestionario.zul", null, null);
		window.doModal();
	}

	@Command
	public void verQuest(@BindingParam("questionario") Questionario questionario) {
		session.setAttribute("questionario", questionario);
		Window window = (Window) Executions.createComponents(
				"/verQuestionario.zul", null, null);
		window.doModal();
	}

	@Command
	public void respostas() {
		Window w = (Window) Executions.createComponents(
				"/respostasEspecificas.zul", null, null);
		w.doModal();
	}

	@Command
	@NotifyChange({ "respostas", "resposta" })
	public void addRespostaEspecifica() {
		if (new GenericBusiness().campoStrValido(resposta
				.getRespostaEspecifica())) {
			resposta.setRespostaEspecifica(resposta.getRespostaEspecifica()
					.trim());
			resposta.setPergunta(pergunta);
			respostas.add(resposta);
			session.setAttribute("respostas", respostas);
			resposta = new RespostaEspecifica();
		}
	}

	/*
	 * Método privado para finalizar a criação de uma pergunta
	 * e coloca la no questionario. Verificações são feitas antes
	 * da chamada desse método.
	 */
	private void finalizar(Button b) {
		pergunta.setTituloPergunta(pergunta.getTituloPergunta().trim());
		pergunta.setQuestionario(questionario);
		perguntas.add(pergunta);
		if (pergunta.getTipoPergunta() == 3) {
			for (int i = spinnerInicio; i <= spinnerFinal; i++) {
				resposta = new RespostaEspecifica();
				resposta.setRespostaEspecifica(Integer.toString(i));
				resposta.setPergunta(pergunta);
				respostas.add(resposta);
			}
		}
		pergunta.setRespostasEspecificas(respostas);
		pergunta = new Pergunta();
		respostas = new ArrayList<RespostaEspecifica>();
		session.setAttribute("respostas", respostas);
		b.setDisabled(true);
		Button p = (Button) b.getPreviousSibling();
		p.setDisabled(true);
	}

	@Command
	@NotifyChange({ "perguntas", "pergunta" })
	public void addPergunta(@BindingParam("button") Button b) {
		if ((new GenericBusiness().campoStrValido(pergunta.getTituloPergunta()))
				&& (pergunta.getTipoPergunta() != null)) {
			if (pergunta.getTipoPergunta() != 3) {
				if (pergunta.getTipoPergunta() == 0) {
					finalizar(b);
				} else {
					if (!respostas.isEmpty()) {
						finalizar(b);
					} else {
						Messagebox.show("Nenhuma resposta cadastrada para essa pergunta.");
					}
				}
			} else {
				if (spinnerFinal > spinnerInicio) {
					finalizar(b);
				} else {
					Messagebox.show("Escala inicial menor ou igual à final");
				}
			}
		}
	}

	@Command
	@NotifyChange({ "prazos", "prazo" })
	public void adicionaPrazo() {
		prazos.add(prazo);
		prazo = new PrazoQuestionario();
	}

	@Command
	public void adcPrazo(@BindingParam("questionario") Questionario questionario) {
		session.setAttribute("questionario", questionario);
		Window window = (Window) Executions.createComponents("/add-prazo.zul",
				null, null);
		window.doModal();
	}

	@Command
	public void addPrazo(@BindingParam("window") Window w) {
		if (new QuestionariosBusiness().prazoValido(prazo)) {
			if (validadaData(prazo)) {
				prazo.setQuestionario((Questionario) session
						.getAttribute("questionario"));
				prazoDAO.salvar(prazo);
				prazos.add(prazo);
				w.detach();
				Messagebox.show("Prazo Adicionado!");
			}
		} else {
			Messagebox.show("Data final e/ ou inicial inválida");
		}
		w.detach();
	}

	private boolean validadaData(PrazoQuestionario prazo) {
		if (prazo.getDataFinal().before(prazo.getDataInicial())) {
			Messagebox.show("Data final antes da data inicial");
			return false;
		}
		if (questionario.getPrazos() != null)
			for (int i = 0; i < questionario.getPrazos().size(); i++) {
				if (questionario.getPrazos().get(i).getDataFinal()
						.after(prazo.getDataInicial())) {
					Messagebox.show("Não pode criar nessa data");
					return false;
				}
			}
		return true;
	}

	@Command
	public void excluiPrazo(@BindingParam("prazo") PrazoQuestionario prazo) { // deleta
																				// um
																				// prazo
																				// se
																				// for
																				// possivel
		if (avaliacaoDAO.alguemJaAvaliou(questionario))
			Messagebox.show("Prazo não pode ser excluido, já está em uso");

		else {
			prazoDAO.exclui(prazo); // exclui o prazo
			if (questionario.isAtivo()) {
				questionario.setAtivo(false);
				questionarioDAO.editar(questionario);
			}

			Messagebox.show("Prazo excluido", "Concluído", Messagebox.OK,
					Messagebox.INFORMATION, new EventListener<Event>() {
						@Override
						public void onEvent(Event event) throws Exception {
							Executions.sendRedirect(null);
						}
					});
		}

	}

	@Command
	@NotifyChange({ "perguntas", "pergunta" })
	public void excluiPergunta(@BindingParam("pergunta") Pergunta pergunta) {
		perguntas.remove(pergunta);
	}

	@Command
	@NotifyChange({ "questionariosCoord", "questionariosProf",
			"questionariosAuto", "questionariosInfra", "questionario" })
	public void exclui() {
		perguntas = questionario.getPerguntas();
		prazos = prazoDAO.getPrazos(questionario);
		perguntas.get(0).getTipoPergunta();
		// tenho que excluir as repostas especificas antes AINDA NAO TESTEI
		for (int i = 0; i < perguntas.size(); i++)
			respostaEspecificaDAO.excluiLista(respostaEspecificaDAO
					.getRespostasEspecificasPerguntas(perguntas.get(i)));
		perguntaDAO.excluiLista(perguntas);
		prazoDAO.excluiLista(prazos);
		if (questionarioDAO.exclui(questionario)) {
			Messagebox.show("Questionário Excluído", "Concluído",
					Messagebox.OK, Messagebox.INFORMATION,
					new EventListener<Event>() {
						@Override
						public void onEvent(Event event) throws Exception {
							Executions.sendRedirect(null);
						}
					});
			listaQuestionarios(questionario.getTipoQuestionario()).remove(
					questionario);
		}

	}

	@Command
	public void salvarQuest() {
		if (perguntas.size() > 1) {
			if (questionarioDAO.editar(questionario))
				if (perguntaDAO.excluiLista(perguntasSessao)) {
					for (Pergunta pergunta : perguntas) {
						pergunta.setQuestionario(questionario);
					}

					if (perguntaDAO.salvarLista(perguntas)) {
						Messagebox.show("Questionário Salvo", "Concluído",
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
		}
	}

	@Command
	@NotifyChange({ "perguntas", "pergunta" })
	public void top(@BindingParam("pergunta") Pergunta pergunta) {
		for (int index = perguntas.indexOf(pergunta); index > 0; index--) {
			perguntas.set(index, perguntas.get(index - 1));
		}
		perguntas.set(0, pergunta);
	}

	@Command
	@NotifyChange({ "perguntas", "pergunta" })
	public void bottom(@BindingParam("pergunta") Pergunta pergunta) {
		for (int index = perguntas.indexOf(pergunta); index < perguntas.size() - 1; index++) {
			perguntas.set(index, perguntas.get(index + 1));
		}
		perguntas.set(perguntas.size() - 1, pergunta);
	}

	@Command
	@NotifyChange("perguntas")
	public void down(@BindingParam("pergunta") Pergunta pergunta) {
		int index = perguntas.indexOf(pergunta);
		Pergunta aux = perguntas.get(index + 1);
		perguntas.set(index + 1, pergunta);
		perguntas.set(index, aux);
	}

	@Command
	@NotifyChange("perguntas")
	public void up(@BindingParam("pergunta") Pergunta pergunta) {
		int index = perguntas.indexOf(pergunta);
		Pergunta aux = perguntas.get(index - 1);
		perguntas.set(index - 1, pergunta);
		perguntas.set(index, aux);
	}

	@Command
	public void tipoPergunta(@BindingParam("textbox") Textbox text,
			@BindingParam("div") Div div, @BindingParam("button") Button b) {
		Button c = (Button) b.getNextSibling();
		c.setDisabled(false);
		if (pergunta.getTipoPergunta() == 0) {
			text.setDisabled(true);
			text.setVisible(true);
			div.setVisible(false);
			b.setDisabled(true);
		} else {
			if (pergunta.getTipoPergunta() == 3) {
				text.setVisible(false);
				div.setVisible(true);
				b.setDisabled(true);
			} else {
				text.setVisible(true);
				text.setDisabled(false);
				div.setVisible(false);
				b.setDisabled(false);
			}
		}
	}

	@Command
	@NotifyChange("questionario")
	public void ativa(@BindingParam("questionario") Questionario questionario) {
		if (!questionario.getPrazos().isEmpty()) {
			for (Questionario q : listaQuestionarios(questionario
					.getTipoQuestionario())) {
				if (q.getIdQuestionario() == questionario.getIdQuestionario())
					q.setAtivo(true);
				else
					q.setAtivo(false);
				questionarioDAO.editar(q);
			}
			Messagebox.show("Ativado", "Concluído", Messagebox.OK,
					Messagebox.INFORMATION, new EventListener<Event>() {
						@Override
						public void onEvent(Event event) throws Exception {
							Executions.sendRedirect(null);
						}
					});
		} else
			Messagebox.show("Questionário não possui prazo");
	}

	public List<Pergunta> getPerguntas() {
		return perguntas;
	}

	public void setPerguntas(List<Pergunta> perguntas) {
		this.perguntas = perguntas;
	}

	public Pergunta getPergunta() {
		return pergunta;
	}

	public void setPergunta(Pergunta pergunta) {
		this.pergunta = pergunta;
	}

	public QuestionarioDAO getQuestionarioDAO() {
		return questionarioDAO;
	}

	public void setQuestionarioDAO(QuestionarioDAO questionarioDAO) {
		this.questionarioDAO = questionarioDAO;
	}

	public List<Questionario> listaQuestionarios(Integer tipoQuestionario) {
		if (tipoQuestionario == 0)
			return questionariosCoord;
		else if (tipoQuestionario == 1)
			return questionariosProf;
		else if (tipoQuestionario == 2)
			return questionariosAuto;
		else
			return questionariosInfra;
	}

	public Questionario getQuestionario() {
		return questionario;
	}

	public void setQuestionario(Questionario questionario) {
		this.questionario = questionario;
	}

	public List<Questionario> getQuestionariosCoord() {
		return questionariosCoord;
	}

	public void setQuestionariosCoord(List<Questionario> questionariosCoord) {
		this.questionariosCoord = questionariosCoord;
	}

	public List<Questionario> getQuestionariosProf() {
		return questionariosProf;
	}

	public void setQuestionariosProf(List<Questionario> questionariosProf) {
		this.questionariosProf = questionariosProf;
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

	public boolean isAtivo() {
		return ativo;
	}

	@Command
	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public Avaliacao getAvaliacao() {
		return avaliacao;
	}

	public void setAvaliacao(Avaliacao avaliacao) {
		this.avaliacao = avaliacao;
	}

	public List<Integer> getTiposQuestionario() {
		return tiposQuestionario;
	}

	public void setTiposQuestionario(List<Integer> tiposQuestionario) {
		this.tiposQuestionario = tiposQuestionario;
	}

	public List<Pergunta> getPerguntasSessao() {
		return perguntasSessao;
	}

	public void setPerguntasSessao(List<Pergunta> perguntasSessao) {
		this.perguntasSessao = perguntasSessao;
	}

	public PrazoQuestionario getPrazo() {
		return prazo;
	}

	public void setPrazo(PrazoQuestionario prazo) {
		this.prazo = prazo;
	}

	public List<PrazoQuestionario> getPrazos() {
		return prazos;
	}

	public void setPrazos(List<PrazoQuestionario> prazos) {
		this.prazos = prazos;
	}

	public List<Integer> getTiposPergunta() {
		return tiposPergunta;
	}

	public void setTiposPergunta(List<Integer> tiposPergunta) {
		this.tiposPergunta = tiposPergunta;
	}

	public Integer getSpinnerInicio() {
		return spinnerInicio;
	}

	public void setSpinnerInicio(Integer spinnerInicio) {
		this.spinnerInicio = spinnerInicio;
	}

	public Integer getSpinnerFinal() {
		return spinnerFinal;
	}

	public void setSpinnerFinal(Integer spinnerFinal) {
		this.spinnerFinal = spinnerFinal;
	}

	public List<RespostaEspecifica> getRespostas() {
		return respostas;
	}

	public void setRespostas(List<RespostaEspecifica> respostas) {
		this.respostas = respostas;
	}

	public RespostaEspecifica getResposta() {
		return resposta;
	}

	public void setResposta(RespostaEspecifica resposta) {
		this.resposta = resposta;
	}

	public Questionario getQuestSessao() {
		return questSessao;
	}

	public void setQuestSessao(Questionario questSessao) {
		this.questSessao = questSessao;
	}

	public List<PrazoQuestionario> getPrazosSessao() {
		return prazosSessao;
	}

	public void setPrazosSessao(List<PrazoQuestionario> prazosSessao) {
		this.prazosSessao = prazosSessao;
	}

}

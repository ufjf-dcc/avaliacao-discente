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
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

import br.ufjf.avaliacao.model.Avaliacao;
import br.ufjf.avaliacao.model.Pergunta;
import br.ufjf.avaliacao.model.PrazoQuestionario;
import br.ufjf.avaliacao.model.Questionario;
import br.ufjf.avaliacao.persistent.impl.PerguntaDAO;
import br.ufjf.avaliacao.persistent.impl.PrazoQuestionarioDAO;
import br.ufjf.avaliacao.persistent.impl.QuestionarioDAO;

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
	private List<Pergunta> perguntasAntigas = new ArrayList<Pergunta>();
	private Pergunta pergunta = new Pergunta();
	private PerguntaDAO perguntaDAO = new PerguntaDAO();
	private Avaliacao avaliacao = new Avaliacao();
	private Questionario questionario = new Questionario();
	private static Questionario questionarioEditar = new Questionario();
	private List<Integer> tiposQuestionario = Arrays.asList(0, 1, 2, 3);
	private List<Integer> tiposPergunta = Arrays.asList(0, 1, 2, 3);
	private PrazoQuestionario prazo = new PrazoQuestionario();
	private List<PrazoQuestionario> prazos = new ArrayList<PrazoQuestionario>();
	private List<PrazoQuestionario> prazosAntigos = new ArrayList<PrazoQuestionario>();
	private PrazoQuestionarioDAO prazoDAO = new PrazoQuestionarioDAO();

	@Init
	public void init() throws HibernateException, Exception {
		testaPermissaoCoord();
		if (QuestionariosController.questionarioEditar.getPerguntas().isEmpty())
			perguntas = new ArrayList<Pergunta>();
		else {
			this.questionario = QuestionariosController.questionarioEditar;
			perguntas = perguntaDAO
					.getPerguntasQuestionario(QuestionariosController.questionarioEditar);
			perguntasAntigas = perguntaDAO
					.getPerguntasQuestionario(QuestionariosController.questionarioEditar);
			prazos = prazoDAO
					.getPrazo(QuestionariosController.questionarioEditar);
			prazosAntigos = prazos;
		}
	}

	@Command
	public void criarQuest() {
		QuestionariosController.questionarioEditar = new Questionario();
		Window window = (Window) Executions.createComponents(
				"/criarQuestionario.zul", null, null);
		window.doModal();
	}

	@Command
	public void editarQuest() {
		Window window = (Window) Executions.createComponents(
				"/editarQuestionario.zul", null, null);
		window.doModal();
	}

	@Command
	public void verQuest(@BindingParam("questionario") Questionario questionario) {
		QuestionariosController.questionarioEditar = questionario;
		Window window = (Window) Executions.createComponents(
				"/verQuestionario.zul", null, null);
		window.doModal();
	}

	@Command
	@NotifyChange({ "perguntas", "pergunta" })
	public void adicionaPergunta() {
		perguntas.add(pergunta);
		pergunta = new Pergunta();
	}

	@Command
	public void adcPrazo(@BindingParam("questionario") Questionario questionario) {
		QuestionariosController.questionarioEditar = questionario;
		Window window = (Window) Executions.createComponents("/add-prazo.zul",
				null, null);
		window.doModal();
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
		prazos = prazoDAO.getPrazo(questionario);
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
	@NotifyChange({ "perguntas", "questionario" })
	public void cria() {
		questionario.setCurso(usuario.getCurso());
		if (questionarioDAO.salvar(questionario)) {
			prazo.setQuestionario(questionario);
			prazoDAO.salvar(prazo);
			prazos.add(prazo);
			questionario.setPrazos(prazos);
			if (isAtivo()) {
				for (Questionario q : listaQuestionarios(questionario
						.getTipoQuestionario())) {
					q.setAtivo(false);
					questionarioDAO.editar(q);
				}
				questionario.setAtivo(true);
			}
			for (Pergunta pergunta : perguntas) {
				pergunta.setQuestionario(questionario);
			}
			if (perguntaDAO.salvarLista(perguntas)) {

				questionario = new Questionario();
				pergunta = new Pergunta();
				perguntas = new ArrayList<Pergunta>();
				prazo = new PrazoQuestionario();
			}
			Messagebox.show("Questionario Criado", "Concluído", Messagebox.OK,
					Messagebox.INFORMATION, new EventListener<Event>() {
						@Override
						public void onEvent(Event event) throws Exception {
							Executions.sendRedirect(null);
						}
					});
		}
	}

	@Command
	public void salvarQuest() {
		if (perguntas.size() > 1) {
			if (questionarioDAO.editar(questionario)) {
				if (prazoDAO.excluiLista(prazosAntigos)) {
					for (PrazoQuestionario p : prazos)
						p.setQuestionario(questionario);
					prazoDAO.salvarLista(prazos);
				}
				if (perguntaDAO.excluiLista(perguntasAntigas)) {
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
	@NotifyChange({ "perguntas", "pergunta" })
	public void up(@BindingParam("pergunta") Pergunta pergunta) {
		int index = perguntas.indexOf(pergunta);
		Pergunta aux = perguntas.get(index - 1);
		perguntas.set(index - 1, pergunta);
		perguntas.set(index, aux);
	}

	@Command
	@NotifyChange({ "questionariosCoord", "questionariosProf",
			"questionariosAuto", "questionariosInfra", "questionario" })
	public void ativa(@BindingParam("questionario") Questionario quest) {
		for (Questionario q : listaQuestionarios(quest.getTipoQuestionario())) {
			if (q.getIdQuestionario() == quest.getIdQuestionario())
				q.setAtivo(true);
			else
				q.setAtivo(false);
			questionarioDAO.editar(q);
		}
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

	public Questionario getQuestionarioEditar() {
		return QuestionariosController.questionarioEditar;
	}

	public void setQuestionarioEditar(Questionario questionarioEditar) {
		QuestionariosController.questionarioEditar = questionarioEditar;
	}

	public List<Integer> getTiposQuestionario() {
		return tiposQuestionario;
	}

	public void setTiposQuestionario(List<Integer> tiposQuestionario) {
		this.tiposQuestionario = tiposQuestionario;
	}

	public List<Pergunta> getPerguntasAntigas() {
		return perguntasAntigas;
	}

	public void setPerguntasAntigas(List<Pergunta> perguntasAntigas) {
		this.perguntasAntigas = perguntasAntigas;
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

	public List<PrazoQuestionario> getPrazosAntigos() {
		return prazosAntigos;
	}

	public void setPrazosAntigos(List<PrazoQuestionario> prazosAntigos) {
		this.prazosAntigos = prazosAntigos;
	}

}

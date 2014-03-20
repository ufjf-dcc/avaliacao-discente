package br.ufjf.avaliacao.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.CategoryModel;
import org.zkoss.zul.PieModel;
import org.zkoss.zul.SimpleCategoryModel;
import org.zkoss.zul.SimplePieModel;
import org.zkoss.zul.Window;
import org.zkoss.zul.event.ChartDataListener;

import br.ufjf.avaliacao.model.Avaliacao;
import br.ufjf.avaliacao.model.Pergunta;
import br.ufjf.avaliacao.model.PrazoQuestionario;
import br.ufjf.avaliacao.model.Questionario;
import br.ufjf.avaliacao.model.Resposta;
import br.ufjf.avaliacao.model.RespostaEspecifica;
import br.ufjf.avaliacao.model.Turma;
import br.ufjf.avaliacao.persistent.impl.AvaliacaoDAO;
import br.ufjf.avaliacao.persistent.impl.QuestionarioDAO;
import br.ufjf.avaliacao.persistent.impl.RespostaDAO;
import br.ufjf.avaliacao.persistent.impl.TurmaDAO;

public class ResultadosController extends GenericController {

	private TurmaDAO turmaDAO = new TurmaDAO();
	private RespostaDAO respostaDAO = new RespostaDAO();
	private List<String> semestres = respostaDAO.getAllSemestres();
	private String semestre = new String();
	private List<Turma> turmas = new ArrayList<Turma>();
	private Turma turma = new Turma();
	private List<Avaliacao> avaliacoes = new ArrayList<Avaliacao>();
	private PrazoQuestionario prazo = new PrazoQuestionario();
	private List<Pergunta> perguntas = new ArrayList<>();
	private Pergunta perguntaSelecionada;

	@Command
	@NotifyChange("turmas")
	public void carregarTurmas() {
		setTurmas(getLetraDisciplinaTurma());
		turmas = getLetraDisciplinaTurma();
	}

	@Command
	@NotifyChange("perguntas")
	public void carregarPerguntas() {
		avaliacoes = new AvaliacaoDAO().avaliacoesTurma(turma);
		prazo = avaliacoes.get(0).getPrazoQuestionario();
		perguntas = prazo.getQuestionario().getPerguntas();
	}

	private List<Turma> getLetraDisciplinaTurma() {
		List<Turma> turmas = new ArrayList<Turma>();
		for (Turma t : new TurmaDAO().getAllTurmas(semestre)) {
			turmas.add(t);
		}
		QuestionarioDAO questionarioDAO = new QuestionarioDAO();
		List<Questionario> quest = questionarioDAO
				.retornaQuestionariosSemestreProfessor(semestre);
		return turmas;
	}

	@Command
	public void abrirGrafico() {
		session.setAttribute("graficoTurma", turma);
		Window w = (Window) Executions.createComponents("/grafico.zul", null,
				null);
		w.doPopup();
	}

	@Command
	public void getGrafico() {
		PieModel model = new SimplePieModel();
		List<Resposta> respostas = new RespostaDAO().getRespostasPerguntaSemestre(perguntaSelecionada, semestre);
		List<RespostaEspecifica> alternativas = perguntaSelecionada.getRespostasEspecificasBanco();
		HashMap<String, Integer> contagem = new HashMap<>();
		for(RespostaEspecifica re : alternativas) {
			contagem.put(re.getRespostaEspecifica(), 0);
		}
		for(Resposta r : respostas) {
			for(RespostaEspecifica re : alternativas) {
				if(r.getResposta() == re.getRespostaEspecifica()) {
					contagem.put(re.getRespostaEspecifica(), (contagem.get(re.getRespostaEspecifica())+1));
				}
			}
		}
		Iterator<String> keyIterator = contagem.keySet().iterator();
		while(keyIterator.hasNext()){
			String key = keyIterator.next();
			System.out.println("Resposta: " + key + " quantidade: " + contagem.get(key));	
		}
	}

	public List<String> getSemestres() {
		return semestres;
	}

	public void setSemestres(List<String> semestres) {
		this.semestres = semestres;
	}

	public String getSemestre() {
		return semestre;
	}

	public void setSemestre(String semestre) {
		this.semestre = semestre;
	}

	public List<Turma> getTurmas() {
		return turmas;
	}

	public void setTurmas(List<Turma> turmas) {
		this.turmas = turmas;
	}

	public Turma getTurma() {
		return turma;
	}

	public void setTurma(Turma turma) {
		this.turma = turma;
	}

	public List<Pergunta> getPerguntas() {
		return perguntas;
	}

	public void setPerguntas(List<Pergunta> perguntas) {
		this.perguntas = perguntas;
	}

	public Pergunta getPerguntaSelecionada() {
		return perguntaSelecionada;
	}

	public void setPerguntaSelecionada(Pergunta perguntaSelecionada) {
		this.perguntaSelecionada = perguntaSelecionada;
	}

}

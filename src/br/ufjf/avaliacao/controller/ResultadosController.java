package br.ufjf.avaliacao.controller;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.CategoryModel;
import org.zkoss.zul.SimpleCategoryModel;
import org.zkoss.zul.Window;

import br.ufjf.avaliacao.model.Avaliacao;
import br.ufjf.avaliacao.model.Pergunta;
import br.ufjf.avaliacao.model.Questionario;
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

	@Command
	@NotifyChange("turmas")
	public void carregarTurmas() {
		setTurmas(getLetraDisciplinaTurma());
		turmas = getLetraDisciplinaTurma();
	}

	private List<Turma> getLetraDisciplinaTurma() {
		List<Turma> turmas = new ArrayList<Turma>();
		for (Turma t : new TurmaDAO().getAllTurmas(semestre)) {
			turmas.add(t);
		}
		QuestionarioDAO questionarioDAO = new QuestionarioDAO();
		respostaDAO.retornaRespostaSemestre(semestre);
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
	public void gerarGrafico() {
		turma = (Turma) session.getAttribute("graficoTurma");
		avaliacoes = (new AvaliacaoDAO().avaliacoesTurma(turma));
	}

	public CategoryModel getModel() {
		SimpleCategoryModel model = new SimpleCategoryModel();
		Questionario q = avaliacoes.get(0).getPrazoQuestionario().getQuestionario();
		
		for (Pergunta p : q.getPerguntas() ) {
			
			switch (p.getTipoPergunta()) {
			case 1: model.setValue("1", p.getTituloPergunta(), new RespostaDAO().numRespostasPergunta(p, "1"));
					model.setValue("2", p.getTituloPergunta(), new RespostaDAO().numRespostasPergunta(p, "2"));
					model.setValue("3", p.getTituloPergunta(), new RespostaDAO().numRespostasPergunta(p, "3"));
					model.setValue("4", p.getTituloPergunta(), new RespostaDAO().numRespostasPergunta(p, "4"));
					model.setValue("5", p.getTituloPergunta(), new RespostaDAO().numRespostasPergunta(p, "5"));
				break;
			case 2: model.setValue("Muito Ruim", p.getTituloPergunta(), new RespostaDAO().numRespostasPergunta(p, "Muito Ruim"));
					model.setValue("Ruim", p.getTituloPergunta(), new RespostaDAO().numRespostasPergunta(p, "Ruim"));
					model.setValue("Regular", p.getTituloPergunta(), new RespostaDAO().numRespostasPergunta(p, "Regular"));
					model.setValue("Bom", p.getTituloPergunta(), new RespostaDAO().numRespostasPergunta(p, "Bom"));
					model.setValue("Muito Bom", p.getTituloPergunta(), new RespostaDAO().numRespostasPergunta(p, "Muito Bom"));
				break;
			case 3: model.setValue("Sim", p.getTituloPergunta(), new RespostaDAO().numRespostasPergunta(p, "SIM"));
					model.setValue("Não", p.getTituloPergunta(), new RespostaDAO().numRespostasPergunta(p, "NAO"));
			default:
				break;
			}
					
		}		
		//model.setValue(resposta possiveis, pergunta, numero de respostas);
		return model;
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
}

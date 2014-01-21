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
import br.ufjf.avaliacao.model.Turma;
import br.ufjf.avaliacao.persistent.impl.AvaliacaoDAO;
import br.ufjf.avaliacao.persistent.impl.TurmaDAO;

public class ResultadosController extends GenericController {

	private TurmaDAO turmaDAO = new TurmaDAO();
	private List<String> semestres = turmaDAO.getAllSemestres();
	private String semestre = new String();
	private List<Turma> turmas = new ArrayList<Turma>();
	private Turma turma = new Turma();
	private List<Avaliacao> avaliacoes = new ArrayList<Avaliacao>();
	

	@Command
	@NotifyChange("ts")
	public void carregarTurmas() {
		setTurmas(getLetraDisciplinaTurma());
		turmas = getLetraDisciplinaTurma();
	}
	
	private List<Turma> getLetraDisciplinaTurma() {
		List<Turma> turmas = new ArrayList<Turma>();
		for (Turma t : new TurmaDAO().getAllTurmas(semestre)) {
			turmas.add(t);
		}
		return turmas;
	}
	
	@Command
	public void abrirGrafico() {
		session.setAttribute("graficoTurma", turma);
		Window w = (Window) Executions.createComponents("/grafico.zul", null, null);
		w.doPopup();
	}
	
	@Command
	public void gerarGrafico() {
		turma = (Turma) session.getAttribute("graficoTurma");
		avaliacoes = (new AvaliacaoDAO().avaliacoesTurma(turma));
	}
	
	public CategoryModel getModel() {
		SimpleCategoryModel model = new SimpleCategoryModel();
		
		for(Avaliacao a : avaliacoes) {
			//model.setValue(resposta possiveis, pergunta, numero de respostas);
		}
		
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

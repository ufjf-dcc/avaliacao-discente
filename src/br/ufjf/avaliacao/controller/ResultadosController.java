package br.ufjf.avaliacao.controller;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;

import br.ufjf.avaliacao.model.Turma;
import br.ufjf.avaliacao.persistent.impl.TurmaDAO;

public class ResultadosController extends GenericController {

	private TurmaDAO turmaDAO = new TurmaDAO();
	private List<String> semestres = turmaDAO.getAllSemestres();
	private String semestre = new String();
	private List<String> ts = new ArrayList<String>();

	@Command
	@NotifyChange("ts")
	public void carregarTurmas() {
		setTs(getletraDisciplinaTurma());
		ts = getletraDisciplinaTurma();
		System.out.println(ts.get(0));
	}
	
	private List<String> getletraDisciplinaTurma() {
		List<String> turmas = new ArrayList<String>();
		for (Turma t : new TurmaDAO().getAllTurmas(semestre)) {
			String nomeTurma = t.getDisciplina().getNomeDisciplina() + " - "
					+ t.getLetraTurma();
			turmas.add(nomeTurma);
		}
		return turmas;
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

	public List<String> getTs() {
		return ts;
	}

	public void setTs(List<String> ts) {
		this.ts = ts;
	}
}

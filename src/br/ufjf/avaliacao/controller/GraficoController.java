package br.ufjf.avaliacao.controller;

import org.zkoss.zul.ChartModel;
import org.zkoss.zul.PieModel;

import br.ufjf.avaliacao.model.Pergunta;
import br.ufjf.avaliacao.model.Turma;
import br.ufjf.avaliacao.model.Usuario;

public class GraficoController extends GenericController{
	
	private Usuario aluno;
	private Usuario coordenador;
	private ChartModel model;
	private Pergunta perguntaSelecionada;
	private String semestre;
	private Turma turma = new Turma();
	private String type;	
	
	public Usuario getAluno() {
		return (Usuario) session.getAttribute("aluno");
	}
	public void setAluno(Usuario aluno) {
		this.aluno = aluno;
	}
	public Usuario getCoordenador() {
		return (Usuario) session.getAttribute("coordenador");
	}
	public void setCoordenador(Usuario coordenador) {
		this.coordenador = coordenador;
	}
	public ChartModel getModel() {
		return (ChartModel) session.getAttribute("model");
	}
	public void setModel(ChartModel model) {
		this.model = model;
	}
	public String getSemestre() {
		return (String) session.getAttribute("semestre");
	}
	public void setSemestre(String semestre) {
		this.semestre = semestre;
	}
	public Pergunta getPerguntaSelecionada() {
		return (Pergunta) session.getAttribute("pergunta");
	}
	public void setPerguntaSelecionada(Pergunta perguntaSelecionada) {
		this.perguntaSelecionada = perguntaSelecionada;
	}
	public Turma getTurma() {
		return (Turma) session.getAttribute("turma");
	}
	public void setTurma(Turma turma) {
		this.turma = turma;
	}
	public String getType() {
		return (String) session.getAttribute("type");
	}
	public void setType(String type) {
		this.type = type;
	}
	
	
}

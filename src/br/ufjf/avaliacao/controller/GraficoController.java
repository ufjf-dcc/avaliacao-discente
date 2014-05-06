package br.ufjf.avaliacao.controller;

import org.zkoss.zul.PieModel;

import br.ufjf.avaliacao.model.Pergunta;
import br.ufjf.avaliacao.model.Turma;
import br.ufjf.avaliacao.model.Usuario;

public class GraficoController extends GenericController{
	
	private Turma turma = new Turma();
	private Pergunta perguntaSelecionada;
	private PieModel model;
	private String semestre;
	private Usuario coordenador;
	private Usuario aluno;
	
	
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
	public PieModel getModel() {
		return (PieModel) session.getAttribute("model");
	}
	public void setModel(PieModel model) {
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
	
	
}

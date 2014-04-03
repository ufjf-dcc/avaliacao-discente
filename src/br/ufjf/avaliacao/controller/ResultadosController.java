package br.ufjf.avaliacao.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.PieModel;
import org.zkoss.zul.Row;
import org.zkoss.zul.SimplePieModel;
import org.zkoss.zul.Window;

import br.ufjf.avaliacao.model.Avaliacao;
import br.ufjf.avaliacao.model.Pergunta;
import br.ufjf.avaliacao.model.PrazoQuestionario;
import br.ufjf.avaliacao.model.Resposta;
import br.ufjf.avaliacao.model.RespostaEspecifica;
import br.ufjf.avaliacao.model.Turma;
import br.ufjf.avaliacao.model.Usuario;
import br.ufjf.avaliacao.persistent.impl.AvaliacaoDAO;
import br.ufjf.avaliacao.persistent.impl.RespostaDAO;
import br.ufjf.avaliacao.persistent.impl.TurmaDAO;
import br.ufjf.avaliacao.persistent.impl.UsuarioDAO;

public class ResultadosController extends GenericController implements
		Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6731467107690993996L;

	private RespostaDAO respostaDAO = new RespostaDAO();
	private List<String> semestres = respostaDAO.getAllSemestres();
	private String semestre = new String();
	private List<Turma> turmas = new ArrayList<Turma>();
	private Turma turma = new Turma();
	private List<Avaliacao> avaliacoes = new ArrayList<Avaliacao>();
	private PrazoQuestionario prazo = new PrazoQuestionario();
	private List<Pergunta> perguntas = new ArrayList<>();
	private Pergunta perguntaSelecionada;
	private List<Usuario> professores = new ArrayList<Usuario>();
	private String professorEscolhido = null;
	
	
	
	@Command
	@NotifyChange("turmas")
	public void carregarTurmas() {
		setTurmas(getLetraDisciplinaTurma());
	}
	
	@Command
	@NotifyChange("professores")
	public void carregarProfessores() {
		setTurmas(getLetraDisciplinaTurma());
	}
	
	@Command
	@NotifyChange("professores")
	public void carregarSemestres() {
		setTurmas(getLetraDisciplinaTurma());
	}

	// @Command
	// @NotifyChange("perguntas")
	// public void carregarPerguntas() {
	// avaliacoes = new AvaliacaoDAO().avaliacoesTurma(turma);
	// prazo = avaliacoes.get(0).getPrazoQuestionario();
	// perguntas = prazo.getQuestionario().getPerguntas();
	// }

	private List<Turma> getLetraDisciplinaTurma() {
		List<Turma> turmas = new ArrayList<Turma>();
		for (Turma t : new TurmaDAO().getAllTurmas(semestre)) {
			turmas.add(t);
		}
		return turmas;
	}

	@Command
	@NotifyChange("perguntas")
	public void verificaTurma() {
		avaliacoes = new AvaliacaoDAO().avaliacoesTurma(turma);
		if (!avaliacoes.isEmpty()) {
			prazo = avaliacoes.get(0).getPrazoQuestionario();
			if (prazo.getDataInicial().before(new Date())
					&& prazo.getDataFinal().after(new Date())) {
				Messagebox.show("Período para avaliação não terminado ainda.");
				perguntas = null;
			} else {
				perguntas = prazo.getQuestionario().getPerguntas();
			}
		} else {
			perguntas = null;
			Messagebox
					.show("Não foram feitas avaliações para essa turma e/ou período para avaliação não terminou ainda.");
		}
	}

	@Command
	public void gerarGrafico() {
		getGraficoProfessor();
		session.setAttribute("turma", turma);
		session.setAttribute("pergunta", perguntaSelecionada);
		Window w = (Window) Executions.createComponents("/grafico.zul", null,
				null);
		w.setClosable(true);
		w.setMinimizable(true);
		w.doOverlapped();
	}

	public void getGraficoProfessor() {
		List<Resposta> respostas = new RespostaDAO()
				.getRespostasPerguntaSemestre(perguntaSelecionada, semestre,
						turma);
		List<RespostaEspecifica> alternativas = perguntaSelecionada
				.getRespostasEspecificasBanco();
		Map<String, Integer> contagem = new LinkedHashMap<>();
		PieModel model = new SimplePieModel();
		for (RespostaEspecifica re : alternativas) {
			contagem.put(re.getRespostaEspecifica(), 0);
		}
		for (Resposta r : respostas) {
			for (RespostaEspecifica re : alternativas) {
				if (r.getResposta().equals(re.getRespostaEspecifica())) {
					contagem.put(re.getRespostaEspecifica(),
							(contagem.get(re.getRespostaEspecifica()) + 1));
				}
			}
		}
		Iterator<String> keyIterator = contagem.keySet().iterator();
		while (keyIterator.hasNext()) {
			String key = keyIterator.next();
			model.setValue(key, contagem.get(key));
		}
		session.setAttribute("model", model);
	}

	@Command
	public void avaliacaoEscolhida(@BindingParam("row") Row row,
			@BindingParam("combo") Combobox combo) {
		professorEscolhido = null;
		switch (combo.getSelectedItem().getValue().toString()) {
		case "0":
			row.getNextSibling().setVisible(true);
			row.getNextSibling().getNextSibling().setVisible(false);
			row.getNextSibling().getNextSibling().getNextSibling().setVisible(false);
			row.getNextSibling().getNextSibling().getNextSibling().getNextSibling().setVisible(false);
			break;
		case "1":
			row.getNextSibling().setVisible(true);
			row.getNextSibling().getNextSibling().setVisible(true);
			row.getNextSibling().getNextSibling().getNextSibling().setVisible(true);
			row.getNextSibling().getNextSibling().getNextSibling().getNextSibling().setVisible(true);
			break;
		case "2":
			row.getNextSibling().setVisible(true);
			row.getNextSibling().getNextSibling().setVisible(false);
			row.getNextSibling().getNextSibling().getNextSibling().setVisible(false);
			row.getNextSibling().getNextSibling().getNextSibling().getNextSibling().setVisible(false);
			break;
		case "3":
			row.getNextSibling().setVisible(true);
			row.getNextSibling().getNextSibling().setVisible(false);
			row.getNextSibling().getNextSibling().getNextSibling().setVisible(false);
			row.getNextSibling().getNextSibling().getNextSibling().getNextSibling().setVisible(false);
			break;
		default:
			;
			break;
		}
	}

	public List<String> getSemestres() {
		semestres.add("Todos");
		if(professorEscolhido != null){
			TurmaDAO turmaDAO = new TurmaDAO();
			semestres.addAll(turmaDAO.getSemestresUsuario(getProfessorEscolhido(professorEscolhido)));
		}
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

	public List<String> getProfessores() {
		UsuarioDAO usuarioDAO = new UsuarioDAO();
		professores = usuarioDAO.retornaProfessorCurso(usuario.getCurso());
		List<String> nomesProfessores = new ArrayList<String>();
		for(int i=0;i<professores.size();i++)
			nomesProfessores.add(professores.get(i).getNome());
		return nomesProfessores;
	}

	public void setProfessores(List<Usuario> professores) {
		this.professores = professores;
	}

	public String getProfessorEscolhido() {
		return professorEscolhido;
	}

	public void setProfessorEscolhido(String professorEscolhido) {
		this.professorEscolhido = professorEscolhido;
	}
	
	private Usuario getProfessorEscolhido(String nomeProfessor){
		if(professorEscolhido != null){
			for(int i=0;i<professores.size();i++)
				if(professores.get(i).getNome() == professorEscolhido)
					return professores.get(i);
		}
		return null;
	}

}

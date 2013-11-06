package br.ufjf.avaliacao.controller;

import java.util.List;

import org.hibernate.HibernateException;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zhtml.Messagebox;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Window;

import br.ufjf.avaliacao.business.TurmaBusiness;
import br.ufjf.avaliacao.model.Disciplina;
import br.ufjf.avaliacao.model.Turma;
import br.ufjf.avaliacao.model.Usuario;
import br.ufjf.avaliacao.persistent.impl.DisciplinaDAO;
import br.ufjf.avaliacao.persistent.impl.TurmaDAO;
import br.ufjf.avaliacao.persistent.impl.UsuarioDAO;

public class TurmasController extends GenericController {

	private TurmaDAO turmaDAO = new TurmaDAO();
	private DisciplinaDAO disciplinaDAO = new DisciplinaDAO();
	private UsuarioDAO usuarioDAO = new UsuarioDAO();
	private Turma turma = new Turma();
	private Disciplina disciplina = new Disciplina();
	private Usuario professor = new Usuario();
	private List<Turma> turmas = (List<Turma>) turmaDAO.getTodasTurmas();
	private List<Disciplina> disciplinas = (List<Disciplina>) disciplinaDAO
			.getTodasDisciplinas();

	@Init
	public void init() throws HibernateException, Exception {
		if (testaLogado())
			testaPermissao(3);
	}

	@Command
	public void abreCadastro() {
		Window window = (Window) Executions.createComponents(
				"/cadastrarTurma.zul", null, null);
		window.doModal();
		System.out.println();
	}

	@Command
	@NotifyChange("turmas")
	public void edita(@BindingParam("turma") Turma turma) {
		BindUtils.postNotifyChange(null, null, turma, "editingStatus");

	}

	@Command
	@NotifyChange("turmas")
	public void exclui(@BindingParam("turma") Turma turma) {
		turmaDAO.exclui(turma);
		turmas.remove(turma);
		Messagebox.show("Turma excluida com sucesso!");
	}

	@Command
	@NotifyChange({ "turmas", "turma" })
	public void cadastra() throws HibernateException, Exception {
		TurmaBusiness turmaBusiness = new TurmaBusiness();
		if (!turmaBusiness.cadastroValido(turma.getLetraTurma(),
				turma.getSemestre())) {
			Messagebox.show("Preencha todos os campos!");
		} else if (turmaBusiness.cadastrado(turma.getLetraTurma(),
				turma.getSemestre(), turma.getDisciplina())) {
			Messagebox.show("Turma já cadastrada!");
			turma = new Turma();
		} else if (turmaDAO.salvar(turma)) {
			turmas.add(turma);
			turma = new Turma();
			Messagebox.show("Turma cadastrada com sucesso!");
		}

	}

	@Command
	public void changeEditableStatus(@BindingParam("turma") Turma turma) {
		turma.setEditingStatus(!turma.isEditingStatus());
		refreshRowTemplate(turma);
	}

	@Command
	public void confirm(@BindingParam("turma") Turma turma)
			throws HibernateException, Exception {
		TurmaBusiness turmaBusiness = new TurmaBusiness();
		changeEditableStatus(turma);
		TurmaDAO turmaDAO = new TurmaDAO();
		turmaDAO.editar(turma);
		refreshRowTemplate(turma);
	}

	public void refreshRowTemplate(Turma turma) {
		BindUtils.postNotifyChange(null, null, turma, "editingStatus");
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

	public Disciplina getDisciplina() {
		return disciplina;
	}

	public void setDisciplina(Disciplina disciplina) {
		this.disciplina = disciplina;
	}

	public Usuario getProfessor() {
		return professor;
	}

	public void setProfessor(Usuario professor) {
		this.professor = professor;
	}

	public List<Disciplina> getDisciplinas() {
		return disciplinas;
	}

	public void setDisciplinas(List<Disciplina> disciplinas) {
		this.disciplinas = disciplinas;
	}

	@Command
	public String getProfessores(@BindingParam("turma") Turma turma ) {
		List<Usuario> professores = (List<Usuario>) usuarioDAO.retornaProfessoresTurma(turma);
		String profs = "";
		if (professores == null)
			System.out.println("Professores Vazio!");
		for(Usuario prof : professores){
			System.out.println(prof.getNome());
			profs = profs.concat(prof.getNome() + ", ");
		}
		return profs;
	}
	
	
}

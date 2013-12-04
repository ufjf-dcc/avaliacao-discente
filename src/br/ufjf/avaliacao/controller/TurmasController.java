package br.ufjf.avaliacao.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.HibernateException;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.util.media.Media;
import org.zkoss.zhtml.Messagebox;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zul.Label;
import org.zkoss.zul.Window;
import org.zkoss.zul.Messagebox.ClickEvent;

import br.ufjf.avaliacao.business.TurmaBusiness;
import br.ufjf.avaliacao.model.Curso;
import br.ufjf.avaliacao.model.Disciplina;
import br.ufjf.avaliacao.model.Turma;
import br.ufjf.avaliacao.model.Usuario;
import br.ufjf.avaliacao.persistent.impl.CursoDAO;
import br.ufjf.avaliacao.persistent.impl.DisciplinaDAO;
import br.ufjf.avaliacao.persistent.impl.TurmaDAO;
import br.ufjf.avaliacao.persistent.impl.UsuarioDAO;

public class TurmasController extends GenericController {

	private TurmaDAO turmaDAO = new TurmaDAO();
	private Turma turma = new Turma();
	private List<Turma> turmas = (List<Turma>) turmaDAO.getTodasTurmas();
	private DisciplinaDAO disciplinaDAO = new DisciplinaDAO();
	private Disciplina disciplina = new Disciplina();
	private List<Disciplina> disciplinas = (List<Disciplina>) disciplinaDAO
			.getTodasDisciplinas();
	private UsuarioDAO usuarioDAO = new UsuarioDAO();
	private List<Usuario> professores = usuarioDAO.retornaProfessores();
	private Usuario professor = new Usuario();

	@Init
	public void init() throws HibernateException, Exception {
		testaPermissaoAdmin();
	}

	@Command
	public void abreCadastro() {
		Window window = (Window) Executions.createComponents(
				"/cadastrarTurma.zul", null, null);
		window.doModal();
		System.out.println();
	}

	@Command
	public void abreCadastroTurma() {
		Window window = (Window) Executions.createComponents(
				"/cadastrarTurmaCsv.zul", null, null);
		window.doModal();
		System.out.println();
	}

	@Command("uploadTurmas")
	public void uploadTurmas(@BindingParam("evt") UploadEvent evt)
			throws IOException {
		Media media = evt.getMedia();
		if (!media.getName().contains(".csv")) {
			Messagebox
					.show("Este não é um arquivo válido! Apenas CSV são aceitos.");
			return;
		}

		try {

			BufferedReader in = new BufferedReader(media.getReaderData());
			String linha;
			Turma turma;
			Usuario professor;
			Disciplina d;
			List<Turma> nturmas = new ArrayList<Turma>();

			while ((linha = in.readLine()) != null) {

				String conteudo[] = linha.split(";");
				// Verifica se possui professor
				if (usuarioDAO.retornaUsuario(conteudo[4]) != null) {
					professor = usuarioDAO.retornaUsuario(conteudo[4]);
				} else {
					professor = new Usuario(conteudo[4], conteudo[5],
							conteudo[6]);
					usuarioDAO.salvar(professor);
				}
				// Verifica se possui a disciplina
				if (disciplinaDAO.retornaDisciplinaCod(conteudo[0]) != null) {
					d = disciplinaDAO
							.retornaDisciplinaCod(conteudo[0]);
				} else {
					d = new Disciplina(conteudo[0], conteudo[1]);
					disciplinaDAO.salvar(d);
				}

				turma = new Turma(
						disciplinaDAO.retornaDisciplinaCod(conteudo[0]),
						conteudo[2], conteudo[3], professor);
				nturmas.add(turma);
			}

			if (turmaDAO.salvarLista(nturmas))
				Messagebox.show("Usuarios cadastrados com sucesso", null,
						new org.zkoss.zk.ui.event.EventListener<ClickEvent>() {
							public void onEvent(ClickEvent e) {
								if (e.getButton() == Messagebox.Button.OK)
									Executions.sendRedirect(null);
								else
									Executions.sendRedirect(null);
							}
						});
		}

		catch (IllegalStateException e) {
			String csv = new String(media.getByteData());
			String linhas[] = csv.split("\\r?\\n");
			Turma turma;
			Usuario professor;
			Disciplina d;
			List<Turma> nturmas = new ArrayList<Turma>();

			for (String linha : linhas) {
				String conteudo[] = linha.split(",|;|:");
				// Verifica se possui professor
				if (usuarioDAO.retornaUsuario(conteudo[4]) != null) {
					professor = usuarioDAO.retornaUsuario(conteudo[4]);
				} else {
					professor = new Usuario(conteudo[4], conteudo[5],
							conteudo[6]);
					usuarioDAO.salvaOuEdita(professor);
				}
				// Verifica se possui a disciplina
				if (disciplinaDAO.retornaDisciplinaCod(conteudo[0]) != null) {
					d = disciplinaDAO
							.retornaDisciplinaCod(conteudo[0]);
				} else {
					d = new Disciplina(conteudo[0], conteudo[1]);
					disciplinaDAO.salvar(d);
				}

				turma = new Turma(
						disciplinaDAO.retornaDisciplinaCod(conteudo[0]),
						conteudo[2], conteudo[3], professor);
				nturmas.add(turma);
			}

			if (turmaDAO.salvarLista(nturmas))
				Messagebox.show("Usuarios cadastrados com sucesso", null,
						new org.zkoss.zk.ui.event.EventListener<ClickEvent>() {
							public void onEvent(ClickEvent e) {
								if (e.getButton() == Messagebox.Button.OK)
									Executions.sendRedirect(null);
								else
									Executions.sendRedirect(null);
							}
						});

		}
	}
	
	@Command("uploadAlunos")
	public void uploadAlunos(@BindingParam("evt") UploadEvent evt) throws IOException {
		Media media = evt.getMedia();
		if (!media.getName().contains(".csv")) {
			Messagebox
					.show("Este não é um arquivo válido! Apenas CSV são aceitos.");
			return;
		}
		
		
		try {
			
			// Leitura com o BufferReader que e mais rapido
			
			BufferedReader in = new BufferedReader(media.getReaderData());
			String linha;
			Usuario nusuario;
			Curso curso;
			CursoDAO cursoDAO = new CursoDAO();
			while ((linha = in.readLine()) != null) {
				String conteudo[] = linha.split(";");
				
				// verifica se o curso esta cadastrado, se nao ele cria
				if(cursoDAO.getCursoNome(conteudo[4])!=null){
					curso = cursoDAO.getCursoNome(conteudo[4]);
				}
				else{
					curso = new Curso(conteudo[4]);
					cursoDAO.salvar(curso);
				}
				if(usuarioDAO.retornaUsuario(conteudo[1])!=null){
					nusuario = usuarioDAO.retornaUsuario(conteudo[1]);
				}	
				else{
				nusuario = new Usuario(conteudo[0], conteudo[1], conteudo[2],cursoDAO.getCursoNome(conteudo[4]),
						Integer.parseInt(conteudo[3]));
					usuarioDAO.salvar(nusuario);
				}
				
				Turma t = turmaDAO.retornaTurma(conteudo[6], conteudo[7], disciplinaDAO.retornaDisciplinaCod(conteudo[5]));
				nusuario.getTurmas().add(t);
				usuarioDAO.salvar(nusuario);
				//turma.setUsuarios(usuarios);
			}
				
			if (true)
				Messagebox.show("Usuarios cadastrados com sucesso", null,
						new org.zkoss.zk.ui.event.EventListener<ClickEvent>() {
					public void onEvent(ClickEvent e) {
						if (e.getButton() == Messagebox.Button.OK)
							Executions.sendRedirect(null);
						else
							Executions.sendRedirect(null);
					}
				});
		
		
	}
		catch (IllegalStateException e) {
			String csv = new String(media.getByteData());
			String linhas[] = csv.split("\\r?\\n");
			Usuario nusuario;
			Curso curso;
			CursoDAO cursoDAO = new CursoDAO();
			
			for (String linha : linhas) {
				String conteudo[] = linha.split(",|;|:");
				// verifica se o curso está cadastrado, se não ele cria
				if(cursoDAO.getCursoNome(conteudo[4])!=null){
					curso = cursoDAO.getCursoNome(conteudo[4]);
				}
				else{
					curso = new Curso(conteudo[4]);
					cursoDAO.salvar(curso);
				}
				if(usuarioDAO.retornaUsuarioEmail(conteudo[1])!=null){
					nusuario = usuarioDAO.retornaUsuarioEmail(conteudo[1]);
				}	
				else{
					nusuario = new Usuario(conteudo[0], conteudo[1], conteudo[2],cursoDAO.getCursoNome(conteudo[4]),
						Integer.parseInt(conteudo[3]));
					usuarioDAO.salvar(nusuario);
				}
				
				Turma t = turmaDAO.retornaTurma(conteudo[6], conteudo[7], disciplinaDAO.retornaDisciplinaCod(conteudo[5]));
				System.out.println(t);

				nusuario.getTurmas().add(t);
				usuarioDAO.salvar(nusuario);
				//turma.setUsuarios(usuarios);
				
			}
			if (true)
				Messagebox.show("Usuarios cadastrados com sucesso", null,
						new org.zkoss.zk.ui.event.EventListener<ClickEvent>() {
					public void onEvent(ClickEvent e) {
						if (e.getButton() == Messagebox.Button.OK)
							Executions.sendRedirect(null);
						else
							Executions.sendRedirect(null);
					}
				});

		}
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

	public List<Usuario> getProfessores() {
		return professores;
	}

	public void setProfessores(List<Usuario> professores) {
		this.professores = professores;
	}

	public List<Disciplina> getDisciplinas() {
		return disciplinas;
	}

	public void setDisciplinas(List<Disciplina> disciplinas) {
		this.disciplinas = disciplinas;
	}

	@Command
	public void getProfessores(@BindingParam("turma") Turma turma,
			@BindingParam("label") Label label) {
		List<Usuario> professores = (List<Usuario>) usuarioDAO
				.retornaProfessoresTurma(turma);
		String profs = "";
		if (!professores.isEmpty()) {
			for (Usuario prof : professores) {
				profs = profs.concat(prof.getNome() + ", ");
			}
			label.setValue(profs.substring(0, profs.length() - 2));
		} else {
			label.setValue("Sem professor");
		}
		

	}

	public Usuario getProfessor() {
		return professor;
	}

	public void setProfessor(Usuario professor) {
		this.professor = professor;
	}

}

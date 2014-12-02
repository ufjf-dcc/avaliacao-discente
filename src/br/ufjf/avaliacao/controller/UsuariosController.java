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
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Messagebox.ClickEvent;
import org.zkoss.zul.Window;

import br.ufjf.avaliacao.business.UsuarioBusiness;
import br.ufjf.avaliacao.model.Curso;
import br.ufjf.avaliacao.model.Usuario;
import br.ufjf.avaliacao.persistent.impl.CursoDAO;
import br.ufjf.avaliacao.persistent.impl.UsuarioDAO;

public class UsuariosController extends GenericController {

	private UsuarioDAO usuarioDAO = new UsuarioDAO();
	private Usuario usuario = new Usuario();
	private CursoDAO cursoDAO = new CursoDAO();
	private List<Usuario> usuarios = usuarioDAO.getTodosUsuarios();
	private List<Curso> cursos = (List<Curso>) cursoDAO.getTodosCursos();
	private Combobox cmbCurso;

	public Combobox getCmbCurso() {
		return cmbCurso;
	}

	
	
	@Command
	public void setCmbCurso(@BindingParam("cmbCurso") Combobox cmbCurso) {
		this.cmbCurso = cmbCurso;
	}

	@Init
	public void init() throws HibernateException, Exception {
		testaPermissaoAdmin();
	}

	@Command
	public void abreCadastro() {
		Window window = (Window) Executions.createComponents(
				"/cadastrarUsuario.zul", null, null);
		window.doModal();
	}

	@Command
	@NotifyChange("usuarios")
	public void exclui(@BindingParam("usuario") Usuario usuario) {
		UsuarioBusiness usuarioBusiness = new UsuarioBusiness();
		if (!usuarioBusiness.usuarioUsado(usuario)) {
			usuarioDAO.exclui(usuario);
			usuarios.remove(usuario);
		} else {
			Messagebox
					.show("ImpossÃ­vel excluir. O professor estÃ¡ associado a alguma turma.");
		}
	}

	@Command
	@NotifyChange({ "usuarios", "usuario" })
	public void cadastra() throws HibernateException, Exception {
		UsuarioBusiness usuarioBusiness = new UsuarioBusiness();
		if (!usuarioBusiness.cadatroValido(usuario)) {
			Messagebox.show("Preencha todos os campos!");
		} else if (usuarioBusiness.cadastrado(usuario.getEmail(),
				usuario.getNome())) {
			Messagebox.show("Nome e/ou email jÃ¡ cadastrado!");
		} else {
			if (usuario.getTipoUsuario() == 1)
				usuario.setCurso(null);
			if (usuarioDAO.salvar(usuario)) {
				usuarios.add(usuario);
				Messagebox.show("Usuario Cadastrado");
				usuario = new Usuario();
			}
		}
	}

	@Command
	public void changeEditableStatus(@BindingParam("usuario") Usuario usuario) {
		usuario.setEditingStatus(!usuario.isEditingStatus());
		refreshRowTemplate(usuario);
	}

	public void refreshRowTemplate(Usuario usuario) {
		BindUtils.postNotifyChange(null, null, usuario, "editingStatus");
	}

	@Command
	public void confirm(@BindingParam("usuario") Usuario usuario)
			throws HibernateException, Exception {
		UsuarioBusiness business = new UsuarioBusiness();
		if (business.cadatroValido(usuario)) {
			changeEditableStatus(usuario);
			UsuarioDAO usuarioDAO = new UsuarioDAO();
			usuarioDAO.editar(usuario);
			refreshRowTemplate(usuario);
		} else {
			Messagebox.show("UsuÃ¡rio jÃ¡ cadastrado ou invÃ¡lido");
		}
	}

	@Command
	public void desabilita(@BindingParam("combobox") Combobox cmb) {
		if (cmb.getValue().contains("Professor")) {
			cmbCurso.setDisabled(true);
			cmbCurso.setValue("NÃ£o se aplica");
		} else {
			cmbCurso.setDisabled(false);
			cmbCurso.setValue("");
		}
	}

	@Command
	public void desabilitarEdicao(@BindingParam("combo") Combobox cmb) {
		((Combobox) cmb.getPreviousSibling()).setDisabled(cmb.getValue()
				.contains("Professor"));
	}

	@Command("upload")
	// FORMATO ARQUIVO: nome,email,codigo do curso, tipo de usuario
	public void upload(@BindingParam("evt") UploadEvent evt) {
		Media media = evt.getMedia();
		if (!media.getName().contains(".csv")) {
			Messagebox
					.show("Este nÃ£o Ã© um arquivo vÃ¡lido! Apenas CSV sÃ£o aceitos.");
			return;
		}
		try {
			BufferedReader in = new BufferedReader(media.getReaderData());
			String linha;
			Usuario usuario;
			CursoDAO cursoDAO = new CursoDAO();
			List<Usuario> usuarios = new ArrayList<Usuario>();
			while ((linha = in.readLine()) != null) {
				String conteudo[] = linha.split(";");
				if (usuarioDAO.retornaUsuario(conteudo[0]) == null) {
					usuario = new Usuario(conteudo[0], conteudo[1], "12345",
							cursoDAO.getCursoNome(conteudo[2]),
							Integer.parseInt(conteudo[3]));
					usuarios.add(usuario);
				}
			}
			if (usuarioDAO.salvarLista(usuarios))
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

			Usuario usuario;
			CursoDAO cursoDAO = new CursoDAO();
			List<Usuario> usuarios = new ArrayList<Usuario>();
			String csv = new String(media.getByteData());
			String linhas[] = csv.split("\\r?\\n");
			for (String linha : linhas) {
				String conteudo[] = linha.split(";");
				if (usuarioDAO.retornaUsuario(conteudo[0]) == null) {
					usuario = new Usuario(conteudo[0], conteudo[1], "12345",
							cursoDAO.getCursoNome(conteudo[2]),
							Integer.parseInt(conteudo[3]));
					usuarios.add(usuario);
				}
			}

			if (usuarioDAO.salvarLista(usuarios))
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

		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public List<Usuario> getUsuarios() {
		return usuarios;
	}

	public void setUsuarios(List<Usuario> usuarios) {
		this.usuarios = usuarios;
	}

	public List<Curso> getCursos() {
		return cursos;
	}

	public void setCursos(List<Curso> cursos) {
		this.cursos = cursos;
	}

}

package br.pucminas.rest.dto;

import java.io.Serializable;
import br.pucminas.model.Professor;
import javax.persistence.EntityManager;
import java.util.Date;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ProfessorDTO implements Serializable {

	private static final long serialVersionUID = -6349572576272075367L;
	private Long id;
	private int version;
	private String nome;
	private Date dataNascimento;
	private String cpf;

	public ProfessorDTO() {
	}

	public ProfessorDTO(final Professor entity) {
		if (entity != null) {
			this.id = entity.getId();
			this.version = entity.getVersion();
			this.nome = entity.getNome();
			this.dataNascimento = entity.getDataNascimento();
			this.cpf = entity.getCpf();
		}
	}

	public Professor fromDTO(Professor entity, EntityManager em) {
		if (entity == null) {
			entity = new Professor();
		}
		entity.setVersion(this.version);
		entity.setNome(this.nome);
		entity.setDataNascimento(this.dataNascimento);
		entity.setCpf(this.cpf);
		entity = em.merge(entity);
		return entity;
	}

	public Long getId() {
		return this.id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public int getVersion() {
		return this.version;
	}

	public void setVersion(final int version) {
		this.version = version;
	}

	public String getNome() {
		return this.nome;
	}

	public void setNome(final String nome) {
		this.nome = nome;
	}

	public Date getDataNascimento() {
		return this.dataNascimento;
	}

	public void setDataNascimento(final Date dataNascimento) {
		this.dataNascimento = dataNascimento;
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

}
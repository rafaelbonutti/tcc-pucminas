package br.pucminas.disciplina.model;

import javax.persistence.Entity;
import java.io.Serializable;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Column;
import javax.persistence.Version;
import javax.persistence.Lob;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "TBDISCIPLINA")
@XmlRootElement
public class Disciplina implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", updatable = false, nullable = false)
	private Long id;

	@Version
	@Column(name = "VERSION")
	private int version;

	@Column(length = 7, name = "CODIGO", nullable = false)
	private String codigo;

	@Column(length = 50, name = "NOME", nullable = false)
	private String nome;

	@Lob
	@Column(length = 2147483647, name = "EMENTA")
	private String ementa;

	@Column(length = 1, name = "CREDITOS", nullable = false)
	private Integer creditos;

	@Column(length = 3, name = "CARGAHORARIA", nullable = false)
	private Integer cargaHoraria;

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

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Disciplina)) {
			return false;
		}
		Disciplina other = (Disciplina) obj;
		if (id != null) {
			if (!id.equals(other.id)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getEmenta() {
		return ementa;
	}

	public void setEmenta(String ementa) {
		this.ementa = ementa;
	}

	public Integer getCreditos() {
		return creditos;
	}

	public void setCreditos(Integer creditos) {
		this.creditos = creditos;
	}

	public Integer getCargaHoraria() {
		return cargaHoraria;
	}

	public void setCargaHoraria(Integer cargaHoraria) {
		this.cargaHoraria = cargaHoraria;
	}

	@Override
	public String toString() {
		String result = getClass().getSimpleName() + " ";
		if (codigo != null && !codigo.trim().isEmpty())
			result += "codigo: " + codigo;
		if (nome != null && !nome.trim().isEmpty())
			result += ", nome: " + nome;
		if (ementa != null && !ementa.trim().isEmpty())
			result += ", ementa: " + ementa;
		if (creditos != null)
			result += ", creditos: " + creditos;
		if (cargaHoraria != null)
			result += ", cargaHoraria: " + cargaHoraria;
		return result;
	}
}
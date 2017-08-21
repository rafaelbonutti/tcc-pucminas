package br.pucminas.web.model;

import java.io.Serializable;

public class Disciplina implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;

	private int version;

	private String codigo;

	private String nome;

	private String ementa;

	private Integer creditos;

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
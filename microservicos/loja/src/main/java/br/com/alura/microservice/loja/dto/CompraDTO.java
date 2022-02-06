package br.com.alura.microservice.loja.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

public class CompraDTO {

	@JsonIgnore
	private Long compraId;

	private List<ItemDaCompraDTO> itens;
	
	private EnderecoDTO endereco;

	public Long getCompraId() {
		return compraId;
	}

	public void setCompraId(Long compraId) {
		this.compraId = compraId;
	}

	public List<ItemDaCompraDTO> getItens() {
		return itens;
	}

	public void setItens(List<ItemDaCompraDTO> itens) {
		this.itens = itens;
	}

	public EnderecoDTO getEndereco() {
		return endereco;
	}

	public void setEndereco(EnderecoDTO endereco) {
		this.endereco = endereco;
	}
	
	
}

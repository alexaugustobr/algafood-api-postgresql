package br.com.apssystem.algafood.api.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.apssystem.algafood.api.mapper.CidadeMapper;
import br.com.apssystem.algafood.api.model.CidadeModel;
import br.com.apssystem.algafood.api.model.input.CidadeInput;
import br.com.apssystem.algafood.domain.model.Cidade;
import br.com.apssystem.algafood.domain.service.CidadeService;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/cidades")
@AllArgsConstructor
public class CidadeController {

	private CidadeService service;
	private CidadeMapper mapper;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public CidadeModel salvar(@Valid @RequestBody CidadeInput cidadeInput) {
		Cidade cidade = mapper.toDomainObject(cidadeInput);
		return mapper.toModel(service.salvar(cidade));
	}

	@PutMapping("/{id}")
	public ResponseEntity<CidadeModel> atualizar(@Valid @RequestBody CidadeInput cidadeInput, @PathVariable Long id) {
		Cidade cidade = service.buscarPorId(id);
		mapper.copyToDomainObject(cidadeInput, cidade);
		return ResponseEntity.ok(mapper.toModel(service.atualizar(cidade)));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<String> excluir(@PathVariable Long id) {
		service.excluir(id);
		return new ResponseEntity<String>("Cidade de código " + id + " foi excluído com sucesso!",
				HttpStatus.NO_CONTENT);
	}

	@GetMapping("/{id}")
	public CidadeModel buscarPorId(@PathVariable Long id) {
		return mapper.toModel(service.buscarPorId(id));
	}

	@GetMapping
	public List<CidadeModel> listarTodos() {
		return mapper.toCollectionModel(service.listarTodos());
	}

}

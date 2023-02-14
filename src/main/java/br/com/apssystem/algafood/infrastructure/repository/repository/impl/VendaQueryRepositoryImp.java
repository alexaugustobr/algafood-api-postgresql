package br.com.apssystem.algafood.infrastructure.repository.repository.impl;

import br.com.apssystem.algafood.domain.enums.StatusPedido;
import br.com.apssystem.algafood.domain.model.Pedido;
import br.com.apssystem.algafood.domain.model.dto.Venda;
import br.com.apssystem.algafood.domain.model.filter.VendaFilter;
import br.com.apssystem.algafood.infrastructure.repository.repository.query.VendaQuery;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Repository
public class VendaQueryRepositoryImp implements VendaQuery {

    private static final String DATA_CRIACAO = "dataCriacao";
    private static final String STATUS = "status";

    @PersistenceContext
    private EntityManager manager;


    @Override
    public List<Venda> consultarVendasDiarias(VendaFilter filtro) {
        var builder = manager.getCriteriaBuilder();
        var query = builder.createQuery(Venda.class);
        var root = query.from(Pedido.class);


        var functionConvertDataCriacao = builder.function(
                "timezone", Date.class, builder.literal("+00:00"), root.get(DATA_CRIACAO));


        var functionDateDataCriacao = builder.function("TO_CHAR", String.class,
                functionConvertDataCriacao,
                builder.literal("dd/MM/yyyy"));

        var selection = builder.construct(
                Venda.class, functionDateDataCriacao, builder.count(root.get("id")),
                builder.sum(root.get("valorTotal")));

        var predicates = new ArrayList<Predicate>();
        predicates = precondicoesParaFitro(filtro, builder, root, predicates);

        query.select(selection).
                where(predicates.toArray(new Predicate[0])).
                groupBy(root.get(DATA_CRIACAO));

        return manager.createQuery(query).getResultList();
    }

    @Override
    public List<Venda> consultarVendasMensais(VendaFilter filtro) {
        var builder = manager.getCriteriaBuilder();
        var query = builder.createQuery(Venda.class);
        var root = query.from(Pedido.class);
        var predicates = new ArrayList<Predicate>();

        var functionMesDataCriacao = builder.function(
                "month", Integer.class,
                root.get(DATA_CRIACAO));

        var selection = builder.construct(Venda.class,
                functionMesDataCriacao,
                builder.count(root.get("id")),
                builder.sum(root.get("valorTotal")));

        precondicoesParaFitro(filtro, builder, root, predicates);

        predicates.add(root.get(STATUS).in(
                StatusPedido.CONFIRMADO, StatusPedido.ENTREGUE));

        query.select(selection).
                where(predicates.toArray(new Predicate[0])).
                groupBy(functionMesDataCriacao);

        return manager.createQuery(query).getResultList();
    }

    @Override
    public List<Venda> consultarVendasAnuais(VendaFilter filtro) {
        var builder = manager.getCriteriaBuilder();
        var query = builder.createQuery(Venda.class);
        var root = query.from(Pedido.class);

        var predicates = new ArrayList<Predicate>();

        var functionAnoDataCriacao = builder.function(
                "year", Integer.class,
                root.get(DATA_CRIACAO));

        var selection = builder.construct(Venda.class,
                builder.count(root.get("id")),
                builder.sum(root.get("valorTotal")),
                functionAnoDataCriacao);

        precondicoesParaFitro(filtro, builder, root, predicates);

        predicates.add(root.get(STATUS).in(
                StatusPedido.CONFIRMADO, StatusPedido.ENTREGUE));

        query.select(selection).
                where(predicates.toArray(new Predicate[0])).
                groupBy(functionAnoDataCriacao);

        return manager.createQuery(query).getResultList();
    }

    private ArrayList<Predicate> precondicoesParaFitro(VendaFilter filtro, CriteriaBuilder builder, Root<Pedido> root,
                                                       ArrayList<Predicate> predicates) {
        if (filtro.getRestauranteId() != null) {
            predicates.add(builder.equal(root.get("restaurante"), filtro.getRestauranteId()));
        }

        if (filtro.getDataCriacaoInicio() != null) {
            predicates.add(builder.greaterThanOrEqualTo(root.get(DATA_CRIACAO),
                    filtro.getDataCriacaoInicio()));
        }

        if (filtro.getDataCriacaoFim() != null) {
            predicates.add(builder.lessThanOrEqualTo(root.get(DATA_CRIACAO),
                    filtro.getDataCriacaoFim()));
        }

        predicates.add(root.get(STATUS).in(
                StatusPedido.CONFIRMADO, StatusPedido.ENTREGUE));

        return predicates;
    }
}

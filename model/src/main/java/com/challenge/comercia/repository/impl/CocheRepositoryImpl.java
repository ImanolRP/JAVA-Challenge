package com.challenge.comercia.repository.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.challenge.comercia.entity.AlquilerCoche;
import com.challenge.comercia.entity.Coche;
import com.challenge.comercia.repository.custom.CocheRepositoryCustom;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;

/**
 * The type Coche repository.
 */
public class CocheRepositoryImpl implements CocheRepositoryCustom {

  // equivalencia entre columnas CocheDisponibleDto y columnas de la query
  private static final Map<String, String> columnReferences =
      Map.of("id", "root.id", //
          "matricula", "root.matricula", //
          "cocheTipoNombre", "root.cocheTipo.cocheTipoNombre" //
      );

  @PersistenceContext
  private EntityManager entityManager;

  @Override
  public Page<Coche> findDisponibles(LocalDate fechaInicio, LocalDate fechaFin,
      String cocheTipoId, Pageable pageable) {
    CriteriaBuilder builder = entityManager.getCriteriaBuilder();

    // Consulta de datos
    CriteriaQuery<Coche> query = builder.createQuery(Coche.class);
    Root<Coche> root = query.from(Coche.class);
    query.where(
        getWhere(builder, root, query, fechaInicio, fechaFin, cocheTipoId));
    query.orderBy(getOrderBy(pageable.getSort(), builder, root));
    List<Coche> content = entityManager.createQuery(query)
        .setFirstResult((int) pageable.getOffset())
        .setMaxResults(pageable.getPageSize()).getResultList();

    // Consulta de conteo total
    CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
    Root<Coche> countRoot = countQuery.from(Coche.class);
    countQuery.select(builder.countDistinct(countRoot.get("id")));
    countQuery.where(getWhere(builder, countRoot, countQuery, fechaInicio,
        fechaFin, cocheTipoId));
    Long total = entityManager.createQuery(countQuery).getSingleResult();

    return new PageImpl<>(content, pageable, total);
  }

  private Predicate[] getWhere(CriteriaBuilder builder, Root<Coche> root,
      CriteriaQuery<?> query, LocalDate fechaInicio, LocalDate fechaFin,
      String cocheTipoId) {
    List<Predicate> listWhere = new ArrayList<>();

    // Excluir coches con alquileres que se solapan con el rango solicitado.
    Subquery<Long> subquery = query.subquery(Long.class);
    Root<AlquilerCoche> alquilerCoche = subquery.from(AlquilerCoche.class);
    subquery.select(alquilerCoche.get("cocheId"));
    subquery.where(
        builder.lessThan(alquilerCoche.get("alquiler").get("fechaInicio"),
            fechaFin),
        builder.greaterThan(alquilerCoche.get("alquiler").get("fechaFin"),
            fechaInicio));
    listWhere.add(builder.not(root.get("id").in(subquery)));

    // Filtro opcional por tipo de coche.
    if (StringUtils.isNotBlank(cocheTipoId)) {
      listWhere
          .add(builder.equal(root.get("cocheTipo").get("id"), cocheTipoId));
    }

    return listWhere.toArray(new Predicate[0]);
  }

  private List<Order> getOrderBy(Sort sort, CriteriaBuilder builder,
      Root<Coche> root) {
    List<Order> orders = new ArrayList<>();
    if (!sort.isSorted()) {
      // order default de la query
      orders.add(builder.asc(root.get("id")));
      return orders;
    }
    sort.forEach(order -> {
      if (order.isAscending()) {
        orders.add(builder.asc(getOrderReference(root, order.getProperty())));
      } else {
        orders.add(builder.desc(getOrderReference(root, order.getProperty())));
      }
    });
    return orders;
  }

  private Path<Object> getOrderReference(Root<Coche> root, String columnName) {
    String columnReference = columnReferences.get(columnName);
    if (columnReference.startsWith("root.")) {
      return root.get(columnReference.split("\\.")[1]);
    }
    return null;
  }

}


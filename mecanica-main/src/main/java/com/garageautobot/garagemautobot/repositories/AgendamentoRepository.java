package com.garageautobot.garagemautobot.repositories;

import com.garageautobot.garagemautobot.entities.Agendamento;
import com.garageautobot.garagemautobot.entities.PeriodoAgendamento;
import com.garageautobot.garagemautobot.entities.StatusAgendamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {

    // Agendamentos de uma data específica, ordenados por período
    List<Agendamento> findByDataAgendadaOrderByPeriodoAsc(LocalDate data);

    // Agendamentos de uma data e período
    List<Agendamento> findByDataAgendadaAndPeriodo(LocalDate data, PeriodoAgendamento periodo);

    // Agendamentos por status
    List<Agendamento> findByStatusOrderByDataAgendadaAsc(StatusAgendamento status);

    // Agendamentos entre duas datas (para a visão de semana/mês)
    List<Agendamento> findByDataAgendadaBetweenOrderByDataAgendadaAscPeriodoAsc(
            LocalDate inicio, LocalDate fim);

    // Agendamentos futuros ainda não atendidos (status AGENDADO a partir de hoje)
    List<Agendamento> findByStatusAndDataAgendadaGreaterThanEqualOrderByDataAgendadaAsc(
            StatusAgendamento status, LocalDate data);

    // Conta agendamentos de uma data/período (para mostrar lotação)
    long countByDataAgendadaAndPeriodoAndStatus(
            LocalDate data, PeriodoAgendamento periodo, StatusAgendamento status);
}